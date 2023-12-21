package com.touchfish.Controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "浏览的文献id")
    public Result<String> createHistory(@RequestBody Map<String, String> map){
        History formerHistory = history.lambdaQuery().eq(History::getId, map.get("paper_id")).one();
        if(formerHistory == null){
            try {
                history.save(new History(UserContext.getUser().getUid(), map.get("paper_id"), getTimeNow()));
            }catch (Exception e){
                e.printStackTrace();
                return Result.fail("创建浏览记录失败");
            }
        }

        else {
            formerHistory.setCreate_time(getTimeNow());
            history.save(formerHistory);
        }

        return Result.ok("创建历史记录成功");
    }

    @PostMapping("/get")
    @LoginCheck
    @Operation(summary = "获取用户浏览记录", security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "用户id")
    public Result<List<History>> getHistoriesByUid(@RequestBody Map<String, String> map){
        List<History> historyList = history.lambdaQuery().eq(History::getUser_id, map.get("uid")).list();
        return Result.ok("查询用户浏览记录成功", historyList);
    }



}
