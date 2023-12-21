package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@TableName(value = "author_paper", autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
public class AuthorPaper {
    @TableId(type = IdType.INPUT)
    private String id;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> papers;
}
