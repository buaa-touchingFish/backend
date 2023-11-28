package com.touchfish.Controller;

import com.touchfish.Po.Comment;
import com.touchfish.Service.impl.CommentImpl;
import com.touchfish.Tool.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentImpl commentService;

    @PostMapping
    public Result save(@RequestBody Map<String, String> map) {
        String content = map.get("content");
        String paper_id = map.get("paper_id");
        Integer sender_id = Integer.parseInt(map.get("sender_id"));
        LocalDateTime send_time = LocalDateTime.now();
        if(map.get("receiver_id")!=null) {
            Integer receiver_id = Integer.parseInt(map.get("receiver_id"));
            commentService.save(new Comment(content, paper_id, sender_id, send_time, receiver_id));
        }
        else {
            commentService.save(new Comment(content, paper_id, sender_id, send_time));
        }
        return Result.ok("评论成功");
    }

    @GetMapping
    public Result getCommentByPaper(@RequestBody Map<String, String> map) {
        String paper_id = map.get("paper_id");
        List<Comment> list = commentService.lambdaQuery().eq(Comment::getPaper_id, paper_id).list();
        return Result.ok("获取评论列表成功", list);
    }
}
