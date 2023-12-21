package com.touchfish.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.JsonNode;
import com.touchfish.Po.Paper;

import java.util.List;
import java.util.Map;

public interface IPaper extends IService<Paper> {
    public Paper getPaperByAlex(String id);
}
