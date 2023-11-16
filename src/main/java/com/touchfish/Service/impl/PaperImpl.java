package com.touchfish.Service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.PaperMapper;
import com.touchfish.Po.Paper;
import com.touchfish.Service.IPaper;
import org.springframework.stereotype.Service;

@Service
public class PaperImpl extends ServiceImpl<PaperMapper, Paper> implements IPaper {

}
