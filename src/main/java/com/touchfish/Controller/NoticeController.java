package com.touchfish.Controller;

import com.touchfish.Dto.NoticeInfo;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.Po.*;
import com.touchfish.Service.impl.*;
import com.touchfish.Tool.LoginCheck;
import com.touchfish.Tool.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notice")
@Tag(name = "消息相关接口")
public class NoticeController {

    @Autowired
    private NoticeImpl notice;
    @Autowired
    private PaperImpl paper;
    @Autowired
    private AuthorImpl author;
    @Autowired
    private SubscribeImpl subscribe;

    private static String getTimeNow(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }
    @PostMapping("/create")
//    @LoginCheck
    @Operation(summary = "创建消息")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "消息标题 消息内容 接收者id")
    public Result<String> createNotice(@RequestBody NoticeInfo noticeInfo) {
        try{
            notice.save(new Notice(noticeInfo.getTitle(), noticeInfo.getContent(), getTimeNow(),
                    noticeInfo.getUser_id(), false));
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail("创建消息失败");
        }

        return Result.ok("创建消息成功");
    }

    @PostMapping("/get")
//    @LoginCheck
    @Operation(summary = "获取用户所有消息（按时间从新到旧排序）")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "用户id")
    public Result<List<Notice>> getNoticesByUid(@RequestBody Map<String, String> map){
        List<Notice> userNoticeList = notice.lambdaQuery().eq(Notice::getUser_id, map.get("uid")).list();
        return Result.ok("查询用户所有消息成功", userNoticeList);
    }

    @PostMapping("/update")
//    @LoginCheck
    @Operation(summary = "更新消息为已读")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "消息id")
    public Result<String> updateNoticeStatus(@RequestBody Map<String, String> map) {
        Notice targetNotice = notice.getBaseMapper().selectById(map.get("id"));
        if(targetNotice == null){
            return Result.fail("消息不存在");
        }
        targetNotice.setRead_status(true);
        notice.updateById(targetNotice);
        return Result.ok("消息改已读成功");
    }


    /**
     *
     * @param paperId 更新paper的id
     * @return 消息通知是否成功，true为成功
     */
    public boolean onPaperUpdate(String paperId) {
        Paper updatedPaper = paper.getBaseMapper().selectById(paperId);
        if(updatedPaper == null) {
            System.out.println("论文不存在");
            return false;
        }

        List<AuthorShip> authorShipList = updatedPaper.getAuthorships();

        if(authorShipList != null) {
            for (AuthorShip eachAuthorShip: authorShipList) {
                onAuthorUpdate(eachAuthorShip.getAuthor().id);
            }
        }

        return true;
    }

    /**
     *
     * @param authorId 更新学者门户的id
     * @return 消息通知是否成功，true为成功
     */
    public boolean onAuthorUpdate(String authorId) {
        Author updatedAuthor =  author.getBaseMapper().selectById(authorId);
        if(updatedAuthor == null) {
            return false;
        }

        List<Subscribe> allSubscribes = subscribe.getBaseMapper().selectList(null);
        for (Subscribe eachSubscribe : allSubscribes) {
            for (String subscribeAuthorId : eachSubscribe.getAuthor_id()) {
                if(subscribeAuthorId.equals(authorId)) {
                    try{
                        notice.save(new Notice("更新通知",
                                "你订阅的学者"+updatedAuthor.getDisplay_name()+"更新啦",
                                getTimeNow(),
                                eachSubscribe.getUser_id(),
                                false));
                    }catch (Exception e){
                        e.printStackTrace();
                        return false;
                    }

                    break;
                }
            }
        }

        return true;
    }

    /**
     *
     * @param paperId 更新论文id
     * @param noticeInfo 自定义的通知内容
     * @return 消息通知是否成功，true为成功
     */
    public boolean onPaperUpdate(String paperId, NoticeInfo noticeInfo) {
        Paper updatedPaper = paper.getBaseMapper().selectById(paperId);
        if(updatedPaper == null) {
            System.out.println("论文不存在");
            return false;
        }

        List<AuthorShip> authorShipList = updatedPaper.getAuthorships();

        if(authorShipList != null) {
            for (AuthorShip eachAuthorShip: authorShipList) {
                onAuthorUpdate(eachAuthorShip.getAuthor().id, noticeInfo);
            }
        }

        return true;
    }

    /**
     *
     * @param authorId 更新学者门户的id
     * @param noticeInfo 自定义的通知内容
     * @return 消息通知是否成功，true为成功
     */
    public boolean onAuthorUpdate(String authorId, NoticeInfo noticeInfo) {
        Author updatedAuthor =  author.getBaseMapper().selectById(authorId);
        if(updatedAuthor == null) {
            return false;
        }

        List<Subscribe> allSubscribes = subscribe.getBaseMapper().selectList(null);
        for (Subscribe eachSubscribe : allSubscribes) {
            for (String subscribeAuthorId : eachSubscribe.getAuthor_id()) {
                if(subscribeAuthorId.equals(authorId)) {
                    try{
                        notice.save(new Notice(noticeInfo.getTitle(), noticeInfo.getContent(), getTimeNow(),
                                noticeInfo.getUser_id(), false));
                    }catch (Exception e){
                        e.printStackTrace();
                        return false;
                    }

                    break;
                }
            }
        }

        return true;
    }


}
