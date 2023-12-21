package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.DisplayInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "paper",autoResultMap = true)
//@Document(indexName = "papers")
public class Paper {
    @TableId(type = IdType.INPUT)
   // @Id
    private String id;
    private String title;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<AuthorShip> authorships;//注意这个地方从数据库读出来的时候会把内层的AuthorShip转化成LinkedHashMap,需要使用ObjectMapper进行转换
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
    private  String lan;
    private  String issn;
    private  Boolean is_active;
    public Paper(PaperDoc paperDoc){
        this.id=paperDoc.getId();
        this.title=paperDoc.getTitle();

    }
}
