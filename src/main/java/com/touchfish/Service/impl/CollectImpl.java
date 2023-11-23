package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.CollectMapper;
import com.touchfish.Po.Collect;
import com.touchfish.Service.ICollect;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CollectImpl extends ServiceImpl<CollectMapper, Collect> implements ICollect {
    public boolean saveCollect(Integer user_id, String paper_id) {
        Collect collect = getById(user_id);
        if(collect == null) {
            collect = new Collect(user_id, new ArrayList<>());
            collect.getPaper_id().add(paper_id);
            save(collect);
        }
        else {
            if(collect.getPaper_id().contains(paper_id))
                return false;
            collect.getPaper_id().add(paper_id);
            updateById(collect);
        }
        return true;
    }

    public boolean deleteCollect(Integer user_id, String paper_id) {
        Collect collect = getById(user_id);
        if(collect == null) {
            return false;
        }
        else {
            if(!collect.getPaper_id().contains(paper_id))
                return false;
            collect.getPaper_id().remove(paper_id);
            updateById(collect);
        }
        return true;
    }

    public ArrayList<String> getCollects(Integer user_id) {
        Collect collect = getById(user_id);
        if(collect == null)
            return new ArrayList<>();
        else
            return collect.getPaper_id();
    }
}
