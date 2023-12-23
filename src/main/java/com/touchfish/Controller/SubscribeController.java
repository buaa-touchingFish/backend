package com.touchfish.Controller;

import com.touchfish.Po.SubscribeCnt;
import com.touchfish.Service.impl.SubscribeCntImpl;
import com.touchfish.Service.impl.SubscribeImpl;
import com.touchfish.Tool.LoginCheck;
import com.touchfish.Tool.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Autowired
    private SubscribeCntImpl subscribeCntService;

    @PostMapping
    @LoginCheck
    @Operation(summary = "订阅学者", security = {@SecurityRequirement(name = "bearer-key")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"user_id\":\"用户id\", \"author_id\":\"学者id\"")
    public Result<String> save(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String author_id = map.get("author_id");
        if (subscribeService.saveSubscribe(user_id, author_id)) {
            SubscribeCnt subscribeCnt = subscribeCntService.getById(author_id);
            if (subscribeCnt == null)
                subscribeCntService.save(new SubscribeCnt(author_id, 1));
            else {
                subscribeCnt.setSubscribe_cnt(subscribeCnt.getSubscribe_cnt() + 1);
                subscribeCntService.updateById(subscribeCnt);
            }
            return Result.ok("订阅成功");
        } else
            return Result.fail("已订阅");
    }

    @DeleteMapping
    @LoginCheck
    @Operation(summary = "取消订阅", security = {@SecurityRequirement(name = "bearer-key")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"user_id\":\"用户id\", \"author_id\":\"学者id\"")
    public Result<String> delete(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String author_id = map.get("author_id");
        if (subscribeService.deleteSubscribe(user_id, author_id)) {
            SubscribeCnt subscribeCnt = subscribeCntService.getById(author_id);
            subscribeCnt.setSubscribe_cnt(subscribeCnt.getSubscribe_cnt() - 1);
            subscribeCntService.updateById(subscribeCnt);
            return Result.ok("取消订阅成功");
        } else
            return Result.fail("未订阅");
    }

    @GetMapping
    @LoginCheck
    @Operation(summary = "获取订阅列表", security = {@SecurityRequirement(name = "bearer-key")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"user_id\":\"用户id\"")
    public Result<ArrayList<String>> getAuthorByUser(Integer user_id) {
        ArrayList<String> subscribes = subscribeService.getSubscribes(user_id);
        return Result.ok("获取订阅列表成功", subscribes);
    }
}
