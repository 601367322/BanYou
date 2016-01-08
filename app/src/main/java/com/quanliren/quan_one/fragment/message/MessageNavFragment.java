package com.quanliren.quan_one.fragment.message;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.user.ChatActivity;
import com.quanliren.quan_one.bean.BadgeBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.base.BaseViewPagerChildFragment;
import com.quanliren.quan_one.fragment.base.BaseViewPagerChildNavFragment;
import com.quanliren.quan_one.post.CounterPost;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shen on 2015/11/25.
 */
@EFragment
public class MessageNavFragment extends BaseViewPagerChildNavFragment implements ViewPager.OnPageChangeListener {

    @Override
    public void lazyInit() {
        super.lazyInit();
        onVisible();
    }

    @Override
    public List<Fragment> initFragments() {
        List<Fragment> list = new ArrayList<>();
        list.add(ChatListFragment_.builder().build());
        list.add(RelationFragment_.builder().build());
        return list;
    }

    @Override
    public String getTabStr() {
        return getString(R.string.message_nav_str);
    }

    @Override
    public boolean needBack() {
        return false;
    }

    @Override
    public void onPageSelected(int position) {
        if (fragments.get(position) instanceof ChatListFragment) {
            setTitleLeftTxt(getString(R.string.edit));
            leftBtn.setVisibility(View.VISIBLE);
            rightBtn.setVisibility(View.GONE);
        } else {
            leftBtn.setVisibility(View.GONE);
            ((ChatListFragment) fragments.get(0)).edit_close();
        }
    }

    @Override
    public void leftClick(View v) {
        if (!getString(R.string.ok).equals(title_left_txt.getText().toString())) {
            if(((ChatListFragment) fragments.get(0)).edit_open()){
                setTitleLeftTxt(getString(R.string.ok));
            }else{
                Util.toast(getActivity(), getString(R.string.empty_message));
            }
        } else {
            setTitleLeftTxt(getString(R.string.edit));
            ((ChatListFragment) fragments.get(0)).edit_close();
        }
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
            for (int i = 0; i < fragments.size(); i++) {
                ((BaseViewPagerChildFragment)fragments.get(i)).onVisible();
            }
        }
    }

    //更新联系tab上的红点 群组和粉丝
    public void updateBadge(){
        LoginUser loginUser = ac.getUser();
        BadgeBean badgeBean = DBHelper.badgeDao.getBadge(loginUser.getId());
        if(badgeBean!=null) {
            if (badgeBean.getBean().isFunsBadge() || badgeBean.getBean().isGroupBadge()) {
                actionbar_tab.setBadge(1, true);
            } else {
                actionbar_tab.setBadge(1, false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setCount();
    }

    //更新消息tab上的红点
    @Receiver(actions = ChatActivity.ADDMSG)
    public void setCount(){
        if(actionbar_tab!=null) {
            LoginUser user = ac.getUser();
            try {
                int num = DBHelper.dfMessageDao.getAllUnReadMessageCount(user.getId());
                if (num > 0) {
                    actionbar_tab.setBadge(0, true);
                } else {
                    actionbar_tab.setBadge(0, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        updateBadge();
    }
}
