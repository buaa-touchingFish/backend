package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.PaperAppealMapper;
import com.touchfish.Po.PaperAppeal;
import com.touchfish.Service.IPaperAppeal;
import org.springframework.stereotype.Service;

@Service
public class PaperAppealImpl extends ServiceImpl<PaperAppealMapper, PaperAppeal> implements IPaperAppeal{
}
