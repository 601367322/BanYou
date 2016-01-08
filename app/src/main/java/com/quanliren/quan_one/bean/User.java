package com.quanliren.quan_one.bean;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quanliren.quan_one.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String id;
    private String avatar;
    private String mobile;
    private String pwd;
    private String sex;
    private String nickname;
    private String usernumber;
    private String signature;
    private String qq;
    private String job;
    private String hobby;
    private int isvip;
    private String height;
    private String weight;
    private String nature;
    private String hometown;
    private String birthday;
    private int isblacklist;
    private String education;
    private String avtwidth;
    private String avtheight;
    private String connum;
    private String viptime;
    private String vipday;
    private String attenstatus;
    private String powernum;
    private String distance;
    private String levelname;
    private String dyid;
    private String dycontent;
    private String dyimgurl;
    private String dytime;
    private String constell;
    private String cityname;
    private String levelid;
    private int identity;
    private String actionTime;
    private String longitude;
    private String latitude;
    private String income;
    private String emotion;
    private String uvid;
    private String visittime;
    private String introduce;
    private int pay;
    private int imgCount;
    private int showState;
    private int identityState;
    private int derail;
    private String age;
    private String appearance;
    private String memberType;
    private boolean checked;
    private int title = -1;  //-1:默认无title;0:群成员;1:群主;3:我关注的人

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public User(int title) {
        this.title = title;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public int getDerail() {
        return derail;
    }

    public void setDerail(int derail) {
        this.derail = derail;
    }

    public int getIdentityState() {
        return identityState;
    }

    public void setIdentityState(int identityState) {
        this.identityState = identityState;
    }

    public String getAge() {
        if (Util.isStrNotNull(age)) {
            return age;
        }
        return "0";
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public int getShowState() {
        return showState;
    }

    public void setShowState(int showState) {
        this.showState = showState;
    }

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }

    public int getImgCount() {
        return imgCount;
    }

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public String getActionTime() {
        return actionTime;
    }

    public void setActionTime(String actionTime) {
        this.actionTime = actionTime;
    }

    public void setHeader(UserHeader header) {
        this.nickname = header.getNickname();
        this.avatar = header.getAvatar();
        this.isvip = header.getIsvip();
        this.viptime = header.getViptime();
        this.vipday = header.getVipday();
    }

    public String getVisittime() {
        return visittime;
    }

    public void setVisittime(String visittime) {
        this.visittime = visittime;
    }

    public String getUvid() {
        return uvid;
    }

    public void setUvid(String uvid) {
        this.uvid = uvid;
    }


    public String getViptime() {
        return viptime;
    }

    public void setViptime(String viptime) {
        this.viptime = viptime;
    }

    public String getVipday() {
        return vipday;
    }

    public void setVipday(String vipday) {
        this.vipday = vipday;
    }

    public String getLongitude() {
        if(TextUtils.isEmpty(longitude)){
            return "0.0";
        }
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        if(TextUtils.isEmpty(latitude)){
            return "0.0";
        }
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    private String token;
    private int userrole;
    private String userid;
    private String ctime;

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public int getIsblacklist() {
        return isblacklist;
    }

    public void setIsblacklist(int isblacklist) {
        this.isblacklist = isblacklist;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getUserrole() {
        return userrole;
    }

    public void setUserrole(int userrole) {
        this.userrole = userrole;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getLevelid() {
        if (levelid.equals("")) {
            return "0";
        }
        return levelid;
    }

    public void setLevelid(String levelid) {
        this.levelid = levelid;
    }


    private String imgs;

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getSex() {
        if (sex == null || sex.equals("男")) {
            return "1";
        }
        if (sex.equals("女") || "0".equals(sex)) {
            return "0";
        }
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public int getAvtwidth() {
        if (avtwidth == null || avtwidth.equals("")) {
            return 0;
        }
        return Integer.valueOf(avtwidth);
    }

    public void setAvtwidth(String avtwidth) {
        this.avtwidth = avtwidth;
    }

    public int getAvtheight() {
        if (avtheight == null || avtheight.equals("")) {
            return 0;
        }
        return Integer.valueOf(avtheight);
    }

    public void setAvtheight(String avtheight) {
        this.avtheight = avtheight;
    }

    public String getImgs() {
        return new Gson().toJson(imglist);
    }

    public void setImgs(String imgs) {
        this.imglist = new Gson().fromJson(imgs,
                new TypeToken<List<ImageBean>>() {
                }.getType());
        this.imgs = imgs;
    }

    private ArrayList<ImageBean> imglist;

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


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsernumber() {
        return usernumber;
    }

    public void setUsernumber(String usernumber) {
        this.usernumber = usernumber;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }


    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }


    public String getConstell() {
        return constell;
    }

    public void setConstell(String constell) {
        this.constell = constell;
    }


    public ArrayList<ImageBean> getImglist() {
        return imglist;
    }

    public void setImglist(ArrayList<ImageBean> imglist) {
        this.imglist = imglist;
        this.imgs = new Gson().toJson(imglist);
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public User(String id, String avatar, String userName, String passWord,
                int sex, String nickname, String mobile, String usernumber, String signature, String qq, String job,
                String hobby, int isvip, String height, String weight,
                String nature, String hometown, String birthday, String education,
                int powernum, String constell, String actionTime, int isOnline, int identity, int imgCount, int showState, int pay, String introduce,
                ArrayList<ImageBean> imglist) {
        super();
        this.id = id;
        this.avatar = avatar;
        this.nickname = nickname;
        this.usernumber = usernumber;
        this.mobile = mobile;
        this.signature = signature;
        this.qq = qq;
        this.job = job;
        this.hobby = hobby;
        this.height = height;
        this.weight = weight;
        this.nature = nature;
        this.hometown = hometown;
        this.birthday = birthday;
        this.education = education;
        this.constell = constell;
        this.imglist = imglist;
        this.actionTime = actionTime;
        this.identity = identity;
        this.imgCount = imgCount;
        this.showState = showState;
        this.pay = pay;
        this.introduce = introduce;
    }


    public int getIsvip() {
        return isvip;
    }

    public void setIsvip(int isvip) {
        this.isvip = isvip;
    }

    public String getConnum() {
        if (connum.equals("")) {
            return "0";
        }
        return connum;
    }

    public void setConnum(String connum) {
        this.connum = connum;
    }

    public String getAttenstatus() {
        return attenstatus;
    }

    public void setAttenstatus(String attenstatus) {
        this.attenstatus = attenstatus;
    }

    public String getPowernum() {
        if (powernum == null || "".equals(powernum)) {
            return "0";
        }
        return powernum;
    }

    public void setPowernum(String powernum) {
        this.powernum = powernum;
    }

    public User() {
        super();

    }

    public User(String id, String avatar, String nickname) {
        this.avatar = avatar;
        this.nickname = nickname;
        this.id = id;
    }

    public User(MessageListBean bean) {
        this.avatar = bean.getAvatar();
        this.nickname = bean.getNickname();
        this.id = bean.getSenduid();
    }

    public String getLevelname() {
        return levelname;
    }

    public void setLevelname(String levelname) {
        this.levelname = levelname;
    }

    public String getDyid() {
        return dyid;
    }

    public void setDyid(String dyid) {
        this.dyid = dyid;
    }

    public String getDycontent() {
        return dycontent;
    }

    public void setDycontent(String dycontent) {
        this.dycontent = dycontent;
    }

    public String getDyimgurl() {
        return dyimgurl;
    }

    public void setDyimgurl(String dyimgurl) {
        this.dyimgurl = dyimgurl;
    }

    public String getDytime() {
        return dytime;
    }

    public void setDytime(String dytime) {
        this.dytime = dytime;
    }

}
