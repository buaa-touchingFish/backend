package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.NoticeMapper;
import com.touchfish.Po.Notice;
import com.touchfish.Service.INotice;
import org.springframework.stereotype.Service;

@Service
public class NoticeImpl extends ServiceImpl<NoticeMapper, Notice> implements INotice {
}
