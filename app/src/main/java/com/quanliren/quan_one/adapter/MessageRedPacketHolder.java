package com.quanliren.quan_one.adapter;

import android.view.View;
import android.widget.TextView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.DfMessage;

import butterknife.Bind;

/**
 * Created by Shen on 2015/7/22.
 */
public class MessageRedPacketHolder extends MessageBaseHolder {

    @Bind(R.id.red_content)
    TextView redContent;
    @Bind(R.id.img_ll)
    View img_ll;

    public MessageRedPacketHolder(View view) {
        super(view);
    }

    @Override
    public void bind(DfMessage bean, int position) {
        super.bind(bean, position);
        img_ll.setTag(bean);
        img_ll.setOnLongClickListener(long_click);
        img_ll.setOnClickListener(click);
        redContent.setText(bean.getRedPacket().content);
    }
}
