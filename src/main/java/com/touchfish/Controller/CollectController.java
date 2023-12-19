package com.touchfish.Controller;

import com.touchfish.MiddleClass.CollectInfo;
import com.touchfish.Po.Label;
import com.touchfish.Service.impl.CollectImpl;
import com.touchfish.Service.impl.LabelImpl;
import com.touchfish.Tool.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/collect")
@Tag(name = "收藏相关接口")
public class CollectController {
    @Autowired
    private CollectImpl collectService;
    @Autowired
    private LabelImpl labelService;

    @PostMapping
    @Operation(summary = "收藏文章")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"user_id\":\"用户id\", \"paper_id\":\"文章id\"")
    public Result<String> saveCollect(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String paper_id = map.get("paper_id");
        if (collectService.saveCollect(user_id, paper_id))
            return Result.ok("收藏成功");
        else
            return Result.fail("已收藏");
    }

    @DeleteMapping
    @Operation(summary = "取消收藏")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"user_id\":\"用户id\", \"paper_id\":\"文章id\"")
    public Result<String> deleteCollect(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String paper_id = map.get("paper_id");
        if (collectService.deleteCollect(user_id, paper_id))
            return Result.ok("取消收藏成功");
        else
            return Result.fail("未收藏");
    }

    @GetMapping
    @Operation(summary = "获取收藏列表")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"user_id\":\"用户id\"")
    public Result<List<CollectInfo>> getCollectByUser(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        List<CollectInfo> collects = collectService.getCollects(user_id);
        return Result.ok("获取收藏列表成功", collects);
    }

    @PostMapping("/label")
    @Operation(summary = "添加标签")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"user_id\":\"用户id\", \"paper_id\":\"文章id\", \"label_name\":\"标签名称\"")
    public Result<String> addLabel(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String paper_id = map.get("paper_id");
        String label_name = map.get("label_name");
        if (collectService.addLabel(user_id, paper_id, label_name)) {
            Label label = labelService.lambdaQuery().eq(Label::getName, label_name).one();
            if (label != null) {
                label.setCount(label.getCount() + 1);
                labelService.updateById(label);
            } else {
                labelService.save(new Label(label_name, 1));
            }
            return Result.ok("添加标签成功");
        } else
            return Result.fail("已添加");
    }

    @DeleteMapping("/label")
    @Operation(summary = "删除标签")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"user_id\":\"用户id\", \"paper_id\":\"文章id\", \"label_name\":\"标签名称\"")
    public Result<String> deleteLabel(@RequestBody Map<String, String> map) {
        Integer user_id = Integer.parseInt(map.get("user_id"));
        String paper_id = map.get("paper_id");
        String label_name = map.get("label_name");
        if (collectService.deleteLabel(user_id, paper_id, label_name)) {
            Label label = labelService.lambdaQuery().eq(Label::getName, label_name).one();
            label.setCount(label.getCount() - 1);
            labelService.updateById(label);
            return Result.ok("删除标签成功");
        } else
            return Result.fail("未添加");
    }

    @GetMapping("/label")
    @Operation(summary = "获取标签列表")
    public Result<List<Label>> getLabels() {
        List<Label> labels = labelService.list();
        return Result.ok("获取标签列表成功", labels);
    }
}
