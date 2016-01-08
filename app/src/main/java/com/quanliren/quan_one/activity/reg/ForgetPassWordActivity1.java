package com.quanliren.quan_one.activity.reg;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity
public class ForgetPassWordActivity1 extends BaseActivity {

	@ViewById(R.id.phone)
	EditText phoneEt;
	@ViewById(R.id.password)
	EditText password;
	@ViewById(R.id.confirm_password)
	EditText confirm_password;
	@ViewById(R.id.code)
	EditText code;
	@ViewById(R.id.sendCode)
	Button sendCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forget_first);
		title.setText(R.string.findpassword);
	}
	int allSec = 180;
	@Click(R.id.sendCode)
	void sendCode(View view){
		String pstr = phoneEt.getText().toString();
		if (Util.isMobileNO(pstr)) {
			RequestParams ap=getAjaxParams();
			ap.put("mobile", pstr);
			ac.finalHttp.post(URL.FINDPASSWORD_FIRST, ap, new MyJsonHttpResponseHandler(mContext,Util.progress_arr[1]) {
				@Override
				public void onSuccessRetCode(JSONObject jo) throws Throwable {
					allSec = 180;
					sendCode.setText("重新获取" + allSec + "");
					handler.postDelayed(runable, 1000);
					sendCode.setEnabled(false);
				}
			});
		} else {
			showCustomToast("请输入正确的手机号码！");
			return;
		}
	}
	Runnable runable = new Runnable() {

		@Override
		public void run() {
			allSec--;
			sendCode.setText("重新获取"+allSec + "");
			if (allSec > 0) {
				handler.postDelayed(runable, 1000);
			}else {
				sendCode.setText("获取验证码");
				sendCode.setEnabled(true);
			}
		}
	};

	Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			super.dispatchMessage(msg);
		}

		;
	};
	@Click(R.id.commit)
	void commit(View view){
		String str_password=password.getText().toString().trim();
		String phone=phoneEt.getText().toString().trim();
		String str_confirm_password=confirm_password.getText().toString().trim();
		String str_code=code.getText().toString().trim();
		if (!Util.isMobileNO(phone)) {
			showCustomToast("请输入正确的手机号码！");
			return;
		}
		if(str_code.trim().length()!=6){
			showCustomToast("请输入正确的验证码！");
			return;
		}else	if(str_password.length()>16||str_password.length()<6){
			showCustomToast("密码长度为6-16个字符");
			return;
		}else if(!str_password.matches("^[a-zA-Z0-9 -]+$")){
			showCustomToast("密码中不能包含特殊字符");
			return;
		}else if(!str_confirm_password.equals(str_password)){
			showCustomToast("确认密码与密码不同");
			return;
		}

		RequestParams ap=getAjaxParams();
		ap.put("mobile", phone);
		ap.put("authcode", str_code);
		ap.put("pwd", str_password);
		ap.put("repwd", str_confirm_password);

		User lou=new User();
		lou.setMobile(phone);
		lou.setPwd(str_password);
		ac.finalHttp.post(URL.FINDPASSWORD_SECOND,ap, new MyJsonHttpResponseHandler(mContext, Util.progress_arr[1]) {
			@Override
			public void onSuccessRetCode(JSONObject jo) throws Throwable {
				showCustomToast("修改成功");
				finish();
			}
		});
	}

}
