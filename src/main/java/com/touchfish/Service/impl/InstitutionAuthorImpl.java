package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.InstitutionAuthorMapper;
import com.touchfish.Po.InstitutionAuthor;
import com.touchfish.Service.IInstitutionAuthor;
import org.springframework.stereotype.Service;

@Service
public class InstitutionAuthorImpl extends ServiceImpl<InstitutionAuthorMapper, InstitutionAuthor>
        implements IInstitutionAuthor {
}
