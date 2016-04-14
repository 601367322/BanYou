package com.quanliren.quan_one.activity.user;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.bean.RedPacketDetail;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Shen on 2016/3/3.
 */
public class RedPacketDetailAdapter extends BaseAdapter<RedPacketDetail.OtherUser> {

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.activity_red_packet_detail_item;
    }

    public RedPacketDetailAdapter(Context context) {
        super(context);
    }

    class ViewHolder extends BaseHolder<RedPacketDetail.OtherUser> {

        @Bind(R.id.username)
        TextView username;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.money)
        TextView money;
        @Bind(R.id.userlogo)
        ImageView userlogo;

        @Override
        public void bind(RedPacketDetail.OtherUser bean, int position) {
            time.setText(Util.getTimeDateChinaStr(bean.createTime));
            money.setText("￥" + bean.money);
            username.setText(bean.nickname);
            ImageLoader.getInstance().displayImage(bean.avatar + StaticFactory._160x160, userlogo, ac.options_userlogo);
            userlogo.setTag(R.id.logo_tag,bean.userId);
        }

        public ViewHolder(View view) {
            super(view);
        }

        @OnClick(R.id.userlogo)
        public void userLogoClick(View view) {
            Util.startUserInfoActivity(context, view.getTag(R.id.logo_tag).toString());
        }
    }
}
