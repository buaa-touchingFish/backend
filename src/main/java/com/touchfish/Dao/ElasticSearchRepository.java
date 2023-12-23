package com.touchfish.Dao;

import com.touchfish.Po.PaperDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticSearchRepository extends ElasticsearchRepository<PaperDoc, String> {

    Page<PaperDoc> findByInformation(String keyword, Pageable pageable);
    Page<PaperDoc> findByAuthorships(String author,Pageable pageable);
    Page<PaperDoc> findByPublishers(String publisher,Pageable pageable);
    @Query("{\"match\": {\"name\": {\"query\": \"?0\"}}}")
    Page<PaperDoc> find(String name,Pageable pageable);
}