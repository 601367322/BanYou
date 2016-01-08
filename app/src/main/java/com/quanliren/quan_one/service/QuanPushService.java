package com.quanliren.quan_one.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;

import com.quanliren.quan_one.util.BroadcastUtil;
import com.quanliren.quan_one.util.LogUtil;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class QuanPushService extends Service {
	private static final String TAG = "QuanPushService";
	private static final long KEEP_ALIVE_INTERVAL = 1000 * 30;

	private ConnectionThread mConnection;

	public IBinder onBind(Intent intent) {
		LogUtil.d(TAG, "IBinder onBind(Intent intent)");
		return stub;
	}

	public void onCreate() {
		super.onCreate();
		
		/**屏幕启动时的广播**/
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(broadcast = new ChatBroadcast(), filter);
		
	}


	public void onStart(Intent intent, int startId) {
		if(intent==null){
			return;
		}
		LogUtil.d(TAG, intent.getAction());
		if (intent.getAction().equals(BroadcastUtil.ACTION_CONNECT)) {
			startConnection();
			Util.setAlarmTime(this, System.currentTimeMillis() + BroadcastUtil.CHECKCONNECT,BroadcastUtil.ACTION_CHECKCONNECT, BroadcastUtil.CHECKCONNECT);
		} else if (intent.getAction().equals(BroadcastUtil.ACTION_RECONNECT)) {
			reconnectIfNecessary();
		} else if(intent.getAction().equals(BroadcastUtil.ACTION_KEEPALIVE)){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				startKeepAlives();
			}
			keepAlive();
		}
	}


	ChatBroadcast broadcast;

	class ChatBroadcast extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				Intent i = new Intent(BroadcastUtil.ACTION_CHECKCONNECT);
				context.sendBroadcast(i);
			}
		}
	}

	public void onDestroy() {
		LogUtil.d(TAG, "onDestroy");
		super.onDestroy();
		unregisterReceiver(broadcast);
		stopConnection();
	}
	
	private IQuanPushService.Stub stub = new IQuanPushService.Stub() {

		@Override
		public void sendMessage(String str) throws RemoteException {
			if (mConnection != null)
				mConnection.write(str);
		}

		@Override
		public boolean getServerSocket() throws RemoteException {
			if (mConnection == null)
			{
				return false;
			}else{
				return true;
			}
		}

		@Override
		public void closeAll() throws RemoteException {
			LogUtil.d(TAG, "closeAll()");
			stopConnection();
			cancleAllAlarm();
		}
	};

	public void cancleAllAlarm() {
		Util.canalAlarm(getApplicationContext(), BroadcastUtil.ACTION_CHECKCONNECT);
	}

	private void startKeepAlives() {

		Intent i = new Intent(BroadcastUtil.ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			alarmMgr.setWindow(AlarmManager.RTC, System.currentTimeMillis()
					+ KEEP_ALIVE_INTERVAL, KEEP_ALIVE_INTERVAL, pi);
		} else {
			alarmMgr.setRepeating(AlarmManager.RTC, System.currentTimeMillis()
					+ KEEP_ALIVE_INTERVAL, KEEP_ALIVE_INTERVAL, pi);
		}
		
	}
	private void stopKeepAlives() {
		Intent i = new Intent(BroadcastUtil.ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	public class ConnectionThread extends Thread{
		private final Socket mSocket;
		private final String mHost;
		private final int mPort;
		private ClientHandlerWord handler;

		public ConnectionThread(String host, int port) {
			mHost = host;
			mPort = port;
			handler=new ClientHandlerWord(getApplicationContext());
			mSocket = new Socket();
		}

		public boolean isConnected() {
			return mSocket.isConnected();
		}

		public void run() {
			Socket s = mSocket;

			try {
				s.connect(new InetSocketAddress(mHost, mPort), 25000);

				startKeepAlives();

				BufferedReader in = new BufferedReader(new InputStreamReader(
						s.getInputStream()));

				handler.sessionConnected(mConnection);
				
				String str=null;
				while ((str = in.readLine())!=null) {
					LogUtil.d(TAG,"receive------------" + str);
					if(str.equals("#")||str.equals("*")){
						continue;
					}
					try {
						handler.messageReceived(mConnection, str);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				LogUtil.d(TAG, "Unexpected I/O error: " + e.toString());
			} finally {
				stopKeepAlives();
				try {
					if(!s.isClosed())
						s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mConnection = null;
				LogUtil.d(TAG, "DisConnect");
			}
		}

		public void sendKeepAlive() throws IOException {
			try {
				Socket s = mSocket;
				s.getOutputStream().write(("#\r\n").getBytes());
				LogUtil.d(TAG,"send-------#");
			} catch (Exception e) {
				e.printStackTrace();
				if (mConnection != null)
				{
					mConnection.abort();
					mConnection = null;
				}
			}
		}
		
		public void write(String str){
			try {
				LogUtil.d(TAG,"send-------" + str);
				Socket s = mSocket;
				s.getOutputStream().write((str+"\r\n").getBytes());
			} catch (Exception e) {
				e.printStackTrace();
				if (mConnection != null)
				{
					mConnection.abort();
					mConnection = null;
				}
			}
		}

		public void abort() {
			try {
				mSocket.shutdownOutput();
			} catch (IOException e) {
			}

			try {
				mSocket.shutdownInput();
			} catch (IOException e) {
			}

			try {
				mSocket.close();
			} catch (IOException e) {
			}

			while (true) {
				try {
					join();
					break;
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private synchronized void startConnection()
	{
		
		stopConnection();
		
		mConnection = new ConnectionThread(URL.IP, URL.PORT);
		mConnection.start();
	}
	private synchronized void stopConnection()
	{


		if (mConnection != null)
		{
			mConnection.abort();
			mConnection = null;
		}
	}
	
	private synchronized void reconnectIfNecessary()
	{
		if (mConnection == null)
		{
			mConnection = new ConnectionThread(URL.IP, URL.PORT);
			mConnection.start();
		}
	}
	
	private synchronized void keepAlive()
	{
		try {
			if (mConnection != null)
				mConnection.sendKeepAlive();
			else
				reconnectIfNecessary();
		} catch (IOException e) {}
	}
}
