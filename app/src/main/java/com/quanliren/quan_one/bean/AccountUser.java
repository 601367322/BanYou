package com.quanliren.quan_one.bean;

import java.io.Serializable;

/**
 * Created by kong on 2016/3/4.
 */
public class AccountUser implements Serializable {
    private  String name;
    private  String account;

    public AccountUser(String name, String account) {
        this.name = name;
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
