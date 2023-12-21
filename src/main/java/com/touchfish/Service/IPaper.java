package com.touchfish.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.JsonNode;
import com.touchfish.Po.Paper;
import com.touchfish.Po.PaperDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.List;

import java.util.List;
import java.util.Map;

public interface IPaper extends IService<Paper> {
    public Paper getPaperByAlex(String id);
}
