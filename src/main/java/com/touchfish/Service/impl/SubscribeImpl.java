package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.SubscribeMapper;
import com.touchfish.Po.Subscribe;
import com.touchfish.Service.ISubscribe;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SubscribeImpl extends ServiceImpl<SubscribeMapper, Subscribe> implements ISubscribe {
    public boolean saveSubscribe(Integer user_id, String author_id) {
        Subscribe subscribe = getById(user_id);
        if(subscribe == null) {
            subscribe = new Subscribe(user_id, new ArrayList<>());
            subscribe.getAuthor_id().add(author_id);
            save(subscribe);
        }
        else {
            if(subscribe.getAuthor_id().contains(author_id))
                return false;
            subscribe.getAuthor_id().add(author_id);
            updateById(subscribe);
        }
        return true;
    }

    public boolean deleteSubscribe(Integer user_id, String author_id) {
        Subscribe subscribe = getById(user_id);
        if(subscribe == null) {
            return false;
        }
        else {
            if(!subscribe.getAuthor_id().contains(author_id))
                return false;
            subscribe.getAuthor_id().remove(author_id);
            updateById(subscribe);
        }
        return true;
    }

    public ArrayList<String> getSubscribes(Integer user_id) {
        Subscribe subscribe = getById(user_id);
        if(subscribe == null)
            return new ArrayList<>();
        else
            return subscribe.getAuthor_id();
    }
}
