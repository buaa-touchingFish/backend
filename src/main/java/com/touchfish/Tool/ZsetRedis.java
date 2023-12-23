package com.touchfish.Tool;


import com.touchfish.Service.impl.HotPointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ZsetRedis {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public Integer incrementScore(String key, String member, double delta) {
        Double aDouble = stringRedisTemplate.opsForZSet().incrementScore(key, member, delta);
        return aDouble.intValue();
    }

    public Integer getScore(String key,String member){
        Double aDouble = stringRedisTemplate.opsForZSet().score(key,member);
        if (aDouble == null ) return 0;
        return aDouble.intValue();
    }

    public void delKey(String key,String member){
        stringRedisTemplate.opsForZSet().remove(key,member);
    }

    public void setTTL(String key, long timeout, TimeUnit timeUnit){
        if (stringRedisTemplate.getExpire(key) == -1){
            stringRedisTemplate.expire(key,timeout,timeUnit);
        }
    }
}
