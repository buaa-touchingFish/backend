package com.touchfish.Controller;

import com.touchfish.Po.ClaimRequest;
import com.touchfish.Service.impl.ClaimRequestImpl;
import com.touchfish.Tool.LoginCheck;
import com.touchfish.Tool.Result;
import com.touchfish.Tool.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/claim")
@Tag(name = "认领申请相关接口")
public class ClaimRequestController {
    @Autowired
    private ClaimRequestImpl claimRequest;

    private static String getTimeNow(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }


    @PostMapping("/create")
    @LoginCheck
    @Operation(summary = "创建认领申请")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "申请认领的门户id")
    public Result<String> createClaimRequest(@RequestBody Map<String, String> map) {
        try{
            claimRequest.save(new ClaimRequest(UserContext.getUser().getUid(), getTimeNow(), map.get("author_id")));
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail("申请认领门户失败");
        }

        return Result.ok("申请认领门户成功");
    }


}
