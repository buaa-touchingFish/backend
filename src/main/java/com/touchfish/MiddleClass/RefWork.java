package com.touchfish.MiddleClass;

import lombok.Data;

import java.util.List;

@Data
public class RefWork {
    private String id;//引用文献的id
    private String title;//文献标题
    private String publication_date;//发表日期
    private Integer cited_by_count;//被引用次数
    private List<DisplayInfo> authors;//作者信息
    private String publisher;//发表刊物
    private String Abstract; // 摘要
}
