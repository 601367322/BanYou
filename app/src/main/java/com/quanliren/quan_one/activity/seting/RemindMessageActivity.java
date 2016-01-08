package com.quanliren.quan_one.activity.seting;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.shop.ShopVipDetailActivity_;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity
public class RemindMessageActivity extends BaseActivity implements
		OnCheckedChangeListener {

	@ViewById(R.id.voice_cb)
	CheckBox voice_cb;
	@ViewById(R.id.msg_cb)
	CheckBox msg_cb;
	@ViewById(R.id.zhendong_cb)
	CheckBox zhendong_cb;
	@ViewById(R.id.go_vip)
	public TextView go_vip;
	@ViewById(R.id.tv_reg_notice)
	public TextView tv_reg_notice;
	@ViewById(R.id.cb_reg_notice)
	CheckBox cb_reg_notice;
	User user=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.remind_message);
		setTitleTxt("消息通知");
		initCb();
		voice_cb.setOnCheckedChangeListener(this);
		msg_cb.setOnCheckedChangeListener(this);
		zhendong_cb.setOnCheckedChangeListener(this);
		user=ac.getUserInfo();
		derail=user.getDerail();
		if(user!=null&&user.getIsvip()==2){
			cb_reg_notice.setOnCheckedChangeListener(this);
			cb_reg_notice.setChecked(user.getDerail()==0?true:false);
			go_vip.setVisibility(View.GONE);
		}else{
			go_vip.setVisibility(View.VISIBLE);
			tv_reg_notice.setTextColor(getResources().getColor(R.color.signature));
			cb_reg_notice.setChecked(false);
			cb_reg_notice.setClickable(false);
		}
	}

	public void initCb() {
		int num = ac.cs.getMSGOPEN();
		if (num == 1) {
			msg_cb.setChecked(true);
			if (ac.cs.getVIDEOOPEN() == 1) {
				voice_cb.setChecked(true);
			}else{
				voice_cb.setChecked(false);
			}
			if (ac.cs.getZHENOPEN() == 1) {
				zhendong_cb.setChecked(true);
			}else{
				zhendong_cb.setChecked(false);
			}
		} else {
			msg_cb.setChecked(false);
			voice_cb.setChecked(false);
			zhendong_cb.setChecked(false);
		}

	}
	void initRN(){
		if(user!=null){
			if(user.getIsvip()==2){
				cb_reg_notice.setChecked(user.getDerail()==0?true:false);
			}else{
				cb_reg_notice.setChecked(false);
			}
		}
	}
	int derail=0;
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.voice_cb:
			ac.cs.setVIDEOOPEN(isChecked?1:0);
			if(isChecked)
				ac.cs.setMSGOPEN(1);
			else{
				if(!zhendong_cb.isChecked()){
					ac.cs.setMSGOPEN(0);
				}
			}
			initCb();
			break;
		case R.id.msg_cb:
			ac.cs.setMSGOPEN(isChecked ? 1 : 0);
			initCb();
			break;
		case R.id.zhendong_cb:
			ac.cs.setZHENOPEN(isChecked ? 1 : 0);
			if(isChecked)
				ac.cs.setMSGOPEN(1);
			else{
				if(!voice_cb.isChecked()){
					ac.cs.setMSGOPEN(0);
				}
			}
			initCb();
			break;
		case R.id.cb_reg_notice:  //开启或者关闭新注册用户消息通知
			if(user!=null){
				derail=isChecked ? 0 : 1;
				if(derail!=user.getDerail()){
					ac.finalHttp.post(URL.EDIT_USER_INFO, getAjaxParams("derail", derail+""), new MyJsonHttpResponseHandler(mContext) {

						@Override
						public void onSuccessRetCode(JSONObject jo) throws Throwable {
							user.setDerail(derail);
							DBHelper.userTableDao.updateUser(user);
							initRN();
							showCustomToast("修改成功");
						}

					});
				}
			}
			break;
		}

	}
	@Click(R.id.go_vip)
	public void go_Vip(View view){
		ShopVipDetailActivity_.intent(mContext).start();
	}
}
