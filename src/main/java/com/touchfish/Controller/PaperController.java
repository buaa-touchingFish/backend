package com.touchfish.Controller;

import com.touchfish.Dao.ElasticSearchRepository;
import com.touchfish.Dto.SearchInfo;
import com.touchfish.Po.Paper;
import com.touchfish.Po.PaperDoc;
import com.touchfish.Service.impl.PaperImpl;
import com.touchfish.Tool.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/paper")
@Tag(name = "论文相关接口")
public class PaperController {

    @Autowired
    private PaperImpl paper;
    @Autowired
    private ElasticSearchRepository es;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping ("/single")
    @Operation(summary = "获取单个文献")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "文献id号  格式:\"id\":\"文献id号\"")
    public Result<Paper> getSingleWork(  @RequestBody  Map<String,String> json){
        Paper one = paper.lambdaQuery().eq(Paper::getId,json.get("id")).one();
        return Result.ok("200",one);
    }
    @GetMapping("/search")
    @Operation(summary = "搜索论文")
    //@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"content\":\"内容相关（title、abstract、keyword）\"")
    public Result<List<Paper>> searchWorkbyContent(){
        //String content=json.get("content");
        List<SearchHit<PaperDoc>> paperDocList=paper.findByTitleContains("computer");
        System.out.println(paperDocList.get(1));
        return Result.ok("200");
    }
   /* @GetMapping("/title")
    @Operation(summary = "获取文献")
    public Result<PaperDoc> getWork(){
        PaperDoc paperDoc=paper.findByTitle("Extrapolation and bubbles");
        System.out.println(paperDoc);
        return Result.ok("200",paperDoc);
    }*/
    @GetMapping("/abstract")
    public Result<List<PaperDoc>> getAbstract(){
        List<SearchHit<PaperDoc>>paper1=paper.findByAbstract("computer");
        for(SearchHit<PaperDoc> paperDocSearchHit:paper1){
            System.out.println(paperDocSearchHit.getContent());
        }
        return Result.ok("200");
    }
}
