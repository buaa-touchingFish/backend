package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.DisplayInfo;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "paper",autoResultMap = true)
public class Paper {
    @TableId(type = IdType.INPUT)
    private String id;
    private String title;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<AuthorShip> authorships;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> keywords;
    @TableField("abstract")
    private String Abstract;
    private Integer cited_by_count;
    private String oa_url;
    private String doi;
    private String publication_date;
    private String type;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private DisplayInfo publisher;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> referenced_works;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> related_works;

}
