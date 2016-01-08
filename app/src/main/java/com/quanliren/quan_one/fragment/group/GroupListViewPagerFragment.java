package com.quanliren.quan_one.fragment.group;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.fragment.base.BaseViewPagerChildFragment;

import org.androidannotations.annotations.EFragment;

/**
 * Created by Shen on 2015/12/24.
 */
@EFragment
public class GroupListViewPagerFragment extends BaseViewPagerChildFragment {

    @Override
    public int getConvertViewRes() {
        return R.layout.activity_only_fragment;
    }

    @Override
    public void lazyInit() {
        getChildFragmentManager().beginTransaction().add(R.id.content, GroupListFragment_.builder().groupType(GroupListFragment.GroupType.near).build()).commitAllowingStateLoss();
    }
}
