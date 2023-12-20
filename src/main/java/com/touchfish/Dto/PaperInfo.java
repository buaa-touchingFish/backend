package com.touchfish.Dto;

import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.RefWork;
import com.touchfish.MiddleClass.RelWork;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaperInfo {
    private String id;
    private String title; //标题
    private String Abstract;//摘要
    private Integer cited_by_count; //被引用次数
    private String oa_url; //pdf地址
    private String doi;
    private String publication_date; //发表日期
    private String type;  //文章类型
    private String lan; //语言
    private String issn;
    private String  publisher ;// 发表的刊物
    private List<String> keywords; // 关键词
    private List<AuthorShip> authors;//作者相关信息
    private List<RelWork> relWorks = new ArrayList<>();//相关文献
    private List<RefWork> refWorks = new ArrayList<>();//参考文献

}
