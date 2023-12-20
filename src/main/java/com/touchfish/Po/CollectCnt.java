package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName(value = "collect_cnt")
@NoArgsConstructor
@AllArgsConstructor
public class CollectCnt {
    @TableId(type = IdType.INPUT)
    private String paper_id;
    private Integer collect_cnt;
}
