package com.touchfish.Controller;

import cn.hutool.json.JSONUtil;
import com.touchfish.Dto.InstitutionWithAuthorInfo;
import com.touchfish.Po.Author;
import com.touchfish.Po.AuthorPaper;
import com.touchfish.Po.Institution;
import com.touchfish.Po.InstitutionAuthor;
import com.touchfish.Service.impl.AuthorImpl;
import com.touchfish.Service.impl.AuthorPaperImpl;
import com.touchfish.Service.impl.InstitutionAuthorImpl;
import com.touchfish.Service.impl.InstitutionImpl;
import com.touchfish.Tool.OpenAlex;
import com.touchfish.Tool.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/institution")
@Tag(name = "机构相关接口")
public class InstitutionController {
    @Autowired
    private InstitutionImpl institution;

    @Autowired
    private InstitutionAuthorImpl institutionAuthor;
    
    @Autowired
    private AuthorImpl author;

    private AuthorPaperImpl authorPaperImpl;

    /** 通过id获取机构
     * */
    @PostMapping("/get")
    @Operation(summary = "获取机构")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "机构id")
    public Result<InstitutionWithAuthorInfo> getInstitutionById(@RequestBody Map<String, String> map)
    {
        Institution targetInstitution = institution.getBaseMapper().selectById(map.get("id"));
        if(targetInstitution == null)
        {
            return Result.fail("id not exist");
        }

        InstitutionAuthor members = institutionAuthor.getBaseMapper().selectById(map.get("id"));
        if(members == null)
        {
            return Result.fail("id not exist");
        }

        InstitutionWithAuthorInfo institutionWithAuthorInfo = new InstitutionWithAuthorInfo();
        institutionWithAuthorInfo.setInstitution(targetInstitution);

        List<Author> authorList = new ArrayList<>();
        if(members.getAuthor_ids() != null) {
            for (String eachAuthorId : members.getAuthor_ids()) {
                Author targetAuthor = author.getBaseMapper().selectById(eachAuthorId);
                if (targetAuthor == null){
                    targetAuthor = (Author) OpenAlex.sendResponse("author",eachAuthorId);
                    author.saveOrUpdate(targetAuthor);
                    if (!authorPaperImpl.lambdaQuery().eq(AuthorPaper::getId,targetAuthor.getId()).exists()){
                        AuthorPaper authorPaper = new AuthorPaper(targetAuthor.getId(),new ArrayList<>());
                        authorPaperImpl.saveOrUpdate(authorPaper);
                    }
                }
                authorList.add(targetAuthor);
            }
        }
        institutionWithAuthorInfo.setAuthorList(authorList);

        return Result.ok("get institution success", institutionWithAuthorInfo);

    }

}
