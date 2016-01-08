package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.date.DateViewHolder;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.bean.DateBean;
import com.quanliren.quan_one.util.Utils;

public class DateAdapter extends BaseAdapter<DateBean> {

    public void setListener(IQuanAdapter listener) {
        this.listener = listener;
    }

    IQuanAdapter listener;

    boolean coin;

    public DateAdapter(Context context) {
        super(context);
        coin = Utils.showCoin(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new DateViewHolder(view, context, detailClick,coin);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.date_item;
    }


    OnClickListener detailClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            listener.detailClick((DateBean) v.getTag());
        }
    };

    public interface IQuanAdapter {
        public void detailClick(DateBean bean);
    }
}
