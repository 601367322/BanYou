package com.quanliren.quan_one.activity.shop;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.fragment.ShopVipFrament_;

import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_only_fragment)
public class ShopVipDetailActivity extends BaseActivity {

	@Override
	public void init() {
		super.init();
		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.content, ShopVipFrament_.builder().needBack(true).build()).commit();
	}

}
