package com.touchfish.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchfish.Po.Paper;
import com.touchfish.Po.PaperDoc;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.List;

public interface IPaper extends IService<Paper> {
    PaperDoc findByTitle(String title);
    List<SearchHit<PaperDoc>> findByAbstract(String keyword);

}
