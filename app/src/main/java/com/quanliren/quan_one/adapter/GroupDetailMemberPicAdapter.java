package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.util.StaticFactory;

import butterknife.Bind;

/**
 * Created by Shen on 2015/12/25.
 */
public class GroupDetailMemberPicAdapter extends BaseAdapter<User>{

    public GroupDetailMemberPicAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.group_detail_member_pic_item;
    }

    class ViewHolder extends BaseHolder<User>{

        @Bind(R.id.img)
        ImageView img;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(User bean, int position) {
            ImageLoader.getInstance().displayImage(bean.getAvatar() + StaticFactory._160x160, img,ac.options_userlogo);
        }
    }
}
