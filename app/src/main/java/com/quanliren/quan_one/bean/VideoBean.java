package com.quanliren.quan_one.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

public class VideoBean implements Serializable {

    private String uvId;
    private String content;
    private String userId;
    private String zambia;
    private String cnum;
    private String ctime;
    private int zambiastate;
    private int videoType;
    private VideoB video;

    private List<DateReplyBean> commlist;

    public List<DateReplyBean> getCommlist() {
        return commlist;
    }

    public void setCommlist(List<DateReplyBean> commlist) {
        this.commStr = new Gson().toJson(commlist);
        this.commlist = commlist;
    }

    private String commStr;

    public String getCommStr() {
        return new Gson().toJson(commlist);
    }

    public void setCommStr(String commStr) {
        this.commlist = new Gson().fromJson(commStr,
                new TypeToken<List<DateReplyBean>>() {
                }.getType());
        this.commStr = commStr;
    }

    public String getUvId() {
        return uvId;
    }

    public void setUvId(String uvId) {
        this.uvId = uvId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getZambia() {
        return zambia;
    }

    public void setZambia(String zambia) {
        this.zambia = zambia;
    }

    public String getCnum() {
        return cnum;
    }

    public void setCnum(String cnum) {
        this.cnum = cnum;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public VideoB getVideo() {
        return video;
    }

    public void setVideo(VideoB video) {
        this.video = video;
        this.videoStr = new Gson().toJson(video);
    }

    private String videoStr;
    public String getVideoStr() {
        return new Gson().toJson(commlist);
    }
    public void setVideoStr(String videoStr) {
        this.video = new Gson().fromJson(videoStr,
                new TypeToken<VideoB>() {
                }.getType());
        this.videoStr = videoStr;
    }
    public int getZambiastate() {
        return zambiastate;
    }

    public void setZambiastate(int zambiastate) {
        this.zambiastate = zambiastate;
    }

    public int getVideoType() {
        return videoType;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }
}
