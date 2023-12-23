package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName(value = "hot_point")
@NoArgsConstructor
@AllArgsConstructor
public class HotPoint {
    @TableId(type = IdType.INPUT)
    private String paper_id;
    private Integer collect_cnt = 0;
    private Integer good_cnt = 0;
    private Integer browse_cnt = 0;
}
