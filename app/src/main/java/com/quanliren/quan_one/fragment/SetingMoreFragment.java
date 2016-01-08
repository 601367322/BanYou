package com.quanliren.quan_one.fragment;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.fragment.base.BaseViewPagerChildFragment;
import com.quanliren.quan_one.activity.date.PersonalDateListActivity_;
import com.quanliren.quan_one.activity.date.VisitorListActivity_;
import com.quanliren.quan_one.activity.seting.AboutUsActivity_;
import com.quanliren.quan_one.activity.seting.EmoticonListActivity_;
import com.quanliren.quan_one.activity.seting.RemindMessageActivity_;
import com.quanliren.quan_one.activity.seting.TestSetting_;
import com.quanliren.quan_one.activity.user.BlackListActivity_;
import com.quanliren.quan_one.activity.user.LoginActivity_;
import com.quanliren.quan_one.activity.user.ModifyPasswordActivity_;
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

@EFragment
public class SetingMoreFragment extends BaseViewPagerChildFragment {

    public static final String UPDATE_USERINFO = "com.quanliren.quan_one.fragment.SetingMoreFragment.UPDATE_USERINFO";
    public static final String UPDATE_USERINFO_ = "com.quanliren.quan_one.fragment.SetingMoreFragment.UPDATE_USERINFO_";
    @ViewById(R.id.listview)
    ListView listview;
    List<SetBean> list = new ArrayList<SetBean>();
    RelativeLayout head_bg;
    ImageView userlogo;
    TextView nickname;
    View head = null;
    ImageView vip;
    SetBean clearSb, emotionSb, visitSb;
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
        head_bg = (RelativeLayout) head.findViewById(R.id.head_bg);
        userlogo = (ImageView) head.findViewById(R.id.userlogo);
        nickname = (TextView) head.findViewById(R.id.nickname);
        vip = (ImageView) head.findViewById(R.id.vip);

        Intent i = PersonalDateListActivity_.intent(getActivity()).type(DateListFragment.MY).get();
        Intent i_conllect = PersonalDateListActivity_.intent(getActivity()).type(DateListFragment.COLLECT).get();
        final Intent i_visitor = VisitorListActivity_.intent(getActivity()).get();
        list.add(visitSb = new SetBean(R.drawable.set_icon_6, "访客记录(0)", 0, true, i_visitor));
        list.add(new SetBean(R.drawable.set_icon_1, "我发布的", 0, false, i));

        list.add(emotionSb = new SetBean(R.drawable.set_icon_2, "表情下载", 1, false, EmoticonListActivity_.intent(getActivity()).get()));
        list.add(new SetBean(R.drawable.set_icon_5, "我的收藏", 0, false, i_conllect));
        list.add(new SetBean(R.drawable.set_icon_3, "修改密码", 0, false, ModifyPasswordActivity_.intent(getActivity()).get()));
        list.add(new SetBean(R.drawable.set_icon_4, "邀请好友", 0, true, InviteFriendActivity_.intent(getActivity()).get()));
        list.add(new SetBean(R.drawable.set_icon_9, "关于我们", 0, false, AboutUsActivity_.intent(getActivity()).get()));
        list.add(clearSb = new SetBean(R.drawable.set_icon_13, "清除缓存", 0, false, null));
        list.add(new SetBean(R.drawable.set_icon_12, "消息通知", 0, false, RemindMessageActivity_.intent(getActivity()).get()));
        list.add(new SetBean(R.drawable.feedback, "意见反馈", 0, false, null));

        list.add(new SetBean(R.drawable.set_icon_10, "黑名单", 0, true, BlackListActivity_.intent(getActivity()).get()));
        try {
            ApplicationInfo appInfo = getActivity().getPackageManager()
                    .getApplicationInfo(getActivity().getPackageName(),
                            PackageManager.GET_META_DATA);
            String msg = appInfo.metaData.getString("TEST_SETTING");
            if (msg.equals("open")) {
                list.add(new SetBean(R.drawable.set_icon_11, "测试设置", 0, false, TestSetting_.intent(getActivity()).get()));
            } else {
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        list.add(new SetBean(R.drawable.set_icon_11, "退出当前账号", 0, false, null));

        listview.setAdapter(adapter = new SetAdapter(getActivity(), list));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                if (position == list.size()) {
                    loginout(view);
                } else if (position == 10) {
                    FeedbackAgent agent = new FeedbackAgent(getActivity());
                    agent.closeAudioFeedback();
                    agent.closeFeedbackPush();
                    agent.startFeedbackActivity();

                } else if (position == 8) {
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
                UserInfoActivity_.intent(getActivity()).start();
            }
        });
        getFileSize();
        receiveBroadcast(new String[]{UPDATE_USERINFO,UPDATE_USERINFO_}, handler);

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
            }else if(action.equals(UPDATE_USERINFO_)){
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
        if(init.get()){
            //初始化用户信息
            User user = ac.getUserInfo();
            initSource(user);

            //获取缓存信息
            getFileSize();

            //更新消息统计
            statistic();

            updateCount();
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
        if(init.get()){
            //更新用户信息
            User user = ac.getUserInfo();
            initSource(user);
            //更新消息统计
            updateCount();
        }
    }

    public void updateCount(){
        LoginUser loginUser = ac.getUser();
        CounterBean counterBean = DBHelper.counterDao.getCounter(loginUser.getId());
        BadgeBean badgeBean = DBHelper.badgeDao.getBadge(loginUser.getId());
        if(badgeBean!=null) {
            if (badgeBean.getBean().isEmotionBadge()) {
                emotionSb.img = 1;
            } else {
                emotionSb.img = 0;
            }
        }
        if(counterBean!=null) {
            visitSb.title = "访客记录(" + counterBean.getBean().getVcnt() + ")";

        }
        adapter.notifyDataSetChanged();
    }

    public void initSource(User user) {
        ImageLoader.getInstance().displayImage(
                user.getAvatar() + StaticFactory._320x320, userlogo, ac.options_userlogo);
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
