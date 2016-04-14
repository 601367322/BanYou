package com.quanliren.quan_one.activity.seting.auth;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.user.PlayVideoFragment.VideoDownBean;
import com.quanliren.quan_one.activity.user.VideoDownLoadManager;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.custom.RoundProgressBar;
import com.quanliren.quan_one.util.StaticFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import de.greenrobot.event.EventBus;

/**
 * Created by kong on 2016/4/6.
 */
@EFragment(R.layout.fragment_custom_play_video)
public class CustomPlayVideoFragment extends Fragment implements TextureView.SurfaceTextureListener
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
    private PlayVideoListener pvlListener;

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

    public void setPlayListener(PlayVideoListener listener) {
        this.pvlListener = listener;
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

    void playStart(){
        mediaPlayer.start();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (pvlListener != null) {
            pvlListener.onComple();
        }
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

    public interface PlayVideoListener {
        void onComple();
    }
}
