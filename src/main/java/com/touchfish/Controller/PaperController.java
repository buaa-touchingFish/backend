package com.touchfish.Controller;

import com.touchfish.Po.Paper;
import com.touchfish.Service.impl.PaperImpl;
import com.touchfish.Tool.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/paper")
public class PaperController {

    @Autowired
    PaperImpl paper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping ("/single")
    public Result getSingleWork(@RequestBody Map<String,String> json){
        Paper one = paper.lambdaQuery().eq(Paper::getId,json.get("id")).one();
        return Result.ok("200",one);
    }
}
