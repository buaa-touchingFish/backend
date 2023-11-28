package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("comment")
//@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String content;
    private String paper_id;
    private Integer sender_id;
    private LocalDateTime send_time;
    private Integer receiver_id;     //被回复的评论的id，可以为null

    public Comment(String content, String paper_id, Integer send_id, LocalDateTime send_time) {
        this.content = content;
        this.paper_id = paper_id;
        this.sender_id = send_id;
        this.send_time = send_time;
    }

    public Comment(String content, String paper_id, Integer send_id, LocalDateTime send_time, Integer receiver_id) {
        this.content = content;
        this.paper_id = paper_id;
        this.sender_id = send_id;
        this.send_time = send_time;
        this.receiver_id = receiver_id;
    }

}
