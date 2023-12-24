package com.touchfish.Dto;

import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.RefWork;
import com.touchfish.MiddleClass.RelWork;
import com.touchfish.Po.Comment;
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
    private Integer browse ; //浏览量
    private Integer good ; //点赞量
    private Integer collect ; //收藏量
    public boolean isCollected = false;//是否被收藏
    public boolean is_active = true;
    private List<String> keywords; // 关键词
    private List<AuthorShip> authorships;//作者相关信息
    private List<String> referenced_works = new ArrayList<>();//相关文献
    private List<String> related_works = new ArrayList<>();//参考文献
}
