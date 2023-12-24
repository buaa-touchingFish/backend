package com.touchfish.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.CollectInfo;
import com.touchfish.Po.Collect;
import com.touchfish.Po.Label;
import com.touchfish.Po.Paper;
import com.touchfish.ReturnClass.RetCollectPaperInfo;

import com.touchfish.Service.impl.CollectImpl;
import com.touchfish.Service.impl.LabelImpl;
import com.touchfish.Service.impl.PaperImpl;
import com.touchfish.Tool.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/collect")
@Tag(name = "收藏相关接口")
public class CollectController {
    @Autowired
    private CollectImpl collectService;
    @Autowired
    private LabelImpl labelService;
    @Autowired
    private PaperImpl paperService;
    @Autowired
    private ZsetRedis zsetRedis;

    @PostMapping
    @LoginCheck
    @Operation(summary = "收藏文章", security = {@SecurityRequirement(name = "bearer-key")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"paper_id\":\"文章id\"")
    public Result<String> saveCollect(@RequestBody Map<String, String> map) {
        Integer user_id = UserContext.getUser().getUid();
        String paper_id = map.get("paper_id");
        if (collectService.saveCollect(user_id, paper_id)) {
            zsetRedis.incrementScore(RedisKey.COLLECT_CNT_KEY,paper_id,1.0);
            zsetRedis.incrementScore(RedisKey.COLLECT_CNT_KEY+RedisKey.getEveryDayKey(),paper_id,1.0);
            zsetRedis.setTTL(RedisKey.COLLECT_CNT_KEY+RedisKey.getEveryDayKey(),2l, TimeUnit.DAYS);
            return Result.ok("收藏成功");
        } else
            return Result.fail("已收藏");
    }

    @PostMapping("/delete")
    @LoginCheck
    @Operation(summary = "取消收藏", security = {@SecurityRequirement(name = "bearer-key")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"paper_id\":\"文章id\"")
    public Result<String> deleteCollect(@RequestBody Map<String, String> map) {
        Integer user_id = UserContext.getUser().getUid();
        String paper_id = map.get("paper_id");
        if (collectService.deleteCollect(user_id, paper_id)) {
            zsetRedis.incrementScore(RedisKey.COLLECT_CNT_KEY,paper_id,-1.0);
            zsetRedis.incrementScore(RedisKey.COLLECT_CNT_KEY+RedisKey.getEveryDayKey(),paper_id,-1.0);
            zsetRedis.setTTL(RedisKey.COLLECT_CNT_KEY+RedisKey.getEveryDayKey(),2l, TimeUnit.DAYS);
            return Result.ok("取消收藏成功");
        } else
            return Result.fail("未收藏");
    }

    @GetMapping
    @LoginCheck
    @Operation(summary = "获取收藏列表", security = {@SecurityRequirement(name = "bearer-key")})
    public Result<List<RetCollectPaperInfo>> getCollectByUser() {
        Integer user_id = UserContext.getUser().getUid();
        List<CollectInfo> collects = collectService.getCollects(user_id);
        List<RetCollectPaperInfo> retCollectPaperInfos = new ArrayList<>();
        collects = new ObjectMapper().convertValue(collects, new TypeReference<>() {
        });
        for (CollectInfo collectInfo : collects) {
            String paperId = collectInfo.getPaper_id();
            Paper paper = paperService.getById(paperId);
            List<String> authors = new ArrayList<>();
            List<AuthorShip> authorships = paper.getAuthorships();
            authorships = new ObjectMapper().convertValue(authorships, new TypeReference<>() {
            });
            for (AuthorShip authorShip : authorships)
                authors.add(authorShip.getAuthor().getDisplay_name());
            RetCollectPaperInfo retCollectPaperInfo = new RetCollectPaperInfo(collectInfo.getPaper_id(), paper.getTitle(), authors, paper.getPublisher().getDisplay_name(), paper.getCited_by_count(), collectInfo.getLabels());
            retCollectPaperInfos.add(retCollectPaperInfo);
        }
        return Result.ok("获取收藏列表成功", retCollectPaperInfos);
    }

    @PostMapping("/label")
    @LoginCheck
    @Operation(summary = "添加标签", security = {@SecurityRequirement(name = "bearer-key")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"paper_id\":\"文章id\", \"label_name\":\"标签名称\"")
    public Result<String> addLabel(@RequestBody Map<String, String> map) {
        Integer user_id = UserContext.getUser().getUid();
        String paper_id = map.get("paper_id");
        String label_name = map.get("label_name");
        if (paper_id != null)
        {
            if (collectService.addLabel(user_id, paper_id, label_name))
            {
                labelService.addLabel(user_id, label_name, true);
                return Result.ok("添加标签成功");
            } else
                return Result.fail("已添加");
        }
        else
        {
            if(labelService.addLabel(user_id, label_name, false))
                return Result.ok("添加标签成功");
            else
                return Result.fail("已添加");
        }
    }

    @PostMapping("/label/delete")
    @LoginCheck
    @Operation(summary = "删除标签", security = {@SecurityRequirement(name = "bearer-key")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"paper_id\":\"文章id\", \"label_name\":\"标签名称\"")
    public Result<String> deleteLabel(@RequestBody Map<String, String> map) {
        Integer user_id = UserContext.getUser().getUid();
        String paper_id = map.get("paper_id");
        String label_name = map.get("label_name");
        if(paper_id !=null) {
            collectService.deleteLabel(user_id, paper_id, label_name);
            labelService.deleteLabel(user_id, label_name, true);
            return Result.ok("删除标签成功");
        }
        else {
            Collect collect = collectService.getById(user_id);
            List<CollectInfo> collectInfos = collect.getCollectInfos();
            collectInfos = new ObjectMapper().convertValue(collectInfos, new TypeReference<>() {
            });
            for (CollectInfo collectInfo:collectInfos)
                collectInfo.getLabels().remove(label_name);
            collect.setCollectInfos(collectInfos);
            collectService.updateById(collect);
            labelService.deleteLabel(user_id, label_name, false);
            return Result.ok("删除标签成功");
        }
    }

    @GetMapping("/label")
    @LoginCheck
    @Operation(summary = "获取标签列表", security = {@SecurityRequirement(name = "bearer-key")})
    public Result<Label> getLabels() {
        Integer user_id = UserContext.getUser().getUid();
        Label label = labelService.getById(user_id);
        return Result.ok("获取标签列表成功", label);
    }
}
