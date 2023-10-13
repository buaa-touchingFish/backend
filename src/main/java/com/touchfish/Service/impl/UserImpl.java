package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.UserMapper;
import com.touchfish.Po.User;
import com.touchfish.Service.IUser;
import org.springframework.stereotype.Service;

@Service
public class UserImpl extends ServiceImpl<UserMapper, User> implements IUser {

}
