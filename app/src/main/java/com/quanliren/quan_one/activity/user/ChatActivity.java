package com.quanliren.quan_one.activity.user;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.date.DateDetailActivity_;
import com.quanliren.quan_one.activity.group.GroupDetailActivity_;
import com.quanliren.quan_one.activity.image.ImageBrowserActivity_;
import com.quanliren.quan_one.activity.shop.ShopVipDetailActivity_;
import com.quanliren.quan_one.adapter.MessageAdapter;
import com.quanliren.quan_one.bean.ChatListBean;
import com.quanliren.quan_one.bean.DateBean;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.bean.MessageList;
import com.quanliren.quan_one.bean.MessageListBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean.EmoticonZip.EmoticonJsonBean;
import com.quanliren.quan_one.custom.emoji.XhsEmoticonsKeyBoardBar;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.custom.AddPicFragment;
import com.quanliren.quan_one.fragment.message.ChatListFragment;
import com.quanliren.quan_one.pull.swipe.SwipeRefreshLayout;
import com.quanliren.quan_one.radio.AmrEncodSender;
import com.quanliren.quan_one.radio.AmrEngine;
import com.quanliren.quan_one.radio.MicRealTimeListener;
import com.quanliren.quan_one.service.SocketManage;
import com.quanliren.quan_one.util.BroadcastUtil;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

@EActivity
public class ChatActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        OnTouchListener,
        SensorEventListener,
        XhsEmoticonsKeyBoardBar.KeyBoardBarViewListener {
    /**
     * 动态添加消息广播
     */
    public static final String ADDMSG = "com.quanliren.quan_one.ChatActivity.ADDMSG";
    /**
     * 改变发送状态广播
     */
    public static final String CHANGESEND = "com.quanliren.quan_one.ChatActivity.CHANGESEND";

    public static final int HANDLER_COPY = 3, HANDLER_LONGPRESS = 4, HANDLER_RESEND = 6;

    public enum ChatType {
        friend,
        group
    }


    /**
     * 群聊或者单聊
     */
    @Extra
    public ChatType type = ChatType.friend;
    /**
     * 监听键盘
     */
    @ViewById(R.id.resize)
    XhsEmoticonsKeyBoardBar resize;
    /**
     * 按住说话按钮
     */
    @ViewById(R.id.chat_radio_btn)
    TextView chat_radio_btn;
    /**
     * 录音面板
     */
    @ViewById(R.id.chat_radio_panel)
    View chat_radio_panel;
    /**
     * 消息列表
     */
    @ViewById(R.id.list)
    ListView listview;
    /**
     * 下拉刷新
     */
    @ViewById(R.id.swipe_layout)
    SwipeRefreshLayout swipe_layout;
    /**
     * 声音大小
     */
    @ViewById(R.id.voicesize)
    ImageView voicesize;
    /**
     * 正在加载声音录制
     */
    @ViewById(R.id.loading)
    View loading;
    /**
     * 删除声音
     */
    @ViewById(R.id.delete)
    ImageView delete;
    /**
     * 下方主体
     */
    @ViewById(R.id.layout_bottom)
    View layout_bottom;
    /**
     * 聊天更多
     */
    @ViewById(R.id.chat_add_btn)
    View chat_add_btn;
    /**
     * 数据适配器
     */
    MessageAdapter adapter;
    /**
     * 好友或群组
     */
    @Extra
    public User friend;
    /**
     * 自己
     */
    User user;
    /**
     * 选择的文件路径
     */
    String filename;
    /**
     * 消息通知
     */
    @SystemService
    public NotificationManager nm;
    /**
     * 听筒
     */
    @SystemService
    AudioManager audioManager;
    @SystemService
    SensorManager mSensorManager;
    Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        //设置下拉刷新
        swipe_layout.setOnRefreshListener(this);
        //删掉通知栏
        if (friend != null) {
            nm.cancel((friend.getId() + friend.getNickname()).hashCode());
        }
        //得到自己
        user = ac.getUserInfo();
        //判断是否是客服，隐藏回复框
        if (StaticFactory.Manager_ID.equals(friend.getId())) {
            layout_bottom.setVisibility(View.GONE);
        } else {
            layout_bottom.setVisibility(View.VISIBLE);
            resize.setOnKeyBoardBarViewListener(this);
        }
        //初始化适配器
        adapter = new MessageAdapter(this, new ArrayList<MessageListBean>(), itemHandler);

        listview.setAdapter(adapter);
        listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);


        JSONArray array = new JSONArray();
        array.put(friend.getId());
        //刷新好友信息
        switch (type) {
            case friend:
                updateFriendInfo(array);
                break;
            case group:
                updateGroupInfo(array);
                break;
        }

        //设置标题
        setTitleTxt(friend.getNickname());

        //设置录音按钮的监听
        chat_radio_btn.setOnTouchListener(this);

        //设置右上角按钮
        switch (type) {
            case group:
                setTitleRightIcon(R.drawable.other_group_info_icon);
                break;
            case friend:
                setTitleRightIcon(R.drawable.other_info_icon);
                break;
        }

        //设置listview的触摸
        listview.setOnTouchListener(this);

        //设置传感器
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        //检查socket心跳
        if (user != null) {
            Util.setAlarmTime(this, System.currentTimeMillis(), BroadcastUtil.ACTION_CHECKCONNECT, BroadcastUtil.CHECKCONNECT);
        }

        refresh();

        EventBus.getDefault().register(this);
    }

    /**
     * 刷新数据
     */
    @UiThread(delay = 200l)
    public void refresh() {
        swipe_layout.setRefreshing(true);
    }

    /**
     * 语音播放event
     *
     * @param msg
     */
    public void onEvent(DfMessage msg) {
        if (adapter != null) {
            List<DfMessage> msgs = adapter.getList();
            for (int i = 0; i < msgs.size(); i++) {
                if (msgs.get(i).getMsgid().equals(msg.getMsgid())) {
                    msgs.get(i).setPlaying(msg.isPlaying());
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        if (friend == null) {
            return;
        }
        if (adapter.getCount() != 0) {
            listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        }

        getMsgListInit();
    }

    @Background
    public void getMsgListInit() {
        try {
            //获取上面一条消息id，防止重复加载
            int maxid = -1;
            if (adapter.getList().size() > 0) {
                maxid = adapter.getItem(0).getId();
            }
            //获取最近的15条消息
            final List<DfMessage> list = DBHelper.dfMessageDao.getMsgList(user.getId(), friend.getId(), maxid);
            //下载队列
            final List<DfMessage> downlist = new ArrayList<>();
            //未读消息队列
            List<Integer> ids = new ArrayList<Integer>();

            for (DfMessage dfMessage : list) {
                //将未读变为已读
                if (dfMessage.getIsRead() == 0) {
                    ids.add(dfMessage.getId());
                    dfMessage.setIsRead(1);
                }
                //如果是语音消息，并且没有被下载，添加到下载队列
                if (dfMessage.getMsgtype() == DfMessage.VOICE
                        && (dfMessage.getDownload() == SocketManage.D_nodownload)) {
                    downlist.add(dfMessage);
                }
            }

            //更新未读消息变为已读
            if (ids.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (Integer integer : ids) {
                    sb.append(integer + ",");
                }
                sb.deleteCharAt(sb.length() - 1);
                DBHelper.dfMessageDao.updateMsgReaded(sb.toString());
            }
            //设置消息是否显示时间，如果两条消息间隔1分钟
            if (list.size() > 0) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    if (i < list.size() - 1 && i > 0) {
                        if (Util.fmtDateTime.parse(
                                list.get(i).getCtime()).getTime() - 60 * 1000 > Util.fmtDateTime
                                .parse(list.get(i - 1).getCtime())
                                .getTime()) {
                            list.get(i).setShowTime(true);
                        }
                    } else {
                        list.get(i).setShowTime(true);
                    }
                }
            }
            //发送广播，更新未读消息数量
            Intent broad = new Intent(
                    ChatListFragment.REFEREMSGCOUNT);
            broad.putExtra("id", friend.getId());
            sendBroadcast(broad);

            getMsgListInitUI(list, downlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新列表
     *
     * @param list
     * @param downlist
     */
    @UiThread
    public void getMsgListInitUI(List<DfMessage> list, List<DfMessage> downlist) {
        for (DfMessage dfMessage : list) {
            adapter.addFirstItem(dfMessage);
        }
        adapter.notifyDataSetChanged();
        swipe_layout.setRefreshing(false);

        if (list.size() > 0) {
            listview.setSelection(list.size() - 1);
        }
        //下载
        for (DfMessage dfMessage : downlist) {
            ChatDownLoadManager.getInstance(this).down(dfMessage);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AddPicFragment.Album:
                if (data == null) {
                    return;
                }
                ContentResolver resolver = getContentResolver();
                Uri imgUri = data.getData();
                try {
                    Cursor cursor = resolver.query(imgUri, null, null, null, null);
                    cursor.moveToFirst();
                    filename = cursor.getString(1);
                    ImageUtil.downsize(
                            filename,
                            filename = StaticFactory.APKCardPath
                                    + new Date().getTime(), this);
                    sendFile(new File(filename), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case AddPicFragment.Camera:
                if (filename != null) {
                    File fi = new File(filename);
                    if (fi != null && fi.exists()) {
                        ImageUtil.downsize(filename, filename, this);
                        sendFile(fi, 1);
                    }
                    fi = null;
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 监听广播刷新
     *
     * @param i
     */
    @Receiver(actions = {ADDMSG, CHANGESEND})
    public void receiver(Intent i) {
        receiverUI(i);
    }


    @UiThread
    void receiverUI(Intent i) {
        try {
            String action = i.getAction();
            if (action.equals(ADDMSG)) {
                if (i.getExtras().containsKey("bean")) {
                    DfMessage bean = (DfMessage) i.getExtras().getSerializable("bean");

                    if (bean.getSendUid().equals(friend.getId())) {
                        bean.setIsRead(1);
                        DBHelper.dfMessageDao.update(bean);

                        Intent broad = new Intent(ChatListFragment.REFEREMSGCOUNT);
                        broad.putExtra("id", friend.getId());
                        sendBroadcast(broad);

                        scrollToLast(bean);

                        switch (bean.getMsgtype()) {
                            case DfMessage.VOICE:
                                ChatDownLoadManager.getInstance(this).down(bean);
                                break;
                        }
                        cancelNm();
                    }
                }
            } else if (action.equals(CHANGESEND)) {
                DfMessage bean = (DfMessage) i.getExtras().getSerializable("bean");
                List<DfMessage> list = adapter.getList();
                for (DfMessage dfMessage : list) {
                    if (dfMessage.getId() == bean.getId()) {
                        dfMessage.setDownload(bean.getDownload());
                    }
                }
                if (i.getExtras().containsKey(SocketManage.TYPE)) {
                    int type = i.getIntExtra(SocketManage.TYPE, 0);
                    switch (type) {
                        case SocketManage.ERROR_TYPE_MORE:
                            showMoreMsg();
                            break;
                    }

                }
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    void scrollToLast(DfMessage bean) {

        listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        if (bean != null) {
            adapter.addNewsItem(bean);
            adapter.notifyDataSetChanged();
        }

        if (adapter.getList().size() > 0) {
            listview.smoothScrollToPosition(adapter.getList().size() - 1);
        }
    }

    @UiThread(delay = 1000)
    public void cancelNm() {
        nm.cancel((friend.getId() + friend.getNickname()).hashCode());
    }

    public void showMoreMsg() {
        new AlertDialog.Builder(ChatActivity.this)
                .setMessage("您今天已经向20位陌生人打招呼了，如果想继续与其他陌生人打招呼，请立刻成为会员吧~")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                ShopVipDetailActivity_.intent(mContext).start();
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        }).create().show();
    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public void sendFile(File file, int msgtype) {
        try {
            JSONObject msg = DfMessage.getMessage(user, file.getPath(),
                    friend, msgtype, (int) recodeTime);

            switch (type) {
                case group:
                    JSONObject my = new JSONObject();
                    my.put("avatar", user.getAvatar());
                    my.put("nickname", user.getNickname());
                    my.put("id", user.getId());
                    msg.put("friend", my);
                    break;
            }

            JSONObject jo = new JSONObject();
            jo.put(SocketManage.ORDER, SocketManage.ORDER_SENDMESSAGE);
            jo.put(SocketManage.SEND_USER_ID, user.getId());
            jo.put(SocketManage.RECEIVER_USER_ID, friend.getId());
            jo.put(SocketManage.MESSAGE, msg);
            jo.put(SocketManage.MESSAGE_ID,
                    msg.getString(SocketManage.MESSAGE_ID));

            RequestParams ap = getAjaxParams();
            ap.put("file", file);
            ap.put("msgattr", jo.toString());
            ap.put("devicetype", "0");
            ap.put("pid", ac.cs.getVersionCode() + "");

            String url = URL.SENDFILE;

            switch (type) {
                case group:
                    url = URL.SENDGROUPFILE;
                    break;
            }

            ac.finalHttp.post(
                    url,
                    ap,
                    new sendfileCallBack((DfMessage) new Gson().fromJson(
                            msg.toString(), new TypeToken<DfMessage>() {
                            }.getType())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class sendfileCallBack extends MyJsonHttpResponseHandler {
        DfMessage msg;

        public sendfileCallBack(DfMessage msg) {
            this.msg = msg;
        }

        @Override
        public void onSuccessRetCode(JSONObject jo) throws Throwable {
            JSONObject response = new JSONObject(jo.getString(URL.RESPONSE));
            if (response.has(SocketManage.TYPE)) {
                int type = response.getInt(SocketManage.TYPE);
                if (type == SocketManage.ERROR_TYPE_MORE) {
                    msg.setDownload(SocketManage.D_destroy);
                } else if (type == SocketManage.ERROR_TYPE_BLACK) {
                    msg.setDownload(SocketManage.D_fail);
                }

                DBHelper.dfMessageDao.update(msg);
                Intent i = new Intent(ChatActivity.CHANGESEND);
                i.putExtra("bean", msg);
                i.putExtra("type", response.getInt(SocketManage.TYPE));
                sendBroadcast(i);
            } else {
                msg.setDownload(SocketManage.D_downloaded);
                DBHelper.dfMessageDao.update(msg);
                Intent i = new Intent(ChatActivity.CHANGESEND);
                i.putExtra("bean", msg);
                sendBroadcast(i);
            }
        }

        @Override
        public void onStart() {
            msg.setDownload(SocketManage.D_downloading);

            ChatListBean cb = new ChatListBean(user, msg, friend);

            DBHelper.dfMessageDao.saveMessage(msg, cb);

            Intent broad = new Intent(ChatListFragment.ADDMSG);
            broad.putExtra("bean", cb);
            sendBroadcast(broad);

            listview.setSelection(adapter.getCount() - 1);
            listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            adapter.addNewsItem(msg);
            adapter.notifyDataSetChanged();

            chat_radio_btn.setEnabled(true);
        }


        @Override
        public void onFailRetCode(JSONObject jo) {
            super.onFailRetCode(jo);
            fail();
        }

        @Override
        public void onFailure() {
            super.onFailure();
            fail();
        }

        public void fail() {
            try {
                msg.setDownload(SocketManage.D_destroy);
                DBHelper.dfMessageDao.update(msg);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }


    public void rightClick(View v) {
        if (type == ChatType.group) {
            GroupBean group = new GroupBean();
            group.setId(friend.getId());
            group.setNickname(friend.getNickname());
            GroupDetailActivity_.intent(this).bean(group).start();
        } else {
            Util.startUserInfoActivity(this, friend);
        }
    }


    public void sendTextThread(String t) {
        try {
            JSONObject msg = DfMessage.getMessage(user, t, friend, 0,
                    (int) recodeTime);
            sendMsg(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendGifThread(EmoticonJsonBean bean) {

        try {
            Gson gson = new Gson();
            JSONObject msg = DfMessage.getMessage(user,
                    gson.toJson(bean), friend, 5, (int) recodeTime);
            sendMsg(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(JSONObject msg) {

        try {
            switch (type) {
                case group:
                    JSONObject my = new JSONObject();
                    my.put("avatar", user.getAvatar());
                    my.put("nickname", user.getNickname());
                    my.put("id", user.getId());
                    msg.put("friend", my);
                    break;
            }

            final JSONObject jo = new JSONObject();
            jo.put(SocketManage.ORDER, SocketManage.ORDER_SENDMESSAGE);
            jo.put(SocketManage.SEND_USER_ID, user.getId());
            jo.put(SocketManage.RECEIVER_USER_ID, friend.getId());
            jo.put(SocketManage.MESSAGE, msg);
            jo.put(SocketManage.MESSAGE_ID,
                    msg.getString(SocketManage.MESSAGE_ID));

            recodeTime = 0.0f;
            final DfMessage msgs = new Gson().fromJson(msg.toString(),
                    new TypeToken<DfMessage>() {
                    }.getType());

            msgs.setUserid(user.getId());

            msgs.setDownload(SocketManage.D_downloading);

            ChatListBean cb = new ChatListBean(user, msgs, friend);

            DBHelper.dfMessageDao.saveMessage(msgs, cb);

            Intent broad = new Intent(ChatListFragment.ADDMSG);
            broad.putExtra("bean", cb);
            sendBroadcast(broad);

            String url = "";
            switch (type) {
                case group:
                    url = URL.SEND_GROUP_MESSAGE;
                    break;
                case friend:
                    url = URL.SEND_MESSAGE;
                    break;
            }
            ac.finalHttp.post(url, getAjaxParams("msgattr", jo.toString()), new MyJsonHttpResponseHandler() {

                @Override
                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                    JSONObject response = new JSONObject(jo.getString(URL.RESPONSE));
                    if (response.has(SocketManage.ORDER)) {
                        String cmd = response.optString(SocketManage.ORDER);
                        if (cmd.equals(SocketManage.ORDER_SENDED)) {
                            try {
                                msgs.setDownload(SocketManage.D_downloaded);
                                DBHelper.dfMessageDao.update(msgs);
                                Intent i = new Intent(ChatActivity.CHANGESEND);
                                i.putExtra("bean", msgs);
                                receiver(i);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (cmd.equals(SocketManage.ORDER_SENDERROR)) {
                            try {
                                int type = response.getInt(SocketManage.TYPE);
                                if (type == SocketManage.ERROR_TYPE_MORE) {
                                    msgs.setDownload(SocketManage.D_destroy);
                                } else if (type == SocketManage.ERROR_TYPE_BLACK) {
                                    msgs.setDownload(SocketManage.D_fail);
                                }
                                DBHelper.dfMessageDao.update(msgs);
                                Intent i = new Intent(ChatActivity.CHANGESEND);
                                i.putExtra("bean", msgs);
                                i.putExtra("type", response.getInt(SocketManage.TYPE));
                                receiver(i);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onStart() {
                    listview.setSelection(adapter.getCount() - 1);
                    listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                    adapter.addNewsItem(msgs);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailRetCode(JSONObject jo) {
                    super.onFailRetCode(jo);
                    onFailure();
                }

                @Override
                public void onFailure() {
                    try {
                        msgs.setDownload(SocketManage.D_destroy);
                        DBHelper.dfMessageDao.update(msgs);
                        Intent i = new Intent(ChatActivity.CHANGESEND);
                        i.putExtra("bean", msgs);
                        receiver(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Handler imgHandle = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (AmrEngine.getSingleEngine().isRecordRunning()) {
                        AmrEngine.getSingleEngine().stopRecording();
                        hideall();
                        voiceValue = 0.0;
                        chat_radio_btn.setText(R.string.normaltalk);
                        chat_radio_btn.setEnabled(false);
                        if (recodeTime < MIX_TIME) {
                            showCustomToast("太短了");
                            File o = new File(filename);
                            if (o.exists()) {
                                o.delete();
                            }
                            new Handler().postDelayed(new Runnable() {

                                public void run() {
                                    hideall();
                                    chat_radio_btn.setEnabled(true);
                                }
                            }, 1000);
                        } else {
                            sendFile(new File(filename), 2);
                        }
                    }
                    break;
                case 1:
                    setDialogImage();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.list:
                closeInput();
                listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
                resize.hideAutoView();
                break;
            case R.id.chat_radio_btn:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!AmrEngine.getSingleEngine().isRecordRunning()) {

                            showVoiceLoading();

                            chat_radio_panel.setVisibility(View.VISIBLE);
                            chat_radio_btn.setText(R.string.pressedtalk);

                            File file = new File(StaticFactory.APKCardPathChat);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            AmrEncodSender sender = new AmrEncodSender(
                                    filename = (StaticFactory.APKCardPathChat + String.valueOf((String
                                            .valueOf(new Date().getTime()) + ".amr")
                                            .hashCode())), new MicRealTimeListener() {

                                @Override
                                public void getMicRealTimeSize(double size,
                                                               long time) {
                                    voiceValue = size;
                                }
                            });

                            AmrEngine.getSingleEngine().startRecording();
                            new Thread(sender).start();
                            showVoiceStart();
                            mythread();
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        if (AmrEngine.getSingleEngine().isRecordRunning()) {
                            AmrEngine.getSingleEngine().stopRecording();
                            chat_radio_panel.setVisibility(View.GONE);
                            chat_radio_btn.setText(R.string.normaltalk);

                            voiceValue = 0.0;
                            chat_radio_btn.setEnabled(false);
                            if (recodeTime < MIX_TIME) {
                                showCustomToast("太短了");

                                new Handler().postDelayed(new Runnable() {

                                    public void run() {
                                        hideall();
                                        chat_radio_btn.setEnabled(true);
                                    }
                                }, 1000);
                            } else {
                                showVoiceLoading();
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        hideall();
                                        if (isCanle) {
                                            File o = new File(filename);
                                            if (o.exists()) {
                                                o.delete();
                                            }
                                            chat_radio_btn.setEnabled(true);
                                        } else {
                                            sendFile(new File(filename), 2);
                                        }
                                    }
                                }, 500);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        chat_radio_btn.getLocationOnScreen(location);
                        chat_radio_panel.getLocationOnScreen(location1);
                        if (event.getRawY() < location[1]) {
                            showVoiceCancle();
                            isCanle = true;
                            if (event.getRawY() <= location1[1]
                                    + ImageUtil.dip2px(this, 150)
                                    && event.getRawY() >= location1[1]
                                    && event.getRawX() <= location1[0]
                                    + ImageUtil.dip2px(this, 150)
                                    && event.getRawX() >= location1[0]) {
                                delete.setSelected(true);
                            } else {
                                delete.setSelected(false);
                            }
                        } else {
                            showVoiceStart();
                            isCanle = false;
                        }
                        break;
                }
                break;
        }
        return false;
    }

    int[] location = new int[2];
    int[] location1 = new int[2];
    boolean isCanle = false;
    private MediaPlayer mediaPlayer;
    // Button player;
    private Thread recordThread;

    private static int MAX_TIME = 60; // 最长录制时间，单位秒，0为无时间限制
    private static int MIX_TIME = 1; // 最短录制时间，单位秒，0为无时间限制，建议设为1

    private static float recodeTime = 0.0f; // 录音的时间
    private static double voiceValue = 0.0; // 麦克风获取的音量值

    private static boolean playState = false; // 播放状态

    // 录音计时线程
    public void mythread() {
        recordThread = new Thread(ImgThread);
        recordThread.start();
    }

    private Runnable ImgThread = new Runnable() {

        public void run() {
            recodeTime = 0.0f;
            while (AmrEngine.getSingleEngine().isRecordRunning()) {
                if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
                    imgHandle.sendEmptyMessage(0);
                } else {
                    try {
                        Thread.sleep(200);
                        recodeTime += 0.2;
                        if (AmrEngine.getSingleEngine().isRecordRunning()) {
                            // voiceValue = mr.getAmplitude();
                            imgHandle.sendEmptyMessage(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    // 录音Dialog图片随声音大小切换
    void setDialogImage() {
        float b = (float) voiceValue;
        if (b > 70f) {
            voicesize.setImageResource(R.drawable.hua7);
        } else if (b > 65f) {
            voicesize.setImageResource(R.drawable.hua6);
        } else if (b > 60f) {
            voicesize.setImageResource(R.drawable.hua5);
        } else if (b > 55f) {
            voicesize.setImageResource(R.drawable.hua4);
        } else if (b > 50f) {
            voicesize.setImageResource(R.drawable.hua3);
        } else if (b > 40f) {
            voicesize.setImageResource(R.drawable.hua2);
        } else {
            voicesize.setImageResource(R.drawable.hua1);
        }
    }

    public void hideall() {
        chat_radio_panel.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        voicesize.setVisibility(View.VISIBLE);
    }

    public void showVoiceLoading() {
        voicesize.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
    }

    public void showVoiceCancle() {
        loading.setVisibility(View.GONE);
        voicesize.setVisibility(View.GONE);
        delete.setVisibility(View.VISIBLE);
    }

    public void showVoiceStart() {
        loading.setVisibility(View.GONE);
        voicesize.setVisibility(View.VISIBLE);
        delete.setVisibility(View.GONE);
    }

    @Override
    public void OnAddBtnClick() {
        User u = ac.getUserInfo();
        if (u.getIsvip() == 0) {
            goVip();
            return;
        }
        closeInput();
        AlertDialog dialog = new AlertDialog.Builder(mContext).setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                addClick(which);
            }
        }).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void addClick(int which) {
        switch (which) {
            case 0:
                if (Util.existSDcard()) {
                    Intent intent = new Intent(); // 调用照相机
                    String messagepath = StaticFactory.APKCardPathChat;
                    File fa = new File(messagepath);
                    if (!fa.exists()) {
                        fa.mkdirs();
                    }
                    filename = messagepath + new Date().getTime();// 图片路径
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(filename)));
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, AddPicFragment.Camera);
                } else {
                    Toast.makeText(getApplicationContext(), "亲，请检查是否安装存储卡!",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if (Util.existSDcard()) {
                    Intent intent = new Intent();
                    String messagepath = StaticFactory.APKCardPathChat;
                    File fa = new File(messagepath);
                    if (!fa.exists()) {
                        fa.mkdirs();
                    }
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, AddPicFragment.Album);
                } else {
                    Toast.makeText(getApplicationContext(), "亲，请检查是否安装存储卡!",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    Handler itemHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            AlertDialog dialog;
            switch (msg.what) {
                case DfMessage.TEXT://打开详情
                    startDetail((DfMessage) msg.obj);
                    break;
                case DfMessage.IMAGE://打开图片预览
                    startImage((DfMessage) msg.obj);
                    break;
                case DfMessage.VOICE://播放声音
                    listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
                    ChatPlayVoiceManager.getInstance().playVoice((DfMessage) msg.obj);
                    break;
                case HANDLER_COPY://复制文本
                    dialog = new AlertDialog.Builder(ChatActivity.this).setItems(
                            new String[]{"复制文本", "删除消息"},
                            new content_click((DfMessage) msg.obj)).create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    break;
                case HANDLER_LONGPRESS://长按删除
                    dialog = new AlertDialog.Builder(ChatActivity.this).setItems(
                            new String[]{"删除消息"},
                            new img_click((DfMessage) msg.obj)).create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    break;
                case HANDLER_RESEND:// 消息重发
                    DfMessage dm = (DfMessage) msg.obj;
                    if (dm.getReceiverUid().equals(user.getId())) {
                        reReceiver(dm);//如果是下载失败
                    } else {
                        reSend(dm);//
                    }
                    break;
            }
            super.dispatchMessage(msg);
        }

    };

    void startDetail(DfMessage bean) {
        DfMessage.OtherHelperMessage msg = bean.getOtherHelperContent();
        DateBean date = new DateBean();
        date.setDyid(msg.getDyid());
        DateDetailActivity_.intent(mContext).bean(date).start();
    }

    /**
     * 重新获取语音
     *
     * @param msg
     */

    public void reReceiver(final DfMessage msg) {
        if (msg.getMsgtype() == DfMessage.VOICE) {
            ChatDownLoadManager.getInstance(this).down(msg);
        }
    }

    class img_click implements DialogInterface.OnClickListener {
        DfMessage msg;

        public img_click(DfMessage msg) {
            this.msg = msg;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            deleteMsg(msg);
        }
    }

    public void reSend(final DfMessage msg) {
        new AlertDialog.Builder(this).setMessage("您确定要重发这条信息吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        DBHelper.dfMessageDao.delete(msg);
                        adapter.removeObj(msg);
                        adapter.notifyDataSetChanged();
                        switch (msg.getMsgtype()) {
                            case 0:
                                sendTextThread(msg.getContent());
                                break;
                            case 1:
                                sendFile(new File(msg.getContent()), 1);
                                break;
                            case 2:
                                sendFile(new File(msg.getContent()), 2);
                                break;
                            case 5:
                                sendGifThread(msg.getGifContent());
                                break;
                        }
                    }
                }).create().show();
    }

    class content_click implements DialogInterface.OnClickListener {
        DfMessage msg;

        public content_click(DfMessage msg) {
            this.msg = msg;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    copy(msg.getContent());
                    break;
                case 1:
                    deleteMsg(msg);
                    break;
            }
        }
    }

    public void deleteMsg(DfMessage msg) {
        DBHelper.dfMessageDao.delete(msg);
        try {
            if (msg.getMsgtype() > 0) {
                File file = new File(msg.getContent());
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter.removeObj(msg);
        adapter.notifyDataSetChanged();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void copy(String content) {
        android.content.ClipboardManager c = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        c.setPrimaryClip(ClipData.newPlainText(null, content));

        Toast.makeText(this, "已复制", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ChatPlayVoiceManager.getInstance().stopArm(null);
    }

    @Override
    protected void onPause() {

        super.onPause();
        ChatPlayVoiceManager.getInstance().stopArm(null);
        mSensorManager.unregisterListener(this);
    }

    public void startImage(DfMessage msg) {
        List<DfMessage> imgDf = new ArrayList<DfMessage>();
        List<DfMessage> list = adapter.getList();
        for (DfMessage dfMessage : list) {
            if (dfMessage.getMsgtype() == 1) {
                imgDf.add(dfMessage);
            }
        }
        int position = imgDf.indexOf(msg);
        List<ImageBean> beans = new ArrayList<ImageBean>();
        for (DfMessage dfMessage : imgDf) {
            ImageBean ib = new ImageBean();
            ib.imgpath = dfMessage.getContent();
            beans.add(ib);
        }
        MessageList beanlist = new MessageList();
        beanlist.imgList = beans;
        ImageBrowserActivity_.intent(mContext).mPosition(position).mProfile((ArrayList<ImageBean>) beanlist.imgList).isUserLogo(true).start();
    }

    @Override
    protected void onResume() {

        super.onResume();
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float range = event.values[0];
        if (range >= mSensor.getMaximumRange()) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
        } else {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }
    }

    @Override
    public void OnKeyBoardStateChange(int state, int height) {
    }

    @Override
    public void OnSendBtnClick(String msg) {
        sendTextThread(msg);
    }

    @Override
    public void OnSendEmotion(EmoticonActivityListBean.EmoticonZip.EmoticonImageBean bean) {
        sendGifThread(new EmoticonJsonBean(bean.getGifUrl(),
                bean.getFlagName(), bean.getGiffile()));
    }

    @Override
    public void OnMoreItemClick(int position) {

    }

    //更新聊天对象用户信息
    public void updateFriendInfo(JSONArray ids) {
        ac.finalHttp.post(URL.GET_USER_SIMPLE_INFO, getAjaxParams("ids", ids.toString()),
                new MyJsonHttpResponseHandler() {
                    @Override
                    public void onSuccessRetCode(JSONObject jo) throws Throwable {
                        List<User> list = Util.jsonToList(jo.getJSONObject(URL.RESPONSE).getString(URL.LIST), User.class);
                        for (int i = 0; i < list.size(); i++) {
                            //更新聊天列表
                            ChatListBean bean = DBHelper.chatListBeanDao.getChatListBean(user.getId(), list.get(i).getId());
                            if (bean != null) {
                                bean.setNickname(list.get(i).getNickname());
                                bean.setUserlogo(list.get(i).getAvatar());
                                DBHelper.chatListBeanDao.update(bean);

                                Intent j = new Intent(ChatListFragment.UPDATE);
                                j.putExtra("bean", bean);
                                sendBroadcast(j);
                            }
                        }
                        //更新adapter里的用户信息
                        adapter.setFriend(list);
                        adapter.notifyDataSetChanged();

                        if (type == ChatType.friend) {
                            if (list != null && list.size() > 0) {
                                setTitleTxt(list.get(0).getNickname());
                            }
                        }
                    }
                }
        );
    }

    //更新群组信息
    public void updateGroupInfo(JSONArray ids) {
        ac.finalHttp.post(URL.GET_GROUP_SIMPLE_INFO, getAjaxParams("ids", ids.toString()),
                new MyJsonHttpResponseHandler() {
                    @Override
                    public void onSuccessRetCode(JSONObject jo) throws Throwable {
                        List<GroupBean> list = Util.jsonToList(jo.getJSONObject(URL.RESPONSE).getString(URL.LIST), GroupBean.class);
                        for (int i = 0; i < list.size(); i++) {
                            //更新聊天列表
                            ChatListBean bean = DBHelper.chatListBeanDao.getChatListBean(user.getId(), list.get(i).getId());
                            if (bean != null) {
                                bean.setNickname(list.get(i).getGroupName());
                                bean.setUserlogo(list.get(i).getAvatar());

                                friend.setNickname(list.get(i).getGroupName());
                                friend.setAvatar(list.get(i).getAvatar());

                                DBHelper.chatListBeanDao.update(bean);

                                Intent j = new Intent(ChatListFragment.UPDATE);
                                j.putExtra("bean", bean);
                                sendBroadcast(j);
                            }
                        }
                        if (type == ChatType.group) {
                            if (list != null && list.size() > 0) {
                                setTitleTxt(list.get(0).getGroupName());
                            }
                        }
                    }
                }
        );
    }
}
