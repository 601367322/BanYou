package com.quanliren.quan_one.bean;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by Shen on 2015/12/7.
 */
public class BadgeBean implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String userId;
    @DatabaseField(useGetSet = true)
    private String detail;

    private Badge bean = new Badge();


    public Badge getBean() {
        return bean;
    }

    public void setBean(Badge bean) {
        this.bean = bean;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDetail() {
        if (this.bean != null) {
            return new Gson().toJson(bean);
        }
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
        if (!TextUtils.isEmpty(detail)) {
            this.bean = new Gson().fromJson(detail, new TypeToken<Badge>() {
            }.getType());
        }
    }

    public static class Badge implements Serializable{

        private boolean dateBadge;
        private boolean funsBadge;
        private boolean emotionBadge;
        private boolean groupBadge;

        public boolean isGroupBadge() {
            return groupBadge;
        }

        public void setGroupBadge(boolean groupBadge) {
            this.groupBadge = groupBadge;
        }

        public boolean isDateBadge() {
            return dateBadge;
        }

        public void setDateBadge(boolean dateBadge) {
            this.dateBadge = dateBadge;
        }

        public boolean isFunsBadge() {
            return funsBadge;
        }

        public void setFunsBadge(boolean funsBadge) {
            this.funsBadge = funsBadge;
        }

        public boolean isEmotionBadge() {
            return emotionBadge;
        }

        public void setEmotionBadge(boolean emotionBadge) {
            this.emotionBadge = emotionBadge;
        }
    }
}
