package com.touchfish.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchfish.MiddleClass.AuthorHome;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.CoAuthor;
import com.touchfish.Po.Author;
import com.touchfish.Po.AuthorPaper;
import com.touchfish.Po.Institution;
import com.touchfish.Po.Paper;
import com.touchfish.Service.impl.*;
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
    @Autowired
    private InstitutionImpl institutionService;
    @Autowired
    private InstitutionAuthorImpl institutionAuthorService;

    @GetMapping
    @Operation(summary = "获取学者门户")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"author_id\":\"学者id\"")
    public Result<AuthorHome> getAuthorHome(String author_id) {
        List<Paper> papers = new ArrayList<>();
        HashMap<CoAuthor, Integer> CoAuthors = new HashMap<>();
        Author author = authorService.getById(author_id);
        if(author == null)
            author = getAuthorFromOpenAlex(author_id);
        AuthorPaper authorPaper = authorPaperService.getById(author_id);
        for (String paper_id : authorPaper.getPapers()) {
            Paper paper = paperService.getById(paper_id);
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
        for(CoAuthor coAuthor:returnCoAuthors)
        {
            Author author1 = authorService.getById(coAuthor.getId());
            if(author1 == null)
                author1 = getAuthorFromOpenAlex(coAuthor.getId());
            coAuthor.setDisplay_name(author1.getDisplay_name());
            if(author1.getLast_known_institution() != null)
                coAuthor.setLast_known_institution_display_name(author1.getLast_known_institution().getDisplay_name());
        }
        AuthorHome authorHome = new AuthorHome(author, papers, returnCoAuthors);
        return Result.ok("查看学者门户成功", authorHome);
    }

    public Author getAuthorFromOpenAlex(String author_id) {
        Author author = authorService.updateAuthorFromOpenAlex(author_id);
        if (author.getLast_known_institution() != null) {
            Institution institution = institutionService.updateInstFromOpenAlex(author.getLast_known_institution().getId());
            institutionAuthorService.saveInstAuthor(institution.getId(), author.getId());
        }
        return author;
    }

}
