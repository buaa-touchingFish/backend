package com.touchfish.Dto;

import lombok.Data;

@Data
public class PwdChangeInfo {
    private String email;
    private String new_pwd;
    private String captcha;
}
