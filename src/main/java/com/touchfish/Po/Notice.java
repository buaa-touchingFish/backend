package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("notice")
@NoArgsConstructor
public class Notice {
    @TableId(type = IdType.AUTO)
    private int nid;
    private String content;
    private String create_time;
    private int sender_id;
    private String paper_id;

    public Notice(int nid, String content, String create_time, int sender_id, String paper_id) {
        this.nid = nid;
        this.content = content;
        this.create_time = create_time;
        this.sender_id = sender_id;
        this.paper_id = paper_id;
    }
}
