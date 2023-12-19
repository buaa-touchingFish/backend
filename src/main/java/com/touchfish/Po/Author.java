package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.touchfish.MiddleClass.MiddleInstitution;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@TableName(value = "author", autoResultMap = true)
public class Author {
    @TableId(type = IdType.INPUT)
    private String id;
    private String display_name;
    private Integer works_count;
    private Integer cited_by_count;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private MiddleInstitution last_known_institution;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> fields;
    private Integer h_index;
    private Integer claim_uid;
    private String updated_date;
}
