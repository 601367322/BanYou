package com.quanliren.quan_one.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.quanliren.quan_one.activity.user.ChatActivity_;
import com.quanliren.quan_one.application.AM;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

@EActivity
public class OpenFromNotifyActivity extends Activity {

    @Extra
    Class clazz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AM.getActivityManager().contains(MainActivity_.class.getName())) {
            MainActivity_.intent(this).start();
        }
        if (clazz != null) {
            Intent i = new Intent(this, clazz);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (clazz.getName().equals(ChatActivity_.class.getName())) {
                i.putExtras(getIntent().getExtras());
            }
            startActivity(i);
        }
        finish();
    }
}
