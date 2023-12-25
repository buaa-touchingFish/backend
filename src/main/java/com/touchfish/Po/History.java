package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("history")
@NoArgsConstructor
public class History {
    @TableId(type = IdType.AUTO)
    private int id;
    private int user_id;
    private String paper_id;
    private String last_update_time;
    private int view_times;



    public History(int user_id, String paper_id, String last_update_time) {
        this.user_id = user_id;
        this.paper_id = paper_id;
        this.last_update_time = last_update_time;
        this.view_times = 1;
    }
}
