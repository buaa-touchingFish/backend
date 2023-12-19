package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@TableName(value = "institution", autoResultMap = true)
@NoArgsConstructor
public class Institution {
    private String id;
    private String display_name;
    private String ror;
    private String country_code;
    private Integer works_count;
    private Integer cited_by_count;
    private String type;
    private String homepage_url;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> fields;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<InstitutionRelation> associated_institutions;
}
