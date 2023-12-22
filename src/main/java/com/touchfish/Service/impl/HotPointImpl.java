package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.HotPointMapper;
import com.touchfish.Po.HotPoint;
import com.touchfish.Service.IHotPoint;
import org.springframework.stereotype.Service;

@Service
public class HotPointImpl extends ServiceImpl<HotPointMapper, HotPoint> implements IHotPoint {
}
