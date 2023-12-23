package com.touchfish.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.touchfish.Po.History;
import com.touchfish.Service.impl.HistoryImpl;
import com.touchfish.Service.impl.PaperImpl;
import com.touchfish.Tool.LoginCheck;
import com.touchfish.Tool.Result;
import com.touchfish.Tool.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/history")
@Tag(name = "浏览记录相关接口")
public class HistoryController {
    @Autowired
    private HistoryImpl history;

    @Autowired
    private PaperImpl paper;

    private static String getTimeNow(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    @PostMapping("/create")
    @LoginCheck
    @Operation(summary = "创建浏览记录，如果已存在则更新", security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "浏览的文献id\n格式: \"paper_id\":\"string\"")
    public Result<String> createHistory(@RequestBody Map<String, String> map){
        History formerHistory = history.lambdaQuery().eq(History::getPaper_id, map.get("paper_id"))
                .eq(History::getUser_id, UserContext.getUser().getUid())
                .one();
        if(formerHistory == null){
            try {
                history.save(new History(UserContext.getUser().getUid(), map.get("paper_id"), getTimeNow()));
            }catch (Exception e){
                e.printStackTrace();
                return Result.fail("创建浏览记录失败");
            }
        }

        else {
            formerHistory.setLast_update_time(getTimeNow());
            formerHistory.setView_times(formerHistory.getView_times() + 1);
            history.updateById(formerHistory);
        }

        return Result.ok("创建历史记录成功");
    }

    @GetMapping("/get/user")
    @LoginCheck
    @Operation(summary = "获取当前用户浏览记录", security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "不需要")
    public Result<List<History>> getHistoriesByUid(){
        List<History> historyList = history.lambdaQuery().eq(History::getUser_id,
                UserContext.getUser().getUid()).list();
        return Result.ok("查询用户浏览记录成功", historyList);
    }

    @GetMapping("/get/count")
    @Operation(summary = "获取所有用户浏览总量", security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "不需要")
    public Result<Integer> getHistoryCount(){
        QueryWrapper<History> historyQueryWrapper = new QueryWrapper<>();

        historyQueryWrapper.select("view_times");
        List<History> historyList = history.getBaseMapper().selectList(historyQueryWrapper);

        return Result.ok("查询总浏览量成功", historyList.stream().mapToInt(History::getView_times).sum());
    }



}
