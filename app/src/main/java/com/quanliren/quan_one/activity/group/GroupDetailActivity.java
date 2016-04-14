package com.quanliren.quan_one.activity.group;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.image.ImageBrowserActivity_;
import com.quanliren.quan_one.activity.jubao.JuBaoActivity_;
import com.quanliren.quan_one.activity.user.ChatActivity;
import com.quanliren.quan_one.activity.user.ChatActivity_;
import com.quanliren.quan_one.adapter.GroupDetailMemberPicAdapter;
import com.quanliren.quan_one.adapter.GroupDetailPicAdapter;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.CircleImageView;
import com.quanliren.quan_one.custom.PullScrollView;
import com.quanliren.quan_one.custom.UserInfoLayout;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.message.ChatListFragment;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.LogUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.Receiver;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Shen on 2015/12/25.
 */
@EActivity(R.layout.activity_group_detail)
public class GroupDetailActivity extends BaseActivity implements PullScrollView.OnTurnListener {
    private static final int EDIT_USERINFO = 1;
    @Extra
    GroupBean bean;

    ViewHolder holder;

    GroupDetailPicAdapter picAdapter;
    GroupDetailMemberPicAdapter memberPicAdapter;

    @Override
    public void init() {
        super.init();

        setTitleTxt(R.drawable.title_back_icon_normal_dark);

        holder = new ViewHolder(findViewById(android.R.id.content));

        setTitleTxt(bean.getGroupName());

        holder.scrollView.setHeader(holder.topBg);

        onRefresh();

        Util.umengCustomEvent(mContext, "group_detail_view");
    }

    @Receiver(actions = {EditGroupActivity.DISSOLVEGROUP})
    public void onReceiver(@Receiver.Extra("group") GroupBean groupBean) {
        if (groupBean.getId().equals(bean.getId())) {
            finish();
        }
    }

    public void onRefresh() {
        RequestParams params = Util.getRequestParams(this);
        params.put("groupId", bean.getId());
        ac.finalHttp.post(URL.GET_GROUP_DETAIL, params, new MyJsonHttpResponseHandler(mContext, Util.progress_arr[1]) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                initView(jo);
                holder.scrollView.smoothScrollTo(0, 0);
            }
        });
    }

    private void initView(JSONObject jo) throws JSONException {
        GroupBean bean = new Gson().fromJson(jo.getString(URL.RESPONSE), new TypeToken<GroupBean>() {
        }.getType());
        title.setVisibility(View.GONE);//隐藏title
        if (bean.getImglist().size() > 0) {
            ImageLoader.getInstance().displayImage(bean.getImglist().get(0).imgpath, holder.logoBg, AppClass.options_no_defalut);
            if (bean.getImglist().size() == 1) {//如果只有一张图
                holder.onePicLl.setVisibility(View.VISIBLE);
                holder.gridview.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(bean.getImglist().get(0).imgpath + StaticFactory._320x320, holder.groupAvatar, AppClass.options_group_userlogo);
            } else {//否则
                holder.onePicLl.setVisibility(View.GONE);
                holder.gridview.setVisibility(View.VISIBLE);
                title.setVisibility(View.VISIBLE);
                if (picAdapter == null) {
                    holder.gridview.setAdapter(picAdapter = new GroupDetailPicAdapter(mContext));
                }
                picAdapter.setList(bean.getImglist());
                picAdapter.notifyDataSetChanged();
            }
        }
        title.setText(bean.getGroupName());
        holder.groupName.setText(bean.getGroupName());
        holder.content.setVisibility(View.VISIBLE);

        //群主头像
        ImageLoader.getInstance().displayImage(bean.getUser().getAvatar() + StaticFactory._160x160, holder.userLogo, AppClass.options_userlogo);
        holder.userInfo.setUser(bean.getUser());
        holder.groupCreateTime.setText(bean.getCtime());
        holder.groupAddress.setText(bean.getArea());
        holder.groupDesc.setText(bean.getGroupInt());
        holder.groupId.setText(bean.getGroupNum());
        holder.boyNumber.setText(bean.getNsex() + "人");
        holder.girlNumber.setText(bean.getVsex() + "人");
        if (Double.valueOf(bean.getLatitude()) != 0 && Double.valueOf(bean.getLongitude()) != 0
                && !ac.cs.getLat().equals("")) {
            holder.groupJuli.setText(Util.getDistance(
                    Double.valueOf(ac.cs.getLng()),
                    Double.valueOf(ac.cs.getLat()), Double.valueOf(bean.getLongitude()),
                    Double.valueOf(bean.getLatitude()))
                    + "km");
        } else {
            holder.groupJuli.setText("");
        }

        if (bean.getAvatarList() != null && bean.getAvatarList().size() > 0) {
            if (memberPicAdapter == null) {
                holder.memberGridview.setAdapter(memberPicAdapter = new GroupDetailMemberPicAdapter(mContext));
            }
            int maxWidth = holder.memberGridview.getMeasuredWidth() + ImageUtil.dip2px(mContext, 8);
            int maxNum = (int) (Float.valueOf(maxWidth) / Float.valueOf(ImageUtil.dip2px(mContext, 38)));
            if (bean.getAvatarList().size() > maxNum) {
                bean.setAvatarList(new ArrayList<User>(bean.getAvatarList().subList(0, maxNum)));
            }
            memberPicAdapter.setList(bean.getAvatarList());
            memberPicAdapter.notifyDataSetChanged();
        }

        holder.joinBtn.setVisibility(View.GONE);
        holder.exitBtn.setVisibility(View.GONE);
        holder.messageBtn.setVisibility(View.GONE);
        holder.inviteBtn.setVisibility(View.GONE);
        holder.moreMemberIcon.setVisibility(View.GONE);
        holder.scrollView.setOnTurnListener(this);
        holder.bottom_btns.setVisibility(View.VISIBLE);

        switch (bean.getType()) {
            case 0://未加入群组，未申请
                holder.joinBtn.setVisibility(View.VISIBLE);
                break;
            case 1://已加入群组
                holder.exitBtn.setVisibility(View.VISIBLE);
                holder.messageBtn.setVisibility(View.VISIBLE);
                holder.moreMemberIcon.setVisibility(View.VISIBLE);
                break;
            case 2://是群主
                holder.inviteBtn.setVisibility(View.VISIBLE);
                holder.messageBtn.setVisibility(View.VISIBLE);
                holder.moreMemberIcon.setVisibility(View.VISIBLE);
                break;
            case 3://已申请加入
                holder.joinBtn.setVisibility(View.VISIBLE);
                holder.joinBtn.setEnabled(false);
                break;
        }

        if (bean.getUser().getId().equals(ac.getUser().getId())) {
            setTitleRightTxt(getString(R.string.edit));
        } else {
            setTitleRightTxt(getString(R.string.jubao));
        }

        GroupDetailActivity.this.bean = bean;
    }

    @Override
    public void onTurn() {
        ValueAnimator animator = ObjectAnimator.ofFloat(holder.picContents.getAlpha(), 1f).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                holder.picContents.setAlpha(Float.valueOf(animation.getAnimatedValue().toString()));
                holder.logoBgDark.setAlpha(Float.valueOf(animation.getAnimatedValue().toString()));
            }
        });
        animator.start();
    }

    @Override
    public void onScrolling(float scrollY) {
        LogUtil.d(scrollY + "");
        if (scrollY < 0.25f) {
            scrollY = 0f;
        }
        holder.picContents.setAlpha(scrollY);
        holder.logoBgDark.setAlpha(scrollY);
    }

    @Override
    public void rightClick(View v) {
        super.rightClick(v);
        if (bean.getUser().getId().equals(ac.getUser().getId())) {
            EditGroupActivity_.intent(this).group(bean).startForResult(EDIT_USERINFO);
        } else {
            JuBaoActivity_.intent(mContext).other(bean.getUser()).group(bean).startForResult(20001);
        }
    }

    @OnActivityResult(EDIT_USERINFO)
    void onGroupDetail(int resultCode) {
        if (resultCode == RESULT_OK) {
            onRefresh();
        }
    }

    @Click(R.id.group_master_detail)
    public void masterClick() {
        if (bean.getUser() != null) {
            Util.startUserInfoActivity(this, bean.getUser().getId());
        }
    }

    @Click(R.id.group_member_list)
    public void memberClick() {
        if (bean.getType() == 1 || bean.getType() == 2) {
            //组员列表
            GroupMemberListActivity_.intent(this).group(bean).start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'activity_group_detail.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    class ViewHolder {
        @Bind(R.id.logo_bg)
        ImageView logoBg;
        @Bind(R.id.top_bg)
        RelativeLayout topBg;
        @Bind(R.id.group_avatar)
        CircleImageView groupAvatar;
        @Bind(R.id.group_name)
        TextView groupName;
        @Bind(R.id.one_pic_ll)
        LinearLayout onePicLl;
        @Bind(R.id.gridview)
        GridView gridview;
        @Bind(R.id.group_id)
        TextView groupId;
        @Bind(R.id.user_logo)
        CircleImageView userLogo;
        @Bind(R.id.user_info)
        UserInfoLayout userInfo;
        @Bind(R.id.group_master_detail)
        LinearLayout groupMasterDetail;
        @Bind(R.id.group_desc)
        TextView groupDesc;
        @Bind(R.id.girl_number)
        TextView girlNumber;
        @Bind(R.id.boy_number)
        TextView boyNumber;
        @Bind(R.id.member_gridview)
        GridView memberGridview;
        @Bind(R.id.group_address)
        TextView groupAddress;
        @Bind(R.id.group_create_time)
        TextView groupCreateTime;
        @Bind(R.id.scroll_view)
        PullScrollView scrollView;
        @Bind(R.id.join_btn)
        Button joinBtn;
        @Bind(R.id.invite_btn)
        Button inviteBtn;
        @Bind(R.id.exit_btn)
        Button exitBtn;
        @Bind(R.id.message_btn)
        Button messageBtn;
        @Bind(R.id.content)
        RelativeLayout content;
        @Bind(R.id.more_member_icon)
        View moreMemberIcon;
        @Bind(R.id.group_juli)
        TextView groupJuli;
        @Bind(R.id.bottom_btns)
        View bottom_btns;
        @Bind(R.id.logo_bg_dark)
        View logoBgDark;
        @Bind(R.id.pic_contents)
        View picContents;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.group_avatar)
        public void logoClick() {
            try {
                ImageBrowserActivity_.intent(mContext).mPosition(0).mProfile(bean.getImglist()).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @OnClick({R.id.join_btn, R.id.invite_btn, R.id.exit_btn})
        public void btnClick(View view) {
            final RequestParams params = Util.getRequestParams(mContext);
            switch (view.getId()) {
                case R.id.join_btn:
                    joinClick(params, 0);
                    break;
                case R.id.invite_btn:
                    if (bean.getGroupType().equals("2")) {
                        Util.toast(mContext, "您的群组已过期，成为会员激活该群");
                        Util.goVip(mContext, 0);
                        return;
                    }
                    InviteFriendActivity_.intent(mContext).group(bean).start();
                    break;
                case R.id.exit_btn:
                    exitClick(params);
                    break;
            }
        }

        @OnClick(R.id.message_btn)
        public void messageClick() {
            Util.umengCustomEvent(mContext, "group_detail_chat_btn");
            User friend = new User();
            friend.setId(bean.getId());
            friend.setNickname(bean.getGroupName());
            friend.setAvatar(bean.getAvatar());
            AM.getActivityManager().popActivity(ChatActivity_.class);
            ChatActivity_.intent(mContext).type(ChatActivity.ChatType.group).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).friend(friend).start();
        }

        private void exitClick(final RequestParams params) {
            new AlertDialog.Builder(mContext).setMessage(String.format(getString(R.string.are_you_sure_exit_group), bean.getGroupName())).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    post(params, 2);
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).create().show();
        }

        private void joinClick(final RequestParams params, final int type) {

            if (bean.getGroupType().equals("2")) {
                Util.toast(mContext, "该群已过期");
                return;
            }

            final Dialog dialog = new Dialog(mContext, R.style.red_line_dialog);
            View convertView = LayoutInflater.from(mContext).inflate(R.layout.group_apply_join_dialog, null);
            final EditText editText = (EditText) convertView.findViewById(R.id.edittext);
            TextView button = (TextView) convertView.findViewById(R.id.ok);
            convertView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.closeSoftKeyboard(editText);
                    dialog.dismiss();
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(editText.getText().toString())) {
                        params.put("content", editText.getText().toString());
                        post(params, type);
                        Utils.closeSoftKeyboard(editText);
                        dialog.dismiss();
                    } else {
                        Util.toast(mContext, getString(R.string.please_insert_content));
                    }
                }
            });
            dialog.setContentView(convertView);

            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setAttributes(lp);
            dialog.show();

        }

        public void post(RequestParams params, final int type) {
            params.put("groupId", bean.getId());
            params.put("type", type);
            ac.finalHttp.post(URL.GROUP_MANAGER_USER, params, new MyJsonHttpResponseHandler(mContext, Util.progress_arr[4]) {
                @Override
                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                    switch (type) {
                        case 0:
                            Util.toast(mContext, getString(R.string.sended));
                            joinBtn.setEnabled(false);
                            joinBtn.setText(R.string.sended);
                            break;
                        case 2:
                            Util.toast(mContext, getString(R.string.exited));
                            bean.setType(0);//设置为非群员
                            joinBtn.setVisibility(View.VISIBLE);
                            exitBtn.setVisibility(View.GONE);
                            messageBtn.setVisibility(View.GONE);
                            DBHelper.chatListBeanDao.deleteChatList(ac.getUser().getId(), bean.getId());
                            DBHelper.dfMessageDao.deleteAllMessageByFriendId(ac.getUser().getId(), bean.getId());
                            Intent chatlist = new Intent(ChatListFragment.REMOVE);
                            chatlist.putExtra("friendId", bean.getId());
                            sendBroadcast(chatlist);
                            break;
                    }
                }
            });
        }
    }
}
