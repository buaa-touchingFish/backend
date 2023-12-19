package com.touchfish.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchfish.Po.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
