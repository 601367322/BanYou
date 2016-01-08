package com.quanliren.quan_one.activity.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseUserActivity;
import com.quanliren.quan_one.activity.date.PersonalDateListActivity_;
import com.quanliren.quan_one.activity.image.ImageBrowserActivity_;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.date.DateListFragment;
import com.quanliren.quan_one.fragment.message.ChatListFragment;
import com.quanliren.quan_one.fragment.message.FriendListFragment;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.user_other_info)
public class UserOtherInfoActivity extends BaseUserActivity {

    @ViewById(R.id.leavemsg_btn)
    View leavemsg_btn;
    @ViewById(R.id.care_btn)
    Button care_btn;
    @ViewById(R.id.bottom_btn_ll)
    View bottom_btn_ll;
    boolean enable = false;
    @ViewById(R.id.lx_ll)
    View lx_ll;
    @ViewById(R.id.qq_ll)
    View qq_ll;
    @ViewById(R.id.qq)
    TextView qq;
    @ViewById(R.id.mobile_ll)
    View mobile_ll;
    @ViewById(R.id.mobile)
    TextView mobile;
    @ViewById(R.id.dates_sex)
    TextView dates_sex;
    @ViewById(R.id.datting)
    View datting;
    @ViewById(R.id.pic_contents)
    public View pic_contents;
    @ViewById(R.id.constell)
    public TextView constell;
    @ViewById(R.id.vip_icon)
    public ImageView vip_icon;
    @ViewById(R.id.pay_row)
            View pay_row;
    String userId = null;

    @Override
    public void init() {
        super.init();
        userId = getIntent().getExtras().getString("id");
        //本地数据库读取记录
        UserTable ut = DBHelper.userTableDao.getUserById(userId);
        if (ut != null) {
            user = ut.getUser();
        }
        //网络请求用户数据
        getUserData();
        //初始化界面
        if (user != null) {
            initViewUser();
            isblack();
        }
        if (!Utils.showCoin(this)) {
            pay_row.setVisibility(View.GONE);
        }
    }

    @Click(R.id.userlogo)
    public void userlogo() {
        if (user != null) {
            List<ImageBean> str = new ArrayList<ImageBean>();
            str.add(new ImageBean(user.getAvatar()));
            ImageBrowserActivity_.intent(mContext).mPosition(0).mProfile((ArrayList<ImageBean>) str).isUserLogo(true).start();
        }
    }

    UserPicFragment fragment;
    String[] banyouStates = {"私人伴游", "学生伴游", "商务伴游", "交友伴游", "异国伴游", "英语伴游", "景点伴游", "模特影视"};

    void initViewUser() {
        initViewByUser();
        ArrayList<ImageBean> imglist = user.getImglist();
        if (user.getImglist() == null) {
            imglist = new ArrayList<ImageBean>();
        }
        if (fragment == null) {
            fragment = UserPicFragment_.builder().userId(user.getId()).listSource(imglist).needPage(true).maxLine(2).build();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.pic_contents, fragment).commit();
        } else {
            fragment.setList(imglist);
        }
        if (imglist.size() == 0 && !user.getId().equals(ac.getUser().getId())) {
            pic_contents.setVisibility(View.GONE);
        } else {
            pic_contents.setVisibility(View.VISIBLE);
        }
        if (Util.isStrNotNull(user.getConstell())) {
            constell.setText(user.getConstell());
        } else {
            constell.setText(R.string.empty);
        }
        if (user.getIsvip() == 1) {
            vip_icon.setVisibility(View.VISIBLE);
            vip_icon.setImageResource(R.drawable.vip_1);
        } else if (user.getIsvip() == 2) {
            vip_icon.setVisibility(View.VISIBLE);
            vip_icon.setImageResource(R.drawable.vip_2);
        } else {
            vip_icon.setVisibility(View.GONE);
        }
        if (user.getSex().equals("0") || getString(R.string.girl).equals(user.getSex())) {
            sex.setBackgroundResource(R.drawable.girl_icon);
            dates_sex.setText(R.string.her_date);
        } else {
            sex.setBackgroundResource(R.drawable.boy_icon);
            dates_sex.setText(R.string.his_date);
        }
        sex.setText(user.getAge());
        if (user.getIdentity() == 1) {
            identity.setText("伴游  —  " + banyouStates[user.getIdentityState()]);
        }
        updateCareBtn();
    }

    public void updateCareBtn() {
        if (user.getAttenstatus().equals("0")) {
            care_btn.setText(R.string.care_txt);
        } else {
            care_btn.setText(R.string.cared_txt);
        }
    }

    public void getUserData() {
        ac.finalHttp.post(URL.GET_USER_INFO, getAjaxParams("otherid", userId), new MyJsonHttpResponseHandler(this, Util.progress_arr[1]) {

            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                User temp = new Gson().fromJson(jo.getString(URL.RESPONSE),
                        User.class);
                if (temp != null) {
                    user = temp;
                    DBHelper.userTableDao.updateUser(temp);
                    initViewUser();
                    isblack();
                }
                enable = true;
            }
        });
    }


    @Click(R.id.datting)
    public void datting(View view) {
        PersonalDateListActivity_.intent(this).type(DateListFragment.ONCE).title_str(dates_sex.getText().toString()).userId(userId).start();
    }

    @Click(R.id.leavemsg_btn)
    public void leavemsg_btn(View v) {
        if (!enable) {
            return;
        }
        if (ac.getUserInfo().getIsvip() == 0) {
            Util.goVip(mContext, 0);
            return;
        }
        ChatActivity_.intent(mContext).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).friend(user).start();
    }


    public void isblack() {
        if (user == null) {
            return;
        }
        if (StaticFactory.Manager_ID.equals(user.getId()) || "10474".equals(user.getId())) {
            bottom_btn_ll.setVisibility(View.GONE);
            setTitleRightTxt("");
        } else {
            if (user.getIsblacklist() == 0) {
                bottom_btn_ll.setVisibility(View.VISIBLE);
                setTitleRightTxt("举报");
            } else {
                bottom_btn_ll.setVisibility(View.GONE);
                setTitleRightTxt("取消拉黑");
            }
        }

    }

    public void rightClick(View v) {
        if (StaticFactory.Manager_ID.equals(user.getId()) || "10474".equals(user.getId())) {
            return;
        } else {
            if (user.getIsblacklist() == 0) {
                new AlertDialog.Builder(this).setItems(new String[]{"拉黑", "举报&拉黑"}, new pullBlackMenuClick()).create().show();
            } else {
                new AlertDialog.Builder(this).setItems(new String[]{"取消黑名单"}, new cancleBlackMenuClick()).create().show();
            }
        }
    }

    class cancleBlackMenuClick implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            RequestParams ap = getAjaxParams();
            ap.put("otherid", user.getId());
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
//                            .setTitle(getString(R.string.hint))
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            RequestParams ap = getAjaxParams();
                                            ap.put("otherid", user.getId());
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
                    new AlertDialog.Builder(UserOtherInfoActivity.this)
                            .setMessage("您确定要举报&拉黑该用户吗？")
//                            .setTitle(R.string.hint)
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            new AlertDialog.Builder(UserOtherInfoActivity.this).setItems(new String[]{"骚扰信息",
                                                    "个人资料不当", "盗用他人资料",
                                                    "垃圾广告", "色情相关"}, new juBaoMenuClick()).create().show();
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
            user.setIsblacklist(1);
            user.setAttenstatus("0");
            DBHelper.userTableDao.updateUser(user);
            isblack();

            LoginUser my = DBHelper.loginUserDao.getUser();

            DBHelper.chatListBeanDao.deleteChatList(my.getId(), userId);
            DBHelper.dfMessageDao.deleteAllMessageByFriendId(my.getId(), userId);

            Intent chatlist = new Intent(ChatListFragment.REMOVE);
            chatlist.putExtra("friendId", userId);
            sendBroadcast(chatlist);

            AM.getActivityManager().popActivity(ChatActivity_.class);

            Intent i = new Intent(BlackListFragment.ADDEBLACKLIST);
            i.putExtra("bean", user);
            sendBroadcast(i);

            sendBroadcast(new Intent(ChatActivity.ADDMSG));
        }
    }

    class juBaoMenuClick implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            RequestParams ap = getAjaxParams();
            ap.put("otherid", user.getId());
            ap.put("type", which);
            ac.finalHttp.post(URL.JUBAOANDBLACK, ap, new addBlackCallBack());
        }
    }

    class setLogoCallBack extends MyJsonHttpResponseHandler {

        public setLogoCallBack() {
            super(mContext, Util.progress_arr[1]);
        }

        @Override
        public void onSuccessRetCode(JSONObject jo) throws Throwable {
            showCustomToast("取消成功");
            user.setIsblacklist(0);
            DBHelper.userTableDao.updateUser(user);
            isblack();
            Intent i = new Intent(BlackListFragment.CANCLEBLACKLIST);
            i.putExtra("id", user.getId());
            sendBroadcast(i);
        }
    }

    @Click(R.id.care_btn)
    public void careClick() {
        if (user != null) {
            RequestParams params = Util.getRequestParams(this);
            params.put("otherId", userId);
            params.put("type", user.getAttenstatus());
            ac.finalHttp.post(this, URL.ADD_CARE, params, new MyJsonHttpResponseHandler(this, Util.progress_arr[4]) {
                @Override
                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                    user.setAttenstatus(user.getAttenstatus().equals("0") ? "1" : "0");
                    DBHelper.userTableDao.updateUser(user);
                    if (user.getAttenstatus().equals("0")) {
                        Intent intent = new Intent(FriendListFragment.REMOVECARE);
                        intent.putExtra("user", user);
                        sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(FriendListFragment.ADDCARE);
                        intent.putExtra("user", user);
                        sendBroadcast(intent);
                        care_btn.setText(R.string.cared_txt);
                    }
                    updateCareBtn();
                }
            });
        }
    }
}
