package com.quanliren.quan_one.dao;

import android.content.Context;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.quanliren.quan_one.bean.ChatListBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shen on 2015/11/9.
 */
public class ChatListBeanDao extends BaseDao<ChatListBean, Integer> {

    private static ChatListBeanDao instance;

    public static synchronized ChatListBeanDao getInstance(Context context) {
        if (instance == null) {
            instance = new ChatListBeanDao(context.getApplicationContext());
        }
        return instance;
    }

    public ChatListBeanDao(Context context) {
        super(context);
    }

    public List<ChatListBean> getAllMyChatList(String userId) {
        try {
            QueryBuilder qb = dao.queryBuilder();
            qb.where().eq("userid", userId);
            qb.orderBy("id", false);
            return dao.query(qb.prepare());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateChatList(ChatListBean bean) {
        try {
            deleteChatList(bean.getUserid(), bean.getFriendid());
            create(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteChatList(String userId, String friendId) {
        try {
            DeleteBuilder delete = dao.deleteBuilder();
            delete.where().eq("userid", userId).and().eq("friendid", friendId);
            delete.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ChatListBean getChatListBean(String userId, String friendId) {
        Map<String, Object> map = new HashMap<>();
        map.put("userid", userId);
        map.put("friendid", friendId);
        List<ChatListBean> list = dao.queryForFieldValues(map);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
