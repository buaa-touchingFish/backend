package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@TableName(value = "subscribe", autoResultMap = true)
@NoArgsConstructor
@AllArgsConstructor
public class Subscribe {
    @TableId(type = IdType.INPUT)
    private Integer user_id;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ArrayList<String> author_id;
}
