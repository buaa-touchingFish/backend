package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName(value = "paper_appeal", autoResultMap = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaperAppeal {
    @TableId(type = IdType.AUTO)
    int id;
    int applicant_id;
    int handler_id;
    int status;

    String create_time;
    String handle_time;
    String paper_id;

    public PaperAppeal(int applicant_id, String create_time, String paper_id) {
        this.applicant_id = applicant_id;
        this.create_time = create_time;
        this.paper_id = paper_id;

        this.status = 0;
    }
}
