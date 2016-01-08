package com.quanliren.quan_one.dao;

import android.content.Context;

import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean;

/**
 * Created by Shen on 2015/11/9.
 */
public class EmoticonImageBeanDao extends BaseDao<EmoticonActivityListBean.EmoticonZip.EmoticonImageBean, String> {

    private static EmoticonImageBeanDao instance;

    public static synchronized EmoticonImageBeanDao getInstance(Context context) {
        if (instance == null) {
            instance = new EmoticonImageBeanDao(context.getApplicationContext());
        }
        return instance;
    }

    public EmoticonImageBeanDao(Context context) {
        super(context);
    }

}
