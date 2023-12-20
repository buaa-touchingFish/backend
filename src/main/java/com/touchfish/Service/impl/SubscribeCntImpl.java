package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.SubscribeCntMapper;
import com.touchfish.Po.SubscribeCnt;
import com.touchfish.Service.ISubscribeCnt;
import org.springframework.stereotype.Service;

@Service
public class SubscribeCntImpl extends ServiceImpl<SubscribeCntMapper, SubscribeCnt> implements ISubscribeCnt {
}
