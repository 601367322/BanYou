package com.quanliren.quan_one.dao;

import android.content.Context;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.quanliren.quan_one.bean.ChatListBean;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.util.FileUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shen on 2015/11/9.
 */
public class DfMessageDao extends BaseDao<DfMessage, Integer> {

    private static DfMessageDao instance;

    public static synchronized DfMessageDao getInstance(Context context) {
        if (instance == null) {
            instance = new DfMessageDao(context.getApplicationContext());
        }
        return instance;
    }

    public DfMessageDao(Context context) {
        super(context);
    }

    public int getUnReadMessageCount(String userId, String friendId) {
        try {
            QueryBuilder qb = dao.queryBuilder();
            qb.where().eq("userid", userId).and().eq("receiverUid", userId).and().eq("sendUid", friendId).and().eq("isRead", 0);
            return (int) qb.countOf();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getAllUnReadMessageCount(String userId) {
        try {
            QueryBuilder qb = dao.queryBuilder();
            qb.where().eq("userid", userId).and().eq("receiverUid", userId).and().eq("isRead", 0);
            return (int) qb.countOf();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void saveMessage(DfMessage msg, ChatListBean cb) {
        try {
            dao.create(msg);

            //更新聊天列表
            DBHelper.chatListBeanDao.updateChatList(cb);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<DfMessage> getMsgList(String userId, String friendId, int maxid) {
        QueryBuilder<DfMessage, Integer> qb = null;
        try {
            qb = dao.queryBuilder();
            Where<DfMessage, Integer> where = qb.where();
            where.and(
                    where.eq("userid", userId),
                    where.or(where.eq("sendUid", friendId),
                            where.eq("receiverUid", friendId)));
            if (maxid > -1) {
                where.and().lt("id", maxid);
            }
            qb.limit(15l);
            qb.orderBy("id", false);
            return dao.query(qb.prepare());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void updateMsgReaded(String ids) {
        try {
            dao.update(dao.updateBuilder()
                    .updateColumnValue("isRead", 1).where()
                    .in("id", ids).prepareUpdate());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteAllMessageByFriendId(String userId, String friendId) {
        try {
            QueryBuilder<DfMessage, Integer> queryBuilder = dao.queryBuilder();
            Where<DfMessage, Integer> queryWhere = queryBuilder.where();
            queryWhere.and(queryWhere.eq("userid", userId),queryWhere.or(queryWhere.eq("sendUid", friendId), queryWhere.eq("receiverUid", friendId)));
            List<DfMessage> msgs = dao.query(queryBuilder.prepare());
            for (int i = 0; i < msgs.size(); i++) {
                //删除对应文件
                switch (msgs.get(i).getMsgtype()) {
                    case DfMessage.IMAGE:
                    case DfMessage.VOICE:
                        FileUtil.deleteFile(msgs.get(i).getContent());
                        break;
                }
            }
            dao.delete(msgs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
