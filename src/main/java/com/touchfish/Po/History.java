package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private String create_time;


    public History(int user_id, String paper_id, String create_time) {
        this.user_id = user_id;
        this.paper_id = paper_id;
        this.create_time = create_time;
    }
}
