package com.quanliren.quan_one.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.quanliren.quan_one.activity.OpenFromNotifyActivity_;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.user.ChatActivity;
import com.quanliren.quan_one.activity.user.ChatActivity_;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.ChatListBean;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.message.ChatListFragment;
import com.quanliren.quan_one.service.QuanPushService.ConnectionThread;
import com.quanliren.quan_one.util.BitmapCache;
import com.quanliren.quan_one.util.BroadcastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ClientHandlerWord {
    public static final String TAG = "ClientHandlerWord";

    AppClass ac;
    Context c;
    User user;

    public ClientHandlerWord(Context context) {
        ac = (AppClass) context.getApplicationContext();
        this.c = context;
    }

    public void sessionConnected(ConnectionThread session) {
        try {
            JSONObject jo = new JSONObject();
            jo.put(SocketManage.ORDER, SocketManage.ORDER_CONNECT);
            jo.put(SocketManage.TOKEN, ac.getUser().getToken());
            jo.put(SocketManage.DEVICE_TYPE, "0");
            jo.put(SocketManage.DEVICE_ID, ac.cs.getDeviceId());
            session.write(jo.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void messageReceived(ConnectionThread session, Object message)
            throws Exception {
        Log.i(TAG, message.toString());
        user = ac.getUserInfo();
        JSONObject jo = new JSONObject(message.toString());
        String order = jo.getString(SocketManage.ORDER);
        if (order.equals(SocketManage.ORDER_SENDMESSAGE)) {
            getMessage(session, jo);
        } else if (order.equals(SocketManage.ORDER_SENDED)) {
            sended(jo);
        } else if (order.equals(SocketManage.ORDER_OUTLINE)) {
            Intent i = new Intent(BroadcastUtil.ACTION_OUTLINE);
            c.sendBroadcast(i);
        }
    }

    public void sended(JSONObject jo) {
        try {
            String msgid = jo.getString(SocketManage.MESSAGE_ID);
            List<DfMessage> list = DBHelper.dfMessageDao.dao.queryForEq("msgid", msgid);
            if (list.size() > 0) {
                DfMessage m = list.get(0);
                m.setDownload(SocketManage.D_downloaded);
                DBHelper.dfMessageDao.update(m);
                Intent i = new Intent(ChatActivity.CHANGESEND);
                i.putExtra("bean", m);
                c.sendBroadcast(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getMessage(ConnectionThread session, JSONObject jo) {
        final DfMessage defMessage = new Gson().fromJson(
                jo.opt(SocketManage.MESSAGE).toString(), DfMessage.class);

        switch (defMessage.getMsgtype()) {
            case DfMessage.TEXT:
            case DfMessage.HELPER:
            case DfMessage.PACKET:
                defMessage.setDownload(SocketManage.D_downloaded);
                break;
            case DfMessage.IMAGE:
            case DfMessage.FACE:
            case DfMessage.VOICE:
            case DfMessage.VIDEO:
                defMessage.setDownload(SocketManage.D_nodownload);
                break;
            default:
                return;
        }
        try {
            if (jo.opt(SocketManage.MESSAGE_ID) != null) {
                JSONObject jos = new JSONObject();
                jos.put(SocketManage.ORDER, SocketManage.ORDER_SENDED);
                jos.put(SocketManage.MESSAGE_ID, jo.opt(SocketManage.MESSAGE_ID));
                session.write(jos.toString());
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        defMessage.setUserid(user.getId());

        DBHelper.dfMessageDao.create(defMessage);
        ChatListBean cb = new ChatListBean(user, defMessage);
        DBHelper.chatListBeanDao.updateChatList(cb);

        Intent broad = new Intent(ChatActivity.ADDMSG);
        broad.putExtra("bean", defMessage);
        c.sendBroadcast(broad);

        broad = new Intent(ChatListFragment.ADDMSG);
        broad.putExtra("bean", cb);
        c.sendBroadcast(broad);


        String content = null;
        switch (defMessage.getMsgtype()) {
            case DfMessage.HELPER:
                DfMessage.OtherHelperMessage msg = defMessage.getOtherHelperContent();
                switch (msg.getInfoType()) {
                    case DfMessage.OtherHelperMessage.INFO_TYPE_COMMIT:
                        content = msg.getNickname() + c.getResources().getString(R.string.info_type_0);
                        break;
                    case DfMessage.OtherHelperMessage.INFO_TYPE_PAST_DUE:
                        content = c.getResources().getString(R.string.info_type_1);
                        break;
                    case DfMessage.OtherHelperMessage.INFO_TYPE_REPLY_COMMIT:
                        content = msg.getNickname() + c.getResources().getString(R.string.info_type_2);
                        break;
                    case DfMessage.OtherHelperMessage.INFO_TYPE_APPLY_JOIN_GROUP:
                        content = msg.getNickname() + String.format(c.getResources().getString(R.string.apply_your_group), msg.getGroupName());
                        break;
                    case DfMessage.OtherHelperMessage.INFO_TYPE_INVITE_JOIN_GROUP:
                        content = msg.getNickname() + String.format(c.getResources().getString(R.string.invite_your_group), msg.getGroupName());
                        break;
                    default:
                        content = msg.getText();
                        switch (msg.getInfoType()) {
                            case DfMessage.OtherHelperMessage.INFO_TYPE_KICK_OUT:
                            case DfMessage.OtherHelperMessage.INFO_TYPE_JIE_SAN:
                                DBHelper.chatListBeanDao.deleteChatList(user.getId(), msg.getgId());
                                DBHelper.dfMessageDao.deleteAllMessageByFriendId(user.getId(), msg.getgId());
                                Intent chatlist = new Intent(ChatListFragment.REMOVE);
                                chatlist.putExtra("friendId", msg.getgId());
                                c.sendBroadcast(chatlist);
                                break;
                            case DfMessage.OtherHelperMessage.INFO_TYPE_RENZHENG_SUCCESS://认证成功
                                User user = ac.getUserInfo();
                                user.setConfirmType(2);
                                DBHelper.userTableDao.updateUser(user);
                                break;
                            case DfMessage.OtherHelperMessage.INFO_TYPE_RENZHENG_FAIL://认证失败
                                User userf = ac.getUserInfo();
                                userf.setConfirmType(0);
                                DBHelper.userTableDao.updateUser(userf);
                                break;
                        }
                        break;
                }
                break;
            case DfMessage.TEXT:
                content = defMessage.getContent();
                break;
            case DfMessage.IMAGE:
                content = "[图片]";
                break;
            case DfMessage.VOICE:
                content = "[语音]";
                break;
            case DfMessage.FACE:
                content = defMessage.getGifContent().flagName;
                break;
            case DfMessage.VIDEO:
                content = "[视频]";
                break;
            case DfMessage.PACKET:
                content = "[红包]";
                break;
        }

        User friend = new User();
        friend.setId(defMessage.getSendUid());
        friend.setNickname(defMessage.getNickname());
        friend.setAvatar(defMessage.getUserlogo());

        Intent intent = OpenFromNotifyActivity_.intent(c)
                .flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                .clazz(ChatActivity_.class)
                .extra(ChatActivity_.FRIEND_EXTRA, friend)
                .get();

        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        if (!TextUtils.isEmpty(defMessage.getFriendDetail())) {//群聊
            intent.putExtra(ChatActivity_.TYPE_EXTRA, ChatActivity.ChatType.group);
        } else {
            intent.putExtra(ChatActivity_.TYPE_EXTRA, ChatActivity.ChatType.friend);
        }

        notify(defMessage.getSendUid(), defMessage.getNickname(), content, intent);
    }


    int num = 0;

    public void notify(String nick, String title, String content, Intent intent) {
        int notificationId = (nick + title).hashCode();

        PendingIntent viewPendingIntent =

                PendingIntent.getActivity(c, num++, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                c)
                .setTicker(content)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_stat_banyou)
                .setPriority(Notification.PRIORITY_HIGH)
                .setLargeIcon(
                        BitmapCache.getInstance().getBitmap(R.mipmap.ic_launcher, c))
                .setContentTitle(title).setOnlyAlertOnce(true)
                .setContentText(content).setContentIntent(viewPendingIntent);

        if (ac.cs.getZHENOPEN() == 1 && ac.cs.getVIDEOOPEN() == 1) {
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        } else if (ac.cs.getZHENOPEN() == 1) {
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        } else if (ac.cs.getVIDEOOPEN() == 1) {
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(c);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
