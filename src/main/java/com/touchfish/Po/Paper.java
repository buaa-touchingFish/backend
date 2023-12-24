package com.touchfish.Po;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.DisplayInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "paper",autoResultMap = true)
//@Document(indexName = "papers")
public class Paper {
    @TableId(type = IdType.INPUT)
   // @Id
    private String id;
    private String title;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<AuthorShip> authorships;//注意这个地方从数据库读出来的时候会把内层的AuthorShip转化成LinkedHashMap,需要使用ObjectMapper进行转换
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> keywords;
    @TableField("abstract")
    private String Abstract;
    private Integer cited_by_count = 0;
    private String oa_url;
    private String doi;
    private String publication_date;
    private String type;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private DisplayInfo publisher;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> referenced_works;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> related_works;
    private  String lan;
    private  String issn;
    private  Boolean is_active;
    public Paper(PaperDoc paperDoc){
        id=paperDoc.getId();
        title=paperDoc.getTitle();
        Pattern pattern=Pattern.compile("(\\{(?:(?!\\]\\}).)*?\\]\\})");
        Matcher matcher=pattern.matcher(paperDoc.getAuthorships());
        authorships=new ArrayList<>();
        while (matcher.find())
            authorships.add(JSONUtil.toBean(matcher.group(),AuthorShip.class));
        if(paperDoc.getKeywords()!=null)
            keywords= Arrays.asList(paperDoc.getKeywords().substring(1,paperDoc.getKeywords().length()-1).split(","));
        Abstract=paperDoc.getAbstracts()==null?"Unknown":paperDoc.getAbstracts();
        if(paperDoc.getAbstracts()!=null){
            if(paperDoc.getAbstracts().equals(""))
                Abstract="Unknown";
        }
        cited_by_count=paperDoc.getCited_by_count();
        publication_date=paperDoc.getPublication_date();
        type=paperDoc.getType();
        String a=paperDoc.getPublisher();
        if(paperDoc.getPublisher()==null){
            publisher=new DisplayInfo("","暂无");
        }
        else if(!paperDoc.getPublisher().equals("null"))
            publisher=JSONUtil.toBean(paperDoc.getPublisher(), DisplayInfo.class);
        else publisher=new DisplayInfo("","暂无");
        issn=paperDoc.getIssn();
        lan=paperDoc.getLan();
    }
}
