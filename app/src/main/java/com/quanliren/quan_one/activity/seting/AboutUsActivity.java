package com.quanliren.quan_one.activity.seting;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import org.androidannotations.annotations.EActivity;

@EActivity
public class AboutUsActivity extends BaseActivity{


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutus);
		setTitleTxt(R.string.seting_about_us);
	}
	
	public void updateBtn(View view){
		UmengUpdateAgent.setDefault();
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
				switch (updateStatus) {
					case UpdateStatus.Yes: // has update
						UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
						break;
					case UpdateStatus.No: // has no update
						Toast.makeText(mContext, "已是最新版本", Toast.LENGTH_SHORT).show();
						break;
					case UpdateStatus.NoneWifi: // none wifi
						Toast.makeText(mContext, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
						break;
					case UpdateStatus.Timeout: // time out
						Toast.makeText(mContext, "超时", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		});
		UmengUpdateAgent.forceUpdate(this);
	}
}
