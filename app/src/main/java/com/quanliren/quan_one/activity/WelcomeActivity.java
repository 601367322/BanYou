package com.quanliren.quan_one.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.user.LoginActivity_;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.dao.LoginUserDao;
import com.quanliren.quan_one.share.CommonShared;
import com.quanliren.quan_one.util.BitmapCache;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity
public class WelcomeActivity extends BaseActivity implements View.OnTouchListener {

    ArrayList<View> views = new ArrayList<View>();

    @ViewById(R.id.whatsnew_viewpager)
    ViewPager mViewPager;
    @ViewById(R.id.page0)
    ImageView mPage0;
    @ViewById(R.id.page1)
    ImageView mPage1;
    @ViewById(R.id.page2)
    ImageView mPage2;
    @ViewById(R.id.page3)
    ImageView mPage3;
    @ViewById(R.id.enter_btn)
    Button enter_btn;
    @ViewById(R.id.text)
    TextView text;
    @ViewById(R.id.pages)
    View pages;
    @ViewById(R.id.bg)
    View bg;

    String[] str = new String[]{"", "", "", ""};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.whatsnew_viewpager);

        setSwipeBackEnable(false);
        bg.setBackgroundDrawable(new BitmapDrawable(BitmapCache.getInstance().getBitmap(R.drawable.welcome, this)));
        new Handler().postDelayed(new Runnable() {

            public void run() {
                String isFirstStart = ac.cs.getIsFirstStart();
                if ("".equals(isFirstStart)) {
                    pages.setVisibility(View.VISIBLE);
                    text.setText(str[0]);
                    mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
                    try {
                        views = new ArrayList<View>();
                        LayoutInflater mLi = LayoutInflater.from(WelcomeActivity.this);
                        View view1 = mLi.inflate(R.layout.whats1, null);
                        view1.setBackgroundDrawable(new BitmapDrawable(BitmapCache.getInstance().getBitmap(R.drawable.welcome_1, WelcomeActivity.this)));
                        views.add(view1);
                        View view2 = mLi.inflate(R.layout.whats1, null);
                        view2.setBackgroundDrawable(new BitmapDrawable(BitmapCache.getInstance().getBitmap(R.drawable.welcome_2, WelcomeActivity.this)));
                        views.add(view2);
                        View view3 = mLi.inflate(R.layout.whats1, null);
                        view3.setBackgroundDrawable(new BitmapDrawable(BitmapCache.getInstance().getBitmap(R.drawable.welcome_3, WelcomeActivity.this)));
                        views.add(view3);
                        view3.setOnTouchListener(WelcomeActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    PagerAdapter mPagerAdapter = new PagerAdapter() {

                        public boolean isViewFromObject(View arg0, Object arg1) {
                            return arg0 == arg1;
                        }

                        public int getCount() {
                            return views.size();
                        }

                        public void destroyItem(View container, int position, Object object) {
                            ((ViewPager) container).removeView(views.get(position));
                        }

                        public Object instantiateItem(View container, int position) {
                            ((ViewPager) container).addView(views.get(position));
                            return views.get(position);
                        }
                    };

                    mViewPager.setAdapter(mPagerAdapter);
                } else {
                    LoginUser user = LoginUserDao.getInstance(getApplicationContext()).getUser();
                    if (user == null) {
                        LoginActivity_.intent(mContext).start();
                    } else {
                        MainActivity_.intent(mContext).start();
                    }
                    ac.cs.setIsFirstStart("1");
                    finish();
                }
            }
        }, 1000);

        createShorcut(R.mipmap.ic_launcher);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            LoginUser user = LoginUserDao.getInstance(this).getUser();
            if (user == null) {
                LoginActivity_.intent(mContext).start();
            } else {
                MainActivity_.intent(mContext).start();
            }
            ac.cs.setIsFirstStart("1");
            finish();
        }
        return true;
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {

        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
                    mPage0.setImageResource(R.drawable.page_now);
                    mPage1.setImageResource(R.drawable.page);
                    break;
                case 1:
                    mPage1.setImageResource(R.drawable.page_now);
                    mPage0.setImageResource(R.drawable.page);
                    mPage2.setImageResource(R.drawable.page);
                    break;
                case 2:
                    mPage2.setImageResource(R.drawable.page_now);
                    mPage1.setImageResource(R.drawable.page);
                    mPage3.setImageResource(R.drawable.page);
                    break;
                case 3:
                    mPage3.setImageResource(R.drawable.page_now);
                    mPage2.setImageResource(R.drawable.page);
                    break;
            }
            if (arg0 == 3) {
                enter_btn.setVisibility(View.VISIBLE);
            } else {
                enter_btn.setVisibility(View.GONE);
            }
            text.setText(str[arg0]);
        }

        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    @Click(R.id.enter_btn)
    public void startbutton(View v) {
        LoginUser user = LoginUserDao.getInstance(this).getUser();
        if (user == null) {
            LoginActivity_.intent(mContext).start();
        } else {
            MainActivity_.intent(mContext).start();
        }
        ac.cs.setIsFirstStart("1");
        finish();
    }

    private void createShorcut(int id) {
        if (ac.cs.getFastStartIcon() == CommonShared.OPEN) {
            return;
        } else {
            ac.cs.setFastStartIcon(CommonShared.OPEN);
        }
        Intent shortcutintent = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutintent.putExtra("duplicate", false);
        // 需要现实的名称
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                getString(R.string.app_name));
        // 快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(
                getApplicationContext(), id);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        // 点击快捷图片，运行的程序主入口

        //点击快捷方式的操作
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(this, WelcomeActivity_.class);

        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        // 发送广播。OK
        sendBroadcast(shortcutintent);
    }
/*

    public void deleteShortCut(Class<?> clazz) {
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        sendBroadcast(shortcut);
    }
*/

}
