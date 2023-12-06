package com.touchfish.MiddleClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectInfo {
    private String paper_id;
    private LinkedHashSet<String> labels;
}
