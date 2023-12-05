package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("admin_message")
public class AdminMessage {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer type;   //0申请，1申诉
    private String content;
    private LocalDateTime send_time;
    private Integer admin_id;

    public AdminMessage(Integer type, String content, LocalDateTime send_time, Integer admin_id) {
        this.type = type;
        this.content = content;
        this.send_time = send_time;
        this.admin_id = admin_id;
    }
}
