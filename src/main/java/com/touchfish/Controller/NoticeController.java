package com.touchfish.Controller;

import com.touchfish.Dto.NoticeInfo;
import com.touchfish.Service.impl.NoticeImpl;
import com.touchfish.Tool.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeImpl notice;
    public Result<String> createNotice(@RequestBody NoticeInfo noticeInfo) {

    }
}
