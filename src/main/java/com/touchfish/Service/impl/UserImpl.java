package com.touchfish.Service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.UserMapper;
import com.touchfish.Dto.PwdChangeInfo;
import com.touchfish.Po.User;
import com.touchfish.Service.IUser;
import com.touchfish.Tool.Captcha;
import com.touchfish.Tool.RedisKey;
import com.touchfish.Tool.Result;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserImpl extends ServiceImpl<UserMapper, User> implements IUser {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private Captcha captcha;
    public Integer sendCaptcha(String email){ //0为成功,1为邮箱格式不合法
        email = email.replace("\"","");
        boolean isEmail = Validator.isEmail(email);
        if (!isEmail) return 1;
        String code  = captcha.sendCaptcha(email);
        stringRedisTemplate.opsForValue().set(RedisKey.CATPTCHA_KEY+email,code,3, TimeUnit.MINUTES);
        return 0;
    }

}
