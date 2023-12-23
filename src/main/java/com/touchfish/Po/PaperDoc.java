package com.touchfish.Po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "papers")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaperDoc {
    @Id
    private String id;
    @Field(name = "title",type =FieldType.Text)
    private String title;
    @Field(name = "authorships",type =FieldType.Text)
    private String authorships;
    @Field(name = "abstracts",type = FieldType.Text)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String abstracts;
    @Field(name = "keywords",type =FieldType.Text)
    private String keywords;
    private Integer cited_by_count;
    private String oa_url;
    @Field(name="publication_date",type = FieldType.Date)
    private String publication_date;
    //private String @version;

    private String type;
    @Field(name="publisher",type = FieldType.Keyword)
    private String publisher;
    @Field(name="publishers",type = FieldType.Text)
    private String publishers;
    private String information;
    private String lan;
    private String issn;

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthorships(String authorships) {
        this.authorships = authorships;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setCited_by_count(Integer cited_by_count) {
        this.cited_by_count = cited_by_count;
    }

    public void setOa_url(String oa_url) {
        this.oa_url = oa_url;
    }

    public void setPublication_date(String publication_date) {
        this.publication_date = publication_date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPublishers(String publishers) {
        this.publishers = publishers;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorships() {
        return authorships;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public String getKeywords() {
        return keywords;
    }

    public Integer getCited_by_count() {
        return cited_by_count;
    }

    public String getOa_url() {
        return oa_url;
    }

    public String getPublication_date() {
        return publication_date;
    }

    public String getType() {
        return type;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishers() {
        return publishers;
    }

    public String getInformation() {
        return information;
    }

    public String getLan() {
        return lan;
    }

    public String getIssn() {
        return issn;
    }

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
