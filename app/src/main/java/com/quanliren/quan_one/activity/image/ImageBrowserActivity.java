package com.quanliren.quan_one.activity.image;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.adapter.ImageBrowserAdapter;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.custom.PhotoTextView;
import com.quanliren.quan_one.custom.ScrollViewPager;
import com.quanliren.quan_one.util.LogUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

@EActivity
public class ImageBrowserActivity extends BaseActivity implements
		OnPageChangeListener {

	private ScrollViewPager mSvpPager;
	private PhotoTextView mPtvPage;
	private ImageBrowserAdapter mAdapter;
    @Extra
	public int mPosition;
	private int mTotal;
    @Extra
    public ArrayList<ImageBean> mProfile;
    @Extra
    public boolean isUserLogo = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				            WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_imagebrowser);
		initViews();
		initEvents();
		inits();
	}

	protected void initViews() {
		mSvpPager = (ScrollViewPager) findViewById(R.id.imagebrowser_svp_pager);
		mPtvPage = (PhotoTextView) findViewById(R.id.imagebrowser_ptv_page);
	}

	protected void initEvents() {
		mSvpPager.setOnPageChangeListener(this);
	}

	void inits() {

		mTotal = mProfile.size();

        if (mPosition > mTotal) {
			mPosition = mTotal-1 ;
		}
		if (mTotal > 0) {
			mPtvPage.setText((mPosition + 1) + "/" + mTotal);
			mAdapter = new ImageBrowserAdapter(mProfile,this);
            mAdapter.setIsUserLogo(isUserLogo);
			mSvpPager.setAdapter(mAdapter);
			mSvpPager.setCurrentItem(mPosition, false);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		mPosition = arg0;
		mPtvPage.setText((mPosition + 1) + "/" + mTotal);
	}

    public void rightClick(View v) {
            try {
                File fromFile = ImageLoader.getInstance().getDiskCache()
                        .get(mProfile.get(mPosition % mTotal).imgpath);
                if(!fromFile.exists()){
                    return;
                }
                String path=mProfile.get(mPosition % mTotal).imgpath;
                LogUtil.d("----------", path);
                int position=path.lastIndexOf("/");
                File toFile = new File(StaticFactory.SDCardPath
                        + "/DCIM/Camera/");
                if (!toFile.exists()) {
                    toFile.mkdirs();
                }
                if(!path.endsWith(".jpg")){
                    FileInputStream fis = null;
                    FileOutputStream fos = null;
                    byte[] buffer = new byte[100];
                    int temp = 0;
                    try{
                        fis = new FileInputStream(path);
                        toFile=new File(toFile,path.substring(position+1,path.length())+".jpg");
                        fos = new FileOutputStream(toFile);
                        while(true){
                            temp = fis.read(buffer,0,buffer.length);
                            if(temp == -1){
                                break;
                            }
                            fos.write(buffer,0,temp);
                        }
                        ContentValues localContentValues = new ContentValues();
                        localContentValues.put("_data", toFile.toString());
                        localContentValues.put("description", "save image ---");
                        localContentValues.put("mime_type", "image/jpeg");
                        ContentResolver localContentResolver = getContentResolver();
                        Uri localUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        localContentResolver.insert(localUri, localContentValues);
                    }
                    catch(Exception e){
                        showCustomToast("保存失败");
                    }

                }else{
                    toFile = new File(toFile, path.substring(position+1,path.length()));
                }

                if (toFile.exists()){
                    Toast.makeText(ImageBrowserActivity.this,
                            "图片已经保存到" + toFile.getParent() + "文件夹下",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Util.doCopyFile(fromFile, toFile);
                Toast.makeText(ImageBrowserActivity.this,
                        "图片已经保存到" + toFile.getParent() + "文件夹下",
                        Toast.LENGTH_SHORT).show();

                ContentValues localContentValues = new ContentValues();
                localContentValues.put("_data", toFile.toString());
                localContentValues.put("description", "save image ---");
                localContentValues.put("mime_type", "image/jpeg");
                ContentResolver localContentResolver = getContentResolver();
                Uri localUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                localContentResolver.insert(localUri, localContentValues);
            } catch (Exception e) {
                Toast.makeText(ImageBrowserActivity.this, "已保存",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
    }
}
