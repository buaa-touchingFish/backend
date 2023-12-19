package com.touchfish.Dto;

import lombok.Data;

@Data
public class ClaimInfo {
    String author_id;//要认领的学者的id
    String email;//认领时要发送确认邮件的邮箱
    String captcha;//确认时的验证码
}
