package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.DisplayInfo;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
@Document(indexName = "papers")
@Data
public class PaperDoc {
    @Id
    private String id;
    @Field(name = "title",type =FieldType.Text)
    private String title;
    @Field(name = "authorships",type =FieldType.Text)
    private String authorships;
    @Field(name = "keywords",type =FieldType.Text)

    private List<String> keywords;
    @Field(name = "abstract",type = FieldType.Text)
    private String abstracts;
    private Integer cited_by_count;
    private String oa_url;
    private String doi;
    @Field(name="publication_date",type = FieldType.Date)
    private String publication_date;

    private String type;

    private String publisher;
    private String referenced_works;
    private String related_works;
    private String information;
    private String lan;
    private String issn;

    private boolean is_active;
    @Override
    public String toString() {
        return "PaperDoc{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", authorships=" + authorships +
                ", keywords=" + keywords +
                ", abstracts='" + abstracts + '\'' +
                ", cited_by_count=" + cited_by_count +
                ", oa_url='" + oa_url + '\'' +
                ", doi='" + doi + '\'' +
                ", publication_date='" + publication_date + '\'' +
                ", type='" + type + '\'' +
                ", publisher='" + publisher + '\'' +
                ", referenced_works=" + referenced_works +
                ", related_works=" + related_works +
                '}';
    }
}
