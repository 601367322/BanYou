package com.quanliren.quan_one.dao;

import android.content.Context;

import com.quanliren.quan_one.bean.CacheBean;

/**
 * Created by Shen on 2015/11/9.
 */
public class CacheDao extends BaseDao<CacheBean, String> {

    private static CacheDao instance;

    public static synchronized CacheDao getInstance(Context context) {
        if (instance == null) {
            instance = new CacheDao(context.getApplicationContext());
        }
        return instance;
    }

    public CacheDao(Context context) {
        super(context);
    }

}
