package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.LabelMapper;
import com.touchfish.Po.Label;
import com.touchfish.Service.ILabel;
import org.springframework.stereotype.Service;

@Service
public class LabelImpl extends ServiceImpl<LabelMapper, Label> implements ILabel {
}
