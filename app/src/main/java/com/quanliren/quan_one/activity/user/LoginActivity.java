package com.quanliren.quan_one.activity.user;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.MainActivity_;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.location.GDLocation;
import com.quanliren.quan_one.activity.location.ILocationImpl;
import com.quanliren.quan_one.activity.reg.ForgetPassWordActivity1_;
import com.quanliren.quan_one.activity.reg.RegFirst_;
import com.quanliren.quan_one.activity.seting.TestSetting_;
import com.quanliren.quan_one.adapter.ParentsAdapter;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.MoreLoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.custom.CustomRelativeLayout;
import com.quanliren.quan_one.custom.CustomRelativeLayout.OnSizeChangedListener;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.dao.MoreLoginUserDao;
import com.quanliren.quan_one.dao.UserTableDao;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@EActivity
public class LoginActivity extends BaseActivity implements ILocationImpl{

	@ViewById(R.id.userlogo)
    ImageView userlogo;
    @ViewById(R.id.username)
	EditText username;
	@ViewById(R.id.password)
	EditText password;
	@ViewById(R.id.username_ll)
	View username_ll;
	@ViewById(R.id.ip_test)
	Button ip_test;
	@ViewById(R.id.crl)
	CustomRelativeLayout crl;
	@ViewById(R.id.title)
	TextView title;
	@ViewById(R.id.forgetpassword)
	TextView forgetpassword;
	@ViewById(R.id.loginBtn)
	Button loginBtn;
	@ViewById(R.id.regBtn)
	TextView regBtn;
	@ViewById(R.id.margin_ll)LinearLayout margin_ll;
	@ViewById(R.id.delete_username_btn)
	View delete_username_btn;
	@ViewById(R.id.delete_password_btn)
	View delete_password_btn;
	@ViewById(R.id.more_username_btn)
	View more_username_btn;
	boolean isShow = false; // 更多用户名是否展开
	private PopupWindow pop;
	private PopupAdapter adapter;
	private ListView listView;
	private List<MoreLoginUser> names = new ArrayList<MoreLoginUser>();
	String str_username, str_password;
	GDLocation location;
	private int _oldh=-1;
	private boolean isOpenEdit=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		title.setText(R.string.login);
		// forgetpassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		setListener();
		location = new GDLocation(getApplicationContext(),this,true);
		setSwipeBackEnable(false);

		crl.setOnSizeChangedListener(new OnSizeChangedListener() {

			@Override
			public void onSizeChanged(int w, final int h, final int oldw,
					final int oldh) {
				new Handler().post(new Runnable() {

					@Override
					public void run() {
						if (oldh == 0) {
							return;
						}
						if (_oldh == -1) {
							_oldh = oldh;
						}
						if (h >= _oldh) {
							isOpenEdit = false;

						} else if (h < _oldh) {
							isOpenEdit = true;
						}
						if (!isOpenEdit) {
							RelativeLayout.LayoutParams lp=(RelativeLayout.LayoutParams) margin_ll.getLayoutParams();
							lp.topMargin=ImageUtil.dip2px(LoginActivity.this, 50);
							margin_ll.setLayoutParams(lp);

							if (isShow && pop != null) {
								initUserNamePop();
							}
						} else {
							RelativeLayout.LayoutParams lp=(RelativeLayout.LayoutParams) margin_ll.getLayoutParams();
							lp.topMargin=0;
							margin_ll.setLayoutParams(lp);
							if (isShow && pop != null) {
								initUserNamePop();
							}
						}
					}
				});
			}
		});

		try {
			ApplicationInfo appInfo = getPackageManager()
					.getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA);
			String msg = appInfo.metaData.getString("TEST_SETTING");
			if (msg.equals("open")) {
				ip_test.setVisibility(View.VISIBLE);
			} else {
				ip_test.setVisibility(View.GONE);
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void back(View v) {
	}

	public void setListener() {
		username.addTextChangedListener(usernameTW);
		password.addTextChangedListener(passwordTW);
	}

	@Click(R.id.regBtn)
	public void reg(View v) {
		RegFirst_.intent(this).start();
	}

	@Click(R.id.delete_username_btn)
	public void delete_username_btn(View v) {
		username.setText("");
	}

	@Click(R.id.delete_password_btn)
	public void delete_password_btn(View v) {
		password.setText("");
	}

	@Click(R.id.more_username_btn)
	public void more_username_btn(View v) {
		if (!isShow) {
			isShow = true;
			initUserNamePop();
		} else {
			isShow = false;
			initUserNamePop();
		}
	}

	/**
	 * 用户名输入框的输入事件
	 */
	TextWatcher usernameTW = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().length() > 0) {
				delete_username_btn.setVisibility(View.VISIBLE);
			} else {
				delete_username_btn.setVisibility(View.GONE);
			}
            try {
                UserTable uts= UserTableDao.getInstance(getApplicationContext()).getUserByUserName(s.toString());
                if (uts !=null) {
                    ImageLoader.getInstance().displayImage(
                            uts.getUser().getAvatar() + StaticFactory._320x320,
                            userlogo, AppClass.options_userlogo);
                } else {
                    userlogo.setImageResource(R.drawable.touxiang);
                }
            }catch (Exception e){

            }
        }
	};
	TextWatcher passwordTW = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().length() > 0) {
				delete_password_btn.setVisibility(View.VISIBLE);
			} else {
				delete_password_btn.setVisibility(View.GONE);
			}
		}
	};

    @Override
    public void onLocationSuccess() {

    }

    @Override
    public void onLocationFail() {

    }

    class PopupAdapter extends ParentsAdapter {

		public PopupAdapter(Context c, List list) {
			super(c, list);
		}

		public View getView(final int position, View convertView, ViewGroup arg2) {

			ViewHolder holder = null;
			final String name = ((MoreLoginUser) list.get(position))
					.getUsername();
			final String pass = ((MoreLoginUser) list.get(position))
					.getPassword();
			if (convertView == null) {
				convertView = View.inflate(c, R.layout.username_popup, null);
				holder = new ViewHolder();
				holder.tv = (TextView) convertView.findViewById(R.id.more_user);
				holder.iv = (ImageView) convertView
						.findViewById(R.id.more_clear);
				holder.ll = (LinearLayout) convertView
						.findViewById(R.id.more_user_ll);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv.setText(name);
			holder.ll.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {

					initUserNamePop();
					username.setText(name);
					password.setText(pass);
				}
			});

			holder.iv.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					names.remove(position);
                    DBHelper.moreLoginUserDao.delete(name);
					adapter.notifyDataSetChanged();
					if (names.size() == 0) {
						initUserNamePop();
					}
				}
			});
			if (position == (list.size() - 1)) {
				holder.ll.setBackgroundResource(R.drawable.input_btm_btn);
			} else {
				holder.ll.setBackgroundResource(R.drawable.input_mid_btn);
			}
			return convertView;
		}

	}

	static class ViewHolder {
		TextView tv;
		ImageView iv;
		LinearLayout ll;
	}

	public void initUserNamePop() {
		if (pop == null) {
			if (adapter == null) {
				names = MoreLoginUserDao.getInstance(getApplicationContext()).findAllMoreUser();
				adapter = new PopupAdapter(getApplicationContext(), names);
				listView = new ListView(LoginActivity.this);
				int width = username_ll.getWidth();
				pop = new PopupWindow(listView, width,
						LayoutParams.WRAP_CONTENT);
				pop.setOutsideTouchable(true);
				listView.setItemsCanFocus(false);
				listView.setDivider(null);
				listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				listView.setAdapter(adapter);
				pop.setFocusable(false);
				pop.showAsDropDown(username_ll);
				isShow = true;
				more_username_btn.animate().setDuration(200).rotation(180)
						.start();
			}
		} else if (pop.isShowing()) {
			pop.dismiss();
			isShow = false;
			more_username_btn.animate().setDuration(200).rotation(0).start();
		} else if (!pop.isShowing()) {
			names = MoreLoginUserDao.getInstance(getApplicationContext()).findAllMoreUser();
			adapter.setList(names);
			adapter.notifyDataSetChanged();
			pop.showAsDropDown(username_ll);
			isShow = true;
			more_username_btn.animate().setDuration(200).rotation(180).start();
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (isShow && pop != null) {
				initUserNamePop();
				return true;
			}
		}
		return super.onTouchEvent(event);
	}

	public void onBackPressed() {

		if (isShow && pop != null) {
			initUserNamePop();
			return;
		}
		super.onBackPressed();
	}

	@Click(R.id.loginBtn)
	public void login(View v) {
		str_username = username.getText().toString().trim();
		str_password = password.getText().toString().trim();

		if (!Util.isMobileNO(str_username)) {
			showCustomToast("请输入正确的用户名");
			return;
		} else if (!Util.isPassword(str_password)) {
			showCustomToast("请输入正确的密码");
			return;
		}

		RequestParams ap = getAjaxParams();
		ap.put("mobile", str_username);
		ap.put("pwd", str_password);
		ap.put("cityid", String.valueOf(ac.cs.getLocationID()));
		ap.put("longitude", ac.cs.getLng());
		ap.put("latitude", ac.cs.getLat());
		ap.put("area", ac.cs.getArea());
		ap.put("dtype", "0");
		ap.put("deviceid", ac.cs.getDeviceId());

		ac.finalHttp.post(URL.LOGIN, ap, new MyJsonHttpResponseHandler(this,Util.progress_arr[2]) {

			@Override
			public void onStart() {
				super.onStart();
				Utils.closeSoftKeyboard(mContext);
			}

			@Override
			public void onSuccessRetCode(JSONObject jo) throws Throwable {
				// 登陆记录
				DBHelper.moreLoginUserDao.update(str_username, str_password);

				User u = new Gson().fromJson(jo.getString(URL.RESPONSE),
						User.class);
				LoginUser lu = new LoginUser(u.getId(), str_username,
						str_password, u.getToken());

				//保存用户
				DBHelper.userTableDao.updateUser(u);

				//保存登陆用户
				DBHelper.loginUserDao.clearTable();
				DBHelper.loginUserDao.create(lu);

				ac.startServices();

				if (!AM.getActivityManager().contains(
						MainActivity_.class.getName())) {
					MainActivity_.intent(mContext).start();
				}
				tongJi();
				finish();
			}

		});
	}


	protected void onDestroy() {
		super.onDestroy();
		location.destory();
	};

	@Click(R.id.forgetpassword)
	public void findpassword(View v) {
		ForgetPassWordActivity1_.intent(this).start();
	}

	@Click(R.id.ip_test)
	public void ip_test(View view){
		TestSetting_.intent(mContext).start();
	}
}
