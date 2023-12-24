package com.touchfish.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {
    private String username;
    private String email;
    private String phone;
}
