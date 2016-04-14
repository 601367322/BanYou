package com.quanliren.quan_one.bean;

import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean;

import java.io.Serializable;

@DatabaseTable(tableName = "ChatListBean")
public class ChatListBean implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(index = true)
    private String userid;
    @DatabaseField(index = true)
    private String friendid;
    @DatabaseField
    private String content;
    @DatabaseField
    private String ctime;
    @DatabaseField
    private String userlogo;
    @DatabaseField
    private String nickname;
    @DatabaseField
    private int type;//0好友 1群组

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private boolean isChoosed;

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean isChoosed) {
        this.isChoosed = isChoosed;
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

    private User friend;
    private int msgCount = 0;

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setContent(DfMessage msg) {
        if (msg != null) {
            switch (msg.getMsgtype()) {
                case DfMessage.HELPER:
                case DfMessage.TEXT:
                    this.content = msg.getContent();
                    break;
                case DfMessage.IMAGE:
                    this.content = "[图片]";
                    break;
                case DfMessage.VOICE:
                    this.content = "[语音]";
                    break;
                case DfMessage.FACE:
                    this.content = msg.getGifContent().flagName;
                    break;
                case DfMessage.VIDEO:
                    this.content = "[视频]";
                    break;
                case DfMessage.PACKET:
                    this.content = "[红包]";
                    break;
                default:
                    this.content = "";
                    break;
            }
        } else {
            this.content = "";
        }
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    public ChatListBean() {
        super();

    }

    public ChatListBean(User user, DfMessage msg) {
        this.userid = user.getId();
        this.friendid = msg.getSendUid().equals(user.getId()) ? msg.getReceiverUid() : msg.getSendUid();
        this.ctime = msg.getCtime();
        this.userlogo = msg.getUserlogo();
        this.nickname = msg.getNickname();
        switch (msg.getMsgtype()) {
            case DfMessage.HELPER:
            case DfMessage.TEXT:
                this.content = msg.getContent();
                break;
            case DfMessage.IMAGE:
                this.content = "[图片]";
                break;
            case DfMessage.VOICE:
                this.content = "[语音]";
                break;
            case DfMessage.FACE:
                this.content = msg.getGifContent().flagName;
                break;
            case DfMessage.VIDEO:
                this.content = "[视频]";
                break;
            case DfMessage.PACKET:
                this.content = "[红包]";
                break;
        }
        if (!TextUtils.isEmpty(msg.getFriendDetail())) {
            this.type = 1;
        }
    }

    public ChatListBean(User user, DfMessage msg, User friend) {
        this.userid = user.getId();
        this.friendid = friend.getId();
        this.ctime = msg.getCtime();
        this.userlogo = friend.getAvatar();
        this.nickname = friend.getNickname();
        switch (msg.getMsgtype()) {
            case DfMessage.HELPER:
            case DfMessage.TEXT:
                this.content = msg.getContent();
                break;
            case DfMessage.IMAGE:
                this.content = "[图片]";
                break;
            case DfMessage.VOICE:
                this.content = "[语音]";
                break;
            case DfMessage.FACE:
                EmoticonActivityListBean.EmoticonZip.EmoticonJsonBean ejb = msg.getGifContent();
                this.content = ejb.getFlagName();
                break;
            case DfMessage.VIDEO:
                this.content = "[视频]";
                break;
            case DfMessage.PACKET:
                this.content = "[红包]";
                break;
        }
        if (!TextUtils.isEmpty(msg.getFriendDetail())) {
            this.type = 1;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFriendid() {
        return friendid;
    }

    public void setFriendid(String friendid) {
        this.friendid = friendid;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }
}
