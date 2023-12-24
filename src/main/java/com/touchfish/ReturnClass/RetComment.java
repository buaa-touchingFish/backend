package com.touchfish.ReturnClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetComment {
    private Integer id;
    private String content;
    private String paper_id;
    private Integer sender_id;
    private String sender_name;
    private LocalDateTime send_time;
    private Integer receiver_id;     //被回复的评论的id，可以为null
    private String avatar = "s5usfv19s.hb-bkt.clouddn.com/OIP-C.jpg"; //头像url地址
}
