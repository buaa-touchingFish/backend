package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("label")
@NoArgsConstructor
@AllArgsConstructor
public class Label {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer count;

    public Label(String name, Integer count) {
        this.name = name;
        this.count = count;
    }
}
