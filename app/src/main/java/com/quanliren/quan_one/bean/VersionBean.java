package com.quanliren.quan_one.bean;

import java.io.Serializable;

public class VersionBean implements Serializable {
    private int id;
    private String remark;
    private String vname;
    private String url;
    private int isnewest;
    private String size;
    private String vcode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public VersionBean() {
        super();

    }

    public VersionBean(String remark, String vname, String url, int isnewest,
                       long size, int vcode) {
        super();
        this.remark = remark;
        this.vname = vname;
        this.url = url;
        this.isnewest = isnewest;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsnewest() {
        return isnewest;
    }

    public void setIsnewest(int isnewest) {
        this.isnewest = isnewest;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }
}
