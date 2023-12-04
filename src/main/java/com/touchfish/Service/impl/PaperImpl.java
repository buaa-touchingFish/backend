package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.ElasticSearchRepository;
import com.touchfish.Dao.PaperMapper;
import com.touchfish.Po.Paper;
import com.touchfish.Po.PaperDoc;
import com.touchfish.Service.IPaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaperImpl extends ServiceImpl<PaperMapper, Paper> implements IPaper {
    @Autowired
    ElasticSearchRepository repository;
    public PaperDoc findByTitle(String title){
        return repository.findByTitle(title);
    }
    public List<SearchHit<PaperDoc>> findByAbstract(String keyword){
        return repository.findByAbstracts(keyword);
    }

}
