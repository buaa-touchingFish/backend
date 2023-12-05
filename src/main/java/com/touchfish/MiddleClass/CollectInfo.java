package com.touchfish.MiddleClass;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;

@Data
@AllArgsConstructor
public class CollectInfo {
    private String paper_id;
    private HashSet<String> labels;
}
