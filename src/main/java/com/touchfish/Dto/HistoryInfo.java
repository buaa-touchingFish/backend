package com.touchfish.Dto;

import lombok.Data;

@Data
public class HistoryInfo {
    private int id;
    private int user_id;
    private String paper_id;
    private String last_update_time;
    private int view_times;
    private String paper_name;

}
