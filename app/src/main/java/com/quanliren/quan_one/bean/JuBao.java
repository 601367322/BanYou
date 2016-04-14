package com.quanliren.quan_one.bean;

import java.io.Serializable;

/**
 * Created by Shen on 2016/4/7.
 */
public class JuBao implements Serializable {

    public int id;
    public String rpType;
    public boolean isChecked;

    public JuBao() {
    }

    public JuBao(int id, String name, boolean isChecked) {
        this.id = id;
        this.rpType = name;
        this.isChecked = isChecked;
    }
}
