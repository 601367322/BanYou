package com.quanliren.quan_one.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.service.QuanPushService;
import com.quanliren.quan_one.util.BroadcastUtil;
import com.quanliren.quan_one.util.LogUtil;
import com.quanliren.quan_one.util.NetWorkUtil;
import com.quanliren.quan_one.util.Util;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    public void onReceive(Context context, Intent intent) {
        LogUtil.d(intent.getAction());
        NetWorkUtil netWorkUtil = new NetWorkUtil(context);
        AppClass ac = (AppClass) context.getApplicationContext();
        if (netWorkUtil.hasInternet() && BroadcastUtil.ACTION_CHECKCONNECT.equals(intent.getAction())) {
            try {
                if (ac.getUser() != null) {
                    if (!Util.isServiceRunning(context, QuanPushService.class.getName())) {
                        ac.startServices();
                    } else if (ac.remoteService == null) {
                        ac.bindServices();
                    } else if (!ac.isConnectSocket()) {
                        Intent i = new Intent(context, QuanPushService.class);
                        i.setAction(BroadcastUtil.ACTION_RECONNECT);
                        context.startService(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Util.setAlarmTime(context, System.currentTimeMillis() + BroadcastUtil.CHECKCONNECT, BroadcastUtil.ACTION_CHECKCONNECT, BroadcastUtil.CHECKCONNECT);
            }
        } else if (BroadcastUtil.ACTION_OUTLINE.equals(intent.getAction())) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(0);
            ac.dispose();
        }
    }

}