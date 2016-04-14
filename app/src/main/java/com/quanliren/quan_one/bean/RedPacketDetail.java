package com.quanliren.quan_one.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Shen on 2016/3/3.
 */
public class RedPacketDetail implements Serializable {

    public OtherUser myRed;
    public List<OtherUser> otherRed;
    public int totalCount;
    public int remainCount;
    public double surMoney;
    public int redType;


    public static class OtherUser implements Serializable {
        public int id;
        public double money;
        public String createTime;
        public String avatar;
        public String nickname;
        public String userId;
        public int rId;
        public String content;
    }
}
