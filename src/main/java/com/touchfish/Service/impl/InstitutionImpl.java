package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.InstitutionMapper;
import com.touchfish.Po.Institution;
import com.touchfish.Service.IInstitution;
import org.springframework.stereotype.Service;

@Service
public class InstitutionImpl extends ServiceImpl<InstitutionMapper, Institution> implements IInstitution {

}
