package com.touchfish.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("claim_request")
public class ClaimRequest {
    @TableId(type = IdType.AUTO)
    int id;
    int applicant_id;
    int handler_id;
    int status;

    String create_time;
    String handle_time;
    String author_id;

    String photo_url;


    public ClaimRequest(int applicant_id, String create_time, String author_id) {
        this.applicant_id = applicant_id;
        this.create_time = create_time;
        this.author_id = author_id;

        this.status = 0;
    }

}
