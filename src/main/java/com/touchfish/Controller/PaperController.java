package com.touchfish.Controller;


import cn.hutool.core.convert.Convert;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
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
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository;
import org.springframework.data.elasticsearch.core.query.BaseQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.SearchOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/paper")
@Tag(name = "论文相关接口")
public class PaperController {
    Integer pageSize=100;

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
    @PostMapping ("/search")
    @Operation(summary = "根据关键词查询文献")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"pageNum\":\"页数\",\"keyword\":\"内容相关（title、abstract、keyword）\",\"author\":\"作者姓名\",\"publisher\":\"刊物\",\"institution\":\"机构\"")
    public Result<List<Paper>> searchKeyword(@RequestBody SearchInfo searchInfo) {
        Integer pageNum=searchInfo.getPageNum();
        String keyword=searchInfo.getKeyword();
        String author=searchInfo.getAuthor();
        String institution=searchInfo.getInstitution();
        String publisher=searchInfo.getPublisher();
        Page<PaperDoc> page;
        if(pageNum<0)
            pageNum=0;
        Pageable pageable = PageRequest.of(pageNum,pageSize);
        if(!keyword.equals(""))
            page = es.findByInformation(keyword,pageable);
        else if(!author.equals(""))
            page=es.findByAuthorships(author,pageable);
        else if(!institution.equals(""))
            page=es.findByAuthorships(institution,pageable);
        else
            page=es.findByPublisher(publisher,pageable);
        List<Paper>papers=new ArrayList<>();
        for(PaperDoc paperDoc:page) {
            papers.add(new Paper(paperDoc));
        }
        return Result.ok("查询成功",papers);
    }
    //关键词作者刊物年份
    /*@PostMapping("/author")
    @Operation(summary = "根据作者查文献")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"pageNum\":\"页数\",\"author\":\"作者姓名\"")
    public Result<List<PaperDoc>> searchAuthor(@RequestBody Map<String,String> json){
        Integer pageNum=Integer.parseInt(json.get("pageNum"));
        String content=json.get("author");
        if(pageNum<0)
            pageNum=0;
        Pageable pageable = PageRequest.of(pageNum,pageSize);
        Page<PaperDoc> page = es.findByAuthorships(content,pageable);
        List<PaperDoc>paperDocs=new ArrayList<>();
        for(PaperDoc paperDoc:page) {
            paperDocs.add(paperDoc);
        }
        return Result.ok("查询成功",paperDocs);
    }*/
    @PostMapping("/ultraSearch")
    @Operation(summary = "高级搜索")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"pageNum\":\"页数\",\"keyword\":\"内容相关（title、abstract、keyword）\",\"author\":\"作者姓名\",\"publisher\":\"刊物\"")
    public Result<List<PaperDoc>> ultraSearch(@RequestBody SearchInfo searchInfo){
        /*long a=es.count();
        Query.findAll();
        System.out.println(a);*/

        //count.query();
        //String author=json.get("author");
        //Pageable pageable = PageRequest.of(0,pageSize);

        //Page<PaperDoc> page=es.findByAuthorships(author,pageable);
        /*List<PaperDoc>paperDocs=new ArrayList<>();
        for(PaperDoc paperDoc:page) {
            paperDocs.add(paperDoc);
        }*/
        //return Result.ok("查询成功",paperDocs);

        return Result.ok("");
    }
}
