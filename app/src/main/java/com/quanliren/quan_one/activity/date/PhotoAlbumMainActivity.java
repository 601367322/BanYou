package com.quanliren.quan_one.activity.date;

import android.content.Intent;
import android.view.View;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.PhotoAibum;
import com.quanliren.quan_one.bean.PhotoItem;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import java.util.ArrayList;

@EActivity(R.layout.photo_album_main)
public class PhotoAlbumMainActivity extends BaseActivity {

	@Extra
	int maxnum = 0;
	@Extra
	ArrayList<String> paths = new ArrayList<String>();

	@Override
	public void init() {
		super.init();
		setTitleTxt("相册");
		setTitleRightTxt("确定(" + paths.size() + "/" + maxnum + ")");
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, PhotoAlbumActivity_.builder().build()).commit();
	}

	PhotoActivity pa;

	public void replaceFragment(PhotoAibum i) {
		
		for (PhotoItem item : i.getBitList()) {
			if(paths.contains(item.getPath())){
				item.setSelect(true);
			}
		}
		
		pa = PhotoActivity_.builder().aibum(i).build();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, pa).addToBackStack(null).commit();
	}

	@Override
	public void back(View v) {
		if (pa != null && pa.isVisible()) {
			onBackPressed();
		} else {
			finish();
		}
	}

	@Override
	public void rightClick(View v) {
		Intent i = new Intent();
		i.putStringArrayListExtra("images", paths);
		setResult(1,i);
		finish();
	}
	
	public void changeNum() {
		setTitleRightTxt("确定(" + paths.size() + "/" + maxnum + ")");
	}

	public void addPath(String path) {
		paths.add(path);
		changeNum();
	}

	public void removePath(String path) {
		paths.remove(path);
		changeNum();
	}
}
