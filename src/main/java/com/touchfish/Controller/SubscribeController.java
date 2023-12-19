package com.touchfish.Controller;

import com.touchfish.Service.impl.SubscribeImpl;
import com.touchfish.Tool.LoginCheck;
import com.touchfish.Tool.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/subscribe")
@Tag(name = "订阅相关接口")
public class SubscribeController {
    @Autowired
    private SubscribeImpl subscribeService;

    @PostMapping
    @LoginCheck
    @Operation(summary = "订阅学者")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"user_id\":\"用户id\", \"author_id\":\"学者id\"")
    public Result<String> save(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String author_id = map.get("author_id");
        if(subscribeService.saveSubscribe(user_id, author_id))
            return Result.ok("订阅成功");
        else
            return Result.fail("已订阅");
    }

    @DeleteMapping
    @LoginCheck
    @Operation(summary = "取消订阅")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"user_id\":\"用户id\", \"author_id\":\"学者id\"")
    public Result<String> delete(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String author_id = map.get("author_id");
        if(subscribeService.deleteSubscribe(user_id, author_id))
            return Result.ok("取消订阅成功");
        else
            return Result.fail("未订阅");
    }

    @GetMapping
    @LoginCheck
    @Operation(summary = "获取订阅列表")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"user_id\":\"用户id\"")
    public Result<ArrayList<String>> getAuthorByUser(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        ArrayList<String> subscribes = subscribeService.getSubscribes(user_id);
        return Result.ok("获取订阅列表成功", subscribes);
    }
}
