package com.quanliren.quan_one.activity.date;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.location.GDLocation;
import com.quanliren.quan_one.activity.location.ILocationImpl;
import com.quanliren.quan_one.activity.user.PlayVideoFragment_;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.fragment.custom.AddPicFragment;
import com.quanliren.quan_one.fragment.date.ChosePositionFragment;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;
import com.quanliren.quan_one.util.VideoUtil;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import co.lujun.androidtagview.TagBean;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

@EActivity(R.layout.publish)
public class PublishActivity extends BaseActivity implements
        ILocationImpl, TagView.OnTagClickListener, TextWatcher {

    public static final int CHOOSE_DATE_CONTENT_RESPONSE = 12;

    @ViewById(R.id.tv_address)
    TextView tv_address;
    @ViewById(R.id.text)
    EditText edittxt;

    @ViewById(R.id.pb_time)
    ImageView pb_time;
    @ViewById(R.id.pb_place)
    ImageView pb_place;
    @ViewById(R.id.pb_cash)
    ImageView pb_cash;
    @ViewById(R.id.pb_remark)
    ImageView pb_remark;
    @ViewById(R.id.pb_dytype)
    ImageView pb_dytype;
    @ViewById(R.id.pb_relation)
    ImageView pb_relation;
    @ViewById(R.id.pb_pic)
    public ImageView pb_pic;

    @FragmentById(R.id.picFragment)
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
    @ViewById(R.id.tags)
    TagContainerLayout tags;
    @ViewById(R.id.date_content_hint)
    View date_content_hint;
    @ViewById(R.id.relation_text)
    TextView relation_text;
    @ViewById(R.id.add_video_btn)
    ImageView addVideoBtn;//录制视频按钮
    @ViewById(R.id.add_video_text)
    TextView addVideoText;

    GDLocation location;//定位
    File[] videoFiles;//视频 0视频 1缩略图
    String[] pays = {"面议", "100-500元/天", "500-1000元/天", "1000-2000元/天", "2000-3000元/天", "3000-4000元/天", "4000-5000元/天", "5000-10000元/天", "1万元以上/天"};
    String[] phone_items = new String[]{"只对会员公开", "不公开"};
    int phoneMode = 1;//默认手机号不公开
    String cityName = "";//城市名称
    int cityid = 0;//城市ID
    AtomicBoolean canPost = new AtomicBoolean(true);//防止重复提交
    private String dyid;//上传完文字后返回得到约会id

    @Override
    public void init() {
        super.init();
        //开启定位
        location = new GDLocation(this, this, false);

        //设置标题和按钮文字
        setTitleRightTxt("完成");
        setTitleTxt("发布");

        //umeng在线参数判断是否显示薪酬
        if (!Utils.showCoin(this)) {
            pay_parent.setVisibility(View.GONE);
        }

        //umeng统计打开次数
        Util.umengCustomEvent(mContext, "create_date_btn");

        //约会内容点击事件
        tags.setOnTagClickListener(this);

        //其他说明的文字输入事件
        edittxt.addTextChangedListener(this);

        //设置录像按钮宽高
        int videoWidth = (int) ((float) (getResources().getDisplayMetrics().widthPixels - ImageUtil
                .dip2px(mContext, 24 + 3 * 4)) / 4);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) addVideoBtn.getLayoutParams();
        params.width = videoWidth;
        params.height = videoWidth;
        addVideoBtn.setLayoutParams(params);
        addVideoBtn.setTag(R.id.logo_tag, AddPicFragment.DEFAULT);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(edittxt.getText().toString().trim())) {
            pb_remark.setSelected(false);
        } else {
            pb_remark.setSelected(true);
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


    public void onBackPressed() {
        dialogFinish();
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
            String relation_str = relation_text.getText().toString().trim();
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
            } else if (tags.getTags().size() == 0) {
                showCustomToast("请选择约会内容");
                return;
            } else if (TextUtils.isEmpty(relation_str)) {
                showCustomToast("请选择是否公开联系方式");
                return;
            } else if (TextUtils.isEmpty(remark)) {
                showCustomToast("请填写其他说明");
                return;
            } else if (fragment.imagePath.size() == 0 && videoFiles == null) {
                showCustomToast("图片与视频至少选择一项添加");
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
            canPost.compareAndSet(true, false);
            location.startLocation();
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
            JSONArray ids = new JSONArray();
            for (int i = 0; i < tags.getTags().size(); i++) {
                ids.put(tags.getTags().get(i).getId());
            }
            ap.put("dyType", ids);
            ap.put("phoneMode", phoneMode);
            ac.finalHttp.post(URL.PUBLISH_TXT, ap, new MyJsonHttpResponseHandler(mContext, Util.progress_arr[3]) {
                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                    if (fragment.imagePath.size() > 0) {
                        dyid = jo.getJSONObject(URL.RESPONSE).getString("dyid");
                        uploadImg(0);
                    } else if (videoFiles != null) {
                        uploadVideo();
                    } else {
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

                @Override
                public void onFailure() {
                    super.onFailure();
                    canPost.compareAndSet(false, true);
                }
            });
        }
    }

    @Override
    public void onLocationFail() {
        showCustomToast("定位失败");
        canPost.compareAndSet(false, true);
    }

    public void uploadImg(int i) {
        if (i == fragment.imagePath.size()) {
            canPost.compareAndSet(false, true);
            return;
        }
        LoginUser user = ac.getUser();
        try {
            RequestParams ap = getAjaxParams();
            File newFile = new File(StaticFactory.APKCardPath + fragment.imagePath.get(i).toString());
            ImageUtil.downsize(fragment.imagePath.get(i), newFile.getPath(), mContext);
            ap.put("file", newFile);
            ap.put("userid", user.getId());
            ap.put("dyid", dyid);
            ap.put("position", i + "");
            ac.finalHttp.post(URL.PUBLISH_IMG, ap, new callBack(i));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            canPost.compareAndSet(false, true);
        }
    }

    class callBack extends MyJsonHttpResponseHandler {

        int index = 0;

        public callBack(int index) {
            super(mContext, "正在上传第" + (index + 1) + "张图片");
            this.index = index;
        }

        @Override
        public void onSuccessRetCode(JSONObject jo) throws Throwable {
            if (index == (fragment.imagePath.size() - 1)) {
                if (videoFiles != null) {
                    uploadVideo();
                } else {
                    uploadSuccess();
                }
            } else {
                uploadImg(index + 1);
            }
        }

        @Override
        public void onFailRetCode(JSONObject jo) {
            super.onFailRetCode(jo);
            finish();
        }

        @Override
        public void onFailure() {
            super.onFailure();
            finish();
        }
    }

    private void uploadVideo() throws FileNotFoundException {
        RequestParams params = Util.getRequestParams(mContext);
        params.put("dyid", dyid);
        params.put("file", videoFiles[0]);
        params.put("file1", videoFiles[1]);
        ac.finalHttp.post(mContext, URL.PUBLISH_VIDEO, params, new MyJsonHttpResponseHandler(mContext, "正在上传视频") {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                uploadSuccess();
            }

            @Override
            public void onFailRetCode(JSONObject jo) {
                super.onFailRetCode(jo);
                finish();
            }

            @Override
            public void onFailure() {
                super.onFailure();
                finish();
            }
        });
    }

    private void uploadSuccess() {
        showCustomToast("上传成功");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (String str : fragment.imagePath) {
            File file = new File(str);
            if (file.exists()) {
                file.delete();
            }
        }
        location.destory();
    }

    @OnActivityResult(66)
    public void onChooseLocationResult(int result, Intent data) {
        if (result == RESULT_OK) {
            cityName = data.getStringExtra("cityName");
            cityid = data.getIntExtra("cityId", 1001);
            tv_address.setText(cityName);
            pb_place.setSelected(true);
        }
    }


    @Click(R.id.chose_content_btn)
    public void startChoseContenActivity() {
        ChooseContentActivity_.intent(this).list((ArrayList<TagBean>) tags.getTags()).startForResult(CHOOSE_DATE_CONTENT_RESPONSE);
    }

    @OnActivityResult(value = CHOOSE_DATE_CONTENT_RESPONSE)
    public void onChooseContentResult(int result, Intent intent) {
        if (result == RESULT_OK) {

            tags.setVisibility(View.VISIBLE);

            List<TagBean> list = (List<TagBean>) intent.getSerializableExtra("tags");
            tags.removeAllTags();
            tags.setTags(list);
            date_content_hint.setVisibility(View.GONE);

            pb_dytype.setSelected(true);
        }
    }


    @Click(R.id.relation_btn)
    public void onRelationBtnClick() {
        new AlertDialog.Builder(this).setItems(phone_items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int position) {
                phoneMode = position;
                relation_text.setText(phone_items[position]);
                pb_relation.setSelected(true);
            }
        }).create().show();
    }

    @Click(R.id.add_video_btn)
    public void addVideoBtnClick(View view) {
        if (videoFiles == null) {
            VideoUtil.getInstance(mContext).startRecording(mContext);
        } else {
            new AlertDialog.Builder(mContext).setItems(new String[]{"播放", "删除"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            PlayVideoFragment_.builder().bean(new DfMessage.VideoBean(videoFiles[0].getPath(), videoFiles[1].getPath())).build().show(getSupportFragmentManager(), "dialog");
                            break;
                        case 1:
                            videoFiles = null;
                            addVideoBtn.setImageResource(R.drawable.publish_add_pic_icon_big);
                            addVideoText.setVisibility(View.VISIBLE);
                            isPbPicSelected();
                            break;
                    }
                }
            }).create().show();
        }
    }

    @OnActivityResult(value = 10001)
    public void onVideoComplete(int resultCode, Intent data) {
        videoFiles = VideoUtil.getInstance(mContext).getVideoFiles(mContext, resultCode, data);
        if (videoFiles != null && videoFiles.length > 1) {
            ImageLoader.getInstance().displayImage(Util.FILE + videoFiles[1].getPath(), addVideoBtn);
            addVideoText.setVisibility(View.GONE);
            isPbPicSelected();
        }
    }

    @Click(R.id.tags)
    public void onTagsClick() {
        startChoseContenActivity();
    }

    @Override
    public void onTagClick(int position, String text) {
        startChoseContenActivity();
    }

    @Override
    public void onTagLongClick(int position, String text) {
    }

    public void isPbPicSelected() {
        if (fragment.imagePath.size() > 0 || videoFiles != null) {
            pb_pic.setSelected(true);
        } else {
            pb_pic.setSelected(false);
        }
    }
}
