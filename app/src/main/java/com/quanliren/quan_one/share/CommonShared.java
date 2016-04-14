package com.quanliren.quan_one.share;

import android.content.Context;

public class CommonShared {
    private SharedDataUtil sp;
    private SharedDataUtil.SharedDataEditor editor;
    private final String LOCATION = "location";// 定位的城市
    private final String AREA = "area";//区域
    private final String CHOSELOCATION = "choselocation";// 选择的城市
    private final String LOCATIONID = "locationid";// 城市id
    private final String CHOSELOCATIONID = "choselocationid";// 城市id
    private final String FASTSTARTICON = "faststarticon4";// 创建快捷方式
    public static final int OPEN = 1;
    public static final int CLOSE = 2;
    public final String VERSIONCODE = "versionCode";
    public final String VERSIONNAME = "versionName";
    public final String CHANNEL = "channel";
    public final String LAT = "lat";
    public final String LNG = "lng";
    public final String ISFIRSTSTART = "isfirststart";// 是否第一次打开软件
    public final String ISFIRSTSEND = "isfirstsend";
    public final String DEVICEID = "deviceid";
    public final String ISFIRSTGROUP = "isfirstgroup";//第一次打开群组

    public final String NEWMSG_OPEN = "newmsgopen";
    public final String VIDEO_OPEN = "videoopen";
    public final String ZHENDONG_OPEN = "zhendongopen";

    public final String FIRST_PUBLISH = "first_publish";
    public final String FIRST_CHOSE_LOCATION = "first_chose_location";
    public final String TRUE_NAME = "true_name";//新增实名认证
    public final String WALLET = "wallet";//我的钱包
    public final String ZHIFUBAO = "zhifubao";//支付宝账号
    public final String POPULAR_VALUE = "popular_value";//我的人气

    public final String ISFIRSTCHAT = "isfirstchat"; //第一次聊天

    public CommonShared(Context context) {
        sp = SharedDataUtil.getInstance(context);
        editor = sp.getSharedDataEditor();
    }

    public void setLocation(String str) {
        editor.putString(LOCATION, str);
        editor.commit();
    }

    public String getLocation() {
        return sp.getString(LOCATION, "北京");
    }

    public void setArea(String str) {
        editor.putString(AREA, str);
        editor.commit();
    }

    public String getArea() {
        return sp.getString(AREA, getLocation() + "");
    }

    public void setChoseLocation(String str) {
        editor.putString(CHOSELOCATION, str);
        editor.commit();
    }

    public String getChoseLocation() {
        return sp.getString(CHOSELOCATION, getLocation());
    }

    public void setDeviceId(String str) {
        editor.putString(DEVICEID, str);
        editor.commit();
    }

    public String getDeviceId() {
        return sp.getString(DEVICEID, "");
    }

    public void setIsFirstStart(String str) {
        editor.putString(ISFIRSTSTART + getVersionCode(), str);
        editor.commit();
    }

    public String getIsFirstStart() {
        return sp.getString(ISFIRSTSTART + getVersionCode(), "");
    }

    public void setIsFirstSend(String str) {
        editor.putString(ISFIRSTSEND + getVersionCode(), str);
        editor.commit();
    }

    public void setLocationID(int str) {
        editor.putInt(LOCATIONID, str);
        editor.commit();
    }

    public String getLocationID() {
        return String.valueOf(sp.getInt(LOCATIONID, 1001));
    }

    public void setChoseLocationID(int str) {
        editor.putInt(CHOSELOCATIONID, str);
        editor.commit();
    }

    public String getChoseLocationID() {
        return String.valueOf(sp.getInt(CHOSELOCATIONID, Integer.valueOf(getLocationID())));
    }

    public void setFastStartIcon(int str) {
        editor.putInt(FASTSTARTICON, str);
        editor.commit();
    }

    public int getFastStartIcon() {
        return sp.getInt(FASTSTARTICON, CLOSE);
    }

    public String getVersionName() {
        return sp.getString(VERSIONNAME, "1.0");
    }

    public void setVersionName(String str) {
        editor.putString(VERSIONNAME, str);
        editor.commit();
    }

    public String getLat() {
        return sp.getString(LAT, "");
    }

    public void setLat(String str) {
        editor.putString(LAT, str);
        editor.commit();
    }

    public String getLng() {
        return sp.getString(LNG, "");
    }

    public void setLng(String str) {
        editor.putString(LNG, str);
        editor.commit();
    }

    public String getChannel() {
        return sp.getString(CHANNEL, "QUANONE");
    }

    public void setChannel(String str) {
        editor.putString(CHANNEL, str);
        editor.commit();
    }

    public int getVersionCode() {
        return sp.getInt(VERSIONCODE, 1);
    }

    public void setVersionCode(int str) {
        editor.putInt(VERSIONCODE, str);
        editor.commit();
    }

    public void setBind(boolean flag) {
        String flagStr = "not";
        if (flag) {
            flagStr = "ok";
        }
        editor.putString("bind_flag", flagStr);
        editor.commit();
    }

    public int getMSGOPEN() {
        return sp.getInt(NEWMSG_OPEN, 1);
    }

    public void setMSGOPEN(int str) {
        editor.putInt(NEWMSG_OPEN, str);
        editor.commit();
    }

    public int getVIDEOOPEN() {
        return sp.getInt(VIDEO_OPEN, 1);
    }

    public void setVIDEOOPEN(int str) {
        editor.putInt(VIDEO_OPEN, str);
        editor.commit();
    }

    public int getZHENOPEN() {
        return sp.getInt(ZHENDONG_OPEN, 1);
    }

    public void setZHENOPEN(int str) {
        editor.putInt(ZHENDONG_OPEN, str);
        editor.commit();
    }

    public int getFIRST_PUBLISH() {
        return sp.getInt(FIRST_PUBLISH, OPEN);
    }

    public void setFIRST_PUBLISH(int str) {
        editor.putInt(FIRST_PUBLISH, str);
        editor.commit();
    }

    public int getFIRST_CHOSE_LOCATION() {
        return sp.getInt(FIRST_CHOSE_LOCATION, OPEN);
    }

    public void setFIRSTCHOSE_LOCATION(int str) {
        editor.putInt(FIRST_CHOSE_LOCATION, str);
        editor.commit();
    }

    public int getFIRST_GROUP() {
        return sp.getInt(ISFIRSTGROUP, OPEN);
    }

    public void setFIRST_GROUP(int str) {
        editor.putInt(ISFIRSTGROUP, str);
        editor.commit();
    }

    public int getTRUE_NAME() {
        return sp.getInt(TRUE_NAME, 0);
    }

    public void setTRUE_NAME(int str) {
        editor.putInt(TRUE_NAME, str);
        editor.commit();
    }

    public int getWALLET() {
        return sp.getInt(WALLET, 0);
    }

    public void setWALLET(int str) {
        editor.putInt(WALLET, str);
        editor.commit();
    }

    public int getPopularValue() {
        return sp.getInt(POPULAR_VALUE, 0);
    }

    public void setPopularValue(int str) {
        editor.putInt(POPULAR_VALUE, str);
        editor.commit();
    }

    public int getFIRST_CHAT() {
        return sp.getInt(ISFIRSTCHAT, OPEN);
    }

    public void setFIRST_CHAT(int str) {
        editor.putInt(ISFIRSTCHAT, str);
        editor.commit();
    }

    public String getZHIFUBAO() {
        return sp.getString(ZHIFUBAO, "");
    }

    public void setZHIFUBAO(String str) {
        editor.putString(ZHIFUBAO, str);
        editor.commit();
    }


}
