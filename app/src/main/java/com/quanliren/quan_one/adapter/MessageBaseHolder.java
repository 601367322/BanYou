package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.user.ChatActivity;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.service.SocketManage;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

import java.text.ParseException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shen on 2015/7/22.
 */
public abstract class MessageBaseHolder {
    @Nullable
    @Bind(R.id.chat_user_logo)
    ImageView user_logo;
    @Nullable
    @Bind(R.id.time)
    TextView time;

    @Nullable
    @Bind(R.id.progress)
    View progress;
    @Nullable
    @Bind(R.id.error_btn)
    View error_btn;
    @Nullable
    @Bind(R.id.error_msg)
    View error_msg;
    @Nullable
    @Bind(R.id.nickname)
    TextView nickname;

    protected Context context;

    protected AppClass ac;

    protected int msgType;

    private List<DfMessage> list;

    public Handler handler;

    public User user;

    public View view;

    public MessageAdapter adapter;

    public MessageBaseHolder(View view) {
        this.view = view;
        context = view.getContext();
        ac = (AppClass) context.getApplicationContext();
        ButterKnife.bind(this, view);
    }

    public void bind(DfMessage bean, int position) {
        try {
            //头像
            if (user_logo != null) {
                user_logo.setTag(R.id.logo_tag, bean);
                if (bean.getSendUid().equals(user.getId())) {
                    ImageLoader.getInstance().displayImage(
                            user.getAvatar() + StaticFactory._160x160,
                            user_logo, ac.options_userlogo);
                } else {
                    ImageLoader.getInstance().displayImage(
                            adapter.friends.get(bean.getFriend().getId()).getAvatar() + StaticFactory._160x160,
                            user_logo, ac.options_userlogo);
                    user_logo.setOnClickListener(logo_click);
                }
            }
            //昵称
            if (nickname != null) {
                if (adapter.type == ChatActivity.ChatType.group) {
                    nickname.setVisibility(View.VISIBLE);
                    nickname.setText(adapter.friends.get(bean.getFriend().getId()).getNickname());
                } else {
                    nickname.setVisibility(View.GONE);
                }
            }

            //错误按钮
            if (error_msg != null)
                error_msg.setVisibility(View.GONE);

            if (error_btn != null && progress != null) {
                error_btn.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
                if (bean.getDownload() == SocketManage.D_downloading) {
                    progress.setVisibility(View.VISIBLE);
                } else if (bean.getDownload() == SocketManage.D_destroy) {
                    error_btn.setVisibility(View.VISIBLE);
                    error_btn.setTag(bean);
                    error_btn.setOnClickListener(logo_click);
                } else if (bean.getDownload() == SocketManage.D_fail) {
                    error_btn.setVisibility(View.VISIBLE);
                    error_msg.setVisibility(View.VISIBLE);
                    error_btn.setTag(bean);
                    error_btn.setOnClickListener(logo_click);
                }
            }
            //消息时间
            if (time != null) {
                if (bean.isShowTime()
                        || (position > 0 && Util.fmtDateTime.parse(bean.getCtime())
                        .getTime() - 60 * 1000 > Util.fmtDateTime.parse(
                        list.get(position - 1)
                                .getCtime()).getTime())) {
                    time.setVisibility(View.VISIBLE);
                    time.setText(Util.getChatTime(bean.getCtime()));
                } else {
                    time.setVisibility(View.GONE);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener click = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            DfMessage msg = (DfMessage) v.getTag();
            Message ms = handler.obtainMessage();
            ms.what = ChatActivity.HANDLER_CLICK;
            ms.obj = msg;
            ms.sendToTarget();
        }
    };

    View.OnLongClickListener long_click = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            DfMessage msg = (DfMessage) v.getTag();
            Message ms = handler.obtainMessage();
            ms.what = ChatActivity.HANDLER_LONG_CLICK;
            ms.obj = msg;
            ms.sendToTarget();
            return true;
        }
    };

    View.OnClickListener logo_click = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.chat_user_logo://头像
                    DfMessage logoMsg = (DfMessage) v.getTag(R.id.logo_tag);
                    if (!StaticFactory.Manager_ID.equals(logoMsg.getFriend().getId())) {
                        Util.startUserInfoActivity(context, logoMsg.getFriend());
                    }
                    break;
                case R.id.error_btn://重发
                    DfMessage msg = (DfMessage) v.getTag();
                    Message ms = handler.obtainMessage();
                    ms.what = ChatActivity.HANDLER_RESEND;
                    ms.obj = msg;
                    ms.sendToTarget();
                    break;
                default:
                    break;
            }

        }
    };

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setList(List<DfMessage> list) {
        this.list = list;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public MessageAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(MessageAdapter adapter) {
        this.adapter = adapter;
    }
}
