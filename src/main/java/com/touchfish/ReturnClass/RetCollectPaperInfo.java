package com.touchfish.ReturnClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetCollectPaperInfo {
    private String paper_id;
    private String title;
    private List<String> authors;
    private String publisher;
    private Integer cited_by_count;
    private LinkedHashSet<String> labels;
}
