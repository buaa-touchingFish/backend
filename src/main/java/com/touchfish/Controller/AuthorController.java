package com.touchfish.Controller;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchfish.MiddleClass.AuthorHome;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.CoAuthor;
import com.touchfish.Po.*;
import com.touchfish.Service.impl.*;
import com.touchfish.Tool.LoginCheck;
import com.touchfish.Tool.RedisKey;
import com.touchfish.Tool.Result;
import com.touchfish.Tool.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private InstitutionImpl institutionService;
    @Autowired
    private InstitutionAuthorImpl institutionAuthorService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SubscribeImpl subscribeService;

    @GetMapping
    @Operation(summary = "获取学者门户")
    public Result<AuthorHome> getAuthorHome(String author_id, String paper_id) {
        String id = stringRedisTemplate.opsForValue().get(RedisKey.AUTHOR_KEY + author_id);
        if (id != null) {
            AuthorHome authorHome = JSONUtil.toBean(id, AuthorHome.class);
            return Result.ok("查看学者门户成功", authorHome);
        }
        List<Paper> papers = new ArrayList<>();
        HashMap<CoAuthor, Integer> CoAuthors = new HashMap<>();
        Author author = authorService.getById(author_id);
        if (author == null)
            author = getAuthorFromOpenAlex(author_id, paper_id);
        AuthorPaper authorPaper = authorPaperService.getById(author_id);
        for (String author_paper_id : authorPaper.getPapers()) {
            Paper paper = paperService.getById(author_paper_id);
            papers.add(paper);
            List<AuthorShip> authorships = paper.getAuthorships();
            authorships = new ObjectMapper().convertValue(authorships, new TypeReference<>() {
            });
            for (AuthorShip authorShip : authorships) {
                String ship_author_id = authorShip.getAuthor().getId();
                if (ship_author_id.equals(author_id))
                    continue;
                Author ship_author = authorService.getById(ship_author_id);
                if (ship_author == null) {
                    ship_author = new Author();
                    ship_author.setId(ship_author_id);
                }
                CoAuthor coAuthor;
                boolean flag = false;
                for (CoAuthor coAuthor1 : CoAuthors.keySet()) {
                    if (coAuthor1.getId().equals(ship_author_id)) {
                        flag = true;
                        Integer cnt = CoAuthors.get(coAuthor1);
                        CoAuthors.put(coAuthor1, cnt + 1);
                        break;
                    }
                }
                if (!flag) {
                    coAuthor = new CoAuthor(ship_author.getId());
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
        List<CoAuthor> returnCoAuthors = sortedCoAuthors.subList(0, Math.min(5, sortedCoAuthors.size()));
        for (CoAuthor coAuthor : returnCoAuthors) {
            Author author1 = authorService.getById(coAuthor.getId());
            if (author1 == null)
                author1 = getAuthorFromOpenAlex(coAuthor.getId(), null);
            coAuthor.setDisplay_name(author1.getDisplay_name());
            if (author1.getLast_known_institution() != null)
                coAuthor.setLast_known_institution_display_name(author1.getLast_known_institution().getDisplay_name());
        }
        AuthorHome authorHome = new AuthorHome(author, papers, returnCoAuthors);
        String s = JSONUtil.toJsonStr(authorHome);
        stringRedisTemplate.opsForValue().set(RedisKey.AUTHOR_KEY + authorHome.getAuthor().getId(), s, 1, TimeUnit.DAYS);
        return Result.ok("查看学者门户成功", authorHome);
    }

    public Author getAuthorFromOpenAlex(String author_id, String paper_id) {
        if (paper_id != null)
            authorPaperService.saveAuthorPaper(author_id, paper_id);
        Author author = authorService.updateAuthorFromOpenAlex(author_id);
        if (author.getLast_known_institution() != null) {
            Institution institution = institutionService.updateInstFromOpenAlex(author.getLast_known_institution().getId());
            institutionAuthorService.saveInstAuthor(institution.getId(), author.getId());
        }
        return author;
    }

    @GetMapping("/count")
    @Operation(summary = "获取学者总数")
    public Result<Integer> getCount(){
        return Result.ok("成功返回",2325000);
    }

    @GetMapping("/subscribe")
    @LoginCheck
    @Operation(summary = "是否订阅", security = {@SecurityRequirement(name = "bearer-key")})
    public Result<Boolean> getSubscribeStatus(String author_id)
    {
        User user = UserContext.getUser();
        Subscribe subscribe = subscribeService.getById(user.getUid());
        ArrayList<String> authorIds = subscribe.getAuthor_id();
        authorIds = new ObjectMapper().convertValue(authorIds, new TypeReference<>() {
        });
        boolean flag = false;
        for(String authorId:authorIds)
        {
            if(authorId.equals(author_id)){
                flag = true;
                break;
            }
        }
        return Result.ok("查看订阅状态成功", flag);
    }
}
