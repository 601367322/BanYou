package com.quanliren.quan_one.fragment.base;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.concurrent.atomic.AtomicBoolean;

@EFragment
public abstract class BaseFragment extends Fragment {

    @App
    public AppClass ac;

    //左侧整体按钮
    @ViewById(R.id.title_left_btn)
    public View leftBtn;

    //中间标题
    @ViewById(R.id.title)
    public TextView title;

    //右侧整体按钮
    @ViewById(R.id.title_right_btn)
    public View rightBtn;

    //左侧文字
    @ViewById(R.id.title_left_txt)
    public TextView title_left_txt;

    //左侧按钮右边文字(小)
    @ViewById(R.id.title_left_sub_txt)
    public TextView title_left_sub_txt;

    //右侧文字
    @ViewById(R.id.title_right_txt)
    public TextView title_right_txt;

    //左侧图标
    @ViewById(R.id.title_left_icon)
    public ImageView title_left_icon;

    //右侧图标
    @ViewById(R.id.title_right_icon)
    public ImageView title_right_icon;

    //整体actionBar
    @ViewById
    public View maction_bar;

    //用于判断是否已经初始化完成
    public AtomicBoolean init = new AtomicBoolean(false);

    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getConvertViewRes() != 0) {
            if (mView != null) {
                ViewGroup parent = (ViewGroup) mView.getParent();
                if (parent != null) {
                    parent.removeView(mView);
                }
            } else {
                mView = inflater.inflate(getConvertViewRes(), null);
            }
            return mView;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @AfterViews
    public void init() {
        if (needTitle()) {
            if (maction_bar != null)
                maction_bar.setVisibility(View.VISIBLE);
            if (needBack()) {
                setTitleLeftIcon(R.drawable.title_back_icon);
            }
        } else {
            if (maction_bar != null)
                maction_bar.setVisibility(View.GONE);
        }
    }

    @Click(R.id.title_left_btn)
    public void leftClick(View v) {
        if (needBack()) {
            Utils.closeSoftKeyboard(getActivity());
            getActivity().finish();
        }
    }

    @Click(R.id.title_right_btn)
    public void rightClick(View v) {
    }

    public void setTitleRightTxt(String str) {
        if (this.rightBtn != null)
            this.rightBtn.setVisibility(View.VISIBLE);
        if (this.title_right_txt != null)
            this.title_right_txt.setText(str);
    }

    public void setTitleRightTxt(int str) {
        if (this.rightBtn != null)
            this.rightBtn.setVisibility(View.VISIBLE);
        if (this.title_right_txt != null)
            this.title_right_txt.setText(str);
    }

    public void setTitleLeftTxt(String str) {
        if (this.leftBtn != null)
            this.leftBtn.setVisibility(View.VISIBLE);
        if (this.title_left_txt != null)
            this.title_left_txt.setText(str);
    }

    public void setTitleLeftSubTxt(String str) {
        if (this.leftBtn != null)
            this.leftBtn.setVisibility(View.VISIBLE);
        if (this.title_left_sub_txt != null)
            this.title_left_sub_txt.setText(str);
    }

    public void setTitleLeftIcon(int str) {
        if (this.leftBtn != null)
            this.leftBtn.setVisibility(View.VISIBLE);
        if (this.title_left_icon != null) {
            this.title_left_icon.setVisibility(View.VISIBLE);
            this.title_left_icon.setImageResource(str);
        }

    }

    public void setTitleRightIcon(int str) {
        if (this.rightBtn != null)
            this.rightBtn.setVisibility(View.VISIBLE);
        if (this.title_right_icon != null) {
            this.title_right_icon.setVisibility(View.VISIBLE);
            this.title_right_icon.setImageResource(str);
        }

    }

    public void setTitleTxt(String title) {
        if (this.title != null)
            this.title.setText(title);
    }

    public void setTitleTxt(int title) {
        if (this.title != null)
            this.title.setText(title);
    }

    private BaseBroadcast baseBroadcase;
    private Handler broadcaseHandler;

    class BaseBroadcast extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (broadcaseHandler != null) {
                Message msg = broadcaseHandler.obtainMessage();
                msg.obj = intent;
                msg.sendToTarget();
            }
        }
    }

    public void receiveBroadcast(String[] fileter, Handler handler) {
        IntentFilter filter = new IntentFilter();
        for (int i = 0; i < fileter.length; i++) {
            filter.addAction(fileter[i]);
        }
        getActivity().registerReceiver(baseBroadcase = new BaseBroadcast(), filter);
        this.broadcaseHandler = handler;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (baseBroadcase != null) {
            getActivity().unregisterReceiver(baseBroadcase);
        }
    }

    public void goVip() {
        if (getActivity() == null) {
            return;
        }
        Util.goVip(getActivity(), 0);
    }

    public RequestParams getAjaxParams() {
        return Util.getRequestParams(getActivity());
    }

    public RequestParams getAjaxParams(String str, String obj) {
        RequestParams params = Util.getRequestParams(getActivity());
        params.put(str, obj);
        return params;
    }

    public void showCustomToast(String str) {
        if (getActivity() == null) {
            return;
        }
        Util.toast(getActivity(), str);
    }

    public ProgressDialog progressDialog;

    public void customDismissDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public void customShowDialog(String str) {
        if (getActivity() == null) {
            return;
        }
        progressDialog = Util.progress(getActivity(), str);
    }

    public boolean needBack() {
        return true;
    }

    public boolean needTitle() {
        return true;
    }

    /**
     * 如果重写这个方法，就可以在viewpager中保留
     *
     * @return
     */
    public int getConvertViewRes() {
        return 0;
    }
}
