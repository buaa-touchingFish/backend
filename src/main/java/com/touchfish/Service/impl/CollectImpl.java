package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchfish.Dao.CollectMapper;
import com.touchfish.MiddleClass.CollectInfo;
import com.touchfish.Po.Collect;
import com.touchfish.Service.ICollect;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class CollectImpl extends ServiceImpl<CollectMapper, Collect> implements ICollect {

    public boolean saveCollect(Integer user_id, String paper_id) {
        Collect collect = getById(user_id);
        if (collect == null) {
            collect = new Collect(user_id, new ArrayList<>());
            collect.getCollectInfos().add(new CollectInfo(paper_id, new LinkedHashSet<>()));
            save(collect);
        } else {
            ObjectMapper mapper = new ObjectMapper();
            List<CollectInfo> collectInfos = collect.getCollectInfos();
            List<CollectInfo> list = mapper.convertValue(collectInfos, new TypeReference<>() {
            });
            for (CollectInfo collectInfo : list) {
                if (collectInfo.getPaper_id().equals(paper_id))
                    return false;
            }
            collect.getCollectInfos().add(new CollectInfo(paper_id, new LinkedHashSet<>()));
            updateById(collect);
        }
        return true;

    }

    public boolean deleteCollect(Integer user_id, String paper_id) {
        Collect collect = getById(user_id);
        if (collect == null) {
            return false;
        } else {
            boolean flag = false;
            ObjectMapper mapper = new ObjectMapper();
            List<CollectInfo> collectInfos = collect.getCollectInfos();
            List<CollectInfo> list = mapper.convertValue(collectInfos, new TypeReference<>() {
            });
            for (CollectInfo collectInfo : list) {
                if (collectInfo.getPaper_id().equals(paper_id)) {
                    list.remove(collectInfo);
                    flag = true;
                    break;
                }
            }
            if (flag) {
                collect.setCollectInfos(list);
                updateById(collect);
                return true;
            } else
                return false;
        }
    }

    public List<CollectInfo> getCollects(Integer user_id) {
        Collect collect = getById(user_id);
        if (collect == null)
            return new ArrayList<>();
        else
            return collect.getCollectInfos();
    }

    public boolean addLabel(Integer user_id, String paper_id, String label) {
        Collect collect = getById(user_id);
        ObjectMapper mapper = new ObjectMapper();
        List<CollectInfo> collectInfos = collect.getCollectInfos();
        List<CollectInfo> list = mapper.convertValue(collectInfos, new TypeReference<>() {
        });
        for (CollectInfo collectInfo : list) {
            if (collectInfo.getPaper_id().equals(paper_id)) {
                if (collectInfo.getLabels().contains(label))
                    return false;
                collectInfo.getLabels().add(label);
                break;
            }
        }
        collect.setCollectInfos(list);
        updateById(collect);
        return true;
    }

    public void deleteLabel(Integer user_id, String paper_id, String label) {
        Collect collect = getById(user_id);
        ObjectMapper mapper = new ObjectMapper();
        List<CollectInfo> collectInfos = collect.getCollectInfos();
        List<CollectInfo> list = mapper.convertValue(collectInfos, new TypeReference<>() {
        });
        for (CollectInfo collectInfo : list) {
            if (collectInfo.getPaper_id().equals(paper_id)) {
                collectInfo.getLabels().remove(label);
                break;
            }
        }
        collect.setCollectInfos(list);
        updateById(collect);
    }
}
