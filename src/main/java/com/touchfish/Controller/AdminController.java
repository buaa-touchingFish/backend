package com.touchfish.Controller;

import com.touchfish.Dto.ClaimResultInfo;
import com.touchfish.Po.Author;
import com.touchfish.Po.ClaimRequest;
import com.touchfish.Po.Notice;
import com.touchfish.Po.User;
import com.touchfish.Service.impl.AuthorImpl;
import com.touchfish.Service.impl.ClaimRequestImpl;
import com.touchfish.Service.impl.NoticeImpl;
import com.touchfish.Service.impl.UserImpl;
import com.touchfish.Tool.LoginCheck;
import com.touchfish.Tool.Result;
import com.touchfish.Tool.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
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

    private static String getTimeNow(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    @GetMapping ("/unclaimed")
    @LoginCheck
    @Operation(summary = "获取未处理的认领申请")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "不需要")
    public Result<List<ClaimRequest>> getUnhandledClaim(){
        List<ClaimRequest> unClaimedList = claimRequest.lambdaQuery().eq(ClaimRequest::getStatus, 0).list();
        return Result.ok("查询所有未处理认领门户申请成功", unClaimedList);
    }

    @PostMapping ("/handle/claim")
    @LoginCheck
    @Operation(summary = "处理认领申请")
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

        /// 同时创建消息通知，并在对应用户与学者门户下添加相应内容
        if(claimResultInfo.isResult()){

            targetUser.setAuthor_id(targetClaim.getAuthor_id());
            targetAuthor.setClaim_uid(targetClaim.getApplicant_id());

            user.updateById(targetUser);
            author.updateById(targetAuthor);

            notice.save(new Notice("认领门户申请成功",
                    "您认领的学者门户" + targetAuthor.getDisplay_name() + "成功",
                    getTimeNow(),
                    targetUser.getUid(),
                    false));

        }

        else {
            notice.save(new Notice("认领门户申请失败",
                    "您认领的学者门户" + targetAuthor.getDisplay_name() + "审核未通过",
                    getTimeNow(),
                    targetUser.getUid(),
                    false));
        }

        return Result.ok("处理认领申请成功");
    }

    @GetMapping ("/myclaims")
    @LoginCheck
    @Operation(summary = "获取当前管理员处理的认领申请")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "不需要")
    public Result<List<ClaimRequest>> getAdminClaim(){
        List<ClaimRequest> ClaimedList = claimRequest.lambdaQuery().eq(ClaimRequest::getHandler_id,
                UserContext.getUser().getUid()).list();
        return Result.ok("查询管理员处理的认领门户申请成功", ClaimedList);
    }


}
