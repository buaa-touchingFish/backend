package com.touchfish.Controller;

import cn.hutool.json.JSONUtil;
import com.touchfish.Po.Institution;
import com.touchfish.Po.InstitutionAuthor;
import com.touchfish.Service.impl.InstitutionAuthorImpl;
import com.touchfish.Service.impl.InstitutionImpl;
import com.touchfish.Tool.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/institution")
public class InstitutionController {
    @Autowired
    private InstitutionImpl institution;

    @Autowired
    private InstitutionAuthorImpl institutionAuthor;

    /** 通过id获取机构
     * */
    @PostMapping("/get")
    public Result getInstitutionById(@RequestBody String id)
    {
        Institution targetInstitution = institution.getBaseMapper().selectById(id);
        if(targetInstitution == null)
        {
            return Result.fail("id not exist");
        }

        InstitutionAuthor members = institutionAuthor.getBaseMapper().selectById(id);
        if(members == null)
        {
            return Result.fail("id not exist");
        }

        String jsonStr = JSONUtil.toJsonStr(targetInstitution);

        return Result.ok("get institution success", jsonStr);

    }

}
