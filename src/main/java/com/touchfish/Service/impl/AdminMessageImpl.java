package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.AdminMessageMapper;
import com.touchfish.Po.AdminMessage;
import com.touchfish.Service.IAdminMessage;
import org.springframework.stereotype.Service;

@Service
public class AdminMessageImpl extends ServiceImpl<AdminMessageMapper, AdminMessage> implements IAdminMessage {
}
