package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.custom.RoundProgressBar;
import com.quanliren.quan_one.util.Util;
import com.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;


public class ImageBrowserAdapter extends PagerAdapter {

    private List<ImageBean> mPhotos = new ArrayList<ImageBean>();

    private boolean isUserLogo = false;

    Context c;

    public ImageBrowserAdapter(List<ImageBean> photos, Context c) {
        if (photos != null) {
            mPhotos = photos;
        }
        this.c = c;
    }

    @Override
    public int getCount() {
        return mPhotos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(container.getContext(),
                R.layout.activity_image_item, null);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.photoview);
        photoView.setOnClickListener(imgClick);
        final RoundProgressBar pb = (RoundProgressBar) view
                .findViewById(R.id.loadProgressBar);
        String imgPath = mPhotos.get(position).imgpath;
        if (!imgPath.startsWith("http://")) {
            imgPath = Util.FILE + imgPath;
        }
        DisplayImageOptions options = null;
        if (isUserLogo) {
            options = AppClass.options_userlogo;
        } else {
            options = AppClass.options_defalut;
        }
        ImageLoader.getInstance().displayImage(
                imgPath, photoView,
                options, null, new ImageLoadingProgressListener() {

                    @Override
                    public void onProgressUpdate(String imageUri, View view,
                                                 int current, int total) {
                        if (current == total) {
                            pb.setVisibility(View.GONE);
                        } else {
                            pb.setVisibility(View.VISIBLE);
                            pb.setMax(total);
                            pb.setProgress(current);
                        }
                    }
                });
        container.addView(view, LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    OnClickListener imgClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            ((BaseActivity) c).finish();
        }
    };


    public boolean isUserLogo() {
        return isUserLogo;
    }

    public void setIsUserLogo(boolean isUserLogo) {
        this.isUserLogo = isUserLogo;
    }
}
