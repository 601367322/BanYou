package com.quanliren.quan_one.activity.group;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.date.PhotoAlbumMainActivity_;
import com.quanliren.quan_one.activity.location.GDLocation;
import com.quanliren.quan_one.activity.location.ILocationImpl;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

@EActivity(R.layout.activity_create_group)
public class CreateGroupActivity extends BaseActivity implements ILocationImpl {
    @ViewById(R.id.address)
    TextView address;
    @ViewById(R.id.groupNum)
    TextView groupNum;
    @ViewById(R.id.userlogo)
    ImageView userlogo;
    @ViewById(R.id.et_group_nickname)
    EditText groupNick;
    @ViewById(R.id.et_group_intro)
    EditText groupIntro;
    GDLocation location;
    private static final int CAMERA_USERLOGO = 1;
    private static final int ALBUM_USERLOGO = 2;
    String cameraUserLogo;
    String groupId;
    File fi;
    User user;
    String longitude;
    String latitude;

    @Override
    public void init() {
        super.init();
        setTitleTxt(R.string.create_group);
        address.setText(getString(R.string.location_loading));
        location = new GDLocation(this, this, true);
        user = ac.getUserInfo();
        groupNum.setText(user.getIsvip() == 1 ? "群成员≤20人" : "群成员≤50人");
    }

    @Click(R.id.create_group)
    public void create_group(View view) {
        if(Util.isStrNotNull(longitude)&&Util.isStrNotNull(latitude)){
        }else{
            Util.toast(mContext, "定位失败，点击定位图标重新定位");
            return;
        }
        if (!Util.isStrNotNull(groupNick.getText().toString().trim())) {
            Util.toast(mContext, "群昵称不能为空");
            groupNick.requestFocus();
            return;
        }
        if (Util.hasSpecialByte(groupNick.getText().toString().trim())) {
            showCustomToast("昵称中不能包含特殊字符");
            groupNick.requestFocus();
            return;
        }
        if (!Util.isStrNotNull(groupIntro.getText().toString().trim())) {
            Util.toast(mContext, "群介绍不能为空");
            groupIntro.requestFocus();
            return;
        }
        if (fi == null || !fi.exists()) {
            Util.toast(mContext, "请上传群头像");
            return;
        }
        RequestParams params = getAjaxParams();
        params.put("groupName", groupNick.getText().toString().trim());
        params.put("groupInt", groupIntro.getText().toString().trim());
        params.put("area", address.getText().toString());
        params.put("longitude", longitude);
        params.put("latitude", latitude);
        ac.finalHttp.post(URL.ADD_GROUP, params, new MyJsonHttpResponseHandler(this, Util.progress_arr[3]) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                Util.toast(mContext, "群组创建成功");
                groupId = jo.getJSONObject(URL.RESPONSE).getString("groupId");
                //上传群头像
                uploadUserLogo(fi);
            }
        });
    }

    @Override
    public void onLocationSuccess() {
        address.setText(ac.cs.getLocationArea());
        longitude=ac.cs.getLng();
        latitude=ac.cs.getLat();
    }

    @Override
    public void onLocationFail() {
        address.setText(getString(R.string.location_fail));
    }

    @Click(R.id.address)
    public void address(View view) {
        address.setText(getString(R.string.location_loading));
        location.startLocation();
        longitude="";
        latitude="";
    }

    @Click({R.id.userlogo, R.id.userlogo_ll})
    public void userLogo(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this).setItems(
                new String[]{"相册", "拍照"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 1:
                                if (Util.existSDcard()) {
                                    String messagepath = StaticFactory.APKCardPath;
                                    cameraUserLogo = messagepath
                                            + new Date().getTime();// 图片路径
                                    Intent intent = new Intent(); // 调用照相机
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
                                    PhotoAlbumMainActivity_.intent(mContext).maxnum(1).startForResult(ALBUM_USERLOGO);
                                } else {
                                    Util.toast(mContext, "亲，请检查是否安装存储卡!");
                                }
                                break;
                        }
                    }
                }).create();
        dialog.setTitle("上传群头像");
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ALBUM_USERLOGO) {
            if (data == null) {
                return;
            }
            ArrayList<String> list = data.getStringArrayListExtra("images");
            if (list.size() > 0) {
                cameraUserLogo = list.get(0);
                fi = new File(cameraUserLogo);
                if (fi != null && fi.exists()) {
                    ImageUtil.downsize(cameraUserLogo, cameraUserLogo, this);
                    ImageLoader.getInstance().displayImage(
                            Util.FILE + cameraUserLogo, userlogo);
                }
            }
        } else if (requestCode == CAMERA_USERLOGO) {
            try {
                if (cameraUserLogo != null) {
                    fi = new File(cameraUserLogo);
                    if (fi != null && fi.exists()) {
                        ImageUtil.downsize(cameraUserLogo, cameraUserLogo, this);
                        ImageLoader.getInstance().displayImage(
                                Util.FILE + cameraUserLogo, userlogo);
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadUserLogo(final File file) {
        try {
            RequestParams ap = Util.getRequestParams(getApplicationContext());
            ap.put("groupId", groupId);
            ap.put("file", file);
            ac.finalHttp.post(this, URL.UPLOAD_GROUPLOGO, ap, new MyJsonHttpResponseHandler(this, Util.progress_arr[3]) {
                @Override
                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                    GroupBean bean=new GroupBean();
                    bean.setId(groupId);
                    GroupDetailActivity_.intent(mContext).bean(bean).start();
                    finish();
                }

                @Override
                public void onFailure() {
                    super.onFailure();
                    try {
                        AlertDialog dialog = new AlertDialog.Builder(
                                CreateGroupActivity.this)
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

    public void onBackPressed() {
        dialogFinish();
    }

    @Override
    public void back(View v) {
        closeInput();
        dialogFinish();
    }
}
