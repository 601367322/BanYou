package com.quanliren.quan_one.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class CustomRelativeLayout extends RelativeLayout {

	private OnSizeChangedListener listener;

	public CustomRelativeLayout(Context context) {
		super(context);
	}
	public CustomRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		super.onSizeChanged(w, h, oldw, oldh);
		if (listener != null) {
			listener.onSizeChanged(w, h, oldw, oldh);
		}
	}

	public void setOnSizeChangedListener(OnSizeChangedListener listener) {
		this.listener = listener;
	}

	public interface OnSizeChangedListener {
		public void onSizeChanged(int w, int h, int oldw, int oldh);
	}
}