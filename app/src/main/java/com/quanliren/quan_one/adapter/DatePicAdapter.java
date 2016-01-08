package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.image.ImageBrowserActivity_;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.bean.MessageList;
import com.quanliren.quan_one.util.StaticFactory;

import java.util.ArrayList;
import java.util.List;

public class DatePicAdapter extends ParentsAdapter {

	int imgWidth;
	public DatePicAdapter(Context c, List list, int imgwidth) {
		super(c, list);
		imgWidth=imgwidth;
	}

	public boolean ismy=false;
	
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			convertView = View.inflate(c, R.layout.date_pic_item, null);
			(convertView).setLayoutParams(new AbsListView.LayoutParams(imgWidth, imgWidth));
		} 
		ImageBean ib=(ImageBean) list.get(position);
		ImageLoader.getInstance().displayImage(ib.imgpath+StaticFactory._320x320, (ImageView)convertView);
		(convertView).setTag(position);
		(convertView).setOnClickListener(imgClick);
		return convertView;
	}

	class ViewHolder {
		ImageView iv;
	}

	OnClickListener imgClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int positon=(Integer) v.getTag();
			MessageList ml=new MessageList();
			ml.imgList=list;
			ImageBrowserActivity_.intent(c).mPosition(positon).mProfile((ArrayList<ImageBean>) ml.imgList).start();
		}
	};
}
