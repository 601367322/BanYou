package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.user.ChatActivity;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.util.StaticFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageAdapter extends ParentsAdapter {

    public static final int LEFT_TEXT = 0, LEFT_VOICE = 1, LEFT_IMG = 2, LEFT_FACE = 3, LEFT_HELPER = 4, LEFT_VIDEO = 9, LEFT_RED_PACKET = 11;
    public static final int RIGHT_TEXT = 5, RIGHT_VOICE = 6, RIGHT_IMG = 7, RIGHT_FACE = 8, RIGHT_VIDEO = 10, RIGHT_RED_PACKET = 12;

    AppClass ac;

    User user;

    Handler hanlder;

    Map<String, User> friends = new HashMap<>();

    ChatActivity.ChatType type;

    @Override
    public int getViewTypeCount() {
        return 13;
    }

    public MessageAdapter(Context c, List list, Handler hanler) {
        super(c, list);
        this.hanlder = hanler;
        ac = (AppClass) c.getApplicationContext();
        user = ac.getUserInfo();
        type = ((ChatActivity) c).type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        MessageBaseHolder holder = null;
        if (convertView == null) {
            holder = onCreateViewHolder(arg2, getItemViewType(position));
            convertView = holder.getView();
            convertView.setTag(holder);
        } else {
            holder = (MessageBaseHolder) convertView.getTag();
        }
        holder.bind(getItem(position), position);
        return holder.getView();
    }

    public MessageBaseHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        MessageBaseHolder holder = null;
        switch (type) {
            case LEFT_TEXT:
                holder = new MessageTextHolder(LayoutInflater.from(c).inflate(R.layout.chatting_item_msg_left_text, viewGroup, false));
                break;
            case LEFT_VOICE:
                holder = new MessageVoiceHolder(LayoutInflater.from(c).inflate(R.layout.chatting_item_msg_left_voice, viewGroup, false));
                break;
            case LEFT_IMG:
                holder = new MessageImgHolder(LayoutInflater.from(c).inflate(R.layout.chatting_item_msg_left_img, viewGroup, false));
                break;
            case LEFT_FACE:
                holder = new MessageGifHolder(LayoutInflater.from(c).inflate(R.layout.chatting_item_msg_left_gif, viewGroup, false));
                break;
            case LEFT_HELPER:
                holder = new MessageHelperHolder(LayoutInflater.from(c).inflate(R.layout.chatting_item_msg_left_helper, viewGroup, false));
                break;
            case RIGHT_TEXT:
                holder = new MessageTextHolder(LayoutInflater.from(c).inflate(R.layout.chatting_item_msg_right_text, viewGroup, false));
                break;
            case RIGHT_VOICE:
                holder = new MessageVoiceHolder(LayoutInflater.from(c).inflate(R.layout.chatting_item_msg_right_voice, viewGroup, false));
                break;
            case RIGHT_IMG:
                holder = new MessageImgHolder(LayoutInflater.from(c).inflate(R.layout.chatting_item_msg_right_img, viewGroup, false));
                break;
            case RIGHT_FACE:
                holder = new MessageGifHolder(LayoutInflater.from(c).inflate(R.layout.chatting_item_msg_right_gif, viewGroup, false));
                break;
            case LEFT_VIDEO:
                holder = new MessageVideoHolder(LayoutInflater.from(c).inflate(R.layout.chatting_item_msg_left_video, viewGroup, false));
                break;
            case RIGHT_VIDEO:
                holder = new MessageVideoHolder(LayoutInflater.from(c).inflate(R.layout.chatting_item_msg_right_video, viewGroup, false));
                break;
            case LEFT_RED_PACKET:
                holder = new MessageRedPacketHolder(LayoutInflater.from(c).inflate(R.layout.chatting_item_msg_left_red_packet, viewGroup, false));
                break;
            case RIGHT_RED_PACKET:
                holder = new MessageRedPacketHolder(LayoutInflater.from(c).inflate(R.layout.chatting_item_msg_right_red_packet, viewGroup, false));
                break;
        }
        holder.setMsgType(type);
        holder.setList(list);
        holder.setHandler(hanlder);
        holder.setUser(user);
        holder.setAdapter(this);
        return holder;
    }

    public DfMessage getItem(int position) {
        return (DfMessage) list.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        DfMessage entity = (DfMessage) getList().get(position);
        if (!entity.getSendUid().equals(user.getId())) {
            switch (entity.getMsgtype()) {
                case DfMessage.TEXT:
                    if (StaticFactory.Manager_ID.equals(entity.getSendUid())) {
                        return LEFT_HELPER;
                    } else {
                        return LEFT_TEXT;
                    }
                case DfMessage.HELPER:
                    return LEFT_HELPER;
                case DfMessage.IMAGE:
                    return LEFT_IMG;
                case DfMessage.VOICE:
                    return LEFT_VOICE;
                case DfMessage.FACE:
                    return LEFT_FACE;
                case DfMessage.VIDEO:
                    return LEFT_VIDEO;
                case DfMessage.PACKET:
                    return LEFT_RED_PACKET;
            }
        } else {
            switch (entity.getMsgtype()) {
                case DfMessage.TEXT:
                    return RIGHT_TEXT;
                case DfMessage.IMAGE:
                    return RIGHT_IMG;
                case DfMessage.VOICE:
                    return RIGHT_VOICE;
                case DfMessage.FACE:
                    return RIGHT_FACE;
                case DfMessage.VIDEO:
                    return RIGHT_VIDEO;
                case DfMessage.PACKET:
                    return RIGHT_RED_PACKET;
            }
        }
        return super.getItemViewType(position);
    }


    public void addNewsItem(DfMessage newsitem) {
        super.addNewsItem(newsitem);
        friends.put(newsitem.getFriend().getId(), newsitem.getFriend());
    }

    public void addFirstItem(DfMessage obj) {
        super.addFirstItem(obj);
        if (!friends.containsKey(obj.getFriend().getId())) {
            friends.put(obj.getFriend().getId(), obj.getFriend());
        }
    }

    public void setFriend(List<User> friend) {
        for (int i = 0; i < friend.size(); i++) {
            friends.put(friend.get(i).getId(), friend.get(i));
        }
    }
}
