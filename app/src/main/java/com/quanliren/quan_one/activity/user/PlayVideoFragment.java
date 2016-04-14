package com.quanliren.quan_one.activity.user;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.custom.RoundProgressBar;
import com.quanliren.quan_one.util.StaticFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.Serializable;

import de.greenrobot.event.EventBus;

/**
 * Created by Shen on 2015/9/17.
 */
@EFragment(R.layout.fragment_chat_play_video)
public class PlayVideoFragment extends DialogFragment implements TextureView.SurfaceTextureListener
        , View.OnClickListener, MediaPlayer.OnCompletionListener {

    @ViewById(R.id.video_textureview)
    TextureView video_textureview;
    @FragmentArg
    DfMessage.VideoBean bean;
    @ViewById(R.id.loadProgressBar)
    RoundProgressBar loadProgressBar;
    @ViewById(R.id.thumb_image)
    ImageView thumb_image;

    private MediaPlayer mediaPlayer;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int dialogWidth = dm.widthPixels; // specify a value here
        int dialogHeight = dm.widthPixels; // specify a value here
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    @AfterViews
    public void init() {

        EventBus.getDefault().register(this);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) video_textureview
                .getLayoutParams();
        layoutParams.width = displaymetrics.widthPixels;
        layoutParams.height = displaymetrics.widthPixels;
        video_textureview.setLayoutParams(layoutParams);

        video_textureview.setSurfaceTextureListener(this);
        video_textureview.setOnClickListener(this);

    }

    public void onEvent(VideoDownBean vdb) {
        if (vdb.path.equals(bean.path)) {
            if (vdb.current == vdb.total) {
                loadProgressBar.setVisibility(View.GONE);
            } else {
                loadProgressBar.setVisibility(View.VISIBLE);
                loadProgressBar.setMax(vdb.total);
                loadProgressBar.setProgress(vdb.current);
            }
        }
    }

    public static class VideoDownBean implements Serializable {
        public String path;
        public int current;
        public int total;

        public VideoDownBean(String path) {
            this.path = path;
        }

        public VideoDownBean() {
            super();
        }

        public VideoDownBean(String msg, int current, int total) {
            this.path = msg;
            this.current = current;
            this.total = total;
        }
    }

    /**
     * 改变发送状态广播
     */
    public static final String CHANGESEND = "com.quanliren.quan_one.ChatActivity.CHANGESENDs";

    @Receiver(actions = CHANGESEND)
    public void onMessageChanged(Intent intent) {
        VideoDownBean msg = (VideoDownBean) intent.getSerializableExtra("bean");
        if (msg.path.equals(bean.path)) {
            prepare(mSurface);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().unregister(this);
    }

    private void prepare(Surface surface) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.setOnCompletionListener(null);
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置需要播放的视频
            DfMessage.VideoBean vb = bean;
            boolean b = false;
            if (vb.path.startsWith("http://")) {
                File playFile = null;
                if ((playFile = new File(StaticFactory.APKCardPathChatVoice + vb.path.hashCode())).exists()) {
                    b = true;
                    thumb_image.setVisibility(View.GONE);
                    mediaPlayer.setDataSource(playFile.getPath());
                } else {
                    thumb_image.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(vb.thumb, thumb_image);
                    VideoDownLoadManager.getInstance(getActivity()).down(new VideoDownBean(vb.path));
                }
            } else {
                b = true;
                thumb_image.setVisibility(View.GONE);
                mediaPlayer.setDataSource(vb.path);
            }
            // 把视频画面输出到Surface
            mediaPlayer.setSurface(surface);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
            if (b)
                mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    Surface mSurface = null;

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        prepare(mSurface = new Surface(surface));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Click(R.id.video_textureview)
    public void onTextureviewClick() {
        dismissAllowingStateLoss();
    }
}
