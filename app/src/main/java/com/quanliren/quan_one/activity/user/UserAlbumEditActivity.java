package com.quanliren.quan_one.activity.user;

import android.os.Bundle;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.ImageBean;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity
public class UserAlbumEditActivity extends BaseActivity {
    @ViewById(R.id.pic_contents)
    View pic_contents;
    UserPicFragment fragment;
    @Extra
    public ArrayList<ImageBean> imglist;
    @Extra
    public String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ImageLoader.getInstance().stop();
        setContentView(R.layout.user_ablum_edit);
        setTitleTxt("相册");
        if (fragment == null) {
            fragment = UserPicFragment_.builder().listSource(imglist).userId(userid).needAddBtn(true).maxLine(4).needPage(false).build();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.pic_contents, fragment).commit();
        } else {
            fragment.setList(imglist);
        }
        if (imglist.size() == 0 && !userid.equals(ac.getUser().getId())) {
            pic_contents.setVisibility(View.GONE);
        } else {
            pic_contents.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setResult(RESULT_OK);
    }
}