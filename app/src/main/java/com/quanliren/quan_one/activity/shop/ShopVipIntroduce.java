package com.quanliren.quan_one.activity.shop;

import android.widget.ListView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.adapter.VipIntroduceListAdapter;
import com.quanliren.quan_one.bean.VipIntroduceBean;

import org.androidannotations.annotations.EActivity;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.vip_detai_introduce)
public class ShopVipIntroduce extends BaseActivity {
	public ListView listview;
	VipIntroduceListAdapter adapter;

	@Override
	public void init() {
		super.init();
		listview=(ListView)findViewById(R.id.listview);
		setTitleTxt("会员介绍");
		List<VipIntroduceBean> list = new ArrayList<>();
		list.add(new VipIntroduceBean("给TA发消息", R.drawable.vip_intro_7));
		list.add(new VipIntroduceBean("语音聊天 发送图片", R.drawable.vip_intro_1));
		list.add(new VipIntroduceBean("上传16张图片到相册", R.drawable.vip_intro_2));
		list.add(new VipIntroduceBean("查看访客记录", R.drawable.vip_intro_3));
		list.add(new VipIntroduceBean("使用会员专属聊天表情", R.drawable.vip_intro_4));
		list.add(new VipIntroduceBean("可筛选附近用户出没时间", R.drawable.vip_intro_9));
		list.add(new VipIntroduceBean("创建群组", R.drawable.vip_intro_11));
		list.add(new VipIntroduceBean("富豪特权 以上功能全部拥有\n富豪专享漫游功能", R.drawable.vip_intro_5));
		list.add(new VipIntroduceBean("富豪发布的约会靠前", R.drawable.vip_intro_6));
		list.add(new VipIntroduceBean("高大上的会员标识", R.drawable.vip_intro_8));
		list.add(new VipIntroduceBean("富豪会员可以收到100公里以内最新注册的用户", R.drawable.vip_intro_10));

		adapter = new VipIntroduceListAdapter(this,list);
		listview.setAdapter(adapter);
	}

	@Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
