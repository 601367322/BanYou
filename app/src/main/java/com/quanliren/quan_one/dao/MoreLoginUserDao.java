package com.quanliren.quan_one.dao;

import android.content.Context;

import com.quanliren.quan_one.bean.MoreLoginUser;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Shen on 2015/11/9.
 */
public class MoreLoginUserDao extends BaseDao<MoreLoginUser, Integer> {

    private static MoreLoginUserDao instance;

    public static synchronized MoreLoginUserDao getInstance(Context context) {
        if (instance == null) {
            instance = new MoreLoginUserDao(context.getApplicationContext());
        }
        return instance;
    }

    public MoreLoginUserDao(Context context) {
        super(context);
    }

    public void update(String username, String password) {
        try {
            delete(username);
            dao.create(new MoreLoginUser(username,password));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(String username){
        try {
            dao.delete(dao.deleteBuilder().where().eq("username", username).prepareDelete());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MoreLoginUser> findAllMoreUser(){
        try {
            return dao.query(dao.queryBuilder().orderBy("id",false).prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
