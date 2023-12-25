package com.touchfish.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private Integer uid;
    private String author_id;
}
