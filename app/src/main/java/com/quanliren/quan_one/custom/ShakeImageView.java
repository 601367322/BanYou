package com.quanliren.quan_one.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.seting.ShakeActivity_;

/**
 * kong
 * 自定义抖动图片
 * 
 */
public class ShakeImageView extends ImageView {

    public ShakeImageView(Context context) {
        super(context);
        init(context);
    }

    Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what){
                case 1:
                    if(shakeAnim!=null) {
                        handler.sendEmptyMessageDelayed(1, 5000);
                        ShakeImageView.this.startAnimation(shakeAnim);
                    }
                    break;
            }
        }
    };

    public ShakeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ShakeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    Animation shakeAnim;

    private void init(Context context){
        shakeAnim = AnimationUtils.loadAnimation(context, R.anim.shake_imageview);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler.sendEmptyMessage(1);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeMessages(1);
        shakeAnim = null;
    }

    public void addToListView(ListView list){
        setScaleType(ImageView.ScaleType.CENTER_CROP);
        setImageResource(R.drawable.shake_bannar);
        Bitmap loadedImage = ((BitmapDrawable) getDrawable()).getBitmap();
        int swidth = getResources().getDisplayMetrics().widthPixels;
        float widthScale = (float) swidth / (float) loadedImage.getWidth();
        int height = (int) (widthScale * loadedImage.getHeight());
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(swidth, height);
        setLayoutParams(lp);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ShakeActivity_.intent(getContext()).start();
            }
        });
        setImageBitmap(loadedImage);
        list.addHeaderView(this);
    }
}
