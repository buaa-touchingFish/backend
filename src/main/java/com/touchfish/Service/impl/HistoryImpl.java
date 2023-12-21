package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.HistoryMapper;
import com.touchfish.Po.History;
import com.touchfish.Service.IHistory;
import org.springframework.stereotype.Service;

@Service
public class HistoryImpl extends ServiceImpl<HistoryMapper, History> implements IHistory {
}
