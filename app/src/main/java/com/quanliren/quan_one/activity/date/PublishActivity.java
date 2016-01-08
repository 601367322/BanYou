package com.quanliren.quan_one.activity.date;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.location.GDLocation;
import com.quanliren.quan_one.activity.location.ILocationImpl;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.custom.emoji.EmoteInputView;
import com.quanliren.quan_one.fragment.date.ChosePositionFragment;
import com.quanliren.quan_one.fragment.custom.AddPicFragment;
import com.quanliren.quan_one.fragment.custom.AddPicFragment.OnArticleSelectedListener;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

@EActivity
public class PublishActivity extends BaseActivity implements
        OnArticleSelectedListener, ILocationImpl {

    @ViewById(R.id.tv_address)
    TextView tv_address;
    @ViewById(R.id.ll_place)
    View ll_place;
    @ViewById(R.id.chat_eiv_inputview)
    EmoteInputView gridview;
    @ViewById(R.id.text)
    EditText edittxt;

    @ViewById(R.id.pb_time)
    ImageView pb_time;
    @ViewById(R.id.pb_place)
    ImageView pb_place;
    @ViewById(R.id.pb_gender)
    ImageView pb_gender;
    @ViewById(R.id.pb_cash)
    ImageView pb_cash;
    @ViewById(R.id.pb_remark)
    ImageView pb_remark;

    AddPicFragment fragment;

    @ViewById(R.id.sex_btn)
    RadioGroup sex_btn;
    @ViewById(R.id.time_chose)
    TextView time_chose;
    @ViewById(R.id.pay_chose)
    TextView pay_chose;
    @ViewById(R.id.del_remark)
    TextView del_remark;
    @ViewById(R.id.ll_time)
    View ll_time;
    @ViewById(R.id.pay_ll)
    View pay_ll;
    @ViewById(R.id.pay_parent)
    View pay_parent;
    GDLocation location;

    ImageView tempImageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoader.getInstance().stop();
        setContentView(R.layout.publish);
        setTitleRightTxt("完成");
        setTitleTxt("发布");
        setListener();
        location = new GDLocation(this, this, false);
        if (!Utils.showCoin(this)) {
            pay_parent.setVisibility(View.GONE);
        }
    }

    @Click(R.id.ll_place)
    public void tv_address(View view) {
        if (Util.isFastDoubleClick()) {
            return;
        }
        ChoseLocationActivity_.intent(mContext).fromActivity(ChosePositionFragment.FromActivity.DatePublish).startForResult(66);
    }

    @Click(R.id.del_remark)
    public void del_remark(View view) {
        edittxt.setText("");
    }

    @Click(R.id.ll_time)
    public void date_btn(View view) {
        if (Util.isFastDoubleClick()) {
            return;
        }
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        if (dateAndTime != null) {
            calendar = dateAndTime;
        }
        DatePickerDialog datePickerDialog = DatePickerDialog
                .newInstance(d, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMinDate(Calendar.getInstance(Locale.CHINA));
        Calendar max = Calendar.getInstance(Locale.CHINA);
        max.set(Calendar.DAY_OF_MONTH, max.get(Calendar.DAY_OF_MONTH) + 15);
        datePickerDialog.setMaxDate(max);
        datePickerDialog.setAccentColor(getResources().getColor(R.color.nav_press_txt));
        datePickerDialog.show(getFragmentManager(), "");
    }

    String[] pays = {"面议", "100-500元/天", "500-1000元/天", "1000-2000元/天", "2000-3000元/天", "3000-4000元/天", "4000-5000元/天", "5000-10000元/天", "1万元以上/天"};

    @Click(R.id.pay_ll)
    public void pay_chose(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PublishActivity.this);
        builder.setItems(pays, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int position) {
                pay_chose.setTag(position + "");
                pay_chose.setText(pays[position]);
                pb_cash.setSelected(true);
            }
        });
        AlertDialog pay_alert = builder.create();
        pay_alert.setCanceledOnTouchOutside(true);
        pay_alert.show();
    }

    StringBuilder timeSb = new StringBuilder();
    Calendar dateAndTime;
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            dateAndTime = Calendar.getInstance(Locale.CHINA);
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, day);
            if (calendar.getTime().after(dateAndTime.getTime())) {
                showCustomToast("约会时间不能小于当前时间！");
                return;
            }
            if (calendar.getTime().before(dateAndTime.getTime())) {
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 15);
                if (calendar.getTime().before(dateAndTime.getTime())) {
                    showCustomToast("只能发布15天内约会!");
                    return;
                }
            }
            timeSb = new StringBuilder();
            timeSb.append(year + "-" + (month + 1) + "-" + day);
            Calendar mCalendar = Calendar.getInstance(Locale.CHINA);
            if (dateAndTime != null) {
                mCalendar = dateAndTime;
            }
            TimePickerDialog timePickerDialog24h = TimePickerDialog
                    .newInstance(t, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar
                            .get(Calendar.MINUTE), true);
            if (dateAndTime.get(Calendar.DAY_OF_MONTH) == datePickerDialog.getMaxDate().get(Calendar.DAY_OF_MONTH)) {
                timePickerDialog24h.setMinTime(0, 0, 0);
                Calendar max = Calendar.getInstance(Locale.CHINA);
                timePickerDialog24h.setMaxTime(max.get(Calendar.HOUR_OF_DAY), max.get(Calendar.MINUTE), max.get(Calendar.SECOND));
            }
            timePickerDialog24h.setAccentColor(getResources().getColor(R.color.nav_press_txt));
            timePickerDialog24h.show(getFragmentManager(), "");
        }
    };

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            if (calendar.getTime().after(dateAndTime.getTime())) {
                showCustomToast("约会时间不能小于当前时间！");
                return;
            }
            if (calendar.getTime().before(dateAndTime.getTime())) {
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 15);
                if (calendar.getTime().before(dateAndTime.getTime())) {
                    showCustomToast("只能发布15天内约会!");
                    return;
                }
            }
            timeSb.append(" ").append(pad(hourOfDay)).append(":")
                    .append(pad(minute));
            time_chose.setText(timeSb.toString());
            pb_time.setSelected(true);
        }
    };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public void setListener() {

        fragment = (AddPicFragment) getSupportFragmentManager()
                .findFragmentById(R.id.picFragment);
    }

    public void add_pic_btn(View v) {
        tempImageView = (ImageView) v;
        gridview.setVisibility(View.GONE);
        if (v.getTag().toString().equals(AddPicFragment.DEFAULT)) {
            new AlertDialog.Builder(this).setItems(new String[]{"相机", "从相册中选择"}, menuClick).create().show();
        } else {
            new AlertDialog.Builder(this).setItems(new String[]{"删除"}, menuDeleteClick).create().show();
        }
        closeInput();
    }

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
                        fragment.cameraPath = messagepath + new Date().getTime();// 图片路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(fragment.cameraPath)));
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, AddPicFragment.Camera);
                    } else {
                        Toast.makeText(getApplicationContext(), "亲，请检查是否安装存储卡!",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    if (Util.existSDcard()) {
                        String messagepath = StaticFactory.APKCardPath;
                        File fa = new File(messagepath);
                        if (!fa.exists()) {
                            fa.mkdirs();
                        }
                        PhotoAlbumMainActivity_.intent(mContext).maxnum(3).paths(fragment.getSdibs()).startForResult(AddPicFragment.Album);
                    } else {
                        Toast.makeText(getApplicationContext(), "亲，请检查是否安装存储卡!",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    DialogInterface.OnClickListener menuDeleteClick = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    fragment.removeByView(tempImageView);
                    break;
            }
        }
    };


    public void onBackPressed() {
        if (gridview.getVisibility() == View.VISIBLE) {
            gridview.setVisibility(View.GONE);
            showKeyBoard();
            return;
        } else {
            dialogFinish();
        }
    }

    @Override
    public void back(View v) {
        dialogFinish();
    }

    int sex = 0;
    String remark = "";

    @Override
    public void rightClick(View v) {
        if (canPost.get()) {
            String date_str = time_chose.getText().toString().trim();
            String address_str = tv_address.getText().toString().trim();
            String pay_str = pay_chose.getText().toString().trim();
            remark = edittxt.getText().toString().trim();
            if (TextUtils.isEmpty(date_str)) {
                showCustomToast("请选择时间");
                return;
            } else if (TextUtils.isEmpty(address_str)) {
                showCustomToast("请选择地点");
                return;
            } else if (pay_parent.getVisibility() == View.VISIBLE && TextUtils.isEmpty(pay_str)) {
                showCustomToast("请选择薪酬");
                return;
            }
            int sexButtonId = sex_btn.getCheckedRadioButtonId();
            switch (sexButtonId) {
                case R.id.girl_date_boy:
                    sex = 0;
                    break;
                case R.id.boy_date_girl:
                    sex = 1;
                    break;
                case R.id.everyone:
                    sex = 2;
                    break;
            }
            location.startLocation();
        }

    }

    AtomicBoolean canPost = new AtomicBoolean(true);
    private String dyid;

    public void uploadImg(int i) {
        if (i == fragment.getCount()) {
            canPost.compareAndSet(false, true);
            return;
        }
        LoginUser user = ac.getUser();
        try {
            RequestParams ap = getAjaxParams();
            ap.put("file", new File(fragment.getItem(i)));
            ap.put("userid", user.getId());
            ap.put("dyid", dyid);
            ap.put("position", i + "");
            ac.finalHttp.post(URL.PUBLISH_IMG, ap, new callBack(i));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationSuccess() {
        LoginUser user = ac.getUser();
        if (user != null) {
            RequestParams ap = getAjaxParams();
            ap.put("isvip", ac.getUserInfo().getIsvip() + "");
            ap.put("dtime", time_chose.getText().toString().trim());
            ap.put("address", tv_address.getText().toString());
            ap.put("objsex", sex + "");
            ap.put("remark", remark);
            ap.put("longitude", ac.cs.getLng());
            ap.put("latitude", ac.cs.getLat());
            ap.put("cityid", cityid + "");
            if (pay_parent.getVisibility() == View.VISIBLE) {
                ap.put("pay", java.util.Arrays.asList(pays).indexOf(pay_chose.getText().toString().trim()) + "");
            }
            ap.put("area", ac.cs.getArea());
            ac.finalHttp.post(URL.PUBLISH_TXT, ap, new MyJsonHttpResponseHandler(mContext, Util.progress_arr[3]) {
                @Override
                public void onStart() {
                    super.onStart();
                    canPost.compareAndSet(true, false);
                }

                @Override
                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                    if (fragment.getCount() > 0) {
                        dyid = jo.getJSONObject(URL.RESPONSE).getString("dyid");
                        uploadImg(0);
                    } else {
                        canPost.compareAndSet(false, true);
                        showCustomToast("发布成功");
                        setResult(RESULT_OK);
                        finish();
                    }
                }

                @Override
                public void onFailRetCode(JSONObject jo) {
                    super.onFailRetCode(jo);
                    canPost.compareAndSet(false, true);
                }
            });
        }
    }

    @Override
    public void onLocationFail() {
        showCustomToast("定位失败");
    }

    class callBack extends MyJsonHttpResponseHandler {

        int index = 0;

        public callBack(int index) {
            super(mContext, "正在上传第" + (index + 1) + "张图片");
            this.index = index;
        }

        @Override
        public void onSuccessRetCode(JSONObject jo) throws Throwable {
            if (index == (fragment.getCount() - 1)) {
                canPost.compareAndSet(false, true);
                showCustomToast("上传成功");
                setResult(RESULT_OK);
                finish();
            } else {
                uploadImg(index + 1);
            }
        }

        @Override
        public void onFailRetCode(JSONObject jo) {
            super.onFailRetCode(jo);
            canPost.compareAndSet(false, true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (String str : fragment.getIbs()) {
            File file = new File(str);
            if (file.exists()) {
                file.delete();
            }
        }
        location.destory();
    }

    @Override
    public void onArticleSelected(View articleUri) {
        add_pic_btn(articleUri);
    }

    String cityName = "";
    int cityid = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 66 && resultCode == RESULT_OK) {
            cityName = data.getStringExtra("cityName");
            cityid = data.getIntExtra("cityId", 1001);
            tv_address.setText(cityName);
            pb_place.setSelected(true);
        } else {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
