package com.quanliren.quan_one.activity.user;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;

import org.androidannotations.annotations.EActivity;

/**
 * Created by Shen on 2016/4/12.
 */
@EActivity(R.layout.activity_only_fragment)
public class PopularValueActivity extends BaseActivity {

    @Override
    public void init() {
        super.init();
        ac.cs.setPopularValue(1);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, PopularValueFragment_.builder().build()).commitAllowingStateLoss();
    }
}
