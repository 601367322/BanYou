package com.quanliren.quan_one.activity.friend;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.fragment.message.FriendListFragment;
import com.quanliren.quan_one.fragment.message.FriendListFragment_;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * Created by Shen on 2015/12/22.
 */
@EActivity(R.layout.activity_only_fragment)
public class FriendActivity extends BaseActivity {

    @Extra
    FriendListFragment.FriendType friendType;

    @Override
    public void init() {
        super.init();

        getSupportFragmentManager().beginTransaction().replace(R.id.content, FriendListFragment_.builder().friendType(friendType).build()).commitAllowingStateLoss();
    }
}
