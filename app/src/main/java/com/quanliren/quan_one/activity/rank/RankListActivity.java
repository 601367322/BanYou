package com.quanliren.quan_one.activity.rank;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;

import org.androidannotations.annotations.EActivity;

/**
 * Created by Shen on 2016/4/6.
 */
@EActivity(R.layout.activity_only_fragment)
public class RankListActivity extends BaseActivity {

    @Override
    public void init() {
        super.init();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, RankListFragment_.builder().build()).commitAllowingStateLoss();
    }
}
