package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.AuthorMapper;
import com.touchfish.Po.Author;
import com.touchfish.Po.Paper;
import com.touchfish.Service.IAuthor;
import com.touchfish.Tool.OpenAlex;
import org.springframework.stereotype.Service;

@Service
public class AuthorImpl extends ServiceImpl<AuthorMapper, Author> implements IAuthor {
    public Author updateAuthorFromOpenAlex(String id) {
        Author alexAuthor = (Author) OpenAlex.sendResponse("author", id);
        if(alexAuthor == null) return null;
        Author author = getById(id);
        if(author == null)
            save(alexAuthor);
        else
            updateById(alexAuthor);
        return alexAuthor;
    }

}
