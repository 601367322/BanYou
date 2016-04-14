package com.quanliren.quan_one.fragment.near;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.util.StaticFactory;

import butterknife.Bind;

/**
 * Created by Shen on 2016/4/6.
 */
public class NearPeopleHeaderRankingAdapter extends BaseAdapter<User> {

    public NearPeopleHeaderRankingAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.head_hot_ranking_item;
    }

    class ViewHolder extends BaseHolder<User> {

        @Bind(R.id.logo)
        ImageView logo;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(User bean, int position) {
            ImageLoader.getInstance().displayImage(bean.getAvatar() + StaticFactory._160x160, logo, AppClass.options_userlogo);
        }
    }
}
