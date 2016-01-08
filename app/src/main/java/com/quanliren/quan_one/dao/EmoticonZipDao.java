package com.quanliren.quan_one.dao;

import android.content.Context;

import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean;

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

}
