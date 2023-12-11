package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@TableName("Institution_author")
@NoArgsConstructor
public class InstitutionAuthor {
    private String id;
    private List<String> author_ids;
}
