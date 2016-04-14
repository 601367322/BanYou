package com.quanliren.quan_one.activity.seting.auth;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.VideoB;
import com.quanliren.quan_one.bean.VideoBean;

import butterknife.Bind;

public class VideoViewHolder extends BaseHolder<VideoBean> {
    @Bind(R.id.video_intro)
    TextView videoIntro;
    @Bind(R.id.comment_num)
    TextView commentNum;
    @Bind(R.id.zan_num)
    TextView zanNum;

    Context context;

    public VideoViewHolder(View view, Context context) {
        super(view);
        this.context = context;
    }

    public void bind(VideoBean db, int position) {
        VideoB video = db.getVideo();
        DfMessage.VideoBean bean = new DfMessage.VideoBean();
        bean.path = video.getVideo();
        bean.thumb = video.getVideoImg();
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.content, CustomPlayVideoFragment_.builder().bean(bean).build()).commitAllowingStateLoss();
        if (TextUtils.isEmpty(db.getContent())) {
            videoIntro.setText("无视频描述");
        } else {
            videoIntro.setText(db.getContent());
        }
        commentNum.setText(db.getCnum());
    }
}