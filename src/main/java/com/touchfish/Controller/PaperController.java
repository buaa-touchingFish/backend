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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /*@GetMapping("/id")
    public Result<PaperDoc>getWork(){
        System.out.println(es.findById("W2029916517"));
        return Result.ok("200");
    }*/

    @PostMapping("/single")
    @Operation(summary = "获取单个文献")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "文献id号  格式:\"id\":\"文献id号\"")
    public Result<Paper> getSingleWork(@RequestBody Map<String, String> json) {
        Paper one = paper.lambdaQuery().eq(Paper::getId, json.get("id")).one();
        return Result.ok("200", one);
    }

    /*   @PostMapping("/search")
       @Operation(summary = "搜索论文")
       //@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"content\":\"内容相关（title、abstract、keyword）\"")
       public Result<List<Paper>> searchWorkbyContent(@RequestBody Map<String,String>json){
           //String content=json.get("content");
           List<SearchHit<PaperDoc>> paperDocList=paper.findByTitleContains("computer");
           System.out.println(paperDocList.get(1));
           return Result.ok("200");
       }*/
   /* @GetMapping("/title")
    @Operation(summary = "获取文献")
    public Result<PaperDoc> getWork(){
        PaperDoc paperDoc=paper.findByTitle("Extrapolation and bubbles");
        System.out.println(paperDoc);
        return Result.ok("200",paperDoc);
    }*/
    @GetMapping("/abstract")
    public Result<List<PaperDoc>> getAbstract(@RequestBody Map<String,String> json) {

        //List<SearchHit<PaperDoc>> paper1 = es.findByAbstracts("computer science");
        Integer pageNum=Integer.parseInt(json.get("pageNum"));
        String content=json.get("content");
        if(pageNum<0)
            pageNum=0;
        Integer pageSize=20;
        Pageable pageable = PageRequest.of(pageNum,pageSize);
        Page<PaperDoc> page = es.findByAbstracts(content,pageable);
        List<PaperDoc>paperDocs=new ArrayList<>();
        for(PaperDoc paperDoc:page) {
            paperDocs.add(paperDoc);
        }
        return Result.ok("查询成功",paperDocs);
    }
}
