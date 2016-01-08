package com.quanliren.quan_one.activity.group;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.fragment.group.InviteFriendListFragment_;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

@EActivity(R.layout.activity_only_fragment)
public class InviteFriendActivity extends BaseActivity {
    @Extra
    GroupBean group;

    @Override
    public void init() {
        super.init();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, InviteFriendListFragment_.builder().group(group).build()).commitAllowingStateLoss();
    }
}
