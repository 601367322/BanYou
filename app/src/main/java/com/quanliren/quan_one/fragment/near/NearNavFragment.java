package com.quanliren.quan_one.fragment.near;

import android.support.v4.app.Fragment;
import android.view.View;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.filter.NearPeopleFilterActivity_;
import com.quanliren.quan_one.activity.user.SearchFriendActivity_;
import com.quanliren.quan_one.fragment.base.BaseViewPagerChildNavFragment;
import com.quanliren.quan_one.fragment.group.GroupListViewPagerFragment_;
import com.quanliren.quan_one.share.CommonShared;

import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shen on 2015/12/22.
 */
@EFragment
public class NearNavFragment extends BaseViewPagerChildNavFragment {

    @Override
    public List<Fragment> initFragments() {
        List<Fragment> list = new ArrayList<>();
        list.add(NearPeopleFragment_.builder().build());
        list.add(GroupListViewPagerFragment_.builder().build());
        return list;
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
        rightBtn.setVisibility(View.GONE);

        if (ac.cs.getFIRST_GROUP() == CommonShared.OPEN) {
            actionbar_tab.setBadge(1, true);
        }
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        if (position == 0) {
            rightBtn.setVisibility(View.VISIBLE);
        } else {
            rightBtn.setVisibility(View.GONE);
            ac.cs.setFIRST_GROUP(CommonShared.CLOSE);
            actionbar_tab.setBadge(1, false);
        }
    }

    @Override
    public void rightClick(View v) {
        NearPeopleFilterActivity_.intent(this).startForResult(NearPeopleFragment.NEAR_PEOPLE_FILTER);
    }

    @Override
    public void leftClick(View v) {
        SearchFriendActivity_.intent(getActivity()).start();
    }
}
