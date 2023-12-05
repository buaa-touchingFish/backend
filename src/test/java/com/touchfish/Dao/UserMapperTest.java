package com.touchfish.Dao;

import com.touchfish.Po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testGetById(){
        System.out.println(userMapper.selectById(1));
    }
}
