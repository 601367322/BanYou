package com.quanliren.quan_one.dao;

import android.content.Context;

import com.quanliren.quan_one.bean.CustomFilterBean;

import java.util.List;

/**
 * Created by Shen on 2015/11/9.
 */
public class CustomFilterBeanDao extends BaseDao<CustomFilterBean, String> {

    private static CustomFilterBeanDao instance;

    public static synchronized CustomFilterBeanDao getInstance(Context context) {
        if (instance == null) {
            instance = new CustomFilterBeanDao(context.getApplicationContext());
        }
        return instance;
    }

    public CustomFilterBeanDao(Context context) {
        super(context);
    }

    public List<CustomFilterBean> getAllFilter(){
        return dao.queryForAll();
    }
    public void deleteById(String id){
        dao.deleteById(id);
    }
}
