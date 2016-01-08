package com.quanliren.quan_one.activity.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseUserActivity;
import com.quanliren.quan_one.activity.date.PhotoAlbumMainActivity_;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.dao.LoginUserDao;
import com.quanliren.quan_one.dao.UserTableDao;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

@EActivity
public class UserInfoActivity extends BaseUserActivity implements OnClickListener {

    public static final String USERINFO_UPDATE_UI = "com.quanliren.quan_one.activity.user.UserInfoActivity";

    private static final int CAMERA_USERLOGO = 1;
    private static final int ALBUM_USERLOGO = 2;
    private static final int EDITUSERABLUM = 3;
    private static final int EDITUSERNAME = 5;
    private static final int EDITUSERINFO = 4;
    private static final int USERLOGO = 1;
    private static final int IMG = 2;
    private int uploadImgType;
    @ViewById(R.id.birthday)
    public TextView birthday;
    @ViewById(R.id.img_first)
    public ImageView img_first;
    @ViewById(R.id.vip_key)
    public TextView vip_key;
    @ViewById(R.id.vip_time)
    public TextView vip_time;
    @ViewById(R.id.identitystate)
    public TextView identitystate;
    @ViewById(R.id.vip_tr)
    public LinearLayout vip_tr;

    @ViewById(R.id.userlogo_ll)
    public LinearLayout userlogo_ll;
    @ViewById(R.id.img_first_ll)
    public LinearLayout img_first_ll;
    @ViewById(R.id.nickname_ll)
    public LinearLayout nickname_ll;
    @ViewById(R.id.signature_ll)
    public LinearLayout signature_ll;
    @ViewById(R.id.work_ll)
    public LinearLayout work_ll;
    @ViewById(R.id.income_ll)
    public LinearLayout income_ll;
    @ViewById(R.id.emotion_ll)
    public LinearLayout emotion_ll;
    @ViewById(R.id.introduce_ll)
    public LinearLayout introduce_ll;
    @ViewById(R.id.phone_ll)
    public LinearLayout phone_ll;
    @ViewById(R.id.identity_ll)
    public LinearLayout identity_ll;
    @ViewById(R.id.pay_ll)
    public LinearLayout pay_ll;
    @ViewById(R.id.identitystate_ll)
    public LinearLayout identitystate_ll;
    @ViewById(R.id.by_help)
    public ImageView by_help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_two);
        setTitleTxt("个人资料");
        setListener();
        receiveBroadcast(USERINFO_UPDATE_UI, broad);
    }

    public void setListener() {
        user = LoginUserDao.getInstance(this).getUserInfo();
        ac.finalHttp.post(URL.GET_USER_INFO, Util.getRequestParams(this),
                new MyJsonHttpResponseHandler(this, Util.progress_arr[1]) {
                    @Override
                    public void onSuccessRetCode(JSONObject jo) throws Throwable {
                        user = UserTableDao.getInstance(getApplicationContext()).updateUser(jo);
                        initViewUser();
                    }
                });
        if (user != null) {
            initViewUser();
        }
        userlogo_ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImgType = USERLOGO;
                uploadImg();
            }
        });
        signature_ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signature();
            }
        });

        img_first_ll.setOnClickListener(this);
        nickname_ll.setOnClickListener(this);
        work_ll.setOnClickListener(this);
        income_ll.setOnClickListener(this);
        emotion_ll.setOnClickListener(this);
        introduce_ll.setOnClickListener(this);
        phone_ll.setOnClickListener(this);
        identity_ll.setOnClickListener(this);
        pay_ll.setOnClickListener(this);
        identitystate_ll.setOnClickListener(this);
        birthday.addTextChangedListener(tw_birthday);

        if (!Utils.showCoin(this)) {
            pay_ll.setVisibility(View.GONE);
        }
    }

    TextWatcher tw_birthday = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals(user.getBirthday())) {
                str_age = s.toString();
                ap = Util.getRequestParams(mContext);
                ap.put("birthday", s.toString());
                commitEditInfo(birthday);
            }
        }
    };
    String str_nickname;
    String str_age;
    String str_income;
    String str_work;
    String str_signature;
    String str_emotion;
    String str_introduce;
    int int_showState;
    int int_identity;
    int int_pay;

    int int_id_state;

    void initViewUser() {
        initViewByUser();
        birthday.setText(user.getBirthday());
        if (user.getImglist() != null) {
            if (user.getImglist().size() > 0) {
                img_first.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(user.getImglist().get(0).imgpath, img_first);
            } else {
                img_first.setImageResource(R.drawable.default_userlogo);
            }
        } else {
            img_first.setVisibility(View.GONE);
        }
        if (user.getSex().equals("0") || "女".equals(user.getSex())) {
            sex.setText("女");
        } else {
            sex.setText("男");
        }
        if (user.getIsvip() > 0) {
            vip_tr.setVisibility(View.VISIBLE);
            if (user.getIsvip() == 1) {
                vip_key.setText("普通会员");
            } else {
                vip_key.setText("富豪会员");
            }
            try {
                vip_time.setText(Util.fmtDate.format(Util.fmtDate.parse(user.getViptime())) + " 到期");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            vip_tr.setVisibility(View.GONE);
        }
        if (user.getIdentity() == 0) {
            identitystate_ll.setVisibility(View.GONE);
        } else {
            identitystate_ll.setVisibility(View.VISIBLE);
            identitystate.setText(banyouStates[user.getIdentityState()]);
        }
        str_nickname = user.getNickname();
        str_age = user.getBirthday();
        str_income = user.getIncome();
        str_work = user.getJob();
        str_signature = user.getSignature();
        str_emotion = user.getEmotion();
        str_introduce = user.getIntroduce();
        int_showState = user.getShowState();
        int_identity = user.getIdentity();
        int_pay = user.getPay();
        int_id_state = user.getIdentityState();
    }

    Handler broad = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            Intent i = (Intent) msg.obj;
            String action = i.getAction();
            if (action.equals(USERINFO_UPDATE_UI)) {
                user = DBHelper.loginUserDao.getUserInfo();
                initViewUser();
                if (user != null && !Util.isStrNotNull(user.getAvatar())) {
                    userlogo.setImageResource(R.drawable.default_userlogo);
                }
            }
            super.dispatchMessage(msg);
        }

        ;
    };


    String cameraUserLogo;

    public void uploadImg() {
        AlertDialog dialog = new AlertDialog.Builder(this).setItems(
                new String[]{"相册", "拍照"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 1:
                                if (Util.existSDcard()) {
                                    Intent intent = new Intent(); // 调用照相机
                                    String messagepath = StaticFactory.APKCardPath;
                                    File fa = new File(messagepath);
                                    if (!fa.exists()) {
                                        fa.mkdirs();
                                    }
                                    cameraUserLogo = messagepath
                                            + new Date().getTime();// 图片路径
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(new File(cameraUserLogo)));
                                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent, CAMERA_USERLOGO);
                                } else {
                                    Util.toast(mContext, "亲，请检查是否安装存储卡!");
                                }
                                break;
                            case 0:
                                if (Util.existSDcard()) {
                                    String messagepath = StaticFactory.APKCardPath;
                                    File fa = new File(messagepath);
                                    if (!fa.exists()) {
                                        fa.mkdirs();
                                    }
                                    switch (uploadImgType) {
                                        case IMG:
                                            break;
                                        default:
                                            PhotoAlbumMainActivity_.intent(mContext).maxnum(1).startForResult(ALBUM_USERLOGO);
                                            break;
                                    }
                                } else {
                                    Util.toast(mContext, "亲，请检查是否安装存储卡!");
                                }
                                break;
                        }
                    }
                }).create();
        switch (uploadImgType) {
            case USERLOGO:
                dialog.setTitle("更换头像");
            default:
                break;
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void signature() {
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
        if (user.getSex().equals("1")) {
            body = boy;
        } else if (user.getSex().equals("0")) {
            body = girl;
        }
        npBody.setMaxValue(body.length - 1);
        npBody.setMinValue(0);
        npBody.setFocusable(true);
        npBody.setFocusableInTouchMode(true);
        npBody.setDisplayedValues(body);
        if (!"".equals(signature.getText().toString().trim())) {
            String face_str[] = signature.getText().toString().split(" ");
            for (int i = 0; i < height.length - 1; i++) {
                if (height[i].equals(face_str[0])) {
                    npHeight.setValue(i);
                    break;
                }
            }
            for (int i = 0; i < weight.length - 1; i++) {
                if (weight[i].equals(face_str[1])) {
                    npWeight.setValue(i);
                    break;
                }
            }
            for (int i = 0; i < body.length - 1; i++) {
                if (body[i].equals(face_str[2]) || boy[i].equals(face_str[2]) || girl[i].equals(face_str[2])) {
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
                        str_signature = height[npHeight.getValue()] + " " + weight[npWeight.getValue()] + " " + body[npBody.getValue()];
                        if (!str_signature.equals(user.getSignature())) {
                            ap = Util.getRequestParams(mContext);
                            ap.put("appearance", str_signature);
                            commitEditInfo(signature);
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setView(choseView, 0, 0, 0, 0);
        dialog.show();
    }

    public void uploadUserLogo(final File file) {
        try {
            RequestParams ap = Util.getRequestParams(getApplicationContext());
            ap.put("file", file);
            ac.finalHttp.post(URL.UPLOAD_USER_LOGO, ap, new MyJsonHttpResponseHandler() {
                @Override
                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                    Util.toast(UserInfoActivity.this, "上传成功");
                    try {
                        ImageLoader.getInstance().displayImage(
                                jo.getJSONObject(URL.RESPONSE)
                                        .getString("imgurl")
                                        + StaticFactory._320x320,
                                userlogo);
                        user.setAvatar(jo.getJSONObject(
                                URL.RESPONSE).getString("imgurl"));
                        DBHelper.userTableDao.updateUser(user);
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure() {
                    super.onFailure();
                    try {
                        AlertDialog dialog = new AlertDialog.Builder(
                                UserInfoActivity.this)
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
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @OnActivityResult(EDITUSERINFO)
    void onResultInfo(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            str_introduce = data.getStringExtra("introduce");
            user.setIntroduce(str_introduce);
            DBHelper.userTableDao.updateUser(user);
            introduce.setText(str_introduce);
        }
    }

    @OnActivityResult(EDITUSERABLUM)
    void onResultAblum(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            RequestParams rp = Util.getRequestParams(mContext);
            rp.put("userid", user.getId());
            ac.finalHttp.post(URL.GET_USER_INFO, rp
                    , new MyJsonHttpResponseHandler(this, Util.progress_arr[1]) {
                @Override
                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                    user = UserTableDao.getInstance(getApplicationContext()).updateUser(jo);
                    initViewUser();
                }
            });
        }

    }

    @OnActivityResult(EDITUSERNAME)
    void onResultName(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            str_nickname = data.getStringExtra("nickname");
            user.setNickname(str_nickname);
            DBHelper.userTableDao.updateUser(user);
            nickname.setText(str_nickname);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ALBUM_USERLOGO) {
            if (data == null) {
                return;
            }
            ArrayList<String> list = data.getStringArrayListExtra("images");
            if (list.size() > 0) {
                cameraUserLogo = list.get(0);
                File fi = new File(cameraUserLogo);
                if (fi != null && fi.exists()) {
                    ImageUtil.downsize(cameraUserLogo, cameraUserLogo, this);
                    switch (uploadImgType) {
                        case USERLOGO:
                            ImageLoader.getInstance().displayImage(
                                    Util.FILE + cameraUserLogo, userlogo);
                            break;
                    }
                    switch (uploadImgType) {
                        case 1:
                            uploadUserLogo(fi);
                            break;
                        case 2:

                            break;
                    }
                }
            }
        }else if (requestCode == CAMERA_USERLOGO) {
            try {
                if (cameraUserLogo != null) {
                    File fi = new File(cameraUserLogo);
                    if (fi != null && fi.exists()) {
                        ImageUtil.downsize(cameraUserLogo, cameraUserLogo, this);
                        switch (uploadImgType) {
                            case USERLOGO:
                                ImageLoader.getInstance().displayImage(
                                        Util.FILE + cameraUserLogo, userlogo);
                                break;
                        }
                        switch (uploadImgType) {
                            case 1:
                                uploadUserLogo(fi);
                                break;
                            case 2:

                                break;
                        }
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {

        super.onResume();
        user = LoginUserDao.getInstance(this).getUserInfo();
    }

    String[] height = new String[72];
    String[] weight = new String[72];
    String[] boy = {"清新俊秀", "气宇轩昂", "高大威猛", "文质彬彬", "血性精悍", "儒雅风趣"};
    String[] girl = {"活泼可爱", "温柔可人", "娴静端庄", "秀外慧中", "妩媚妖艳", "火爆性感"};
    String[] body = null;
    String[] ol = {"计算机/互联网/通信", "生产/工艺/制造", "商业/服务业/个人体经营",
            "金融/银行／投资／保险", "文化／广告／传媒", "娱乐／艺术／表演", "医疗／护理／制药", "公务员／事业单位",
            "学生", "无"};
    String[] moneys = {"4000元以下", "4001-6000元", "6001-10000元", "10001-15000元",
            "15001-20000元", "20001-50000元", "50000元以上"};
    String[] loves = {"单身", "恋爱中", "已婚"};
    String[] pays = {"100-500元/天", "500-1000元/天", "1000-2000元/天", "2000-3000元/天", "3000-4000元/天", "4000-5000元/天", "5000-10000元/天", "1万元以上/天"};

    String[] mobileshows = {"只对会员公开", "不公开"};
    String[] identitys = {"游客", "伴游"};
    String[] banyouStates = {"私人伴游", "学生伴游", "商务伴游", "交友伴游", "异国伴游", "英语伴游", "景点伴游", "模特影视"};

    RequestParams ap;

    @Override
    public void onClick(final View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
        switch (v.getId()) {
            case R.id.work_ll:
                builder.setItems(ol, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int position) {
                        work.setTag(position + "");
                        if (position != java.util.Arrays.asList(ol).indexOf(user.getJob())) {
                            str_work = ol[position];
                            ap = Util.getRequestParams(mContext);
                            ap.put("job", position + "");
                            commitEditInfo(work);
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.setCanceledOnTouchOutside(true);
                alert.show();
                break;
            case R.id.income_ll:
                builder.setItems(moneys, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int position) {
                        income.setTag(position + "");
                        if (position != java.util.Arrays.asList(moneys).indexOf(user.getIncome())) {
                            str_income = moneys[position];
                            ap = Util.getRequestParams(mContext);
                            ap.put("income", position + "");
                            commitEditInfo(income);
                        }
                    }
                });
                AlertDialog icalert = builder.create();
                icalert.setCanceledOnTouchOutside(true);
                icalert.show();
                break;
            case R.id.emotion_ll:
                builder.setItems(loves, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int position) {
                        emotion.setTag(position + "");
                        if (position != java.util.Arrays.asList(loves).indexOf(user.getEmotion())) {
                            str_emotion = loves[position];
                            ap = Util.getRequestParams(mContext);
                            ap.put("emotion", position + "");
                            commitEditInfo(emotion);
                        }
                    }
                });
                AlertDialog emo_alert = builder.create();
                emo_alert.setCanceledOnTouchOutside(true);
                emo_alert.show();
                break;
            case R.id.pay_ll:
                builder.setItems(pays, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int position) {
                        pay.setTag(position + "");
                        if (position != user.getPay()) {
                            int_pay = position;
                            ap = Util.getRequestParams(mContext);
                            ap.put("pay", position + "");
                            commitEditInfo(pay);
                        }
                    }
                });
                AlertDialog pay_alert = builder.create();
                pay_alert.setCanceledOnTouchOutside(true);
                pay_alert.show();
                break;
            case R.id.identitystate_ll:
                builder.setItems(banyouStates, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int position) {
                        identitystate.setTag(position + "");
                        if (position != user.getIdentityState()) {
                            int_id_state = position;
                            ap = getAjaxParams();
                            ap.put("identityState", position + "");
                            commitEditInfo(identitystate);
                        }
                    }
                });
                AlertDialog id_state_alert = builder.create();
                id_state_alert.setCanceledOnTouchOutside(true);
                id_state_alert.show();
                break;
            case R.id.phone_ll:
                builder.setItems(mobileshows, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int position) {
                        phone.setTag(position + "");
                        if (position != user.getShowState()) {
                            int_showState = position;
                            ap = Util.getRequestParams(mContext);
                            ap.put("showState", position + "");
                            commitEditInfo(phone);
                        }
                    }
                });
                AlertDialog show_alert = builder.create();
                show_alert.setCanceledOnTouchOutside(true);
                show_alert.show();
                break;
            case R.id.img_first_ll:
                UserAlbumEditActivity_.intent(mContext).userid(user.getId()).imglist(user.getImglist()).startForResult(EDITUSERABLUM);
                break;
            case R.id.nickname_ll:
                NicknameEditActivity_.intent(mContext).str_nickname(str_nickname).startForResult(EDITUSERNAME);
                break;
            case R.id.introduce_ll:
                IntroEditActivity_.intent(mContext).str_introduce(str_introduce).startForResult(EDITUSERINFO);
                break;
            case R.id.identity_ll:
                if (user.getIdentity() == 1) {
                    showCustomToast("亲~，伴游身份不能更改哦");
                    return;
                } else {
                    builder.setItems(identitys, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int position) {

                            if (position == 0) {
                                showCustomToast("您已是游客，无需更改，建议更改为伴游哦~");
                                return;
                            } else {
                                int_identity = 1;
                                transForBanYou();
                            }
                        }
                    });
                    AlertDialog id_alert = builder.create();
                    id_alert.setCanceledOnTouchOutside(true);
                    id_alert.show();

                }

                break;

        }
    }

    /**
     * 游客转伴游
     */
    private void transForBanYou() {
        new AlertDialog.Builder(UserInfoActivity.this)
                .setMessage("选择伴游后，将不可更改，是否继续？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        ap = getAjaxParams();
                        ap.put("identity", int_identity + "");
                        commitEditInfo(identity);
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                }).create().show();
    }

    public void commitEditInfo(View v) {
        ac.finalHttp.post(URL.EDIT_USER_INFO, ap, new EditCallBack(v));
    }

    class EditCallBack extends MyJsonHttpResponseHandler {
        View v;

        public EditCallBack(View v) {
            super(mContext, "正在更新信息");
            this.v = v;
        }

        @Override
        public void onSuccessRetCode(JSONObject jo) throws Throwable {
            switch (v.getId()) {
                case R.id.work:
                    user.setJob(str_work);
                    break;
                case R.id.income:
                    user.setIncome(str_income);
                    break;
                case R.id.emotion:
                    user.setEmotion(str_emotion);
                    break;
                case R.id.pay:
                    user.setPay(int_pay);
                    break;
                case R.id.phone:
                    user.setShowState(int_showState);
                    break;
                case R.id.identity:
                    user.setIdentity(int_identity);
                    break;
                case R.id.signature:
                    user.setSignature(str_signature);
                    break;
                case R.id.birthday:
                    user.setBirthday(str_age);
                    break;
                case R.id.nickname:
                    user.setNickname(str_nickname);
                    break;
                case R.id.introduce:
                    user.setIntroduce(str_introduce);
                    break;
                case R.id.identitystate:
                    user.setIdentityState(int_id_state);
                    break;
            }
            DBHelper.userTableDao.updateUser(user);
            initViewUser();
            showCustomToast("修改成功");
        }
    }

    ;

    /**
     * 跳转伴游身份页面
     */
    @Click(R.id.by_help)
    public void byHelp(View view) {
        HtmlActivity_.intent(mContext).url("file:///android_asset/banyou.html").title_txt("身份说明").start();
    }
}
