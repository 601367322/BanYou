package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.ShopBean;

import java.util.List;

/**
 * Created by BingBing on 2015/5/22.
 */
public class ShopAdapter extends ParentsAdapter {

    VipBuyListener listener;

    public ShopAdapter(Context c, List list, VipBuyListener listener) {
        super(c, list);
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        ShopBean sb = (ShopBean) list.get(position);
        return sb.getViewType();
    }

    @Override
    public int getViewTypeCount() {

        return 4;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(c).inflate(R.layout.shop_item, null);
            holder.price= (ImageView) convertView.findViewById(R.id.price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.price.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        holder.price.setAlpha(100);
                        break;
                    case MotionEvent.ACTION_UP:
                        holder.price.setAlpha(255);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        holder.price.setAlpha(255);
                        break;
                }
                return false;
            }
        });
        ShopBean sb = (ShopBean) list.get(position);
        holder.price.setTag(sb);
        holder.price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.buyClick((ShopBean)v.getTag());
            }
        });
        if(sb.getId()==101){
            holder.price.setImageResource(R.drawable.common_vip);
        }else if(sb.getId()==102){
            holder.price.setImageResource(R.drawable.rich_vip);
        }
//        Bitmap loadedImage = ((BitmapDrawable) holder.price.getDrawable()).getBitmap();
//        int swidth=c.getResources().getDisplayMetrics().widthPixels;
//        float widthScale=(float)swidth/(float)loadedImage.getWidth();
//        int height=(int)(widthScale*loadedImage.getHeight());
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams().LayoutParams(swidth-16,height);
//        holder.price.setLayoutParams(lp);
//        holder.price.setImageBitmap(loadedImage);
        return convertView;
    }

    class ViewHolder {
        ImageView price;
    }

    public interface IBuyListener {
       void buyClick(Button progress);
    }
    public interface VipBuyListener {
        void buyClick(ShopBean sb);
    }
}
