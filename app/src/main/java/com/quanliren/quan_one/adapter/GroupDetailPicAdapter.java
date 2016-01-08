package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.image.ImageBrowserActivity_;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.util.StaticFactory;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Shen on 2015/12/25.
 */
public class GroupDetailPicAdapter extends BaseAdapter<ImageBean> {

    public GroupDetailPicAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.group_detail_pic_item;
    }

    class ViewHolder extends BaseHolder<ImageBean> {

        @Bind(R.id.img)
        ImageView img;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(ImageBean bean, int position) {
            img.setTag(R.id.logo_tag, position);
            ImageLoader.getInstance().displayImage(bean.imgpath + StaticFactory._320x320, img, ac.options_defalut);
        }

        @OnClick(R.id.img)
        public void imgClick(View view) {
            ImageBrowserActivity_.intent(context).mPosition((Integer) view.getTag(R.id.logo_tag)).mProfile((ArrayList<ImageBean>) list).start();
        }
    }
}
