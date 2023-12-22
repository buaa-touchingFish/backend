package com.touchfish.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.CollectInfo;
import com.touchfish.ReturnClass.ReturnCollectPaperInfo;
import com.touchfish.Po.Label;
import com.touchfish.Po.Paper;
import com.touchfish.Service.impl.CollectImpl;
import com.touchfish.Service.impl.LabelImpl;
import com.touchfish.Service.impl.PaperImpl;
import com.touchfish.Tool.LoginCheck;
import com.touchfish.Tool.RedisKey;
import com.touchfish.Tool.Result;
import com.touchfish.Tool.ZsetRedis;
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
//    @Autowired
//    private CollectCntImpl collectCntService;
    @Autowired
    private PaperImpl paperService;

    @Autowired
    private ZsetRedis zsetRedis;

    @PostMapping
    @LoginCheck
    @Operation(summary = "收藏文章", security = {@SecurityRequirement(name = "bearer-key")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"user_id\":\"用户id\", \"paper_id\":\"文章id\"")
    public Result<String> saveCollect(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String paper_id = map.get("paper_id");
        if (collectService.saveCollect(user_id, paper_id)) {
//            CollectCnt collectCnt = collectCntService.getById(paper_id);
//            if (collectCnt == null)
//                collectCntService.save(new CollectCnt(paper_id, 1));
//            else {
//                collectCnt.setCollect_cnt(collectCnt.getCollect_cnt() + 1);
//                collectCntService.updateById(collectCnt);
//            }
            zsetRedis.incrementScore(RedisKey.COLLECT_CNT_KEY,paper_id,1.0);
            zsetRedis.incrementScore(RedisKey.COLLECT_CNT_KEY+RedisKey.getEveryDayKey(),paper_id,1.0);
            zsetRedis.setTTL(RedisKey.COLLECT_CNT_KEY+RedisKey.getEveryDayKey(),2l, TimeUnit.DAYS);
            return Result.ok("收藏成功");
        } else
            return Result.fail("已收藏");
    }

    @DeleteMapping
    @LoginCheck
    @Operation(summary = "取消收藏", security = {@SecurityRequirement(name = "bearer-key")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"user_id\":\"用户id\", \"paper_id\":\"文章id\"")
    public Result<String> deleteCollect(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String paper_id = map.get("paper_id");
        if (collectService.deleteCollect(user_id, paper_id)) {
//            CollectCnt collectCnt = collectCntService.getById(paper_id);
//            collectCnt.setCollect_cnt(collectCnt.getCollect_cnt() - 1);
//            collectCntService.updateById(collectCnt);
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
    public Result<List<ReturnCollectPaperInfo>> getCollectByUser(Integer user_id) {
        List<CollectInfo> collects = collectService.getCollects(user_id);
        List<ReturnCollectPaperInfo> returnCollectPaperInfos = new ArrayList<>();
        collects = new ObjectMapper().convertValue(collects, new TypeReference<>() {
        });
        for(CollectInfo collectInfo:collects)
        {
            String paperId = collectInfo.getPaper_id();
            Paper paper = paperService.getById(paperId);
            List<String> authors = new ArrayList<>();
            List<AuthorShip> authorships = paper.getAuthorships();
            authorships = new ObjectMapper().convertValue(authorships, new TypeReference<>() {
            });
            for(AuthorShip authorShip: authorships)
                authors.add(authorShip.getAuthor().getDisplay_name());
            ReturnCollectPaperInfo returnCollectPaperInfo = new ReturnCollectPaperInfo(collectInfo.getPaper_id(), paper.getTitle(), authors, paper.getPublisher().getDisplay_name(), paper.getCited_by_count(), collectInfo.getLabels());
            returnCollectPaperInfos.add(returnCollectPaperInfo);
        }
        return Result.ok("获取收藏列表成功", returnCollectPaperInfos);
    }

    @PostMapping("/label")
    @LoginCheck
    @Operation(summary = "添加标签", security = {@SecurityRequirement(name = "bearer-key")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"user_id\":\"用户id\", \"paper_id\":\"文章id\", \"label_name\":\"标签名称\"")
    public Result<String> addLabel(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String paper_id = map.get("paper_id");
        String label_name = map.get("label_name");
        if (collectService.addLabel(user_id, paper_id, label_name)) {
            labelService.addLabel(user_id, label_name);
            return Result.ok("添加标签成功");
        } else
            return Result.fail("已添加");
    }

    @DeleteMapping("/label")
    @LoginCheck
    @Operation(summary = "删除标签", security = {@SecurityRequirement(name = "bearer-key")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"user_id\":\"用户id\", \"paper_id\":\"文章id\", \"label_name\":\"标签名称\"")
    public Result<String> deleteLabel(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String paper_id = map.get("paper_id");
        String label_name = map.get("label_name");
        if (collectService.deleteLabel(user_id, paper_id, label_name)) {
            labelService.deleteLabel(user_id, label_name);
            return Result.ok("删除标签成功");
        } else
            return Result.fail("未添加");
    }

    @GetMapping("/label")
    @LoginCheck
    @Operation(summary = "获取标签列表", security = {@SecurityRequirement(name = "bearer-key")})
    public Result<Label> getLabels(Integer user_id) {
        Label label = labelService.getById(user_id);
        return Result.ok("获取标签列表成功", label);
    }
}
