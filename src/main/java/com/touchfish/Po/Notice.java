package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("notice")
@AllArgsConstructor
@NoArgsConstructor
public class Notice {
    @TableId(type = IdType.AUTO)
    private int id;
    /// 消息标题
    private String title;
    /// 消息内容
    private String content;
    /// 创建时间
    private String create_time;
    /// 消息通知的用户id
    private int user_id;
    /// 消息是否已读，true代表已读
    private boolean read_status;

    public Notice(String title, String content, String create_time, int user_id, boolean read_status) {
        this.title = title;
        this.content = content;
        this.create_time = create_time;
        this.user_id = user_id;
        this.read_status = read_status;
    }
}
