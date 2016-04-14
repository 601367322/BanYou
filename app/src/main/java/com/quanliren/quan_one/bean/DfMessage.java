package com.quanliren.quan_one.bean;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean.EmoticonZip.EmoticonJsonBean;
import com.quanliren.quan_one.service.SocketManage;
import com.quanliren.quan_one.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "DfMessage")
public class DfMessage implements Serializable {

    public static final String TABLENAME = "DfMessage";

    @DatabaseField(generatedId = true)
    private int id;//数据库唯一id
    @DatabaseField(index = true)
    private String msgid;//消息唯一id
    @DatabaseField(index = true)
    private String userid;//这台手机上的使用者
    @DatabaseField(index = true)
    private String receiverUid;//接收者的id
    @DatabaseField(index = true)
    private String sendUid;//发送者的id
    @DatabaseField
    private String content;//发送内容
    @DatabaseField(index = true)
    private int isRead = 0;// 是否已读
    @DatabaseField(index = true)
    private String ctime;// 信息发送时间
    private boolean showTime = false;// 是否显示信息
    @DatabaseField(index = true)
    private int msgtype = 0;// 0、文字 1、图片 2、语音
    @DatabaseField(index = true)
    private int download = 0;// 0 未下载 1已下载
    @DatabaseField
    private int timel = 0;// 语音长度
    @DatabaseField
    private String userlogo;//发送者的头像
    @DatabaseField
    private String nickname;//发送者的名字

    @DatabaseField(useGetSet = true)
    private String friendDetail;//群消息的好友信息

    private User friend;

    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    public static final int VOICE = 2;
    public static final int FACE = 5;
    public static final int VIDEO = 6;
    public static final int PACKET = 7;
    public static final int HELPER = 8;

    public String getFriendDetail() {
        if (friend != null) {
            return new Gson().toJson(friend);
        }
        return friendDetail;
    }

    public void setFriendDetail(String friendDetail) {
        friend = new Gson().fromJson(friendDetail, new TypeToken<User>() {
        }.getType());
        this.friendDetail = friendDetail;
    }

    public int getDownload() {
        return download;
    }

    public void setDownload(int download) {
        this.download = download;
    }

    private boolean playing;

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public EmoticonJsonBean getGifContent() {
        return new Gson().fromJson(content, new TypeToken<EmoticonJsonBean>() {
        }.getType());
    }

    public User getFriend() {
        if (friend != null) {
            return friend;
        } else {
            if (!TextUtils.isEmpty(friendDetail)) {
                setFriendDetail(friendDetail);
            } else {
                friend = new User();
                friend.setNickname(this.nickname);
                friend.setAvatar(this.userlogo);
                friend.setId(this.sendUid);
            }
        }
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public String getUserlogo() {
        return userlogo;
    }

    public void setUserlogo(String userlogo) {
        this.userlogo = userlogo;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }


    public void setMsgtype(Integer msgtype) {
        this.msgtype = msgtype;
    }

    public int getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(int msgtype) {
        this.msgtype = msgtype;
    }

    public DfMessage() {
        super();

    }

    public OtherHelperMessage getOtherHelperContent() {
        try {
            return new Gson().fromJson(content, new TypeToken<OtherHelperMessage>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setOtherHelperContent(OtherHelperMessage msg) {
        this.content = new Gson().toJson(msg);
    }

    public static class OtherHelperMessage implements Serializable {

        /**
         * 通知类型 0:评论/回复(留言板) 1:约会过期
         */
        public static final int INFO_TYPE_COMMIT = 0;
        public static final int INFO_TYPE_PAST_DUE = 1;
        public static final int INFO_TYPE_REPLY_COMMIT = 2;//约会回复
        public static final int INFO_TYPE_APPLY_JOIN_GROUP = 3;//申请入群
        public static final int INFO_TYPE_INVITE_JOIN_GROUP = 4;//群主的邀请
        public static final int INFO_TYPE_AGREE_APPLY = 5;//同意了您的申请
        public static final int INFO_TYPE_KICK_OUT = 7;//被踢出
        public static final int INFO_TYPE_JIE_SAN = 8;//解散
        public static final int INFO_TYPE_VIP_OUT_TIME = 9;//会员到期
        public static final int INFO_TYPE_RENZHENG = 11;//认证
        public static final int INFO_TYPE_JUBAO = 12;//举报
        public static final int INFO_TYPE_RENZHENG_SUCCESS = 13;//认证成功
        public static final int INFO_TYPE_RENZHENG_FAIL = 14;//认证失败
        public static final int INFO_TYPE_TIXIAN = 15;//提现消息
        public static final int INFO_TYPE_CARE = 16;//关注提醒
        public static final int INFO_TYPE_INVITE = 17;//邀请奖励
        public static final int INFO_TYPE_RED_RUNTIME = 18;//红包超时

        private int infoType;
        private String dyid;//约会id
        private String grId;//申请记录id
        private String uId;//用户id
        private String gId;//组ID
        private int dtype;
        private String text;
        private String nickname;
        private String groupName;
        private int isAgree;//0未作处理 1已同意 2已拒绝
        private int redId;


        public int getIsAgree() {
            return isAgree;
        }

        public void setIsAgree(int isAgree) {
            this.isAgree = isAgree;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public OtherHelperMessage() {
            super();

        }

        public String getgId() {
            return gId;
        }

        public void setgId(String gId) {
            this.gId = gId;
        }

        public String getuId() {
            return uId;
        }

        public void setuId(String uId) {
            this.uId = uId;
        }

        public String getGrId() {
            return grId;
        }

        public void setGrId(String grId) {
            this.grId = grId;
        }

        public int getInfoType() {
            return infoType;
        }

        public void setInfoType(int infoType) {
            this.infoType = infoType;
        }

        public int getDtype() {
            return dtype;
        }

        public void setDtype(int dtype) {
            this.dtype = dtype;
        }

        public String getDyid() {
            return dyid;
        }

        public void setDyid(String dyid) {
            this.dyid = dyid;
        }
    }

    public static JSONObject getMessage(User user, String content, User friend,
                                        int msgType, int timel) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("content", content);
            msg.put("ctime", Util.fmtDateTime.format(new Date()));//创建时间
            msg.put("receiverUid", friend.getId());
            msg.put("userid", user.getId());
            msg.put("timel", timel);//语音时间
            msg.put("userlogo", user.getAvatar());
            msg.put("nickname", user.getNickname());
            msg.put("sendUid", user.getId());
            msg.put("msgtype", msgType);
            msg.put(SocketManage.MESSAGE_ID, new Date().getTime());
            return msg;
        } catch (JSONException e) {

            e.printStackTrace();
        }
        return null;
    }

    public DfMessage(int id, String msgid, String userid, String receiverUid,
                     String sendUid, String content, int isRead, String ctime,
                     boolean showTime, int msgtype, int download, int timel,
                     String userlogo, String nickname) {
        super();
        this.id = id;
        this.msgid = msgid;
        this.userid = userid;
        this.receiverUid = receiverUid;
        this.sendUid = sendUid;
        this.content = content;
        this.isRead = isRead;
        this.ctime = ctime;
        this.showTime = showTime;
        this.msgtype = msgtype;
        this.download = download;
        this.timel = timel;
        this.userlogo = userlogo;
        this.nickname = nickname;
    }

    public int getTimel() {
        return timel;
    }

    public void setTimel(int timel) {
        this.timel = timel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getSendUid() {
        return sendUid;
    }

    public void setSendUid(String sendUid) {
        this.sendUid = sendUid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public VideoBean getVideoBean() {
        return new Gson().fromJson(content, new TypeToken<VideoBean>() {
        }.getType());
    }

    public RedPacket getRedPacket() {
        return new Gson().fromJson(content, new TypeToken<RedPacket>() {
        }.getType());
    }

    public static class VideoBean implements Serializable {
        public String path;
        public String thumb;

        public VideoBean(String path, String thumb) {
            this.path = path;
            this.thumb = thumb;
        }

        public VideoBean() {
        }
    }

    public static class RedPacket implements Serializable {
        public int rId;
        public String content;
    }
}
