package com.touchfish.Controller;

import com.touchfish.Po.Comment;
import com.touchfish.ReturnClass.RetComment;
import com.touchfish.Service.impl.CommentImpl;
import com.touchfish.Service.impl.UserImpl;
import com.touchfish.Tool.LoginCheck;
import com.touchfish.Tool.Result;
import com.touchfish.Tool.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
@Tag(name = "评论相关接口")
public class CommentController {
    @Autowired
    private CommentImpl commentService;
    @Autowired
    private UserImpl userService;

    @PostMapping
    @LoginCheck
    @Operation(summary = "发表/回复评论", security = {@SecurityRequirement(name = "bearer-key")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"content\":\"内容\", \"paper_id\":\"文章id\", \"sender_id\":\"评论用户id\", \"receiver_id\":\"被回复评论id(若为发表删除这一项)\"")
    public Result<String> save(@RequestBody Map<String, String> map) {
        String content = map.get("content");
        String paper_id = map.get("paper_id");
        Integer sender_id = UserContext.getUser().getUid();
        LocalDateTime send_time = LocalDateTime.now();
        if (map.get("receiver_id") != null) {
            Integer receiver_id = Integer.parseInt(map.get("receiver_id"));
            commentService.save(new Comment(content, paper_id, sender_id, send_time, receiver_id));
        } else {
            commentService.save(new Comment(content, paper_id, sender_id, send_time));
        }
        return Result.ok("评论成功");
    }

    @GetMapping
    @Operation(summary = "获取评论列表")
    public Result<List<RetComment>> getCommentByPaper(String paper_id) {
        List<RetComment> retComments = new ArrayList<>();
        List<Comment> list = commentService.lambdaQuery().eq(Comment::getPaper_id, paper_id).list();
        for(Comment comment:list)
        {
            String avatar = "s5usfv19s.hb-bkt.clouddn.com/OIP-C.jpg";
            if(userService.getById(comment.getSender_id()).getAvatar()!=null)
                avatar = userService.getById(comment.getSender_id()).getAvatar();
            RetComment retComment = new RetComment(comment.getId(), comment.getContent(), comment.getPaper_id(), comment.getSender_id(), userService.getById(comment.getSender_id()).getUsername(), avatar, comment.getSend_time(), comment.getReceiver_id());
            retComments.add(retComment);
        }
        return Result.ok("获取评论列表成功", retComments);
    }
}
