package com.touchfish.Dao;

import com.touchfish.Po.PaperDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
<<<<<<< Updated upstream
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
=======
import org.springframework.data.elasticsearch.annotations.Query;
>>>>>>> Stashed changes
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticSearchRepository extends ElasticsearchRepository<PaperDoc, String> {

    /**
     * 查询内容标题查询
     * @param title 标题
     * @param content 内容
     * @return 返回关键字高亮的结果集
     */
    @Highlight(
            fields = {@HighlightField(name = "title")},
            parameters = @HighlightParameters(preTags = {"<span style='color:red'>"}, postTags = {"</span>"}, numberOfFragments = 0)
    )
    //List<SearchHit<PaperDoc>> findByTitleOrContent(String title, String content);
    //List<SearchHit<PaperDoc>> findById(String id);
<<<<<<< Updated upstream
    List<SearchHit<PaperDoc>> findByTitleContains(String title);
    Page<PaperDoc> findByAbstracts(String keyword, Pageable pageable);
    List<SearchHit<PaperDoc>> findByInformation(String content);
=======
    PaperDoc findByTitle(String title);
    List<SearchHit<PaperDoc>> findByAbstracts(String keyword);

>>>>>>> Stashed changes
}