package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.CommentMapper;
import com.touchfish.Po.Comment;
import com.touchfish.Service.IComment;
import org.springframework.stereotype.Service;

@Service
public class CommentImpl extends ServiceImpl<CommentMapper, Comment> implements IComment {
}
