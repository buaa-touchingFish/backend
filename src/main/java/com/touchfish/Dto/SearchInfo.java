package com.touchfish.Dto;

import lombok.Data;

import java.util.Date;

@Data
public class SearchInfo {
    String keyword;
    String author;
    String institution;
    String publisher;
    Date publication_date;
}
