package com.quanliren.quan_one.activity.seting.auth;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.util.Util;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.user_self_intro_edit)
public class TrueAuthIntroEditActivity extends BaseActivity {
    @ViewById(R.id.intro_edit)
    EditText intro_edit;
    @Extra
    String str_introduce;

    @Override
    public void init() {
        super.init();
        setTitleRightTxt("完成");
        intro_edit.setText(str_introduce);
        setTitleTxt("视频介绍");
    }

    @Override
    public void rightClick(View v) {
        super.rightClick(v);
        String intro = intro_edit.getText().toString().trim();
        if (intro.length() > 0 && !str_introduce.equals(intro)) {
            Intent intent = new Intent();
            intent.putExtra("introduce", intro);
            setResult(RESULT_OK, intent);
            finish();
        } else if (intro.trim().length() == 0) {
            Util.toast(mContext, "请输入视频介绍");
        }
    }
}