package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("user")
@NoArgsConstructor
public class User {
    @TableId(type = IdType.AUTO)
    private Integer uid;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String author_id;//认领的门户

    private String avatar; //头像url

    public User(String username, String password, String email, String author_id) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.author_id = author_id;
    }
}
