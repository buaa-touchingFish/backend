package com.touchfish.Controller;

import com.touchfish.Po.Author;
import com.touchfish.Po.Paper;
import com.touchfish.Service.impl.AuthorImpl;
import com.touchfish.Service.impl.PaperImpl;
import com.touchfish.Tool.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/author")
public class AuthorController {
    @Autowired
    private AuthorImpl authorService;
    @Autowired
    private PaperImpl paperService;

//    @PostMapping
//    public Result apply(@RequestParam Integer user_id, @RequestParam String author_id) {
//        User user = userService.getById(user_id);
//        Author author = authorService.getById(author_id);
//        if(user == null)
//            return Result.fail("用户不存在");
//        else if(author == null)
//            return Result.fail("学者不存在");
//        else {
////            给某个管理员发申请消息,新建个管理员表每次随机分配？
////            AdminMessage adminMessage = new AdminMessage(0，);
////            adminMessageService.save(adminMessage);
//            return Result.ok("门户认领申请成功");
//        }
//    }

    @GetMapping("/a")
    public Result getAuthor(@RequestBody Map<String, String> map) {
        String author_id = map.get("author_id");
        Author author = authorService.getById(author_id);
        if(author == null)
            return Result.fail("学者不存在");
        else
            return Result.ok("查看学者门户成功", author);
    }

    @GetMapping("/p")
    public Result getPaper(@RequestBody Map<String, String> map) {
        String paper_id = map.get("paper_id");
        Paper paper = paperService.getById(paper_id);
        if(paper == null)
            return Result.fail("文献不存在");
        else
            return Result.ok("查看文献成功", paper);
    }
}
