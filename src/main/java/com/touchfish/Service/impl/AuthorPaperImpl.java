package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchfish.Dao.AuthorPaperMapper;
import com.touchfish.Po.Author;
import com.touchfish.Po.AuthorPaper;
import com.touchfish.Po.InstitutionAuthor;
import com.touchfish.Service.IAuthorPaper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorPaperImpl extends ServiceImpl<AuthorPaperMapper, AuthorPaper> implements IAuthorPaper {
    public void saveAuthorPaper(String author_id, String paper_id) {
        AuthorPaper authorPaper = getById(author_id);
        if(authorPaper == null) {
            authorPaper = new AuthorPaper(author_id, new ArrayList<>());
            authorPaper.getPapers().add(paper_id);
            save(authorPaper);
        } else {
            List<String> paperIds = authorPaper.getPapers();
            paperIds = new ObjectMapper().convertValue(paperIds, new TypeReference<>() {
            });
            if(paperIds.contains(paper_id)) return;
            authorPaper.getPapers().add(paper_id);
            updateById(authorPaper);
        }
    }
}
