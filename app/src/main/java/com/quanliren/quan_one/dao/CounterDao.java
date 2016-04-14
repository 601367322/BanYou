package com.quanliren.quan_one.dao;

import android.content.Context;

import com.quanliren.quan_one.bean.CounterBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shen on 2015/12/7.
 */
public class CounterDao extends BaseDao<CounterBean, Integer> {

    public CounterDao(Context context) {
        super(context);
    }

    private static CounterDao instance;

    public static synchronized CounterDao getInstance(Context context) {
        if (instance == null) {
            instance = new CounterDao(context.getApplicationContext());
        }
        return instance;
    }

    public CounterBean getCounter(String userId) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            List<CounterBean> list = dao.queryForFieldValues(map);
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
