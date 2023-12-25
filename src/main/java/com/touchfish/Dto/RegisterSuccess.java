package com.touchfish.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterSuccess {
    private String jwt;
    private Integer uid;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private String  author_id;
}
