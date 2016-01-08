package com.quanliren.quan_one.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.util.BroadcastUtil;
import com.quanliren.quan_one.util.LogUtil;
import com.quanliren.quan_one.util.NetWorkUtil;

public class NetAlarmReceiver extends BroadcastReceiver {
	public static final String TAG="BootCompletedAlarmReceiver";
	public void onReceive(Context context, Intent intent) {
		LogUtil.d(intent.getAction());
		NetWorkUtil netWorkUtil = new NetWorkUtil(context);
		AppClass ac = (AppClass) context.getApplicationContext();
		if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			if(!netWorkUtil.hasInternet()){
				ac.hasNet=false;
				ac.stopServices();
				LogUtil.d(String.valueOf(ac.hasNet));
			}else{
				if(!ac.hasNet){
					ac.hasNet=true;
					Intent i = new Intent(BroadcastUtil.ACTION_CHECKCONNECT);
					context.sendBroadcast(i);
					LogUtil.d(String.valueOf(ac.hasNet));
				}
			}
		}
	}
}