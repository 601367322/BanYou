package com.quanliren.quan_one.dao;

import android.content.Context;

import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;

import java.util.List;

/**
 * Created by Shen on 2015/11/9.
 */
public class LoginUserDao extends BaseDao<LoginUser, String> {

    private static LoginUserDao instance;

    public static synchronized LoginUserDao getInstance(Context context) {
        if (instance == null) {
            instance = new LoginUserDao(context.getApplicationContext());
        }
        return instance;
    }

    public LoginUserDao(Context context) {
        super(context);
    }

    public LoginUser getUser() {
        List<LoginUser> users = dao.queryForAll();
        if (users == null || users.size() == 0) {
            return null;
        } else {
            return users.get(0);
        }
    }

    public User getUserInfo() {
        LoginUser u = getUser();
        UserTable user = null;
        if (u != null) {
            user = DBHelper.userTableDao.getUserById(u.getId());
            if (user != null) {
                return user.getUser();
            }
        }
        return null;
    }

}
