package com.touchfish.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchfish.Po.Author;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthorMapper extends BaseMapper<Author> {
}
