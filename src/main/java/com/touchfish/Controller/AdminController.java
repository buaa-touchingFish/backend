package com.touchfish.Controller;

import cn.hutool.json.JSONUtil;
import com.touchfish.Dto.*;
import com.touchfish.Po.*;
import com.touchfish.Service.impl.*;
import com.touchfish.Tool.LoginCheck;
import com.touchfish.Tool.RedisKey;
import com.touchfish.Tool.Result;
import com.touchfish.Tool.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员相关接口")
public class AdminController {
    @Autowired
    private UserImpl user;
    @Autowired
    private ClaimRequestImpl claimRequest;
    @Autowired
    private AuthorImpl author;
    @Autowired
    private NoticeImpl notice;
    @Autowired
    private PaperAppealImpl paperAppeal;
    @Autowired
    private PaperImpl paper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    private static String getTimeNow(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    private List<ClaimFormInfo> fillClaimForm(List<ClaimRequest> unClaimedList){
        List<ClaimFormInfo> retList = new ArrayList<>();
        for (ClaimRequest each : unClaimedList) {
            retList.add(new ClaimFormInfo(each, user.getBaseMapper().selectById(each.getApplicant_id()),
                    author.getBaseMapper().selectById(each.getAuthor_id())));
        }
        return retList;
    }

    private List<AppealFormInfo> fillAppealForm(List<PaperAppeal> appealList){
        List<AppealFormInfo> retList = new ArrayList<>();
        for (PaperAppeal each : appealList) {
            retList.add(new AppealFormInfo(each, user.getBaseMapper().selectById(each.getApplicant_id()),
                    paper.getBaseMapper().selectById(each.getPaper_id())));
        }
        return retList;
    }

    @GetMapping ("/unclaimed")
    @LoginCheck
    @Operation(summary = "获取未处理的认领申请", security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "不需要")
    public Result<List<ClaimFormInfo>> getUnhandledClaim(){
        List<ClaimRequest> unClaimedList = claimRequest.lambdaQuery().eq(ClaimRequest::getStatus, 0).list();
        return Result.ok("查询所有未处理认领门户申请成功", fillClaimForm(unClaimedList));
    }

    @PostMapping ("/handle/claim")
    @LoginCheck
    @Operation(summary = "处理认领申请", security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "认领申请id 处理结果(false表示不通过，true为通过)")
    public Result<String> handleClaimRequest(@RequestBody ClaimResultInfo claimResultInfo){
        ClaimRequest targetClaim = claimRequest.getBaseMapper().selectById(claimResultInfo.getClaimRequestId());

        if(targetClaim.getStatus() != 0){
            return Result.fail("该认领申请已经被处理");
        }

        /// 1代表通过，-1代表未通过
        targetClaim.setStatus(claimResultInfo.isResult() ? 1 : -1);
        User targetUser = user.getBaseMapper().selectById(targetClaim.getApplicant_id());
        Author targetAuthor = author.getBaseMapper().selectById(targetClaim.getAuthor_id());

        targetClaim.setHandle_time(getTimeNow());
        targetClaim.setHandler_id(UserContext.getUser().getUid());
        /// 同时创建消息通知，并在对应用户与学者门户下添加相应内容
        if(claimResultInfo.isResult()){
            targetUser.setAuthor_id(targetClaim.getAuthor_id());
            targetAuthor.setClaim_uid(targetClaim.getApplicant_id());

            targetClaim.setStatus(1);

            user.updateById(targetUser);
            author.updateById(targetAuthor);

            notice.save(new Notice("认领门户申请成功",
                    "您认领的学者门户" + targetAuthor.getDisplay_name() + "成功",
                    getTimeNow(),
                    targetUser.getUid(),
                    false));

        }

        else {
            targetClaim.setStatus(-1);

            notice.save(new Notice("认领门户申请失败",
                    "您认领的学者门户" + targetAuthor.getDisplay_name() + "审核未通过",
                    getTimeNow(),
                    targetUser.getUid(),
                    false));
        }

        claimRequest.updateById(targetClaim);
        return Result.ok("处理认领申请成功");
    }

    @GetMapping ("/myclaims")
    @LoginCheck
    @Operation(summary = "获取当前管理员处理的认领申请", security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "不需要")
    public Result<List<ClaimFormInfo>> getAdminClaim(){
        List<ClaimRequest> claimedList = claimRequest.lambdaQuery().eq(ClaimRequest::getHandler_id,
                UserContext.getUser().getUid()).list();
        return Result.ok("查询管理员处理的认领门户申请成功", fillClaimForm(claimedList));
    }

    @GetMapping ("/unappealed")
    @LoginCheck
    @Operation(summary = "获取未处理的申诉", security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "不需要")
    public Result<List<AppealFormInfo>> getUnhandledAppeal(){
        List<PaperAppeal> appealList = paperAppeal.lambdaQuery().eq(PaperAppeal::getStatus, false).list();
        return Result.ok("查询未处理申诉成功", fillAppealForm(appealList));
    }

    @PostMapping ("/handle/appeal")
    @LoginCheck
    @Operation(summary = "处理申诉", security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "申诉id 处理结果(false表示不通过，true为通过)")
    public Result<String> handleAppeal(@RequestBody AppealResultInfo appealResultInfo){
        PaperAppeal targetAppeal = paperAppeal.getBaseMapper().selectById(appealResultInfo.getId());
        String timeNow = getTimeNow();
        targetAppeal.setHandle_time(timeNow);
        targetAppeal.setStatus(appealResultInfo.isResult() ? 1 : -1);
        targetAppeal.setHandler_id(UserContext.getUser().getUid());
        Paper targetPaper = paper.getBaseMapper().selectById(targetAppeal.getPaper_id());

        if(appealResultInfo.isResult()){
            targetPaper.setIs_active(false);

            paper.updateById(targetPaper);
            stringRedisTemplate.delete(RedisKey.PAPER_KEY+targetPaper.getId());

            notice.save(new Notice("申诉下架论文成功",
                    "您申诉的论文" + targetPaper.getTitle() + "已下架",
                    timeNow,
                    targetAppeal.getApplicant_id(),
                    false));

        }

        else {
            notice.save(new Notice("申诉下架论文失败",
                    "您申诉下架论文" + targetPaper.getTitle() + "未予以通过",
                    timeNow,
                    targetAppeal.getApplicant_id(),
                    false));
        }

        paperAppeal.updateById(targetAppeal);

        return Result.ok("处理申诉成功");
    }

    @GetMapping ("/myappeal")
    @LoginCheck
    @Operation(summary = "获取未处理的申诉", security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "不需要")
    public Result<List<AppealFormInfo>> getAdminAppeal(){
        List<PaperAppeal> appealList = paperAppeal.lambdaQuery().
                eq(PaperAppeal::getHandler_id, UserContext.getUser().getUid()).list();
        return Result.ok("查询当前管理员处理的申诉成功", fillAppealForm(appealList));
    }

}
