package com.quanliren.quan_one.dao;

import android.content.Context;

import com.quanliren.quan_one.bean.CustomFilterQuanBean;

import java.util.List;

/**
 * Created by Shen on 2015/11/9.
 */
public class CustomFilterBeanQuanDao extends BaseDao<CustomFilterQuanBean, String> {

    private static CustomFilterBeanQuanDao instance;

    public static synchronized CustomFilterBeanQuanDao getInstance(Context context) {
        if (instance == null) {
            instance = new CustomFilterBeanQuanDao(context.getApplicationContext());
        }
        return instance;
    }

    public CustomFilterBeanQuanDao(Context context) {
        super(context);
    }

    public List<CustomFilterQuanBean> getAllFilter(){
        return dao.queryForAll();
    }
    public void deleteById(String id){
        dao.deleteById(id);
    }
}
