package com.quanliren.quan_one.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.SetBean;
import com.quanliren.quan_one.util.ImageUtil;

public class SetAdapter extends ParentsAdapter{

	public SetAdapter(Context c, List list) {
		super(c, list);
	}
    @Override
    public int getItemViewType(int position) {
        SetBean sb = (SetBean) list.get(position);
        return sb.isEmotion;
    }
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
            switch (getItemViewType(position)) {
                case 1:
                    convertView=View.inflate(c, R.layout.seting_item_emotion_new, null);
                    break;
                default:
                    convertView=View.inflate(c, R.layout.seting_item, null);
                    break;
            }
			holder.icon=(ImageView) convertView.findViewById(R.id.icon);
			holder.caret=(ImageView) convertView.findViewById(R.id.caret);
			holder.newimg=(ImageView) convertView.findViewById(R.id.newimg);
			holder.text=(TextView) convertView.findViewById(R.id.text);
			holder.source=(TextView) convertView.findViewById(R.id.source);
			holder.center=(LinearLayout) convertView.findViewById(R.id.center);
			holder.lp1=(LinearLayout.LayoutParams) holder.center.getLayoutParams();
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		SetBean sb=(SetBean) list.get(position);
		holder.icon.setImageResource(sb.icon);
		holder.text.setText(sb.title);
		if(sb.isFirst){
			holder.lp1.topMargin=ImageUtil.dip2px(c, 8);
		}else{
			holder.lp1.topMargin=ImageUtil.dip2px(c, 1);
		}
		holder.center.setLayoutParams(holder.lp1);
        if (sb.title.equals("清除缓存")) {
            holder.caret.setVisibility(View.GONE);
            holder.source.setVisibility(View.VISIBLE);
            holder.source.setText(sb.getSource());
        } else {
            try {
                holder.caret.setVisibility(View.VISIBLE);
                holder.source.setVisibility(View.GONE);
            } catch (Exception e) {
            }
        }
        if(sb.img==1){
            holder.newimg.setVisibility(View.VISIBLE);
        }else {
            holder.newimg.setVisibility(View.GONE);
        }
		return convertView;
	}

	class ViewHolder{
		ImageView icon,newimg,caret;
		TextView text,source;
		LinearLayout center;
		LinearLayout.LayoutParams lp1;
	}
}
