package com.quanliren.quan_one.bean;

import java.io.Serializable;

/**
 * Created by kong on 2015/8/19.
 */
public class UserHeader implements Serializable {
    private String id;
    private String nickname;
    private String avatar;
    private String age;
    private String sex;
    private int isvip;
    private String viptime;
    private String vipday;

    public String getViptime() {
        return viptime;
    }
    public void setViptime(String viptime) {
        this.viptime = viptime;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getIsvip() {
        return isvip;
    }

    public void setIsvip(int isvip) {
        this.isvip = isvip;
    }
    public String getVipday() {
        return vipday;
    }

    public void setVipday(String vipday) {
        this.vipday = vipday;
    }
}
