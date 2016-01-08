package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.VipIntroduceBean;

import java.util.List;

public class VipIntroduceListAdapter extends ParentsAdapter {

    public VipIntroduceListAdapter(Context context, List<VipIntroduceBean> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(c).inflate(R.layout.vip_introduce_list_item, null);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        VipIntroduceBean introB=(VipIntroduceBean)list.get(position);
        holder.img.setImageResource(introB.img);
        holder.text.setText(introB.text);
        return convertView;
    }

    class ViewHolder {
        ImageView img;
        TextView text;
    }
}
