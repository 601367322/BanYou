package com.quanliren.quan_one.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shen on 2015/12/24.
 */
public class GroupBean implements Serializable {

    public GroupBean() {
    }

    public GroupBean(String id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    private String id;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    private String groupName;
    private String groupNum;
    private String userId;
    private String memberNum;
    private String memberSum;
    private String groupInt;
    private String area;
    private String nickname;
    private String age;
    private String sex;
    private String isVip;
    private String ctime;
    private String nsex;
    private String vsex;
    private String birthday;
    private String constell;
    private String longitude;
    private String latitude;
    private ArrayList<ImageBean> imglist;
    private ArrayList<User> avatarList;
    private String avatar;
    private String groupType;
    private int type;//	0：陌生人  1：组员   2：群主

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private User user;

    public String getMemberSum() {
        return memberSum;
    }

    public void setMemberSum(String memberSum) {
        this.memberSum = memberSum;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(String memberNum) {
        this.memberNum = memberNum;
    }

    public String getGroupInt() {
        return groupInt;
    }

    public void setGroupInt(String groupInt) {
        this.groupInt = groupInt;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public ArrayList<User> getAvatarList() {
        return avatarList;
    }

    public void setAvatarList(ArrayList<User> avatarList) {
        this.avatarList = avatarList;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIsVip() {
        return isVip;
    }

    public void setIsVip(String isVip) {
        this.isVip = isVip;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getNsex() {
        return nsex;
    }

    public void setNsex(String nsex) {
        this.nsex = nsex;
    }

    public String getVsex() {
        return vsex;
    }

    public void setVsex(String vsex) {
        this.vsex = vsex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getConstell() {
        return constell;
    }

    public void setConstell(String constell) {
        this.constell = constell;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public ArrayList<ImageBean> getImglist() {
        return imglist;
    }

    public void setImglist(ArrayList<ImageBean> imglist) {
        this.imglist = imglist;
    }
}
