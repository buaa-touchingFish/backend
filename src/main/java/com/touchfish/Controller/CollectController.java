package com.touchfish.Controller;

import com.touchfish.Service.impl.CollectImpl;
import com.touchfish.Tool.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/collect")
public class CollectController {
    @Autowired
    private CollectImpl collectService;

    @PostMapping
    public Result save(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String paper_id = map.get("paper_id");
        if (collectService.saveCollect(user_id, paper_id))
            return Result.ok("收藏成功");
        else
            return Result.fail("已收藏");
    }

    @DeleteMapping
    public Result delete(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String paper_id = map.get("paper_id");
        if (collectService.deleteCollect(user_id, paper_id))
            return Result.ok("取消收藏成功");
        else
            return Result.fail("未收藏");
    }

    @GetMapping
    public Result getPaperByUser(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        ArrayList<String> collects = collectService.getCollects(user_id);
        return Result.ok("获取收藏列表成功", collects);
    }
}
