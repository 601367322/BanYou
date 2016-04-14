package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.group.GroupDetailActivity_;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.bean.ChatListBean;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.listener.ICheckBoxInterface;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

import butterknife.Bind;
import butterknife.OnClick;

public class LeaveMessageAdapter extends BaseAdapter<ChatListBean> {

    public Handler handler = null;
    private ICheckBoxInterface checkBoxInterface;
    public boolean isShow = false;

    public LeaveMessageAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.leave_message_item_normal;
    }

    public void setCheckBoxInterface(ICheckBoxInterface checkBoxInterface) {
        this.checkBoxInterface = checkBoxInterface;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    class ViewHolder extends BaseHolder<ChatListBean> {
        @Bind(R.id.userlogo)
        ImageView userlogo;
        @Bind(R.id.username)
        TextView username;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.signature)
        TextView signature;
        @Bind(R.id.messagecount)
        TextView messagecount;
        @Bind(R.id.rb)
        CheckBox rb;

        @OnClick(R.id.userlogo)
        void userlogo(View v) {
            ChatListBean bean = (ChatListBean) v.getTag(R.id.logo_tag);
            if (bean.getType() == 1) {
                GroupBean group = new GroupBean();
                group.setId(bean.getFriendid());
                group.setNickname(bean.getNickname());
                AM.getActivityManager().popActivity(GroupDetailActivity_.class);
                GroupDetailActivity_.intent(context).bean(group).start();
            } else {
                Util.startUserInfoActivity(context, bean.getFriendid());
            }
        }

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(final ChatListBean bean, final int position) {
            if (isShow) {
                rb.setVisibility(View.VISIBLE);
            } else {
                rb.setVisibility(View.GONE);
            }
            rb.setChecked(bean.isChoosed());
            ImageLoader.getInstance().displayImage(
                    bean.getUserlogo() + StaticFactory._160x160, userlogo, ac.options_userlogo);
            time.setText(Util.getTimeDateStr(bean.getCtime()));
            if (StaticFactory.Manager_ID.equals(bean.getFriendid())) {
                try {
                    DfMessage.OtherHelperMessage msg = new Gson().fromJson(bean.getContent(), new TypeToken<DfMessage.OtherHelperMessage>() {
                    }.getType());
                    switch (msg.getInfoType()) {
                        case DfMessage.OtherHelperMessage.INFO_TYPE_COMMIT:
                            signature.setText(msg.getNickname() + context.getString(R.string.info_type_0));
                            break;
                        case DfMessage.OtherHelperMessage.INFO_TYPE_PAST_DUE:
                            signature.setText(context.getString(R.string.info_type_1));
                            break;
                        case DfMessage.OtherHelperMessage.INFO_TYPE_REPLY_COMMIT:
                            signature.setText(msg.getNickname() + context.getString(R.string.info_type_2));
                            break;
                        case DfMessage.OtherHelperMessage.INFO_TYPE_APPLY_JOIN_GROUP:
                            signature.setText(msg.getNickname() + String.format(context.getString(R.string.apply_your_group), msg.getGroupName()));
                            break;
                        case DfMessage.OtherHelperMessage.INFO_TYPE_INVITE_JOIN_GROUP:
                            signature.setText(msg.getNickname() + String.format(context.getString(R.string.invite_your_group), msg.getGroupName()));
                            break;
                        default:
                            signature.setText(msg.getText());
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                signature.setText(bean.getContent());
            }
            username.setText(bean.getNickname());
            if (bean.getMsgCount() != 0) {
                messagecount.setVisibility(View.VISIBLE);
                messagecount.setText(bean.getMsgCount() + "");
            } else {
                messagecount.setVisibility(View.GONE);
            }
            userlogo.setTag(R.id.logo_tag, bean);
            rb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    bean.setChoosed(rb.isChecked());
                    checkBoxInterface.checkChild(position, rb.isChecked());
                }
            });
        }
    }
}
