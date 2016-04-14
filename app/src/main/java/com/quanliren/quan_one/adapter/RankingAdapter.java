package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.UserInfoLayout;
import com.quanliren.quan_one.util.StaticFactory;

import butterknife.Bind;

/**
 * Created by Shen on 2016/4/6.
 */
public class RankingAdapter extends BaseAdapter<User> {

    public RankingAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.rank_list_item;
    }

    class ViewHolder extends BaseHolder<User> {

        @Bind(R.id.userlogo)
        ImageView userlogo;
        @Bind(R.id.number_img)
        View numberImg;
        @Bind(R.id.number)
        TextView number;
        @Bind(R.id.userinfo)
        UserInfoLayout userinfo;
        @Bind(R.id.popular_value)
        TextView popularValue;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(User bean, int position) {
            ImageLoader.getInstance().displayImage(bean.getAvatar() + StaticFactory._160x160, userlogo, AppClass.options_userlogo);
            userinfo.setUser(bean);
            popularValue.setText(bean.getPopNum() + "");
            if (position == 0) {
                numberImg.setVisibility(View.VISIBLE);
                number.setVisibility(View.GONE);
            } else {
                numberImg.setVisibility(View.GONE);
                number.setVisibility(View.VISIBLE);
                if (position == 1) {
                    number.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                }else if(position == 2){
                    number.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                }else{
                    number.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                }
                number.setText((position + 1) + "");
            }
        }
    }
}
