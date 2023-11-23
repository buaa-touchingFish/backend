package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.AuthorMapper;
import com.touchfish.Po.Author;
import com.touchfish.Service.IAuthor;
import org.springframework.stereotype.Service;

@Service
public class AuthorImpl extends ServiceImpl<AuthorMapper, Author> implements IAuthor {
}
