package com.touchfish.Controller;

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
import com.touchfish.Service.impl.PaperImpl;
import com.touchfish.Tool.OpenAlex;
import com.touchfish.Tool.RedisKey;
import com.touchfish.Tool.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
    private PaperImpl paperImpl;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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
