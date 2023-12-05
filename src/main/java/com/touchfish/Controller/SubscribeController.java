package com.touchfish.Controller;

import com.touchfish.Service.impl.SubscribeImpl;
import com.touchfish.Tool.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/subscribe")
public class SubscribeController {
    @Autowired
    private SubscribeImpl subscribeService;

    @PostMapping
    public Result<String> save(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String author_id = map.get("author_id");
        if(subscribeService.saveSubscribe(user_id, author_id))
            return Result.ok("订阅成功");
        else
            return Result.fail("已订阅");
    }

    @DeleteMapping
    public Result<String> delete(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String author_id = map.get("author_id");
        if(subscribeService.deleteSubscribe(user_id, author_id))
            return Result.ok("取消订阅成功");
        else
            return Result.fail("未订阅");
    }

    @GetMapping
    public Result<ArrayList<String>> getAuthorByUser(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        ArrayList<String> subscribes = subscribeService.getSubscribes(user_id);
        return Result.ok("获取订阅列表成功", subscribes);
    }
}
