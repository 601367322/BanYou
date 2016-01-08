package com.quanliren.quan_one.activity.reg;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.MainActivity_;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.date.PhotoAlbumMainActivity_;
import com.quanliren.quan_one.activity.user.LoginActivity_;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.custom.AddPicFragment;
import com.quanliren.quan_one.share.CommonShared;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@EActivity(R.layout.reg_second)
public class RegSecond extends BaseActivity {

    @Extra
    public String phone;
    @ViewById(R.id.age)
    TextView age;
    @ViewById(R.id.face)
    TextView face;
    @ViewById(R.id.password)
    EditText password;
    @ViewById(R.id.confirm_password)
    EditText confirm_password;
    @ViewById(R.id.nickname)
    EditText nickname;

    @ViewById(R.id.sex_btn)
    RadioGroup sex_btn;
    @ViewById(R.id.girl_rb)
    RadioButton girl_rb;
    @ViewById(R.id.boy_rb)
    RadioButton boy_rb;

    @ViewById(R.id.upload_userlogo)
    ImageView upload_userlogo;
    boolean canPost = true;
    @Override
    public void init() {
        super.init();
        title.setText(R.string.reg);
        setListener();
    }

    public void setListener() {
        upload_userlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_tou_img();
            }
        });
        sex_btn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                face.setText("");
            }
        });
	}
	
	public void rightClick() {
        if(canPost == false){
            return;
        }
		String str_nickname=nickname.getText().toString().trim();
		String str_password=password.getText().toString().trim();
		String str_confirm_password=confirm_password.getText().toString().trim();
		String str_age=age.getText().toString().trim();
        String str_face = face.getText().toString().trim();
        int sex =-1;
        int sexButtonId = sex_btn.getCheckedRadioButtonId();
        switch (sexButtonId) {
            case R.id.girl_rb:
                sex= 0;
                break;
            case R.id.boy_rb:
                sex= 1;
                break;
        }
        if(sex==-1){
            showCustomToast("请选择性别");
            return;
        }
        if (str_nickname.length() == 0) {
            showCustomToast("请输入昵称");
            return;
        } else if (Util.hasSpecialByte(str_nickname)) {
            showCustomToast("昵称中不能包含特殊字符");
            return;
        } else if (!Util.isStrNotNull(str_face)) {
            showCustomToast("请选择外貌");
            face.requestFocus();
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
		}else if(str_age.length()==0){
			showCustomToast("请选择出生日期");
			return;
		}
        if(fi==null){
            showCustomToast("请上传头像");
            return;
        }
		CommonShared cs=new CommonShared(getApplicationContext());
		
		RequestParams ap= getAjaxParams();
		ap.put("mobile", phone);
		ap.put("nickname", str_nickname);
		ap.put("pwd", str_password);
		ap.put("repwd", str_confirm_password);
		ap.put("birthday", str_age);
        ap.put("sex", sex+"");
		ap.put("cityid", String.valueOf(cs.getLocationID()));
		ap.put("longitude", ac.cs.getLng());
		ap.put("latitude", ac.cs.getLat());
		ap.put("area", cs.getArea());
        ap.put("appearance", str_face);
        ap.put("deviceid", ac.cs.getDeviceId());
		User lou=new User();
		lou.setMobile(phone);
		lou.setPwd(str_password);

		ac.finalHttp.post(URL.REG_THIRD, ap, new callBack(lou));
	}
    String[] height = new String[72];
    String[] weight = new String[72];
    String[] boy = { "清新俊秀","气宇轩昂", "高大威猛", "文质彬彬", "血性精悍", "儒雅风趣"};
    String[] girl = {"活泼可爱", "温柔可人", "娴静端庄", "秀外慧中", "妩媚妖艳", "火爆性感"};
    String[] body=null;


    @Click(R.id.face)
    public void face(View view) {
        if(!boy_rb.isChecked()&&!girl_rb.isChecked()){
            showCustomToast("请先选择性别");
            return;
        }
        for (int i = 0; i < 71; i++) {
            height[i] = 130 + i + "cm";
        }
        height[71] = 200 + "cm+";
        for (int i = 0; i < 71; i++) {
            weight[i] = 30 + i + "kg";
        }
        weight[71] = 100 + "kg+";
        View choseView = View.inflate(this, R.layout.chose_face, null);
        final NumberPicker npHeight = (NumberPicker) choseView
                .findViewById(R.id.height);
        npHeight.setMaxValue(height.length - 1);
        npHeight.setMinValue(0);
        npHeight.setFocusable(true);
        npHeight.setFocusableInTouchMode(true);
        npHeight.setDisplayedValues(height);
        npHeight.setValue(30);
        final NumberPicker npWeight = (NumberPicker) choseView
                .findViewById(R.id.weight);
        npWeight.setMaxValue(weight.length - 1);
        npWeight.setMinValue(0);
        npWeight.setFocusable(true);
        npWeight.setFocusableInTouchMode(true);
        npWeight.setDisplayedValues(weight);
        npWeight.setValue(15);
        final NumberPicker npBody = (NumberPicker) choseView
                .findViewById(R.id.body);
        if(boy_rb.isChecked()){
            body=boy;
        }else if(girl_rb.isChecked()){
            body=girl;
        }
        npBody.setMaxValue(body.length - 1);
        npBody.setMinValue(0);
        npBody.setFocusable(true);
        npBody.setFocusableInTouchMode(true);
        npBody.setDisplayedValues(body);
        if(!face.getText().equals("")){
            String face_str[]=face.getText().toString().split(" ");
            for(int i=0;i<height.length-1;i++){
                if (height[i].equals(face_str[0])){
                    npHeight.setValue(i);
                    break;
                }
            }
            for(int i=0;i<weight.length-1;i++){
                if (weight[i].equals(face_str[1])){
                    npWeight.setValue(i);
                    break;
                }
            }
            for(int i=0;i<body.length-1;i++){
                if (body[i].equals(face_str[2])||boy[i].equals(face_str[2])||girl[i].equals(face_str[2])){
                    npBody.setValue(i);
                    break;
                }
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("身高、体重、类型").setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            face.setText(height[npHeight.getValue()] + " "
                                    + weight[npWeight.getValue()] + " "
                                    + body[npBody.getValue()]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setView(choseView, 0, 0, 0, 0);
        dialog.show();
    }
    /**
     * 上传头像
     */
    public void add_tou_img() {
        new AlertDialog.Builder(this).setItems(new String[] { "相机", "从相册中选择" },
                menuClick).create().show();
        closeInput();

    }
    private static final int CAMERA_USERLOGO = 1;
    private static final int ALBUM_USERLOGO = 2;
    String cameraPath;
    DialogInterface.OnClickListener menuClick = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    if (Util.existSDcard()) {
                        Intent intent = new Intent(); // 调用照相机
                        String messagepath = StaticFactory.APKCardPath;
                        File fa = new File(messagepath);
                        if (!fa.exists()) {
                            fa.mkdirs();
                        }
                        cameraPath = messagepath + new Date().getTime();// 图片路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(cameraPath)));
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, AddPicFragment.Camera);
                    } else {
                        showCustomToast("亲，请检查是否安装存储卡!");
                    }
                    break;
                case 1:
                    if (Util.existSDcard()) {
                        String messagepath = StaticFactory.APKCardPath;
                        File fa = new File(messagepath);
                        if (!fa.exists()) {
                            fa.mkdirs();
                        }
                        PhotoAlbumMainActivity_.intent(mContext).maxnum(1).paths(new ArrayList<String>()).startForResult(AddPicFragment.Album);
                    } else {
                        showCustomToast("亲，请检查是否安装存储卡!");
                    }
                    break;
            }
        }
    };

    User user;
	class callBack extends MyJsonHttpResponseHandler {
		User u;
		
		public callBack(User u){
            super(mContext, Util.progress_arr[3]);
			this.u=u;
		}

        @Override
        public void onStart() {
            super.onStart();
            canPost=false;
        }

        @Override
        public void onSuccessRetCode(JSONObject jo) throws Throwable {
            DBHelper.moreLoginUserDao.update(u.getMobile(), u.getPwd());

            user=new Gson().fromJson(jo.getString(URL.RESPONSE), User.class);
            LoginUser lu=new LoginUser(user.getId(), u.getMobile(), u.getPwd(), user.getToken());

            //保存用户
            DBHelper.userTableDao.updateUser(user);

            //保存登陆用户
            DBHelper.loginUserDao.clearTable();
            DBHelper.loginUserDao.create(lu);

            ac.startServices();

            tongJi();
            //上传头像
            if(fi!=null&&!"".equals(fi.toString())){
                uploadUserLogo(fi);
            }else{
                canPost=true;
                finish();
            }
        }

        @Override
        public void onFailRetCode(JSONObject jo) {
            super.onFailRetCode(jo);
            canPost=true;
        }

        @Override
        public void onFailure() {
            super.onFailure();
            canPost=true;
        }
    }
	
	Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);

	public void back(View v) {
		dialogFinish();
	}
	
	public void onBackPressed() {
		dialogFinish();
	}
	
	public void dialogFinish(){
		new AlertDialog.Builder(RegSecond.this)
		.setMessage("您确定要放弃本次注册吗？")
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {

					public void onClick(
							DialogInterface dialog,
							int which) {
						finish();
					}
				})
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {

					public void onClick(
							DialogInterface arg0, int arg1) {
					}
				}).create().show();
	}

    File fi;
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                try {
                    if (cameraPath != null) {
                        fi = new File(cameraPath);
                        if (fi != null && fi.exists()) {
                            ImageUtil.downsize(cameraPath, cameraPath, this);

                            ImageLoader.getInstance().displayImage(
                                    Util.FILE + cameraPath, upload_userlogo);
                        }
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
                break;
            case 2:
                if (data == null) {
                    return;
                }
                ArrayList<String> list = data.getStringArrayListExtra("images");
                if (list.size() > 0) {
                    cameraPath = list.get(0);
                    fi = new File(cameraPath);
                    if (fi != null && fi.exists()) {
                        ImageUtil.downsize(cameraPath, cameraPath, this);
                        ImageLoader.getInstance().displayImage(
                                Util.FILE + cameraPath, upload_userlogo);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 上传头像方法
     *
     * @param file
     */
    public void uploadUserLogo(final File file) {
        try {
            RequestParams ap = getAjaxParams();
            ap.put("file", file);
            ap.put("regState", "1");
            ac.finalHttp.post(URL.UPLOAD_USER_LOGO, ap,
                    new MyJsonHttpResponseHandler(mContext,getString(R.string.upload_avater)) {

                        @Override
                        public void onSuccessRetCode(JSONObject jo) throws Throwable {
                            user.setAvatar(jo.getJSONObject(
                                    URL.RESPONSE).getString("imgurl"));

                            DBHelper.userTableDao.updateUser(user);

                            ac.startServices();

                            AM.getActivityManager().popActivity(LoginActivity_.class.getName());
                            if (!AM.getActivityManager().contains(MainActivity_.class.getName())) {
                                MainActivity_.intent(mContext).start();
                            }
                            finish();
                            canPost = true;
                        }

                        @Override
                        public void onFailRetCode(JSONObject jo) {
                            super.onFailRetCode(jo);
                            canPost=true;
                            finish();
                        }

                        @Override
                        public void onFailure() {
                            super.onFailure();
                            try {
                                AlertDialog dialog = new AlertDialog.Builder(
                                        RegSecond.this)
                                        .setMessage("上传失败，是否重试？")
                                        .setNegativeButton(
                                                "取消",
                                                new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int which) {

                                                    }
                                                })
                                        .setPositiveButton(
                                                "确定",
                                                new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int which) {
                                                        uploadUserLogo(file);
                                                    }
                                                }).create();
                                dialog.setCancelable(false);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }finally {
                                canPost=true;
                                finish();
                            }
                        }
                    });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Click(R.id.commit)
    void commit(View view){
        rightClick();
    }
}
