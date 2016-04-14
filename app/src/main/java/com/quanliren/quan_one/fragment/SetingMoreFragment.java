package com.quanliren.quan_one.fragment;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.date.PersonalDateListActivity_;
import com.quanliren.quan_one.activity.date.VisitorListActivity_;
import com.quanliren.quan_one.activity.seting.AboutUsActivity_;
import com.quanliren.quan_one.activity.seting.EmoticonListActivity_;
import com.quanliren.quan_one.activity.seting.MyWalletActivity_;
import com.quanliren.quan_one.activity.seting.RemindMessageActivity_;
import com.quanliren.quan_one.activity.seting.TestSetting_;
import com.quanliren.quan_one.activity.seting.auth.TrueAuthActivity_;
import com.quanliren.quan_one.activity.seting.auth.TrueNoAuthActivity_;
import com.quanliren.quan_one.activity.user.BlackListActivity_;
import com.quanliren.quan_one.activity.user.LoginActivity_;
import com.quanliren.quan_one.activity.user.ModifyPasswordActivity_;
import com.quanliren.quan_one.activity.user.PopularValueActivity_;
import com.quanliren.quan_one.activity.user.UserInfoActivity_;
import com.quanliren.quan_one.activity.wxapi.InviteFriendActivity_;
import com.quanliren.quan_one.adapter.SetAdapter;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.BadgeBean;
import com.quanliren.quan_one.bean.CounterBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.SetBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.base.BaseViewPagerChildFragment;
import com.quanliren.quan_one.fragment.date.DateListFragment;
import com.quanliren.quan_one.post.CounterPost;
import com.quanliren.quan_one.post.UpdateUserPost;
import com.quanliren.quan_one.util.ACache;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;
import com.umeng.fb.FeedbackAgent;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;

@EFragment
public class SetingMoreFragment extends BaseViewPagerChildFragment {

    public static final String UPDATE_USERINFO = "com.quanliren.quan_one.fragment.SetingMoreFragment.UPDATE_USERINFO";
    public static final String UPDATE_USERINFO_ = "com.quanliren.quan_one.fragment.SetingMoreFragment.UPDATE_USERINFO_";
    @ViewById(R.id.listview)
    ListView listview;

    List<SetBean> list = new ArrayList<SetBean>();
    ImageView head_bg;
    ImageView userlogo;
    TextView nickname;
    View head = null;
    ImageView vip;
    SetBean clearSb, emotionSb, visitSb, trueNameSb, walletSb, inviteSb, popularSb;
    SetAdapter adapter;

    @SystemService
    public NotificationManager nm;

    @Override
    public int getConvertViewRes() {
        return R.layout.seting;
    }

    @Override
    public void lazyInit() {
        super.init();
        listview.addHeaderView(head = LayoutInflater.from(getActivity()).inflate(R.layout.seting_head,
                listview, false));
        head_bg = (ImageView) head.findViewById(R.id.head_bg_img);

        userlogo = (ImageView) head.findViewById(R.id.userlogo);
        nickname = (TextView) head.findViewById(R.id.nickname);
        vip = (ImageView) head.findViewById(R.id.vip);

        Intent i = PersonalDateListActivity_.intent(getActivity()).type(DateListFragment.MY).get();
        Intent i_conllect = PersonalDateListActivity_.intent(getActivity()).type(DateListFragment.COLLECT).get();
        final Intent i_visitor = VisitorListActivity_.intent(getActivity()).get();
        list.add(visitSb = new SetBean(R.drawable.set_icon_6, "访客记录(0)", SetBean.Site.MID, SetBean.ItemType.NORMAL, i_visitor));
        list.add(new SetBean(R.drawable.set_icon_1, "我的约会", SetBean.Site.MID, SetBean.ItemType.NORMAL, i));
        list.add(new SetBean(R.drawable.set_icon_5, "我的收藏", SetBean.Site.MID, SetBean.ItemType.NORMAL, i_conllect));
        list.add(popularSb = new SetBean(R.drawable.set_icon_5, "我的人气", SetBean.Site.MID, SetBean.ItemType.NEW, PopularValueActivity_.intent(getActivity()).get()));
        list.add(walletSb = new SetBean(R.drawable.wallet, "我的钱包", SetBean.Site.MID, SetBean.ItemType.NEW, MyWalletActivity_.intent(getActivity()).get()));
        list.add(emotionSb = new SetBean(R.drawable.set_icon_2, "表情下载", SetBean.Site.MID, SetBean.ItemType.NEW, EmoticonListActivity_.intent(getActivity()).get()));
        list.add(trueNameSb = new SetBean(R.drawable.auth_img, "真人认证", SetBean.Site.MID, SetBean.ItemType.NEW, null));
        list.add(new SetBean(R.drawable.set_icon_3, "修改密码", SetBean.Site.BTM, SetBean.ItemType.NORMAL, ModifyPasswordActivity_.intent(getActivity()).get()));
        list.add(inviteSb = new SetBean(R.drawable.set_icon_4, "邀请好友", SetBean.Site.TOP, SetBean.ItemType.REDPACKET, InviteFriendActivity_.intent(getActivity()).get()));
        list.add(new SetBean(R.drawable.set_icon_9, "关于我们", SetBean.Site.MID, SetBean.ItemType.NORMAL, AboutUsActivity_.intent(getActivity()).get()));
        list.add(clearSb = new SetBean(R.drawable.set_icon_13, "清除缓存", SetBean.Site.MID, SetBean.ItemType.CACHE, null));
        list.add(new SetBean(R.drawable.set_icon_12, "消息通知", SetBean.Site.MID, SetBean.ItemType.NORMAL, RemindMessageActivity_.intent(getActivity()).get()));
        list.add(new SetBean(R.drawable.feedback, "意见反馈", SetBean.Site.BTM, SetBean.ItemType.NORMAL, null));

        list.add(new SetBean(R.drawable.set_icon_10, "黑名单", SetBean.Site.TOP, SetBean.ItemType.NORMAL, BlackListActivity_.intent(getActivity()).get()));
        try {
            ApplicationInfo appInfo = getActivity().getPackageManager()
                    .getApplicationInfo(getActivity().getPackageName(),
                            PackageManager.GET_META_DATA);
            String msg = appInfo.metaData.getString("TEST_SETTING");
            if (msg.equals("open")) {
                list.add(new SetBean(R.drawable.set_icon_11, "测试设置", SetBean.Site.MID, SetBean.ItemType.NORMAL, TestSetting_.intent(getActivity()).get()));
            } else {
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        list.add(new SetBean(R.drawable.set_icon_11, "退出当前账号", SetBean.Site.BTM, SetBean.ItemType.NORMAL, null));

        listview.setAdapter(adapter = new SetAdapter(getActivity()));

        adapter.setList(list);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                if (position == list.size()) {
                    loginout(view);
                } else if (list.get(position-1).title.equals("意见反馈")) {
                    FeedbackAgent agent = new FeedbackAgent(getActivity());
                    agent.closeAudioFeedback();
                    agent.closeFeedbackPush();
                    agent.startFeedbackActivity();
                } else if (list.get(position-1).title.equals("真人认证")) {
                    User user = ac.getUserInfo();
                    if (user.getConfirmType() == 0) {
                        TrueNoAuthActivity_.intent(getActivity()).start();
                    } else {
                        TrueAuthActivity_.intent(getActivity()).start();
                    }
                } else if (list.get(position-1).title.equals("清除缓存")) {
                    clearCache();
                } else {
                    if (position > 0) {
                        if (position == 1 && ac.getUserInfo().getIsvip() <= 0) {
                            goVip();
                            return;
                        }
                        SetBean sb = list.get(position - 1);
                        if (sb.clazz != null)
                            startActivity(sb.clazz);
                    }
                }
            }
        });

        head.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Util.startUserInfoActivity(getActivity(), ac.getUser().getId());
            }
        });
        getFileSize();
        receiveBroadcast(new String[]{UPDATE_USERINFO, UPDATE_USERINFO_}, handler);

        onVisible();
    }

    @Override
    public void init() {

    }

    @Override
    public boolean needBack() {
        return false;
    }

    @UiThread
    void notifyAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Background
    void getFileSize() {
        BigDecimal bd = new BigDecimal(getFolderSize(ImageLoader.getInstance()
                .getDiskCache().getDirectory()));
        bd = bd.divide(new BigDecimal(1024 * 1024));
        clearSb.source = (Util.RoundOf(bd.toPlainString()) + "MB");
        Util.RoundOf(bd.toPlainString(), 0);
        notifyAdapter();
    }

    public double getFolderSize(java.io.File file) {
        double size = 0;
        java.io.File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i]);
            } else {
                size = size + fileList[i].length();
            }
        }
        return (double) size;
    }

    Handler handler = new Handler() {

        public void dispatchMessage(android.os.Message msg) {

            Intent i = (Intent) msg.obj;
            String action = i.getAction();
            if (action.equals(UPDATE_USERINFO)) {
                new UpdateUserPost(getActivity(), null);
            } else if (action.equals(UPDATE_USERINFO_)) {
                initSource((User) i.getExtras().getSerializable("user"));
            }
            super.dispatchMessage(msg);
        }

    };

    public void loginout(View v) {
        new AlertDialog.Builder(getActivity())
                .setMessage("您确定要残忍的离开吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        nm.cancelAll();

                        ac.finalHttp.post(URL.LOGOUT, getAjaxParams(), null);

                        ((AppClass) getActivity().getApplicationContext()).dispose();

                        AM.getActivityManager().popAllActivity();

                        LoginActivity_.intent(getActivity()).start();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }


    @Override
    public void onVisible() {
        super.onVisible();
        if (init.get()) {
            //初始化用户信息
            User user = ac.getUserInfo();
            initSource(user);

            //获取缓存信息
            getFileSize();

            //更新消息统计
            statistic();

            updateCount();
            inviteSb.clazz.putExtra("inviteCode", user.getInviteCode());
        }
    }

    void statistic() {
        new CounterPost(getActivity(), new MyJsonHttpResponseHandler() {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                updateCount();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (init.get()) {
            //更新用户信息
            User user = ac.getUserInfo();
            initSource(user);
            //更新消息统计
            updateCount();
        }
    }

    public void updateCount() {
        LoginUser loginUser = ac.getUser();
        CounterBean counterBean = DBHelper.counterDao.getCounter(loginUser.getId());
        BadgeBean badgeBean = DBHelper.badgeDao.getBadge(loginUser.getId());
        if (badgeBean != null) {
            if (badgeBean.getBean().isEmotionBadge()) {
                emotionSb.img = 1;
            } else {
                emotionSb.img = 0;
            }
        }
        if (counterBean != null) {
            visitSb.title = "访客记录(" + counterBean.getBean().getVcnt() + ")";

        }
        if (ac.cs.getTRUE_NAME() == 0) {
            trueNameSb.img = 1;
        } else {
            trueNameSb.img = 0;
        }
        if (ac.cs.getWALLET() == 0) {
            walletSb.img = 1;
        } else {
            walletSb.img = 0;
        }
        if (ac.cs.getPopularValue() == 0) {
            popularSb.img = 1;
        } else {
            popularSb.img = 0;
        }
        adapter.notifyDataSetChanged();
    }

    public void initSource(User user) {
        ImageLoader.getInstance().displayImage(
                user.getAvatar() + StaticFactory._320x320, userlogo, ac.options_userlogo, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        blur(imageUri, loadedImage);
                    }
                });
        nickname.setText(user.getNickname());
        if (user.getIsvip() == 1) {
            vip.setVisibility(View.VISIBLE);
            vip.setImageResource(R.drawable.vip_1);
        } else if (user.getIsvip() == 2) {
            vip.setVisibility(View.VISIBLE);
            vip.setImageResource(R.drawable.vip_2);
        } else {
            vip.setVisibility(View.GONE);
        }
    }

    @UiThread
    public void blur(String imageUri, Bitmap loadedImage) {
        try {
            if (head_bg.getTag(R.id.logo_tag) == null || !head_bg.getTag(R.id.logo_tag).toString().equals(imageUri)) {
                head_bg.setImageBitmap(loadedImage);
                head_bg.setTag(R.id.logo_tag, imageUri);
                Blurry.with(getActivity())
                        .radius(10)
                        .sampling(8)
                        .capture(head_bg)
                        .into(head_bg);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void clearCache() {
        new AlertDialog.Builder(getActivity()).setMessage("您确定要清除缓存吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                customShowDialog("正在清理");
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        ImageLoader.getInstance().clearDiskCache();
                        ACache.get(getActivity()).clear();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    customDismissDialog();
                                    showCustomToast("清理完成");
                                    clearSb.source = "0.0MB";
                                    notifyAdapter();
                                }
                            });
                        }
                    }
                }).start();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).create().show();
    }

}
