package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.InstitutionMapper;
import com.touchfish.Po.Author;
import com.touchfish.Po.Institution;
import com.touchfish.Service.IInstitution;
import com.touchfish.Tool.OpenAlex;
import org.springframework.stereotype.Service;

@Service
public class InstitutionImpl extends ServiceImpl<InstitutionMapper, Institution> implements IInstitution {
    public Institution updateInstFromOpenAlex(String id) {
        Institution alexInstitution = (Institution) OpenAlex.sendResponse("institution", id);
        if(alexInstitution == null) return null;
        Institution institution = getById(id);
        if(institution == null)
            save(alexInstitution);
        else
            updateById(alexInstitution);
        return alexInstitution;
    }
}
