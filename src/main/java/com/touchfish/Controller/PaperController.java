package com.touchfish.Controller;


import cn.hutool.core.convert.Convert;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.touchfish.Dao.ElasticSearchRepository;
import com.touchfish.Dto.HotPaper;
import com.touchfish.Dto.SearchInfo;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchfish.Dto.PaperInfo;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.RefWork;
import com.touchfish.MiddleClass.RelWork;
import com.touchfish.Po.AuthorPaper;
import com.touchfish.Po.Comment;
import com.touchfish.Po.Paper;
import com.touchfish.Po.PaperAppeal;
import com.touchfish.Po.PaperDoc;
import com.touchfish.Service.impl.PaperAppealImpl;
import com.touchfish.Service.impl.PaperImpl;
import com.touchfish.Tool.*;
import com.touchfish.Service.impl.AuthorPaperImpl;
import com.touchfish.Po.PaperDoc;
import com.touchfish.Service.impl.CommentImpl;
import com.touchfish.Service.impl.PaperImpl;
import com.touchfish.Tool.RedisKey;
import com.touchfish.Tool.Result;

import com.touchfish.Tool.ZsetRedis;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository;
import org.springframework.data.elasticsearch.core.query.BaseQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.SearchOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.awt.*;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/paper")
@Tag(name = "论文相关接口")
public class PaperController {
    Integer pageSize=100;
    @Autowired
    private ElasticSearchRepository es;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    @Autowired
    private PaperImpl paperImpl;
    @Autowired
    private PaperAppealImpl paperAppeal;

    @Autowired
    private AuthorPaperImpl authorPaperImpl;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ZsetRedis zsetRedis;
    /*@GetMapping("/id")
    public Result<PaperDoc>getWork(){
        System.out.println(es.findById("W2029916517"));
        return Result.ok("200");
    }*/

    private static String getTimeNow(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    @PostMapping ("/single")
    @Operation(summary = "点击获取单个文献")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "文献id号  格式:\"id\":\"文献id号\"")
    public Result<PaperInfo> getSingleWork( @RequestBody  Map<String,String> json){
        ThreadUtil.execute(()->{
            stringRedisTemplate.opsForValue().increment(RedisKey.SUM_LOOK_KEY,1);
            zsetRedis.incrementScore(RedisKey.BROWSE_CNT_KEY+RedisKey.getEveryDayKey(),json.get("id"),1.0);
            zsetRedis.setTTL(RedisKey.BROWSE_CNT_KEY+RedisKey.getEveryDayKey(),2l,TimeUnit.DAYS);

        });
        Integer browse = zsetRedis.incrementScore(RedisKey.BROWSE_CNT_KEY, json.get("id"), 1.0);
        String id1 = stringRedisTemplate.opsForValue().get(RedisKey.PAPER_KEY+json.get("id"));
        if (id1 != null){
            PaperInfo paperInfo = JSONUtil.toBean(id1, PaperInfo.class);
            paperInfo.setBrowse(browse);
            paperInfo.setGood(zsetRedis.getScore(RedisKey.GOOD_CNT_KEY,json.get("id")));
            paperInfo.setCollect(zsetRedis.getScore(RedisKey.COLLECT_CNT_KEY,json.get("id")));
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
        paperInfo.setAuthorships(paper.getAuthorships());
        paperInfo.setType(paper.getType());
        paperInfo.setId(paper.getId());
        paperInfo.setReferenced_works(paper.getReferenced_works());
        paperInfo.setRelated_works(paper.getRelated_works());
        paperInfo.setBrowse(browse);
        paperInfo.setGood(zsetRedis.getScore(RedisKey.GOOD_CNT_KEY,json.get("id")));
        paperInfo.setCollect(zsetRedis.getScore(RedisKey.COLLECT_CNT_KEY,json.get("id")));

        ThreadUtil.execute(()->{
            for (String id:referenced_works){
                Paper one = paperImpl.getPaperByAlex(id);
                paperImpl.saveOrUpdate(one);
                List<AuthorShip> authorShipList1 = one.getAuthorships();
                for (AuthorShip authorShip:authorShipList1){
                    if (authorPaperImpl.lambdaQuery().eq(AuthorPaper::getId,authorShip.getAuthor().id).exists()){
                        AuthorPaper one1 = authorPaperImpl.lambdaQuery().eq(AuthorPaper::getId, authorShip.getAuthor().id).one();
                        if (!one1.getPapers().contains(one.getId())){
                            one1.getPapers().add(one.getId());
                        }
                        authorPaperImpl.updateById(one1);
                    }
                }
            }
        });

        ThreadUtil.execute(()->{
            for (String id:related_works){
                Paper one = paperImpl.getPaperByAlex(id);
                paperImpl.saveOrUpdate(one);
                List<AuthorShip> authorShipList1 = one.getAuthorships();
                for (AuthorShip authorShip:authorShipList1){
                    if (authorPaperImpl.lambdaQuery().eq(AuthorPaper::getId,authorShip.getAuthor().id).exists()){
                        AuthorPaper one1 = authorPaperImpl.lambdaQuery().eq(AuthorPaper::getId, authorShip.getAuthor().id).one();
                        if (!one1.getPapers().contains(one.getId())){
                            one1.getPapers().add(one.getId());
                        }
                        authorPaperImpl.updateById(one1);
                    }
                }
            }
        });
        String s = JSONUtil.toJsonStr(paperInfo);
        stringRedisTemplate.opsForValue().set(RedisKey.PAPER_KEY+paperInfo.getId(),s,1, TimeUnit.DAYS);
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

    @PostMapping("/create/appeal")
    @LoginCheck
    @Operation(summary = "申诉下架论文", security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "格式:\"content\":\"string\"\n\"" +
            "paper_id\":\"string\"")
    public Result<String> createAppeal(@RequestBody Map<String, String> map){
        String content = map.get("content");
        String paper_id = map.get("paper_id");

        if(content == null || paper_id == null){
            return Result.fail("content或paper_id参数未找到");
        }

        paperAppeal.save(new PaperAppeal(UserContext.getUser().getUid(), getTimeNow(), paper_id, content));

        return Result.ok("创建申诉成功");
    @GetMapping()
    @Operation(summary = "获取文献总数")
    public Result<Integer> getCount(){
        return Result.ok("成功返回",7541000);
    }

    @GetMapping("/sumlook")
    @Operation(summary = "获取文献总浏览数")
    public Result<Integer> getSumLook(){
        Integer sumLook = Integer.parseInt(stringRedisTemplate.opsForValue().get(RedisKey.SUM_LOOK_KEY));
        return Result.ok("成功返回",sumLook);
    }

    @PostMapping("/good")
    @Operation(summary = "点赞")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "格式:\"id\":")
    public Result<Integer> addGood(@RequestBody Map<String,String> mp){
        Integer ans = zsetRedis.incrementScore(RedisKey.GOOD_CNT_KEY,mp.get("id"),1.0);
        zsetRedis.incrementScore(RedisKey.GOOD_CNT_KEY+RedisKey.getEveryDayKey(),mp.get("id"),1.0);
        zsetRedis.setTTL(RedisKey.GOOD_CNT_KEY+RedisKey.getEveryDayKey(),2l,TimeUnit.DAYS);
        return Result.ok("点赞成功",ans);
    }

    @PostMapping("/nogood")
    @Operation(summary = "取消点赞")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "格式:\"id\":")
    public Result<Integer> addNoGood(@RequestBody Map<String,String> mp){
        Integer ans = zsetRedis.incrementScore(RedisKey.GOOD_CNT_KEY,mp.get("id"),-1.0);
        zsetRedis.incrementScore(RedisKey.GOOD_CNT_KEY+RedisKey.getEveryDayKey(),mp.get("id"),-1.0);
        zsetRedis.setTTL(RedisKey.GOOD_CNT_KEY+RedisKey.getEveryDayKey(),2l,TimeUnit.DAYS);
        return Result.ok("取消点赞",ans);
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门文献")
    public Result<List<HotPaper>> getHot(){
        List<HotPaper> ans = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().reverseRangeWithScores(RedisKey.BROWSE_CNT_KEY + RedisKey.getEveryDayKey(), 0, 9);
        for (var a:typedTuples){
            Paper one = paperImpl.lambdaQuery().eq(Paper::getId, a.getValue()).one();
            ans.add(new HotPaper(a.getValue(),one.getTitle()));
        }
        return Result.ok("成功获取",ans);
    }
}
