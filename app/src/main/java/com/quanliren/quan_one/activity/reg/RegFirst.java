package com.quanliren.quan_one.activity.reg;

import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.BuildConfig;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.user.HtmlActivity_;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity(R.layout.reg_first)
public class RegFirst extends BaseActivity {
    public static final String GETCODE = "com.quanliren.quan_one.activity.reg.RegGetCode";
    @ViewById(R.id.agreement2)
    TextView agreement2;
    @ViewById(R.id.phone)
    EditText phone;
    @ViewById(R.id.authcode)
    EditText authcode;
    @ViewById(R.id.sendCode)
    Button sendCode;
    @ViewById(R.id.commit)
    Button commit;

    @Override
    public void init() {
        super.init();
        title.setText(R.string.reg);
        agreement2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
        agreement2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                HtmlActivity_.intent(mContext).url("file:///android_asset/services.html").title_txt("用户协议").start();
            }
        });
    }
    @Click
    void commit(View view){
        String codes = authcode.getText().toString();
        if (codes.trim().length() != 6) {
            showCustomToast("请输入正确的验证码！");
            return;
        }
        RequestParams ap = getAjaxParams();
        ap.put("mobile", pstr);
        ap.put("authcode", codes);
        ac.finalHttp.post(URL.REG_SENDCODE, ap, new MyJsonHttpResponseHandler(mContext, Util.progress_arr[1]) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                RegSecond_.intent(mContext).phone(pstr).start();
                finish();
            }

        });
    }
    String pstr=null;
    int allSec = 180;
    @Click
    void sendCode(View view) {
        pstr = phone.getText().toString();
        if (Util.isMobileNO(pstr)) {
            RequestParams ap = getAjaxParams();
            ap.put("mobile", pstr);
            ac.finalHttp.post(URL.REG_FIRST, ap, new MyJsonHttpResponseHandler(mContext, Util.progress_arr[1]) {
                @Override
                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                    if(BuildConfig.DEBUG) {
                        authcode.setText(jo.getJSONObject(URL.RESPONSE).getString("authcode"));
                    }
                    allSec = 180;
                    sendCode.setText("重新获取" + allSec + "");
                    handler.postDelayed(runable, 1000);
                    sendCode.setEnabled(false);
                }

                @Override
                public void onFailRetCode(JSONObject jo) {
                    super.onFailRetCode(jo);
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

    Handler handler = new Handler(Looper.myLooper());

    public void back(View v) {
        dialogFinish();
    }

    public void onBackPressed() {
        dialogFinish();
    }

    public void dialogFinish() {
        new AlertDialog.Builder(RegFirst.this)
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
}
