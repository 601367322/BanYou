package com.quanliren.quan_one.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.impl.LoaderImpl;
import com.quanliren.quan_one.activity.user.ChatActivity;
import com.quanliren.quan_one.activity.user.LoginActivity_;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.bean.CounterBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.custom.IndexViewPager;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.SetingMoreFragment_;
import com.quanliren.quan_one.fragment.ShopVipFrament_;
import com.quanliren.quan_one.fragment.date.DateNavFragment_;
import com.quanliren.quan_one.fragment.message.MessageNavFragment_;
import com.quanliren.quan_one.fragment.near.NearNavFragment_;
import com.quanliren.quan_one.post.UpdateUserPost;
import com.quanliren.quan_one.util.BroadcastUtil;
import com.quanliren.quan_one.util.Util;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.update.UmengUpdateAgent;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.properties)
public class MainActivity extends BaseActivity {

    List<Fragment> unaddList = new ArrayList<>(); // 所有将要添加的窗体

    @ViewById(R.id.nav_btn1)
    View btn1;
    @ViewById(R.id.nav_btn2)
    View btn2;
    @ViewById(R.id.nav_btn3)
    View btn3;
    @ViewById(R.id.nav_btn4)
    View btn4;
    @ViewById(R.id.nav_btn5)
    View btn5;
    @ViewById(R.id.messagecount)
    TextView messagecount;
    @ViewById
    IndexViewPager viewpager;

    List<View> list = new ArrayList<>();

    @Override
    public void init() {
        super.init();

        unaddList.add(NearNavFragment_.builder().build());
        unaddList.add(DateNavFragment_.builder().build());
        unaddList.add(MessageNavFragment_.builder().build());
        unaddList.add(ShopVipFrament_.builder().build());
        unaddList.add(SetingMoreFragment_.builder().build());

        list.add(btn1);
        list.add(btn2);
        list.add(btn3);
        list.add(btn5);
        list.add(btn4);

        //umeng自动更新
        UmengUpdateAgent.update(getApplicationContext());
        OnlineConfigAgent.getInstance().updateOnlineConfig(mContext);

        //禁止滑动
        setSwipeBackEnable(false);

        //打开socket定时监听
        if (ac.getUser() != null) {
            Util.setAlarmTime(this, System.currentTimeMillis(), BroadcastUtil.ACTION_CHECKCONNECT, BroadcastUtil.CHECKCONNECT);
        }

        //用户被挤掉线监听
        String[] string = new String[]{ChatActivity.ADDMSG, BroadcastUtil.ACTION_OUTLINE};
        receiveBroadcast(string, handler);

        //初始化计数器
        LoginUser loginUser = ac.getUser();
        if (DBHelper.counterDao.getCounter(loginUser.getId()) == null) {
            CounterBean bean = new CounterBean(loginUser.getId(), new CounterBean.Counter());
            DBHelper.counterDao.create(bean);
        }

        /**更新用户信息**/
        new UpdateUserPost(this, null);

        viewpager.setScanScroll(false);
        viewpager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        setCurrentIndex(btn1);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return unaddList.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            ((LoaderImpl) unaddList.get(position)).refresh();
            super.setPrimaryItem(container, position, object);
        }
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        UmengUpdateAgent.setDefault();
    }

    @Override
    public void onResume() {
        super.onResume();
        setCount();
    }

    private long temptime;

    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - temptime > 3000) {
                Toast.makeText(this, "再按一次将退出程序", Toast.LENGTH_LONG).show();
                temptime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Click({R.id.nav_btn1, R.id.nav_btn2, R.id.nav_btn3, R.id.nav_btn4, R.id.nav_btn5})
    public void setCurrentIndex(View view) {
        viewpager.setCurrentItem(list.indexOf(view), false);
        for (View v : list) {
            if (!v.equals(view))
                v.setSelected(false);
            else
                v.setSelected(true);
        }
    }

    Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            Intent i = (Intent) msg.obj;
            String action = i.getAction();
            if (action.equals(ChatActivity.ADDMSG)) {
                setCount();
            } else if (action.equals(BroadcastUtil.ACTION_OUTLINE)) {
                AM.getActivityManager().popAllActivity();
                LoginActivity_.intent(mContext).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
            super.dispatchMessage(msg);
        }
    };

    public void setCount() {
        LoginUser user = ac.getUser();
        try {
            int num = DBHelper.dfMessageDao.getAllUnReadMessageCount(user.getId());
            if (num > 0) {
                messagecount.setVisibility(View.VISIBLE);
                if (num > 99) {
                    messagecount.setText("99+");
                } else {
                    messagecount.setText(num + "");
                }
            } else {
                messagecount.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
