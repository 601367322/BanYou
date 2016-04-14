package com.quanliren.quan_one.fragment.near;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.filter.NearPeopleFilterActivity_;
import com.quanliren.quan_one.activity.user.SearchFriendActivity_;
import com.quanliren.quan_one.bean.NoticeBean;
import com.quanliren.quan_one.fragment.base.BaseViewPagerChildNavFragment;
import com.quanliren.quan_one.fragment.group.GroupListViewPagerFragment_;
import com.quanliren.quan_one.share.CommonShared;
import com.quanliren.quan_one.util.ACache;
import com.quanliren.quan_one.util.AnimUtil;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shen on 2015/12/22.
 */
@EFragment
public class NearNavFragment extends BaseViewPagerChildNavFragment {

    @ViewById
    ImageView titleRightIcon1;

    @ViewById
    View noticeMain, noticeBg, notice;

    AnimatorSet scaleBigAnimator = new AnimatorSet();
    AnimatorSet scaleSmallAnimator = new AnimatorSet();

    @ViewById
    TextView noticeContent, noticeTime;

    @Override
    public List<Fragment> initFragments() {
        List<Fragment> list = new ArrayList<>();
        list.add(NearPeopleFragment_.builder().build());
        list.add(GroupListViewPagerFragment_.builder().build());
        return list;
    }

    @Override
    public int getConvertViewRes() {
        return R.layout.fragment_near_nav;
    }

    @Override
    public String getTabStr() {
        return getString(R.string.near_nav_str);
    }

    @Override
    public boolean needBack() {
        return false;
    }

    @Override
    public void lazyInit() {
        super.lazyInit();
        leftBtn.setVisibility(View.VISIBLE);
        setTitleLeftIcon(R.drawable.search_user);
        setTitleRightIcon(R.drawable.filter);
        titleRightIcon1.setVisibility(View.VISIBLE);
        titleRightIcon1.setImageResource(R.drawable.action_bar_notice);

        if (ac.cs.getFIRST_GROUP() == CommonShared.OPEN) {
            actionbar_tab.setBadge(1, true);
        }

        getNotice();
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        if (position == 0) {
            title_right_icon.setVisibility(View.VISIBLE);
        } else {
            title_right_icon.setVisibility(View.GONE);
            ac.cs.setFIRST_GROUP(CommonShared.CLOSE);
            actionbar_tab.setBadge(1, false);
        }
    }

    @Click(R.id.title_right_icon)
    public void filterClick() {
        NearPeopleFilterActivity_.intent(this).startForResult(NearPeopleFragment.NEAR_PEOPLE_FILTER);
    }


    @Override
    public void leftClick(View v) {
        Util.umengCustomEvent(getActivity(), "search_btn");
        SearchFriendActivity_.intent(getActivity()).start();
    }

    //获取公告信息
    public void getNotice() {
        ac.finalHttp.post(URL.NOTICE, getAjaxParams(), new MyJsonHttpResponseHandler() {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                NoticeBean bean = null;
                boolean isNew = true;
                try {
                    bean = Util.jsonToBean(jo.getString(URL.RESPONSE), NoticeBean.class);
                    ACache cache = ACache.get(getActivity());
                    NoticeBean oldBean = (NoticeBean) cache.getAsObject("notice");
                    if (oldBean != null) {
                        if(bean.vcode == oldBean.vcode){
                            isNew = false;
                        }
                    }

                    cache.put("notice", bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                noticeContent.setText(bean.content);
                noticeTime.setText(bean.utime);

                //初始化公告板动画
                //下拉
                scaleBigAnimator.playTogether(ObjectAnimator.ofFloat(notice, AnimUtil.SCALEX, 0f, 1f), ObjectAnimator.ofFloat(notice, AnimUtil.SCALEY, 0f, 1f), ObjectAnimator.ofFloat(noticeBg, AnimUtil.ALPHA, 0f, 1f));
                scaleBigAnimator.setDuration(200);
                scaleBigAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        notice.setVisibility(View.VISIBLE);
                        noticeBg.setVisibility(View.VISIBLE);
                    }
                });
                //上

                scaleSmallAnimator.playTogether(ObjectAnimator.ofFloat(notice, AnimUtil.SCALEX, 1f, 0f), ObjectAnimator.ofFloat(notice, AnimUtil.SCALEY, 1f, 0f), ObjectAnimator.ofFloat(noticeBg, AnimUtil.ALPHA, 1f, 0f));
                scaleSmallAnimator.setDuration(200);
                scaleSmallAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        notice.setVisibility(View.GONE);
                        noticeBg.setVisibility(View.GONE);
                    }
                });

                if(isNew){
                    scaleBigAnimator.start();
                }
            }
        });
    }

    @Click({R.id.notice_bg, R.id.notice_close_btn})
    public void noticeBgClick() {
        if (!scaleSmallAnimator.isRunning() && !scaleBigAnimator.isRunning()) {
            scaleSmallAnimator.start();
        }
    }

    @Click(R.id.title_right_icon1)
    public void showNotice() {
        if (!scaleSmallAnimator.isRunning() && !scaleBigAnimator.isRunning()) {
            if (noticeBg.getVisibility() == View.GONE) {
                scaleBigAnimator.start();
            } else {
                scaleSmallAnimator.start();
            }
        }
    }
}
