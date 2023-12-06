package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.touchfish.MiddleClass.CollectInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@TableName(value = "collect", autoResultMap = true)
@NoArgsConstructor
@AllArgsConstructor
public class Collect {
    @TableId(type = IdType.INPUT)
    private Integer user_id;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<CollectInfo> collectInfos;
}
