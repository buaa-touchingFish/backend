package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@TableName("Institution")
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
    private List<String> fields;
    private List<InstitutionRelation> associated_institutions;
}
