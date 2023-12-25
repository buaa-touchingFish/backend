package com.touchfish.Dto;

import com.touchfish.MiddleClass.DisplayInfo;
import lombok.Data;

@Data
public class AggregateInfo {
    String type;
    long count;
    DisplayInfo publisher;

    public AggregateInfo(String type, long count) {
        this.type = type;
        this.count = count;
    }

    public AggregateInfo(long count, DisplayInfo publisher) {
        this.count = count;
        this.publisher = publisher;
    }
}
