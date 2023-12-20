package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.CollectCntMapper;
import com.touchfish.Po.CollectCnt;
import com.touchfish.Service.ICollectCnt;
import org.springframework.stereotype.Service;

@Service
public class CollectCntImpl extends ServiceImpl<CollectCntMapper, CollectCnt> implements ICollectCnt {
}
