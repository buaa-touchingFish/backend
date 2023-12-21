package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@TableName(value = "institution_author", autoResultMap = true)
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionAuthor {
    private String id;
    @Getter
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> author_ids;
}
