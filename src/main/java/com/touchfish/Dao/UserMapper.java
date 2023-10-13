package com.touchfish.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchfish.Po.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
