package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.AuthorPaperMapper;
import com.touchfish.Po.AuthorPaper;
import com.touchfish.Service.IAuthorPaper;
import org.springframework.stereotype.Service;

@Service
public class AuthorPaperImpl extends ServiceImpl<AuthorPaperMapper, AuthorPaper> implements IAuthorPaper {
}
