package com.quanliren.quan_one.adapter;

import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.util.Util;

import butterknife.Bind;

/**
 * Created by Shen on 2015/7/22.
 */
public class MessageVideoHolder extends MessageBaseHolder {

    @Bind(R.id.img)
    ImageView img;
    @Bind(R.id.img_ll)
    View img_ll;

    public MessageVideoHolder(View view) {
        super(view);
    }

    @Override
    public void bind(final DfMessage bean, int position) {
        super.bind(bean, position);
        img_ll.setTag(bean);
        img_ll.setOnLongClickListener(long_click);
        img_ll.setOnClickListener(click);

        DfMessage.VideoBean vb = bean.getVideoBean();
        switch (msgType) {
            case MessageAdapter.RIGHT_VIDEO:
                ImageLoader.getInstance().displayImage(Util.FILE + vb.thumb, img, ac.options_defalut);//本地视频
                break;
            case MessageAdapter.LEFT_VIDEO:
                ImageLoader.getInstance().displayImage(vb.thumb, img, ac.options_defalut);//外部视频
                break;
        }

    }

}
