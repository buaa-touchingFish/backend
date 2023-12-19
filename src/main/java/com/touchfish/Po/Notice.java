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
    private int nid;
    private String content;
    private String create_time;
    private int sender_id;
    private String paper_id;
}
