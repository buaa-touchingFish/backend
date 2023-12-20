package com.touchfish.Controller;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchfish.Dto.PaperInfo;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.RelWork;
import com.touchfish.Po.Paper;
import com.touchfish.Service.impl.PaperImpl;
import com.touchfish.Tool.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/paper")
@Tag(name = "论文相关接口")
public class PaperController {

    @Autowired
    private PaperImpl paperImpl;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping ("/single")
    @Operation(summary = "点击获取单个文献")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "文献id号  格式:\"id\":\"文献id号\"")
    public Result<Paper> getSingleWork(  @RequestBody  Map<String,String> json){
        Paper paper = paperImpl.lambdaQuery().eq(Paper::getId,json.get("id")).one();
        ObjectMapper mapper = new ObjectMapper();
        List<AuthorShip> authorships = paper.getAuthorships();
        List<AuthorShip> authorShipList = mapper.convertValue(authorships, new TypeReference<>() {});
        List<String> referenced_works = paper.getReferenced_works();
        List<String> related_works = paper.getRelated_works();

        PaperInfo paperInfo = new PaperInfo();
        paperInfo.setAbstract(paper.getAbstract());
        paperInfo.setIssn(paper.getIssn());
        paperInfo.setDoi(paper.getDoi());
        paperInfo.setLan(paper.getLan());
        paperInfo.setKeywords(paper.getKeywords());
        paperInfo.setTitle(paper.getTitle());
        paperInfo.setCited_by_count(paper.getCited_by_count());
        paperInfo.setPublication_date(paper.getPublication_date());
        paperInfo.setPublisher(paperInfo.getPublisher());
        paperInfo.setAuthors(paper.getAuthorships());
        paperInfo.setType(paper.getType());


        int apiCnt = 0;

        for (String id:related_works){
            if (apiCnt>5) break;
            RelWork relWork = new RelWork();
            Paper one = null;
            if (paperImpl.lambdaQuery().eq(Paper::getId,id).exists()){
                one = paperImpl.lambdaQuery().eq(Paper::getId, id).one();
                List<AuthorShip> authorships1 = one.getAuthorships();
                relWork.setAbstract(one.getAbstract());
                relWork.setId(one.getId());
                relWork.setTitle(one.getTitle());
                relWork.setPublisher(one.getPublisher().display_name);
                relWork.setCited_by_count(one.getCited_by_count());
                relWork.setPublication_date(one.getPublication_date());
                for (int i=0;i<3&&i<authorships1.size();i++){ //展示至多3位
                    relWork.getAuthors().add(authorships1.get(i).getAuthor());
                }
                paperInfo.getRelWorks().add(relWork);
            }else{


                apiCnt++;
            }

        }

        return Result.ok("666");
    }

}
