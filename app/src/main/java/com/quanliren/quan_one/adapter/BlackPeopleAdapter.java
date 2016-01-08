package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.UserInfoLayout;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

import butterknife.Bind;

public class BlackPeopleAdapter extends BaseAdapter<User> {

    private boolean needTime = false;

    public boolean isNeedTime() {
        return needTime;
    }

    public void setNeedTime(boolean needTime) {
        this.needTime = needTime;
    }

    public BlackPeopleAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.near_people_item;
    }


    class ViewHolder extends BaseHolder<User> {

        @Bind(R.id.userinfo)
        UserInfoLayout userinfo;
        @Bind(R.id.userlogo)
        ImageView userlogo;
        @Bind(R.id.signature)
        TextView signature;
        @Bind(R.id.visit_time)
        TextView visit_time;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(User user, int position) {
            userinfo.setUser(user);
            ImageLoader.getInstance().displayImage(user.getAvatar() + StaticFactory._160x160, userlogo, ac.options_userlogo);
            if (Util.isStrNotNull(user.getSignature())) {
                signature.setText(user.getSignature());
            } else {
                signature.setText(R.string.lazy);
            }
            if(!needTime) {
                visit_time.setVisibility(View.GONE);
            }else{
                visit_time.setVisibility(View.VISIBLE);
                visit_time.setText(Util.getTimeDateChinaStr(user.getVisittime()));
            }
        }
    }
}
