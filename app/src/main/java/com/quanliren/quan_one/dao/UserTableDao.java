package com.quanliren.quan_one.dao;

import android.content.Context;

import com.google.gson.Gson;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.util.URL;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Shen on 2015/11/9.
 */
public class UserTableDao extends BaseDao<UserTable, String> {

    private static UserTableDao instance;

    public static synchronized UserTableDao getInstance(Context context) {
        if (instance == null) {
            instance = new UserTableDao(context.getApplicationContext());
        }
        return instance;
    }

    public UserTableDao(Context context) {
        super(context);
    }

    public UserTable getUserById(String userId) {
        try {
            List<UserTable> list = dao.queryForEq("id", userId);
            if (list.size() > 0) {
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserTable getUserByUserName(String username) {
        List<UserTable> list = dao.queryForEq("mobile", username);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public User updateUser(JSONObject jo){
        try {
            User temp = new Gson().fromJson(jo.getString(URL.RESPONSE),
                    User.class);
            updateUser(temp);
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateUser(User user){
        try {
            UserTable ut = new UserTable(user);
            dao.delete(ut);
            dao.create(ut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
