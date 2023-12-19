package com.touchfish.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchfish.MiddleClass.AuthorHome;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.CoAuthor;
//import com.touchfish.MiddleClass.Institution;
import com.touchfish.Po.Author;
import com.touchfish.Po.AuthorPaper;
import com.touchfish.Po.Paper;
import com.touchfish.Service.impl.AuthorImpl;
import com.touchfish.Service.impl.AuthorPaperImpl;
import com.touchfish.Service.impl.PaperImpl;
import com.touchfish.Tool.OpenAlex;
import com.touchfish.Tool.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/author")
@Tag(name = "学者相关接口")
public class AuthorController {
    @Autowired
    private AuthorImpl authorService;
    @Autowired
    private AuthorPaperImpl authorPaperService;
    @Autowired
    private PaperImpl paperService;

    @GetMapping
    @Operation(summary = "获取学者门户")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"author_id\":\"学者id\"")
    public Result<AuthorHome> getAuthor(@RequestBody Map<String, String> map) {
        List<Paper> papers = new ArrayList<>();
        HashMap<CoAuthor, Integer> CoAuthors = new HashMap<>();
        String author_id = map.get("author_id");
        //减少发请求次数，一次发
        //写成update Author方法，维护所有表
        //多线程 一个返回信息，一个存数据库
        Author author = authorService.getById(author_id);
        AuthorPaper authorPaper = authorPaperService.getById(author_id);
        for(String paper_id:authorPaper.getPapers()) {
            Paper paper = paperService.getById(paper_id);
            papers.add(paper);
            List<AuthorShip> authorships = paper.getAuthorships();
            List<AuthorShip> authorShipList = new ObjectMapper().convertValue(authorships, new TypeReference<>() {
            });
            for(AuthorShip authorShip: authorShipList)
            {
                String ship_author_id = authorShip.getAuthor().getId();
                if(ship_author_id.equals(author_id))
                    continue;
                Author ship_author = authorService.getById(ship_author_id);
                if(ship_author == null) {
                    ship_author = getAuthorFromOpenAlex(ship_author_id);
                    if(ship_author == null) return Result.fail("OpenAlex请求失败");
                }
                CoAuthor coAuthor;
                boolean flag = false;
                for(CoAuthor coAuthor1: CoAuthors.keySet()) {
                    if(coAuthor1.getId().equals(ship_author_id))
                    {
                        flag = true;
                        Integer cnt = CoAuthors.get(coAuthor1);
                        CoAuthors.put(coAuthor1, cnt + 1);
                        break;
                    }
                }
                if(!flag) {
                    coAuthor = new CoAuthor(ship_author.getId(), ship_author.getDisplay_name(), ship_author.getLast_known_institution().getDisplay_name());
                    CoAuthors.put(coAuthor, 1);
                }
            }
        }
        List<Map.Entry<CoAuthor, Integer>> entryList = new ArrayList<>(CoAuthors.entrySet());
        entryList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        List<CoAuthor> sortedCoAuthors = new ArrayList<>();
        for (Map.Entry<CoAuthor, Integer> entry : entryList) {
            sortedCoAuthors.add(entry.getKey());
        }

        AuthorHome authorHome = new AuthorHome(author, papers, sortedCoAuthors.subList(0, Math.min(10, sortedCoAuthors.size())));
        return Result.ok("查看学者门户成功", authorHome);
    }

    public Author getAuthorFromOpenAlex(String author_id) {
        //todo:维护所有表
        Author author = authorService.updateAuthorFromOpenAlex(author_id);
//        Institution institution =
//        if(!institutionService.updateAuthorFromOpenAlex(author_id))  return Result.fail("OpenAlex getInstitution failed");
//        if(!authorService.updateAuthorFromOpenAlex(author_id))  return Result.fail("OpenAlex getAuthor failed");
        return author;
    }
}
