package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchfish.Dao.LabelMapper;
import com.touchfish.MiddleClass.LabelInfo;
import com.touchfish.Po.Label;
import com.touchfish.Service.ILabel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LabelImpl extends ServiceImpl<LabelMapper, Label> implements ILabel {
    public void addLabel(Integer user_id, String label_name) {
        Label label = getById(user_id);
        if(label==null)
        {
            label = new Label(user_id, new ArrayList<>());
            save(label);
        }
        List<LabelInfo> labelList = label.getLabel_list();
        List<LabelInfo> list = new ObjectMapper().convertValue(labelList, new TypeReference<>() {
        });
        boolean flag = false;
        for(LabelInfo labelInfo:list)
        {
            if(labelInfo.getName().equals(label_name))
            {
                flag = true;
                labelInfo.setCount(labelInfo.getCount()+1);
                break;
            }
        }
        if (!flag) list.add(new LabelInfo(label_name, 1));
        label.setLabel_list(list);
        updateById(label);
    }

    public void deleteLabel(Integer user_id, String label_name) {
        Label label = getById(user_id);
        List<LabelInfo> labelList = label.getLabel_list();
        List<LabelInfo> list = new ObjectMapper().convertValue(labelList, new TypeReference<>() {
        });
        for(LabelInfo labelInfo:list)
        {
            if(labelInfo.getName().equals(label_name))
            {
                labelInfo.setCount(labelInfo.getCount()-1);
                break;
            }
        }
        label.setLabel_list(list);
        updateById(label);
    }
}
