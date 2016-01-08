package com.quanliren.quan_one.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "moreloginuser_table")
public class MoreLoginUser implements Serializable{

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String username;
    @DatabaseField
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MoreLoginUser() {
        super();

    }

    public MoreLoginUser(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }
}
