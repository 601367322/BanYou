package com.quanliren.quan_one.activity.user;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;

import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_only_fragment)
public class BlackListActivity extends BaseActivity {

	@Override
	public void init() {
		getSupportFragmentManager().beginTransaction().replace(R.id.content,BlackListFragment_.builder().build()).commitAllowingStateLoss();
	}
}
