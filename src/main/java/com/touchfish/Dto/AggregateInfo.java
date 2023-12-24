package com.touchfish.Dto;

import lombok.Data;

@Data
public class AggregateInfo {
    String type;
    long count;

    public AggregateInfo(String type, long count) {
        this.type = type;
        this.count = count;
    }
}
