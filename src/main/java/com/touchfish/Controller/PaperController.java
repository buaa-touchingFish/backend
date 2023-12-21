package com.touchfish.Controller;


import cn.hutool.core.convert.Convert;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.touchfish.Dao.ElasticSearchRepository;
import com.touchfish.Dto.SearchInfo;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchfish.Dto.PaperInfo;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.RefWork;
import com.touchfish.MiddleClass.RelWork;
import com.touchfish.Po.Paper;
import com.touchfish.Po.PaperDoc;
import com.touchfish.Service.impl.PaperImpl;
import com.touchfish.Tool.OpenAlex;
import com.touchfish.Tool.RedisKey;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private PaperImpl paperImpl;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /*@GetMapping("/id")
    public Result<PaperDoc>getWork(){
        System.out.println(es.findById("W2029916517"));
        return Result.ok("200");
    }*/

    @PostMapping ("/single")
    @Operation(summary = "点击获取单个文献")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "文献id号  格式:\"id\":\"文献id号\"")
    public Result<PaperInfo> getSingleWork( @RequestBody  Map<String,String> json){
        String id1 = stringRedisTemplate.opsForValue().get(RedisKey.PAPER_KEY+json.get("id"));
        if (id1 != null){
            PaperInfo paperInfo = JSONUtil.toBean(id1, PaperInfo.class);
            return Result.ok("成功返回",paperInfo);
        }
        Paper paper = paperImpl.lambdaQuery().eq(Paper::getId,json.get("id")).one();
        ObjectMapper mapper = new ObjectMapper();
        List<AuthorShip> authorships = paper.getAuthorships();
        List<AuthorShip> authorShipList = mapper.convertValue(authorships, new TypeReference<>() {});
        List<String> referenced_works = paper.getReferenced_works();
        List<String> related_works = paper.getRelated_works();

        PaperInfo paperInfo = new PaperInfo();
        paperInfo.setAbstract(paper.getAbstract());
        paperInfo.setIssn(paper.getIssn());
        paperInfo.setDoi(paper.getDoi());
        paperInfo.setLan(paper.getLan());
        paperInfo.setKeywords(paper.getKeywords());
        paperInfo.setTitle(paper.getTitle());
        paperInfo.setCited_by_count(paper.getCited_by_count());
        paperInfo.setPublication_date(paper.getPublication_date());
        paperInfo.setPublisher(paperInfo.getPublisher());
        paperInfo.setAuthors(paper.getAuthorships());
        paperInfo.setType(paper.getType());
        paperInfo.setId(paper.getId());
        paperInfo.setRefWorks(paper.getReferenced_works());
        paperInfo.setRelWorks(paper.getRelated_works());

        ThreadUtil.execute(()->{
            for (String id:referenced_works){
                Paper one = paperImpl.getPaperByAlex(id);
                paperImpl.saveOrUpdate(one);
            }
        });

        ThreadUtil.execute(()->{
            for (String id:related_works){
                Paper one = paperImpl.getPaperByAlex(id);
                paperImpl.saveOrUpdate(one);
            }
        });
        String s = JSONUtil.toJsonStr(paperInfo);
        stringRedisTemplate.opsForValue().set(RedisKey.PAPER_KEY+paperInfo.getId(),s);
        return Result.ok("成功返回",paperInfo);
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
    @PostMapping("/getRef")
    @Operation(summary = "点击获取文献的参考文献")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "格式:\"ref\":[id1,id2,id3....]")
    public Result<List<RefWork>> getRefWork(@RequestBody Map<String,List<String>> mp){
        List<String> refs = mp.get("ref");
        ObjectMapper mapper = new ObjectMapper();
        List<RefWork> ans = new ArrayList<>();
        for (String id:refs){
            RefWork relWork = new RefWork();
            Paper one = null;
            if (paperImpl.lambdaQuery().eq(Paper::getId,id).exists()){
                one = paperImpl.lambdaQuery().eq(Paper::getId, id).one();
            }else {
                continue;
            }
            List<AuthorShip> authorships1 = one.getAuthorships();
            List<AuthorShip> authorShipList1 = mapper.convertValue(authorships1, new TypeReference<>() {});
            relWork.setAbstract(one.getAbstract());
            relWork.setId(one.getId());
            relWork.setTitle(one.getTitle());
            if (one.getPublisher() != null ){
                relWork.setPublisher(one.getPublisher().display_name);
            }
            relWork.setCited_by_count(one.getCited_by_count());
            relWork.setPublication_date(one.getPublication_date());
            for (int i=0;i<3&&i<authorShipList1.size();i++){ //展示至多3位
                relWork.getAuthors().add(authorShipList1.get(i).getAuthor());
            }
            ans.add(relWork);
        }
        return Result.ok("成功返回",ans);
    }

    @PostMapping("/getRel")
    @Operation(summary = "点击获取文献的参考文献")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "格式:\"rel\":[id1,id2,id3....]")
    public Result<List<RelWork>> getRelWork(@RequestBody Map<String,List<String>> mp){
        List<String> rels = mp.get("rel");
        ObjectMapper mapper = new ObjectMapper();
        List<RelWork> ans = new ArrayList<>();
        for (String id:rels){
            RelWork relWork = new RelWork();
            Paper one = null;
            if (paperImpl.lambdaQuery().eq(Paper::getId,id).exists()){
                one = paperImpl.lambdaQuery().eq(Paper::getId, id).one();
            }else {
                continue;
            }
            List<AuthorShip> authorships1 = one.getAuthorships();
            List<AuthorShip> authorShipList1 = mapper.convertValue(authorships1, new TypeReference<>() {});
            relWork.setAbstract(one.getAbstract());
            relWork.setId(one.getId());
            relWork.setTitle(one.getTitle());
            if (one.getPublisher() != null ){
                relWork.setPublisher(one.getPublisher().display_name);
            }
            relWork.setCited_by_count(one.getCited_by_count());
            relWork.setPublication_date(one.getPublication_date());
            for (int i=0;i<3&&i<authorShipList1.size();i++){ //展示至多3位
                relWork.getAuthors().add(authorShipList1.get(i).getAuthor());
            }
            ans.add(relWork);
        }
        return Result.ok("成功返回",ans);
    }
}
