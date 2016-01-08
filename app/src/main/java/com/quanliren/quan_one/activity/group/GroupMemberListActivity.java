package com.quanliren.quan_one.activity.group;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.fragment.group.GroupMemberListFragment_;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * Created by Shen on 2015/12/29.
 */
@EActivity(R.layout.activity_only_fragment)
public class GroupMemberListActivity extends BaseActivity {

    @Extra
    GroupBean group;

    @Override
    public void init() {
        super.init();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, GroupMemberListFragment_.builder().group(group).build()).commitAllowingStateLoss();
    }
}
