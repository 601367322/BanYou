package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseArrayListAdapter;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean;

import java.util.List;
import java.util.Map;

public class EmoteAdapter extends BaseArrayListAdapter {
	Map<String, Integer> mEmoticonsId;
	public EmoteAdapter(Context context, List<String> datas,Map<String, Integer> mEmoticonsId) {
		super(context, datas);
		this.mEmoticonsId=mEmoticonsId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listitem_emote, null);
			holder = new ViewHolder();
			holder.mIvImage = (ImageView) convertView
					.findViewById(R.id.emote_item_iv_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

        String name = (String) getItem(position);
        int id = this.mEmoticonsId.get(name);
        holder.mIvImage.setImageResource(id);
//		ImageLoader.getInstance().displayImage("drawable://"+id, holder.mIvImage,ac.options_no_default);
        EmoticonActivityListBean.EmoticonZip.EmoticonImageBean.EmoticonRes er = new EmoticonActivityListBean.EmoticonZip.EmoticonImageBean.EmoticonRes();
        er.setNickname(name);
        er.setRes(id);
        holder.mIvImage.setTag(er);
		return convertView;
	}

	class ViewHolder {
		ImageView mIvImage;
	}
}
