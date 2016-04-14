package com.quanliren.quan_one.activity.user;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity(R.layout.user_self_intro_edit)
public class IntroEditActivity extends BaseActivity {
	@ViewById(R.id.intro_edit)
	EditText intro_edit;
	@Extra
	String str_introduce;
	@Extra
	public int type;
	@Extra
	public String groupId;
	String intro;
	@Override
	public void init() {
		super.init();
		setTitleRightTxt("完成");
		intro_edit.setText(str_introduce);
		if(type==0){
			setTitleTxt("自我介绍");
		}else if(type==1){
			setTitleTxt("群介绍");
		}
	}

	@Override
	public void rightClick(View v) {
		super.rightClick(v);
		intro=intro_edit.getText().toString().trim();
		if(intro.length()>0&&!str_introduce.equals(intro)) {
			if(type==0){
				RequestParams params = getAjaxParams();
				params.put("introduce", intro);
				httpPost(URL.EDIT_USER_INFO, params);
			}else if(type==1){
				RequestParams params = getAjaxParams();
				params.put("groupId", groupId);
				params.put("groupInt", intro);
				httpPost(URL.EDIT_GROUP,params);
			}
		}else if(intro.trim().length()==0){
			Util.toast(mContext, "请输入文字");
		}
	}
	void httpPost(String url,RequestParams params){
		ac.finalHttp.post(url, params, new MyJsonHttpResponseHandler(mContext,Util.progress_arr[3]) {
			@Override
			public void onSuccessRetCode(JSONObject jo) throws Throwable {
				showCustomToast("修改成功");
				Intent intent = new Intent();
				intent.putExtra("introduce", intro);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
}