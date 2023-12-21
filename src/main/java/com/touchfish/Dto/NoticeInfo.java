package com.touchfish.Dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class NoticeInfo {
    /// 通知标题
    private String title;
    /// 通知内容
    private String content;
    /// 接收用户id
    private int user_id;


}
