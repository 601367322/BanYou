package com.quanliren.quan_one.dao;

import android.content.Context;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.Where;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shen on 2015/11/9.
 */
public class EmoticonZipDao extends BaseDao<EmoticonActivityListBean.EmoticonZip, Integer> {

    private static EmoticonZipDao instance;

    public static synchronized EmoticonZipDao getInstance(Context context) {
        if (instance == null) {
            instance = new EmoticonZipDao(context.getApplicationContext());
        }
        return instance;
    }

    public EmoticonZipDao(Context context) {
        super(context);
    }

    public EmoticonActivityListBean.EmoticonZip getEmoticonById(String userId, int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("userId", userId);
        List<EmoticonActivityListBean.EmoticonZip> list = dao.queryForFieldValues(map);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public List<EmoticonActivityListBean.EmoticonZip> getAllMyEmoticon(String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        List<EmoticonActivityListBean.EmoticonZip> list = dao.queryForFieldValues(map);
        if (list.size() > 0) {
            return list;
        }
        return new ArrayList<>();
    }

    public void deleteEmoticon(String userId, int id) {
        try {
            DeleteBuilder<EmoticonActivityListBean.EmoticonZip, Integer> builder = dao.deleteBuilder();
            Where where = builder.where();
            where.and(where.eq("userId", userId), where.eq("id", id));
            builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
