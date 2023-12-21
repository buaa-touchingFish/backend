package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName(value = "subscribe_cnt")
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeCnt {
    @TableId(type = IdType.INPUT)
    private String author_id;
    private Integer subscribe_cnt;
}
