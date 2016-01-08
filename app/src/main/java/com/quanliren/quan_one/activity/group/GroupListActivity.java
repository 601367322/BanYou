package com.quanliren.quan_one.activity.group;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.fragment.group.GroupListFragment;
import com.quanliren.quan_one.fragment.group.GroupListFragment_;

import org.androidannotations.annotations.EActivity;

/**
 * Created by Shen on 2015/12/24.
 */
@EActivity(R.layout.activity_only_fragment)
public class GroupListActivity extends BaseActivity {


    @Override
    public void init() {
        super.init();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, GroupListFragment_.builder().groupType(GroupListFragment.GroupType.my).build()).commitAllowingStateLoss();
    }
}
