package com.quanliren.quan_one.activity.seting.wallet;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;

import org.androidannotations.annotations.EActivity;

/**
 * Created by Kong on 2016/2/29.
 */
@EActivity(R.layout.activity_only_fragment)
public class PaymentDetailActivity extends BaseActivity {
    @Override
    public void init() {
        getSupportFragmentManager().beginTransaction().replace(R.id.content, PaymentDetailFragment_.builder().build()).commitAllowingStateLoss();
    }
}
