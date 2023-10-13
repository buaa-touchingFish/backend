package com.touchfish.Dto;

import lombok.Data;

@Data
public class RegisterInfo {
    String username;
    String password;
    String email;
    String captcha;

}
