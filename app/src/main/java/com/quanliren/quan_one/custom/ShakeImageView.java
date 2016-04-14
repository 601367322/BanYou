package com.quanliren.quan_one.custom;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.seting.ShakeActivity_;
import com.quanliren.quan_one.util.AnimUtil;

/**
 * kong
 * 自定义抖动图片
 * 
 */
public class ShakeImageView extends ImageView {

    public ShakeImageView(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what){
                case 1:
                    if(shakeAnim!=null) {
                        handler.sendEmptyMessageDelayed(1, 5000);
                        shakeAnim.start();
                    }
                    break;
            }
        }
    };

    public ShakeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public ShakeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    ValueAnimator shakeAnim;

    private void init(){
        this.shakeAnim = ObjectAnimator.ofFloat(this, AnimUtil.ROTATION, 1f, -1f).setDuration(100);
        this.shakeAnim.setRepeatCount(4);
        this.shakeAnim.setRepeatMode(ValueAnimator.REVERSE);
        this.shakeAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setRotation(0f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler.sendEmptyMessage(1);
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeMessages(1);
        shakeAnim.removeAllUpdateListeners();
        shakeAnim.removeAllListeners();
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
