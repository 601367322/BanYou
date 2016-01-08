package com.quanliren.quan_one.activity.date;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;

import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_only_fragment)
public class VisitorListActivity extends BaseActivity {

    @Override
    public void init() {
        super.init();
        getSupportFragmentManager().beginTransaction().replace(R.id.content,VisitorListFragment_.builder().build()).commitAllowingStateLoss();
    }
}
