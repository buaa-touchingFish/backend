package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.CollectMapper;
import com.touchfish.MiddleClass.CollectInfo;
import com.touchfish.Po.Collect;
import com.touchfish.Service.ICollect;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;

@Service
public class CollectImpl extends ServiceImpl<CollectMapper, Collect> implements ICollect {

    public boolean addLabel(Integer user_id, String paper_id, String label) {
        if (label == null) {
            Collect collect = new Collect(user_id, new ArrayList<>());
            CollectInfo collectInfo = new CollectInfo(paper_id, new HashSet<>());
            collect.getCollectInfos().add(collectInfo);
            save(collect);
        }
        else {
            Collect collect = getById(user_id);
            for(CollectInfo collectInfo:collect.getCollectInfos()) {
                if(collectInfo.getPaper_id().equals(paper_id))
                {
                    HashSet<String> labels = collectInfo.getLabels();
                    if(labels.contains(label))
                        return false;
                    labels.add(label);
                    break;
                }
            }
            updateById(collect);
        }
        return true;
    }
    public boolean saveCollect(Integer user_id, String paper_id) {
        Collect collect = getById(user_id);
        if(collect == null) {
            collect = new Collect(user_id, new ArrayList<>());
            collect.getCollectInfos().add(new CollectInfo(paper_id, new HashSet<>()));
            save(collect);
        }
        else {
            ArrayList<CollectInfo> collectInfos = collect.getCollectInfos();

            for(int i=0;i<collectInfos.size();i++) {
                CollectInfo collectInfo = collect.getCollectInfos().get(0);
                System.out.println(collectInfo);
                System.out.println(collectInfo.getLabels());
//                System.out.println(collect.getCollectInfos().get(0).getPaper_id());
                if(collectInfos.get(i).getPaper_id().equals(paper_id))
                    return false;
            }
            for(CollectInfo collectInfo:collect.getCollectInfos()) {
                System.out.println(collectInfo.getPaper_id());
                if(collectInfo.getPaper_id().equals(paper_id))
                    return false;
            }
            collect.getCollectInfos().add(new CollectInfo(paper_id, new HashSet<>()));
            updateById(collect);
        }
        return true;

    }

//    public boolean deleteCollect(Integer user_id, String paper_id) {
//        Collect collect = getById(user_id);
//        if(collect == null) {
//            return false;
//        }
//        else {
//            if(!collect.getPaper_id().contains(paper_id))
//                return false;
//            collect.getPaper_id().remove(paper_id);
//            updateById(collect);
//        }
//        return true;
//    }
//
//    public ArrayList<String> getCollects(Integer user_id) {
//        Collect collect = getById(user_id);
//        if(collect == null)
//            return new ArrayList<>();
//        else
//            return collect.getPaper_id();
//    }
}
