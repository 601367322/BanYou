package com.quanliren.quan_one.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.quanliren.quan_one.util.LogUtil;

/**
 * Created by Shen on 2015/12/28.
 */
public class ChildClickViewWithTouchParentLinearLayout extends LinearLayout {

    public ChildClickViewWithTouchParentLinearLayout(Context context) {
        super(context);
    }

    public ChildClickViewWithTouchParentLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildClickViewWithTouchParentLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    int firstX, secondX, distance;
    boolean isMove;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final int moveX = (int) event.getX();
        final int scape = moveX - firstX;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                firstX = (int) event.getX();//按下的时候开始的x的位置
                return true;
            case MotionEvent.ACTION_MOVE:
                secondX = (int) event.getX();//up的时候x的位置
                distance = secondX - firstX;
                if (10 < Math.abs(distance)) {
                    LogUtil.d("ACTION_MOVE false");
                    return true;
                }
                return false;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                secondX = (int) event.getX();//up的时候x的位置
                distance = secondX - firstX;
                if (10 >= Math.abs(distance)) {
                    if (listener != null) {
                        listener.onClick(this);
                    }
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    OnClickListener listener;

    @Override
    public void setOnClickListener(OnClickListener l) {
        listener = l;
    }
}
