package com.quanliren.quan_one.activity.base;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.util.Constants;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.BaseShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;


@EActivity
public abstract class BaseActivity extends OrmLiteBaseActivity<DBHelper> {

    @App
    public AppClass ac;
    @ViewById(R.id.title)
    public TextView title;

    @ViewById(R.id.title_right_txt)
    public TextView title_right_txt;
    @ViewById(R.id.title_left_icon)
    public ImageView title_left_icon;
    @ViewById(R.id.title_right_icon)
    public ImageView title_right_icon;
    @ViewById(R.id.title_left_btn)
    public View title_left_btn;
    @ViewById(R.id.title_right_btn)
    public View title_right_btn;

    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AM.getActivityManager().pushActivity(this);
        super.onCreate(savedInstanceState);
        this.mContext = this;
    }

    @AfterViews
    public void init() {

    }

    public void setTitleTxt(String title) {
        if (this.title != null)
            this.title.setText(title);
    }

    public void setTitleLeftIcon(int img){
        if (title_left_icon != null) {
            title_left_icon.setVisibility(View.VISIBLE);
            title_left_icon.setImageResource(img);
        }
    }

    public void setTitleRightIcon(int img) {
        if (title_right_icon != null) {
            title_right_icon.setVisibility(View.VISIBLE);
            title_right_icon.setImageResource(img);
        }
    }

    public void setTitleTxt(int title) {
        if (this.title != null)
            this.title.setText(title);
    }

    public void setTitleRightTxt(String str) {
        if (this.title_right_btn != null)
            this.title_right_btn.setVisibility(View.VISIBLE);
        if (this.title_right_txt != null)
            this.title_right_txt.setText(str);
    }


    public void setTitleRightTxt(int str) {
        if (this.title_right_btn != null)
            this.title_right_btn.setVisibility(View.VISIBLE);
        if (this.title_right_txt != null)
            this.title_right_txt.setText(str);
    }

    @Click(R.id.title_left_btn)
    public void back(View v) {
        closeInput();
        finish();
    }

    @Click(R.id.title_right_btn)
    public void rightClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (baseBroadcase != null) {
            unregisterReceiver(baseBroadcase);
        }
        closeInput();
        AM.getActivityManager().popActivity(this);
    }

    public void closeInput() {
        Utils.closeSoftKeyboard(this);
    }

    protected void showKeyBoard() {
        showKeyBoard(null);
    }

    protected void showKeyBoard(EditText editText) {
        Utils.openSoftKeyboard(this, editText);
    }

    private BaseBroadcast baseBroadcase;
    private Handler broadcaseHandler;

    class BaseBroadcast extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (broadcaseHandler != null) {
                Message msg = broadcaseHandler.obtainMessage();
                msg.obj = intent;
                msg.sendToTarget();
            }
        }
    }

    public void receiveBroadcast(String fileter, Handler handler) {
        registerReceiver(baseBroadcase = new BaseBroadcast(), new IntentFilter(
                fileter));
        this.broadcaseHandler = handler;
    }

    public void receiveBroadcast(String[] fileter, Handler handler) {
        IntentFilter filter = new IntentFilter();
        for (int i = 0; i < fileter.length; i++) {
            filter.addAction(fileter[i]);
        }
        registerReceiver(baseBroadcase = new BaseBroadcast(), filter);
        this.broadcaseHandler = handler;
    }

    public void goVip() {
        Util.goVip(mContext, 0);
    }

    public RequestParams getAjaxParams() {
        return Util.getRequestParams(mContext);
    }

    public RequestParams getAjaxParams(String str, String obj) {
        RequestParams params = Util.getRequestParams(mContext);
        params.put(str, obj);
        return params;
    }

    public void showCustomToast(String str) {
        Util.toast(mContext, str);
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void tongJi() {
        /**统计**/
        RequestParams ap = new RequestParams();
        LoginUser user = ac.getUser();
        if (user == null) {
            return;
        }
        ap.put("token", user.getToken());
        ap.put("appname", ac.cs.getVersionName());
        ap.put("appcode", ac.cs.getVersionCode() + "");
        ap.put("channelname", ac.cs.getChannel());
        ap.put("deviceid", ac.cs.getDeviceId());
        ap.put("devicetype", "0");
        ap.put("oscode", android.os.Build.VERSION.SDK);
        ap.put("osversion", android.os.Build.VERSION.RELEASE);
        ap.put("model", android.os.Build.MODEL);
        ac.finalHttp.post(URL.TONGJI, ap, new MyJsonHttpResponseHandler() {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                ac.cs.setIsFirstSend("1");
            }
        });
    }


    public static final int INVITE = 0, DATE = 1;

    /**
     * 配置分享平台参数,并设置分享内容</br>
     */
    public void configPlatforms(UMSocialService mController, int type) {

        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        // 添加QQ、QZone平台
//        addQQZonePlatform(activity);
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, Constants.appId, Constants.appKey);
        qqSsoHandler.setTargetUrl(Constants.shareUrl);
        qqSsoHandler.addToSocialSDK();
        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, Constants.appId, Constants.appKey);
        qZoneSsoHandler.addToSocialSDK();
        // 添加微信、微信朋友圈平台
//        addWXPlatform(activity);
        UMWXHandler wxHandler = new UMWXHandler(this, Constants.APP_ID, Constants.APP_SECRET);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(this, Constants.APP_ID, Constants.APP_SECRET);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        //设置各平台分享内容
        setShareContent(mController, type);
    }

    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    public void setShareContent(UMSocialService mController, int type) {
        //设置分享内容
        // 配置SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        switch (type){
            case DATE:
                mController.setShareContent(Constants.dateShareContent);
                break;
            case INVITE:
                mController.setShareContent(Constants.shareContent);
                break;
        }

        UMImage sinaUrlImage = null;
        UMImage iconUrlImage = null;
        try {
            iconUrlImage = new UMImage(this, BitmapFactory.decodeStream(getAssets().open("rectangle_icon.jpg")));
            sinaUrlImage = new UMImage(this, BitmapFactory.decodeStream(getAssets().open("share.jpg")));
            mController.setShareImage(sinaUrlImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        switch (type){
            case DATE:
                circleMedia.setTitle(Constants.dateShareTitle);
                circleMedia.setShareContent(Constants.dateShareContent);
                break;
            case INVITE:
                circleMedia.setTitle(Constants.wxShareTitle);
                circleMedia.setShareContent(Constants.shareContent);
                break;
        }
        circleMedia.setShareImage(iconUrlImage);
        circleMedia.setTargetUrl(Constants.shareUrl);
        mController.setShareMedia(circleMedia);

        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        shareContent(qzone, iconUrlImage, type);
        mController.setShareMedia(qzone);

        QQShareContent qqContent = new QQShareContent();
        shareContent(qqContent, iconUrlImage,type);
        mController.setShareMedia(qqContent);

        //设置微信分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        shareContent(weixinContent, iconUrlImage, type);
        mController.setShareMedia(weixinContent);
    }

    public void shareContent(BaseShareContent share, UMImage urlImage,int type) {
        switch (type){
            case DATE:
                share.setShareContent(Constants.dateShareContent);
                break;
            case INVITE:
                share.setShareContent(Constants.shareContent);
                break;
        }
        share.setTitle(getResources().getString(R.string.app_name));
        share.setTargetUrl(Constants.shareUrl);
        share.setShareImage(urlImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentManager fm = getSupportFragmentManager();
        int index = requestCode >> 16;
        if (index != 0) {
            index--;
            if (fm.getFragments() == null || index < 0
                    || index >= fm.getFragments().size()) {
                return;
            }
            Fragment frag = fm.getFragments().get(index);
            if (frag == null) {
            } else {
                handleResult(frag, requestCode, resultCode, data);
            }
            return;
        }

    }

    /**
     * 递归调用，对所有子Fragement生效
     *
     * @param frag
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void handleResult(Fragment frag, int requestCode, int resultCode,
                              Intent data) {
        frag.onActivityResult(requestCode & 0xffff, resultCode, data);
        List<Fragment> frags = frag.getChildFragmentManager().getFragments();
        if (frags != null) {
            for (Fragment f : frags) {
                if (f != null)
                    handleResult(f, requestCode, resultCode, data);
            }
        }
    }
    public void dialogFinish() {
        new AlertDialog.Builder(mContext)
                .setMessage("您确定要放弃本次编辑吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                }).create().show();
    }
}
