package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean.EmoticonZip;

import butterknife.Bind;
import butterknife.OnClick;

public class EmoticonDownloadListAdapter extends BaseAdapter<EmoticonZip> {


    public EmoticonDownloadListAdapter(Context context) {
        super(context);
    }

    public ShopAdapter.IBuyListener getListener() {
        return listener;
    }

    public void setListener(ShopAdapter.IBuyListener listener) {
        this.listener = listener;
    }

    ShopAdapter.IBuyListener listener;

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.emoticonlist_item;
    }


    class ViewHolder extends BaseHolder<EmoticonZip>{
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.img)
        ImageView img;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.buy)
        Button buy;
        @Bind(R.id.buied)
        View buied;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(EmoticonZip zip, int position) {

            name.setText(zip.getName());
            title.setText(zip.getTitle());

            ImageLoader.getInstance().displayImage(zip.getIcoUrl(), img,ac.options_defalut_face);
            buy.setVisibility(View.VISIBLE);
            buied.setVisibility(View.GONE);
            switch (zip.getType()) {
                case 0:
                    buy.setText("免费");
                    break;
                case 1:
                    buy.setText("会员");
                    break;
                case 2:
                    buy.setText("¥" + zip.getPrice());
                    break;
            }

            if (zip.isHave()) {
                buy.setVisibility(View.GONE);
                buied.setVisibility(View.VISIBLE);
            }

            switch (zip.getIsBuy()) {
                case 1:
                    buy.setText("下载");
                    break;
            }
            buy.setTag(zip);
        }

        @OnClick(R.id.buy)
        public void buy(View view) {
            listener.buyClick((Button) view);
        }
    }
}
