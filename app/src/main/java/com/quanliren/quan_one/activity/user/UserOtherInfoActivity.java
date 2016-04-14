package com.quanliren.quan_one.activity.user;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.date.PersonalDateListActivity_;
import com.quanliren.quan_one.activity.group.GroupListActivity_;
import com.quanliren.quan_one.activity.image.ImageBrowserActivity_;
import com.quanliren.quan_one.activity.seting.auth.TrueAuthActivity_;
import com.quanliren.quan_one.activity.jubao.JuBaoActivity_;
import com.quanliren.quan_one.activity.shop.ShopVipDetailActivity_;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.PullScrollView;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.date.DateListFragment;
import com.quanliren.quan_one.fragment.group.GroupListFragment;
import com.quanliren.quan_one.fragment.message.ChatListFragment;
import com.quanliren.quan_one.fragment.message.FriendListFragment;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

@EActivity(R.layout.activity_user_other_detail)
public class UserOtherInfoActivity extends BaseActivity {

    ViewHolder holder;

    @Extra
    String id;

    User friend;
    User user;

    @Override
    public void init() {
        super.init();

        setTitleLeftIcon(R.drawable.title_back_icon_normal_dark);

        holder = new ViewHolder(findViewById(android.R.id.content));

        holder.scrollView.setHeader(holder.topBg);
        user =ac.getUserInfo();
        onRefresh();

        Util.umengCustomEvent(mContext, "user_detail_view");
    }

    public void onRefresh() {
        RequestParams params = Util.getRequestParams(this);
        params.put("otherid", id);
        ac.finalHttp.post(URL.GET_USER_INFO, params, new MyJsonHttpResponseHandler(mContext, Util.progress_arr[1]) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                User temp = new Gson().fromJson(jo.getString(URL.RESPONSE),
                        User.class);
                if (temp != null) {
                    friend = temp;
                    holder.bind(friend);
                }
                holder.scrollView.smoothScrollTo(0, 0);
            }
        });
    }


    @Click(R.id.date_list_btn)
    public void goDateList() {
        if (friend != null) {
            if (user != null) {
                if (user.getId().equals(friend.getId())) {
                    //  若是自己详情，跳转自己发布的约会
                    PersonalDateListActivity_.intent(mContext).type(DateListFragment.MY).start();
                } else {
                    AM.getActivityManager().popActivity(PersonalDateListActivity_.class);
                    PersonalDateListActivity_.intent(this).type(DateListFragment.ONCE).title_str(friend.getSex().equals("0") ? getString(R.string.her_date) : getString(R.string.his_date)).userId(id).start();
                }
            }
        }
    }

    @Click(R.id.group_list_btn)
    public void goGroupList() {
        if (friend != null) {
            if (user != null) {
                if (user.getId().equals(friend.getId())) {
                    //  若是自己详情，跳转自己的群组
                    GroupListActivity_.intent(this).start();
                } else {
                    AM.getActivityManager().popActivity(GroupListActivity_.class);
                    GroupListActivity_.intent(this).type(GroupListFragment.GroupType.other).title_str(friend.getSex().equals("0") ? getString(R.string.her_group) : getString(R.string.his_group)).otherId(id).start();
                }
            }
        }
    }

    @Click(R.id.care_btn)
    public void careBtnClick() {
        if (friend != null) {
            RequestParams params = Util.getRequestParams(this);
            params.put("otherId", id);
            params.put("type", friend.getAttenstatus());
            ac.finalHttp.post(this, URL.ADD_CARE, params, new MyJsonHttpResponseHandler(this, Util.progress_arr[4]) {
                @Override
                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                    friend.setAttenstatus(friend.getAttenstatus().equals("0") ? "1" : "0");
                    if (friend.getAttenstatus().equals("0")) {
                        Intent intent = new Intent(FriendListFragment.REMOVECARE);
                        intent.putExtra("user", friend);
                        sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(FriendListFragment.ADDCARE);
                        intent.putExtra("user", friend);
                        sendBroadcast(intent);
                        holder.careBtn.setText(R.string.cared_txt);
                    }
                    holder.isCare(friend);
                }
            });
        }
    }

    @Click(R.id.leavemsg_btn)
    public void chatBtnClick() {
        Util.umengCustomEvent(mContext, "user_detail_chat_btn");
        if (ac.getUserInfo().getIsvip() == 0) {
            Util.goVip(mContext, 0);
            return;
        }
        AM.getActivityManager().popActivity(ChatActivity_.class);
        ChatActivity_.intent(mContext).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).friend(friend).start();
    }

    @Override
    public void rightClick(View v) {
        if (friend == null) {
            return;
        }
        if (user == null) {
            return;
        }
        if (StaticFactory.Manager_ID.equals(friend.getId()) || "10474".equals(friend.getId())) {
            return;
        } else {
            if (user.getId().equals(friend.getId())) {
                UserInfoActivity_.intent(mContext).start();
                return;
            }
            if (friend.getIsblacklist() == 0) {
                new AlertDialog.Builder(this).setItems(new String[]{"拉黑", "举报&拉黑"}, new pullBlackMenuClick()).create().show();
            } else {
                new AlertDialog.Builder(this).setItems(new String[]{"取消黑名单"}, new cancleBlackMenuClick()).create().show();
            }
        }
    }

    @Click(R.id.start_play_btn)
    public void startPlayVideo() {
        if (ac.getUserInfo().getIsvip() == 0) {
            Util.goVip(mContext, 0);
            return;
        }
        TrueAuthActivity_.intent(mContext).otherId(friend.getId()).start();
    }

    @Click(R.id.go_vip)
    public void goVip() {
        ShopVipDetailActivity_.intent(mContext).start();
    }

    @Click(R.id.main_logo)
    public void mainLogo() {
        if (friend != null) {
            List<ImageBean> str = new ArrayList<>();
            str.add(new ImageBean(friend.getAvatar()));
            ImageBrowserActivity_.intent(mContext).mPosition(0).mProfile((ArrayList<ImageBean>) str).isUserLogo(true).start();
        }
    }

    class ViewHolder implements PullScrollView.OnTurnListener {

        @Bind(R.id.logo_bg)
        ImageView logoBg;
        @Bind(R.id.top_bg)
        RelativeLayout topBg;
        @Bind(R.id.vip_str)
        TextView vipStr;
        @Bind(R.id.sex_str)
        TextView sexStr;
        @Bind(R.id.type_str)
        TextView typeStr;
        @Bind(R.id.money_txt)
        TextView moneyTxt;
        @Bind(R.id.money_str)
        TextView moneyStr;
        @Bind(R.id.phone_str)
        TextView phoneStr;
        @Bind(R.id.star_str)
        TextView starStr;
        @Bind(R.id.appearance_str)
        TextView appearanceStr;
        @Bind(R.id.job_str)
        TextView jobStr;
        @Bind(R.id.earn_str)
        TextView earnStr;
        @Bind(R.id.love_str)
        TextView loveStr;
        @Bind(R.id.signature_str)
        TextView signatureStr;
        @Bind(R.id.scroll_view)
        PullScrollView scrollView;
        @Bind(R.id.care_btn)
        Button careBtn;
        @Bind(R.id.leavemsg_btn)
        Button leavemsgBtn;
        @Bind(R.id.bottom_btns)
        LinearLayout bottomBtns;
        @Bind(R.id.content)
        RelativeLayout content;
        @Bind(R.id.vip_icon)
        ImageView vipIcon;
        @Bind(R.id.age_str)
        TextView ageStr;
        @Bind(R.id.pic_contents)
        FrameLayout picContents;
        @Bind(R.id.logo_bg_dark)
        View logoBgDark;
        @Bind(R.id.go_vip)
        View goVip;
        @Bind(R.id.start_play_btn)
        View startPlayBtn;
        @Bind(R.id.popular_str)
        TextView popularStr;
        @Bind(R.id.start_play_rl)
        View startPlayRL;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        String[] banyouStates = {"私人伴游", "学生伴游", "商务伴游", "交友伴游", "异国伴游", "英语伴游", "景点伴游", "模特影视"};
        String[] pays = {"100-500元/天", "500-1000元/天", "1000-2000元/天", "2000-3000元/天", "3000-4000元/天", "4000-5000元/天", "5000-10000元/天", "1万元以上/天"};

        public void bind(User friend) {
            if(friend.getId().equals(user.getId())){
                //底部按钮不示
                bottomBtns.setVisibility(View.GONE);
            }else{
                //底部按钮显示
                bottomBtns.setVisibility(View.VISIBLE);
            }
            //设置scroll监听
            scrollView.setOnTurnListener(this);
            if (friend.getConfirmType() == 2) {
                startPlayRL.setVisibility(View.VISIBLE);
            } else {
                startPlayRL.setVisibility(View.GONE);
            }
            //头像
            ImageLoader.getInstance().displayImage(friend.getAvatar() + StaticFactory._600x600, logoBg, AppClass.options_userlogo);
            //相册
            ArrayList<ImageBean> imglist = friend.getImglist();
            if (friend.getImglist() == null) {
                imglist = new ArrayList<>();
            }
            if (imglist.size() > 0) {
                picContents.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.pic_contents, UserPicFragment_.builder().userId(friend.getId()).listSource(imglist).needPage(true).maxLine(2).build()).commitAllowingStateLoss();
            } else {
                picContents.setVisibility(View.GONE);
                logoBgDark.setVisibility(View.GONE);
            }
            //身份信息
            if (friend.getIsvip() == 1) {
                vipStr.setText("普通会员");
                vipIcon.setVisibility(View.VISIBLE);
                vipIcon.setImageResource(R.drawable.vip_1);
            } else if (friend.getIsvip() == 2) {
                vipStr.setText("富豪会员");
                vipIcon.setVisibility(View.VISIBLE);
                vipIcon.setImageResource(R.drawable.vip_2);
            } else {
                vipStr.setText("普通用户");
                vipIcon.setVisibility(View.GONE);
            }
            //人气
            popularStr.setText(friend.getPopNum()+"");
            //性别
            if (friend.getSex().equals("0")) {
                sexStr.setText("女");
            } else {
                sexStr.setText("男");
            }
            //年龄
            ageStr.setText(friend.getAge() + "岁");
            //标题昵称
            setTitleTxt(friend.getNickname());
            //身份
            if (friend.getIdentity() == 1) {
                typeStr.setText("伴游  —  " + banyouStates[friend.getIdentityState()]);
                moneyTxt.setText("薪　酬");
            } else {
                typeStr.setText("游客");
                moneyTxt.setText("支付能力");
            }
            //支付能力
            moneyStr.setText(pays[friend.getPay()]);
            //手机号
            if (!user.getId().equals(friend.getId())&&ac.getUserInfo().getIsvip() == 0) {
                phoneStr.setText("只对会员公开");
                goVip.setVisibility(View.VISIBLE);
            } else {
                goVip.setVisibility(View.GONE);
                if (friend.getShowState() == 1) {
                    phoneStr.setText("不公开");
                } else if (friend.getShowState() == 0) {
                    phoneStr.setText(friend.getMobile());
                }
            }


            if (Util.isStrNotNull(friend.getConstell())) {
                starStr.setText(friend.getConstell());
            } else {
                starStr.setText(R.string.empty);
            }

            if (Util.isStrNotNull(friend.getSignature())) {
                appearanceStr.setText(friend.getSignature());
            } else {
                appearanceStr.setText(R.string.empty);
            }

            if (Util.isStrNotNull(friend.getJob())) {
                jobStr.setText(friend.getJob());
            } else {
                jobStr.setText(R.string.empty);
            }

            if (Util.isStrNotNull(friend.getIncome())) {
                earnStr.setText(friend.getIncome());
            } else {
                earnStr.setText(R.string.empty);
            }

            if (Util.isStrNotNull(friend.getEmotion())) {
                loveStr.setText(friend.getEmotion());
            } else {
                loveStr.setText(R.string.empty);
            }

            if (Util.isStrNotNull(friend.getIntroduce())) {
                signatureStr.setText(friend.getIntroduce());
            } else {
                signatureStr.setText(R.string.lazy);
            }

            isBlack(friend);

            isCare(friend);
        }

        @Override
        public void onTurn() {
            ValueAnimator animator = ObjectAnimator.ofFloat(picContents.getAlpha(), 1f).setDuration(200);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    picContents.setAlpha(Float.valueOf(animation.getAnimatedValue().toString()));
                    logoBgDark.setAlpha(Float.valueOf(animation.getAnimatedValue().toString()));
                    startPlayBtn.setAlpha(Float.valueOf(animation.getAnimatedValue().toString()));
                }
            });
            animator.start();
        }

        @Override
        public void onScrolling(float scrollY) {
            if (scrollY < 0.15f) {
                scrollY = 0f;
            }
            picContents.setAlpha(scrollY);
            logoBgDark.setAlpha(scrollY);
            startPlayBtn.setAlpha(scrollY);
        }

        public void isBlack(User friend) {
            if (friend == null) {
                return;
            }
            if (user == null) {
                return;
            }
            if (user.getId().equals(friend.getId())) {
                setTitleRightTxt("编辑");
                return;
            }
            if (StaticFactory.Manager_ID.equals(friend.getId()) || "10474".equals(friend.getId())) {
                bottomBtns.setVisibility(View.GONE);
                setTitleRightTxt("");
            } else {
                if (friend.getIsblacklist() == 0) {
                    bottomBtns.setVisibility(View.VISIBLE);
                    setTitleRightTxt("举报");
                } else {
                    bottomBtns.setVisibility(View.GONE);
                    setTitleRightTxt("取消拉黑");
                }
            }

        }

        public void isCare(User friend) {
            if (friend.getAttenstatus().equals("0")) {
                careBtn.setText(R.string.care_txt);
            } else {
                careBtn.setText(R.string.cared_txt);
            }
        }

    }

    class cancleBlackMenuClick implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            RequestParams ap = getAjaxParams();
            ap.put("otherid", friend.getId());
            ac.finalHttp.post(URL.CANCLEBLACK, ap, new setLogoCallBack(
            ));
        }
    }

    class pullBlackMenuClick implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    new AlertDialog.Builder(UserOtherInfoActivity.this)
                            .setMessage("您确定要拉黑该用户吗？")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            RequestParams ap = getAjaxParams();
                                            ap.put("otherid", friend.getId());
                                            ac.finalHttp.post(URL.ADDTOBLACK, ap,
                                                    new addBlackCallBack());
                                        }
                                    })
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                        }
                                    }).create().show();
                    break;
                case 1:
                    JuBaoActivity_.intent(mContext).other(friend).startForResult(20001);
                    break;
            }
        }
    }

    class addBlackCallBack extends MyJsonHttpResponseHandler {
        public addBlackCallBack() {
            super(mContext, Util.progress_arr[1]);
        }

        @Override
        public void onSuccessRetCode(JSONObject jo) throws Throwable {
            showCustomToast("操作成功");
            addToBlack();
        }
    }

    private void addToBlack() {
        friend.setIsblacklist(1);
        friend.setAttenstatus("0");

        holder.isBlack(friend);
        holder.isCare(friend);

        LoginUser my = DBHelper.loginUserDao.getUser();

        DBHelper.chatListBeanDao.deleteChatList(my.getId(), id);
        DBHelper.dfMessageDao.deleteAllMessageByFriendId(my.getId(), id);

        Intent chatlist = new Intent(ChatListFragment.REMOVE);
        chatlist.putExtra("friendId", id);
        sendBroadcast(chatlist);

        AM.getActivityManager().popActivity(ChatActivity_.class);

        Intent i = new Intent(BlackListFragment.ADDEBLACKLIST);
        i.putExtra("bean", friend);
        sendBroadcast(i);

        sendBroadcast(new Intent(ChatActivity.ADDMSG));
    }

    class setLogoCallBack extends MyJsonHttpResponseHandler {

        public setLogoCallBack() {
            super(mContext, Util.progress_arr[1]);
        }

        @Override
        public void onSuccessRetCode(JSONObject jo) throws Throwable {
            showCustomToast("取消成功");
            friend.setIsblacklist(0);
            holder.isBlack(friend);
            Intent i = new Intent(BlackListFragment.CANCLEBLACKLIST);
            i.putExtra("id", friend.getId());
            sendBroadcast(i);
        }
    }

    @OnActivityResult(20001)
    public void onJuBaoResult(int result) {
        if (result == RESULT_OK) {
            addToBlack();
        }
    }

}
