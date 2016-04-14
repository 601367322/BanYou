package com.quanliren.quan_one.bean;

import java.io.Serializable;

/**
 * Created by kong on 2016/4/11.
 */
public class VideoB implements Serializable{
    private String videoImg;
    private String video;

    public String getVideoImg() {
        return videoImg;
    }

    public void setVideoImg(String videoImg) {
        this.videoImg = videoImg;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
