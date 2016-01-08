package com.quanliren.quan_one.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Shen on 2015/12/11.
 */
public class MyChatListView extends ListView implements AbsListView.OnScrollListener {

    public MyChatListView(Context context) {
        super(context);
        init();
    }

    public MyChatListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyChatListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setOnScrollListener(this);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (getChildAt(getChildCount() - 1) != null) {
            if (getLastVisiblePosition() == getAdapter().getCount() - 1
                    && getChildAt(getChildCount() - 1).getBottom() <= getHeight()) {
                setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            }
        }
    }

}