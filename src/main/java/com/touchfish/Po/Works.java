package com.touchfish.Po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "papers")
@Data
public class Works {
    @Id
    private String id;
    @Field(name = "title",type =FieldType.Text)
    private String title;
    @Field(name = "authorships",type =FieldType.Text)
    private String authorships;
    @Field(name = "abstracts",type = FieldType.Text)
    private String abstracts;
    @Field(name = "keywords",type =FieldType.Text)
    private String keywords;
    private Long cited_by_count;
    private String oa_url;
    @Field(name="publication_date",type = FieldType.Date)
    private String publication_date;

    private String type;
    @Field(name="publisher",type = FieldType.Keyword)
    private String publisher;
    @Field(name="publishers",type = FieldType.Text)
    private String publishers;
    private String information;
    private String lan;
    private String issn;

    @Override
    public String toString() {
        return "PaperDoc{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", authorships='" + authorships + '\'' +
                ", keywords='" + keywords + '\'' +
                ", abstracts='" + abstracts + '\'' +
                ", cited_by_count=" + cited_by_count +
                ", oa_url='" + oa_url + '\'' +
                ", publication_date='" + publication_date + '\'' +
                ", type='" + type + '\'' +
                ", publisher='" + publisher + '\'' +
                ", lan='" + lan + '\'' +
                ", issn='" + issn + '\'' +
                '}';
    }
}
