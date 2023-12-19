package com.touchfish.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchfish.Po.Paper;
import com.touchfish.Po.PaperDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.List;

public interface IPaper extends IService<Paper> {
    List<SearchHit<PaperDoc>> findByTitleContains(String title);
    Page<PaperDoc> findByAbstract(String keyword, Pageable pageable);
    List<SearchHit<PaperDoc>> findByInformation(String content);

}
