package com.quanliren.quan_one.activity.date;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.fragment.date.DateListFragment;
import com.quanliren.quan_one.fragment.date.DateListFragment_;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

@EActivity(R.layout.activity_only_fragment)
public class PersonalDateListActivity extends BaseActivity{

	@Extra
	public String userId = "";
	@Extra
	public int type = DateListFragment.ALL;
	@Extra
	public String title_str;

	@Override
	public void init() {
		super.init();
		getSupportFragmentManager().beginTransaction().replace(R.id.content, DateListFragment_.builder().title_str(title_str).type(type).userId(userId).build()).commitAllowingStateLoss();
	}
}
