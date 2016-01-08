package com.quanliren.quan_one.util.http;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.user.LoginActivity_;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.util.LogUtil;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mr.shen on 2015/5/16.
 */
public abstract class MyJsonHttpResponseHandler extends JsonHttpResponseHandler {

    String progress = null;
    Context context = null;
    ProgressDialog dialog = null;
    DialogInterface.OnCancelListener listener;

    public MyJsonHttpResponseHandler() {
        this(null, null);
    }

    public MyJsonHttpResponseHandler(Context context) {
        this(context, null);
    }

    public MyJsonHttpResponseHandler(Context context, String progress) {
        this.context = context;
        this.progress = progress;
    }

    public MyJsonHttpResponseHandler(Context context, String progress, DialogInterface.OnCancelListener listener) {
        this.context = context;
        this.progress = progress;
        this.listener = listener;
    }

    @Override
    public void onStart() {
        if (progress != null && context != null) {
            dialog = Util.progress(context, progress);
            if (listener != null) {
                dialog.setOnCancelListener(listener);
            }
            dialog.show();
        }
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);
        LogUtil.d(response.toString());
        onSuccess(response);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        super.onFailure(statusCode, headers, responseString, throwable);
        onFailure();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        onFailure();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        onFailure();
    }

    public void onSuccess(JSONObject jo) {
        try {
            if (jo.has(URL.STATUS)) {
                int retCode = jo.optInt(URL.STATUS);
                switch (retCode) {
                    case 0:
                        onSuccessRetCode(jo);
                        break;
                    case 1:
                        onFailRetCode(jo);
                        break;
                    case 2:
                        onFailRetCode(jo);

                        AM.getActivityManager().popAllActivity();

                        if(context==null){
                            context = AppClass.getContext();
                        }

                        ((AppClass)context.getApplicationContext()).dispose();
                        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
                        LoginActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
                        break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            onFailure();
        }
    }

    public abstract void onSuccessRetCode(JSONObject jo) throws Throwable;

    public void onFailRetCode(JSONObject jo) {
        if (jo.has(URL.RESPONSE) && context != null) {
            if (jo.optJSONObject(URL.RESPONSE).has(URL.INFO)) {
                Util.toast(context, jo.optJSONObject(URL.RESPONSE).optString(
                        URL.INFO));
            }
        }
    }

    public void onFailure() {
        if (context != null) {
            Util.toast(context, context.getString(R.string.neterror));
        }
    }

    @Override
    public void onFinish() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
