package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.bean.SetBean;
import com.quanliren.quan_one.util.ImageUtil;

import butterknife.Bind;

public class SetAdapter extends BaseAdapter<SetBean> {

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        int type = getItemViewType(position);
        if (type == SetBean.ItemType.NORMAL.getValue()) {
            return R.layout.seting_item_normal;
        } else if (type == SetBean.ItemType.CACHE.getValue()) {
            return R.layout.seting_item_cache;
        } else if (type == SetBean.ItemType.NEW.getValue()) {
            return R.layout.seting_item_new;
        } else if (type == SetBean.ItemType.REDPACKET.getValue()) {
            return R.layout.seting_item_red_packet;
        }
        return R.layout.seting_item_new;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).itemType.getValue();
    }

    public SetAdapter(Context context) {
        super(context);
    }

    class ViewHolder extends BaseHolder<SetBean> {
        @Bind(R.id.icon)
        ImageView icon;
        @Nullable
        @Bind(R.id.newimg)
        ImageView newimg;
        @Nullable
        @Bind(R.id.caret)
        ImageView caret;
        @Bind(R.id.text)
        TextView text;
        @Nullable
        @Bind(R.id.source)
        TextView source;
        @Bind(R.id.center)
        LinearLayout center;
        @Bind(R.id.btm_line)
        View btm_line;
        LinearLayout.LayoutParams lp1;

        @Override
        public void bind(SetBean bean, int position) {
            icon.setImageResource(bean.icon);
            text.setText(bean.title);

            btm_line.setVisibility(View.VISIBLE);

            lp1.topMargin = 0;

            if (bean.site == SetBean.Site.TOP) {
                lp1.topMargin = ImageUtil.dip2px(context, 8);
            } else if (bean.site == SetBean.Site.BTM) {
                btm_line.setVisibility(View.GONE);
            }

            center.setLayoutParams(lp1);

            if (bean.itemType == SetBean.ItemType.CACHE) {
                source.setText(bean.getSource());
            }

            if (newimg != null) {
                if (bean.img == 1) {
                    newimg.setVisibility(View.VISIBLE);
                } else {
                    newimg.setVisibility(View.GONE);
                }
            }
        }

        public ViewHolder(View view) {
            super(view);
            lp1 = (LinearLayout.LayoutParams) center.getLayoutParams();
        }
    }
}
