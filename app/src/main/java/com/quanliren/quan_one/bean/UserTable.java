package com.quanliren.quan_one.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "user_table")
public class UserTable implements Serializable {

    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String mobile;
    @DatabaseField
    private String pwd;
    @DatabaseField(useGetSet = true)
    private String content;


    public UserTable() {
        super();

    }

    public UserTable(User user) {
        super();
        this.user = user;
        this.id = user.getId();
        this.mobile = user.getMobile();
        this.pwd = user.getPwd();
        this.user = user;
    }

    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getContent() {
        if (user == null) {
            return content;
        }
        return new Gson().toJson(user);
    }

    public void setContent(String content) {
        this.content = content;
        user = new Gson().fromJson(content, new TypeToken<User>() {
        }.getType());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
