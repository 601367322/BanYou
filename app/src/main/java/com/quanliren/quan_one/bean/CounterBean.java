package com.quanliren.quan_one.bean;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Shen on 2015/12/7.
 */
@DatabaseTable(tableName = "CounterBean")
public class CounterBean implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String userId;
    @DatabaseField(useGetSet = true)
    private String detail;

    private Counter bean;

    public CounterBean(String userId, Counter bean) {
        this.userId = userId;
        this.bean = bean;
    }

    public CounterBean() {
    }

    public Counter getBean() {
        return bean;
    }

    public void setBean(Counter bean) {
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
            this.bean = new Gson().fromJson(detail, new TypeToken<Counter>() {
            }.getType());
        }
    }

    public static class Counter implements Serializable {

        private String vcnt = "0";//访客记录
        private String pcnt = "0";//我发布的
        private String ccnt = "0";//我收藏的
        private String attdycnt = "0";//关注人新约会
        private String funcnt = "0";//粉丝红点
        private String phizcnt = "0";//表情总数
        private String groupcnt = "0";//群组数量

        public String getVcnt() {
            return vcnt;
        }

        public void setVcnt(String vcnt) {
            this.vcnt = vcnt;
        }

        public String getPcnt() {
            return pcnt;
        }

        public void setPcnt(String pcnt) {
            this.pcnt = pcnt;
        }

        public String getCcnt() {
            return ccnt;
        }

        public void setCcnt(String ccnt) {
            this.ccnt = ccnt;
        }

        public String getAttdycnt() {
            return attdycnt;
        }

        public void setAttdycnt(String attdycnt) {
            this.attdycnt = attdycnt;
        }

        public String getFuncnt() {
            return funcnt;
        }

        public void setFuncnt(String funcnt) {
            this.funcnt = funcnt;
        }

        public String getPhizcnt() {
            return phizcnt;
        }

        public void setPhizcnt(String phizcnt) {
            this.phizcnt = phizcnt;
        }

        public String getGroupcnt() {
            return groupcnt;
        }

        public void setGroupcnt(String groupcnt) {
            this.groupcnt = groupcnt;
        }
    }
}
