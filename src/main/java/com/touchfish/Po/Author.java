package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.touchfish.MiddleClass.Institution;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "author", autoResultMap = true)
public class Author {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String display_name;
    private Integer works_count;
    private Integer cited_by_count;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Institution last_known_institution;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> fields;
    private Integer h_index;
    private Integer claim_uid;
    private LocalDateTime updated_date;
}
