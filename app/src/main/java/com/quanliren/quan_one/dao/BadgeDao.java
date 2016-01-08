package com.quanliren.quan_one.dao;

import android.content.Context;

import com.quanliren.quan_one.bean.BadgeBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shen on 2015/12/7.
 */
public class BadgeDao  extends BaseDao<BadgeBean, Integer> {

    public BadgeDao(Context context) {
        super(context);
    }

    private static BadgeDao instance;

    public static synchronized BadgeDao getInstance(Context context) {
        if (instance == null) {
            instance = new BadgeDao(context.getApplicationContext());
        }
        return instance;
    }

    public BadgeBean getBadge(String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        List<BadgeBean> list = dao.queryForFieldValues(map);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}
