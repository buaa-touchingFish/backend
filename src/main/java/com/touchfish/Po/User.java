package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("User")
@NoArgsConstructor
public class User {
    @TableId(type = IdType.AUTO)
    private Integer uid;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer identity;

    public User(String username, String password, String email, Integer identity) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.identity = identity;
    }
}
