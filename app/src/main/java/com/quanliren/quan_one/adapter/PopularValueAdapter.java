package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.bean.PopularValue;

import butterknife.Bind;

/**
 * Created by Shen on 2016/4/12.
 */
public class PopularValueAdapter extends BaseAdapter<PopularValue> {

    public PopularValueAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.popular_value_detail_item;
    }

    class ViewHolder extends BaseHolder<PopularValue> {

        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.number)
        TextView number;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(PopularValue bean, int position) {
            title.setText(bean.billType);
            number.setText(bean.number);
            time.setText(bean.ctime);
        }
    }
}
