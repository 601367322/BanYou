package com.quanliren.quan_one.activity.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity
public class ModifyPasswordActivity extends BaseActivity {

	@ViewById(R.id.oldpassword)
	EditText oldpassword;
	@ViewById(R.id.password)
	EditText password;
	@ViewById(R.id.confirm_password)
	EditText confirm_password;
	@ViewById(R.id.modifyBtn)
	Button modifyBtn;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.modifypassword);
		setTitleTxt(R.string.seting_modify_password);
	}

	@Click(R.id.modifyBtn)
	public void ok(View v) {
		String str_oldpassword = oldpassword.getText().toString().trim();
		String str_password = password.getText().toString().trim();
		String str_confirm_password = confirm_password.getText().toString().trim();

		if (str_password.length() > 16 || str_password.length() < 6) {
			showCustomToast("密码长度为6-16个字符");
			return;
		} else if (!str_password.matches("^[a-zA-Z0-9 -]+$")) {
			showCustomToast("密码中不能包含特殊字符");
			return;
		} else if (!str_confirm_password.equals(str_password)) {
			showCustomToast("确认密码与密码不同");
			return;
		}

		RequestParams ap = getAjaxParams();
		ap.put("oldpwd", str_oldpassword);
		ap.put("pwd", str_password);
		ap.put("repwd", str_confirm_password);

		User lou = new User();
		lou.setMobile(ac.getUser().getMobile());
		lou.setPwd(str_password);
		ac.finalHttp.post(URL.MODIFYPASSWORD, ap, new callBack(lou));
	}


	class callBack extends MyJsonHttpResponseHandler {
		User u;

		public callBack(User u) {
			super(mContext, Util.progress_arr[1]);
			this.u = u;
		}

		@Override
		public void onSuccessRetCode(JSONObject jo) throws Throwable {
			showCustomToast("修改成功");

			DBHelper.moreLoginUserDao.update(u.getMobile(), u.getPwd());

			User user = new Gson().fromJson(jo.getString(URL.RESPONSE), User.class);
			LoginUser lu = new LoginUser(user.getId(), u.getMobile(), u.getPwd(), user.getToken());

			//保存登陆用户
			DBHelper.loginUserDao.clearTable();
			DBHelper.loginUserDao.create(lu);

			finish();
		}
	}
}
