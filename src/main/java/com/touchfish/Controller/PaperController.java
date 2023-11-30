package com.touchfish.Controller;

import com.touchfish.Po.Paper;
import com.touchfish.Service.impl.PaperImpl;
import com.touchfish.Tool.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/paper")
@Tag(name = "论文相关接口")
public class PaperController {

    @Autowired
    PaperImpl paper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping ("/single")
    @Operation(summary = "获取单个文献")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "文献id号  格式:\"id\":\"文献id号\"")
    public Result<Paper> getSingleWork(  @RequestBody  Map<String,String> json){
        Paper one = paper.lambdaQuery().eq(Paper::getId,json.get("id")).one();
        return Result.ok("200",one);
    }

}
