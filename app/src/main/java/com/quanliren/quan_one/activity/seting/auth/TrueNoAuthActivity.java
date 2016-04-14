package com.quanliren.quan_one.activity.seting.auth;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.fragment.SetingMoreFragment;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.VideoUtil;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;

@EActivity(R.layout.activity_true_no_auth)
public class TrueNoAuthActivity extends BaseActivity implements CustomPlayVideoFragment.PlayVideoListener {
    private static final int EDIT_INTRO_CONTENT = 0;
    @ViewById(R.id.recoding_img)
    ImageView recodingImg;
    @ViewById(R.id.start_recording)
    ImageView startRecording;
    @ViewById(R.id.play_video)
    ImageView playVideo;
    @ViewById(R.id.intro_tv)
    TextView introTv;
    @ViewById(R.id.bottom_btns)
    View bottomBtns;
    @ViewById(R.id.gg)
    View gg;
    CustomPlayVideoFragment fragment;
    FragmentTransaction ft;

    @Override
    public void init() {
        super.init();
        setTitleTxt(getString(R.string.true_auth));
        fragment = CustomPlayVideoFragment_.builder().build();
        ft = getSupportFragmentManager().beginTransaction();
        fragment.setPlayListener(this);
        if (ac.cs.getTRUE_NAME() == 0) {
            ac.cs.setTRUE_NAME(1);
        }
    }

    @Click(R.id.start_recording)
    void startRecording() {
        VideoUtil.getInstance(mContext).startRecording(mContext);
    }

    File[] fileArr;

    @OnActivityResult(value = 10001)
    public void onVideoComplete(int resultCode, Intent data) {
        fileArr = VideoUtil.getInstance(mContext).getVideoFiles(mContext, resultCode, data);
        if (fileArr == null || fileArr.length == 0) {
            return;
        }
        ImageLoader.getInstance().displayImage(Util.FILE + fileArr[1].getPath(), recodingImg);
        bottomBtns.setVisibility(View.VISIBLE);
        startRecording.setVisibility(View.GONE);
        playVideo.setVisibility(View.VISIBLE);
    }

    @Click(R.id.video_intro)
    void inputVideoIntro() {
        //  跳转输入视频描述界面
        TrueAuthIntroEditActivity_.intent(mContext).str_introduce(introTv.getText().toString().trim()).startForResult(EDIT_INTRO_CONTENT);
    }

    @OnActivityResult(EDIT_INTRO_CONTENT)
    void onResultInfo(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            String groupInt = data.getStringExtra("introduce");
            introTv.setText(groupInt);
        }
    }

    @Click(R.id.re_recording_btn)
    void reRecording() {
        VideoUtil.getInstance(mContext).startRecording(mContext);
    }

    @Click(R.id.commit_btn)
    void commitRecording() {
        if (fileArr == null || fileArr.length == 0) {
            showCustomToast("请上传自我介绍视频");
            return;
        }
        uploadFile(fileArr);
    }


    /**
     * 上传视频和缩略图
     *
     * @param files
     */
    void uploadFile(final File[] files) {
        title_right_btn.setEnabled(false);
        RequestParams params = getAjaxParams();
        try {
            params.put("file", files[0]);
            params.put("file1", files[1]);
            params.put("content", introTv.getText().toString().trim());
            ac.finalHttp.post(mContext, URL.UPLOADTRUEAUTH, params, new MyJsonHttpResponseHandler(mContext, Util.progress_arr[3]) {
                @Override
                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                    Intent intent = new Intent(SetingMoreFragment.UPDATE_USERINFO);
                    sendBroadcast(intent);
                    //  跳转审核中的页面
                    TrueAuthActivity_.intent(mContext).start();
                    finish();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    title_right_btn.setEnabled(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fileArr == null || fileArr.length == 0) {
            return;
        }
        for (File f : fileArr) {
            f.delete();
        }
    }

    @Click(R.id.play_video)
    void playVideo() {
        // TODO: 2016/4/13 播放视频 
    }

    @Override
    public void onComple() {
        gg.setVisibility(View.VISIBLE);
    }
}
