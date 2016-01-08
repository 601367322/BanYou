package com.quanliren.quan_one.activity.through;

import com.amap.api.maps2d.model.LatLng;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

@EActivity(R.layout.activity_only_fragment)
public class ThroughListActivity extends BaseActivity {

    @Extra
    public LatLng ll;

    @Override
    public void init() {
        super.init();
        getSupportFragmentManager().beginTransaction().replace(R.id.content,ThroughListFragment_.builder().ll(ll).build()).commitAllowingStateLoss();
    }
}
