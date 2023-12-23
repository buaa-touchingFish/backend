package com.touchfish.Dto;

import lombok.Data;

import java.util.Date;

@Data
public class SearchInfo {
    int pageNum;
    String keyword;
    String author;
    String type;
    String issn;
    String language;
    String institution;
    String publisher;
    String from_date;
    String to_date;

    @Override
    public String toString() {
        return "SearchInfo{" +
                "pageNum=" + pageNum +
                ", keyword='" + keyword + '\'' +
                ", author='" + author + '\'' +
                ", institution='" + institution + '\'' +
                ", publisher='" + publisher + '\'' +
                ", from_date=" + from_date +
                ", to_date=" + to_date +
                '}';
    }
}
