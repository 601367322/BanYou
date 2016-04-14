package com.quanliren.quan_one.fragment.date;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.date.ChoseLocationActivity_;
import com.quanliren.quan_one.activity.date.PublishActivity_;
import com.quanliren.quan_one.activity.filter.DateFilterActivity_;
import com.quanliren.quan_one.bean.BadgeBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.base.BaseViewPagerChildNavFragment;
import com.quanliren.quan_one.post.CounterPost;
import com.quanliren.quan_one.share.CommonShared;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shen on 2015/12/4.
 */
@EFragment
public class DateNavFragment extends BaseViewPagerChildNavFragment implements ViewPager.OnPageChangeListener {

    @ViewById(R.id.publish)
    View publish;

    @ViewById(R.id.publish_txt)
    View pubTxt;

    @ViewById(R.id.location_txt)
    View locTxt;

    @Override
    public int getConvertViewRes() {
        return R.layout.fragment_date_nav;
    }

    @Override
    public void lazyInit() {
        super.lazyInit();

        setTitleLeftIcon(R.drawable.near_icon);
        setTitleLeftSubTxt(ac.cs.getChoseLocation());
        setTitleRightIcon(R.drawable.filter);

        createAnimation();

        actionbar_tab.setSelection(1);
    }

    @Override
    public List<Fragment> initFragments() {
        List<Fragment> list = new ArrayList<>();
        list.add(DateListFragment_.builder().type(DateListFragment.HOT).build());
        list.add(DateListFragment_.builder().type(DateListFragment.ALL).build());
        list.add(DateListFragment_.builder().type(DateListFragment.CARE).build());
        return list;
    }

    @Override
    public String getTabStr() {
        return getString(R.string.date_nav_str);
    }

    float publishY = -1f;
    float publishTopY = -1f;

    @UiThread(delay = 1000l)
    public void createAnimation() {
        if (ac.cs.getFIRST_PUBLISH() == CommonShared.OPEN) {
            if (publishY == -1) {
                publishY = publish.getY();
            }
            pubTxt.setVisibility(View.VISIBLE);
            ObjectAnimator yStart = ObjectAnimator.ofFloat(publish, "y",
                    publishY, publishY - 150).setDuration(400);
            yStart.setInterpolator(new DecelerateInterpolator());
            yStart.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (publishTopY == -1) {
                        publishTopY = publish.getY();
                    }
                    ObjectAnimator yBouncer = ObjectAnimator.ofFloat(publish, "y",
                            publishTopY, publishTopY + 150).setDuration(1000);
                    yBouncer.setInterpolator(new BounceInterpolator());
                    yBouncer.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (!isDestory)
                                createAnimation();
                        }
                    });
                    yBouncer.start();
                }
            });
            yStart.start();
        } else {
            pubTxt.setVisibility(View.GONE);
        }
    }

    boolean isDestory = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestory = true;
    }

    @Override
    public void init() {

    }

    @Override
    public void onPageSelected(int position) {
        /*if (position == 0) {//手动刷新
            DateListFragment fragment = (DateListFragment) fragments.get(position);
            if (fragment != null) {
                fragment.swipeRefresh();
            }
        }*/
        if (position == 1) {
            rightBtn.setVisibility(View.VISIBLE);
            leftBtn.setVisibility(View.VISIBLE);
            if (ac.cs.getFIRST_CHOSE_LOCATION() == CommonShared.OPEN) {
                locTxt.setVisibility(View.VISIBLE);
            }
        } else {
            rightBtn.setVisibility(View.GONE);
            leftBtn.setVisibility(View.GONE);
            locTxt.setVisibility(View.GONE);
        }
    }

    @Click(R.id.publish)
    public void publishClick(View v) {
        ac.cs.setFIRST_PUBLISH(CommonShared.CLOSE);
        ac.finalHttp.post(URL.AFFIRMPUB, getAjaxParams(), new MyJsonHttpResponseHandler(getActivity(), Util.progress_arr[1]) {

            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                jo = jo.getJSONObject(URL.RESPONSE);
                String ratify = jo.getString("ratify");
                if ("0".equals(ratify)) {
                    PublishActivity_.intent(DateNavFragment.this).startForResult(DateListFragment.PUBLISH_RESULT);
                } else {
                    showCustomToast(getString(R.string.have_a_dating));
                }
            }
        });

    }

    @Override
    public void leftClick(View v) {
        Util.umengCustomEvent(getActivity(), "date_location_choose_btn");
        ChoseLocationActivity_.intent(this).fromActivity(ChosePositionFragment.FromActivity.DateList).startForResult(DateListFragment.CHOSE_LOCTION_RESULT);
        locTxt.setVisibility(View.GONE);
    }

    @OnActivityResult(DateListFragment.CHOSE_LOCTION_RESULT)
    public void choseLocationResult(Intent data, int result) {
        if (data != null && result == Activity.RESULT_OK) {
            String cityName = data.getStringExtra("cityName");
            int cityId = data.getIntExtra("cityId", 1002);
            ac.cs.setChoseLocation(cityName);
            ac.cs.setChoseLocationID(cityId);
            setTitleLeftSubTxt(ac.cs.getChoseLocation().replace("市", ""));
        }
    }

    @Override
    public void rightClick(View v) {
        DateFilterActivity_.intent(this).startForResult(DateListFragment.FILTER_RESULT);
    }

    @Override
    public void onVisible() {
        super.onVisible();
        if (getActivity() != null && init.get()) {
            updateBadge();
            new CounterPost(getActivity(), new MyJsonHttpResponseHandler() {
                @Override
                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                    updateBadge();
                }
            });
        }
    }

    public void updateBadge() {
        LoginUser loginUser = ac.getUser();
        BadgeBean badgeBean = DBHelper.badgeDao.getBadge(loginUser.getId());
        if (badgeBean != null) {
            if (badgeBean.getBean().isDateBadge()) {
                actionbar_tab.setBadge(2, true);
            } else {
                actionbar_tab.setBadge(2, false);
            }
        }
    }

    @Override
    public int getDefaultCurrentItem() {
        return 1;
    }
}
