package com.touchfish.Controller;

import com.touchfish.Po.Author;
import com.touchfish.Po.SubscribeCnt;
import com.touchfish.ReturnClass.RetSubscribe;
import com.touchfish.Service.impl.AuthorImpl;
import com.touchfish.Service.impl.SubscribeCntImpl;
import com.touchfish.Service.impl.SubscribeImpl;
import com.touchfish.Service.impl.UserImpl;
import com.touchfish.Tool.LoginCheck;
import com.touchfish.Tool.Result;
import com.touchfish.Tool.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subscribe")
@Tag(name = "订阅相关接口")
public class SubscribeController {
    @Autowired
    private SubscribeImpl subscribeService;
    @Autowired
    private SubscribeCntImpl subscribeCntService;
    @Autowired
    private AuthorImpl authorService;
    @Autowired
    private UserImpl userService;

    @PostMapping
    @LoginCheck
    @Operation(summary = "订阅学者", security = {@SecurityRequirement(name = "bearer-key")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"author_id\":\"学者id\"")
    public Result<String> save(@RequestBody Map<String, String> map) {
        Integer user_id = UserContext.getUser().getUid();
        String author_id = map.get("author_id");
        if (subscribeService.saveSubscribe(user_id, author_id)) {
            SubscribeCnt subscribeCnt = subscribeCntService.getById(author_id);
            if (subscribeCnt == null)
                subscribeCntService.save(new SubscribeCnt(author_id, 1));
            else {
                subscribeCnt.setSubscribe_cnt(subscribeCnt.getSubscribe_cnt() + 1);
                subscribeCntService.updateById(subscribeCnt);
            }
            return Result.ok("订阅成功");
        } else
            return Result.fail("已订阅");
    }

    @PostMapping("/delete")
    @LoginCheck
    @Operation(summary = "取消订阅", security = {@SecurityRequirement(name = "bearer-key")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"author_id\":\"学者id\"")
    public Result<String> delete(@RequestBody Map<String, String> map) {
        Integer user_id = UserContext.getUser().getUid();
        String author_id = map.get("author_id");
        if (subscribeService.deleteSubscribe(user_id, author_id)) {
            SubscribeCnt subscribeCnt = subscribeCntService.getById(author_id);
            subscribeCnt.setSubscribe_cnt(subscribeCnt.getSubscribe_cnt() - 1);
            subscribeCntService.updateById(subscribeCnt);
            return Result.ok("取消订阅成功");
        } else
            return Result.fail("未订阅");
    }

    @GetMapping
    @LoginCheck
    @Operation(summary = "获取订阅列表", security = {@SecurityRequirement(name = "bearer-key")})
    public Result<List<RetSubscribe>> getAuthorByUser() {
        Integer user_id = UserContext.getUser().getUid();
        List<String> subscribes = subscribeService.getSubscribes(user_id);
        List<RetSubscribe> list = new ArrayList<>();
        for (String author_id : subscribes) {
            Author author = authorService.getById(author_id);
            String avatar = "s5usfv19s.hb-bkt.clouddn.com/OIP-C.jpg";
            Integer claimUid = author.getClaim_uid();
            if (claimUid != null)
                avatar = userService.getById(claimUid).getAvatar();
            RetSubscribe retSubscribe = new RetSubscribe(author.getId(), author.getDisplay_name(), author.getLast_known_institution(), avatar);
            list.add(retSubscribe);
        }
        return Result.ok("获取订阅列表成功", list);
    }
}
