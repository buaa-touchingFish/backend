package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchfish.Dao.InstitutionAuthorMapper;
import com.touchfish.Po.InstitutionAuthor;
import com.touchfish.Po.Label;
import com.touchfish.Service.IInstitutionAuthor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InstitutionAuthorImpl extends ServiceImpl<InstitutionAuthorMapper, InstitutionAuthor> implements IInstitutionAuthor {
    public boolean saveInstAuthor(String inst_id, String author_id) {
        System.out.println(inst_id+ " " + author_id);
        InstitutionAuthor institutionAuthor = getById(inst_id);
        if(institutionAuthor == null) {
            institutionAuthor = new InstitutionAuthor(inst_id, new ArrayList<>());
            institutionAuthor.getAuthor_ids().add(author_id);
            save(institutionAuthor);
        } else {
            List<String> authorIds = institutionAuthor.getAuthor_ids();
            authorIds = new ObjectMapper().convertValue(authorIds, new TypeReference<>() {
            });
            if(authorIds.contains(author_id))
                return false;
            institutionAuthor.getAuthor_ids().add(author_id);
            updateById(institutionAuthor);
        }
        return true;
    }
}
