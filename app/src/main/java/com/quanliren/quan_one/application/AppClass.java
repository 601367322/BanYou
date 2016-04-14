package com.quanliren.quan_one.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.longevitysoft.android.xml.plist.PListXMLHandler;
import com.longevitysoft.android.xml.plist.PListXMLParser;
import com.longevitysoft.android.xml.plist.domain.Dict;
import com.longevitysoft.android.xml.plist.domain.PList;
import com.longevitysoft.android.xml.plist.domain.PListObject;
import com.loopj.android.http.PersistentCookieStore;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;
import com.quanliren.quan_one.activity.BuildConfig;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.dao.LoginUserDao;
import com.quanliren.quan_one.service.IQuanPushService;
import com.quanliren.quan_one.service.QuanPushService;
import com.quanliren.quan_one.share.CommonShared;
import com.quanliren.quan_one.util.BitmapCache;
import com.quanliren.quan_one.util.DefaultExceptionHandler;
import com.quanliren.quan_one.util.DeviceUuidFactory;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.VideoUtil;
import com.quanliren.quan_one.util.http.MyHttpClient;

import org.androidannotations.annotations.EApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EApplication
public class AppClass extends Application {

    public CommonShared cs = null;
    public MyHttpClient finalHttp;
    public boolean hasNet = true;
    public static List<String> mEmoticons = new ArrayList<String>();
    public static Map<String, Integer> mEmoticonsId = new HashMap<String, Integer>();
    public static List<String> mEmoticons_Zem = new ArrayList<String>();
    public static List<String> mEmoticons_Zemoji = new ArrayList<String>();
    public static List<String> mEmoticons1 = new ArrayList<String>();
    public static List<String> mEmoticons2 = new ArrayList<String>();
    public static Map<String, Integer> mEmoticons1Id = new HashMap<String, Integer>();
    public static Map<String, Integer> mEmoticons2Id = new HashMap<String, Integer>();

    private static Context context = null;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //如果要加载视频模块，则开启MultiDex模式
//        MultiDex.install(this);
    }

    public AppClass() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化数据库
        DBHelper helper = new DBHelper(this);
        OpenHelperManager.setHelper(helper);
        helper.init();

        context = this;
        //初始化版本信息
        cs = new CommonShared(getApplicationContext());
        cs.setVersionName(Util.getAppVersionName(this));
        cs.setVersionCode(Util.getAppVersionCode(this));
        cs.setChannel(Util.getChannel(this));
        try {
            cs.setDeviceId(new DeviceUuidFactory(this).getDeviceUuid().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //初始化gif缓存
        BitmapCache.getInstance();

        //因为有两个进程，以下是只需要在主进程里初始化的内容
        if (getCurProcessName(this).equals("com.quanliren.quan_one.activity")) {
//    		LeakCanary.install(this);
            //捕获异常重启
            if (!BuildConfig.DEBUG) {
                Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
            }

            //初始化图片加载器
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    getApplicationContext()).defaultDisplayImageOptions(options_defalut)
                    .build();
            L.writeLogs(false);
            L.writeDebugLogs(false);
            ImageLoader.getInstance().init(config);

            //初始化表情
            for (int i = 1; i < 64; i++) {
                String emoticonsName = "[zem" + i + "]";
                int emoticonsId = getResources().getIdentifier("zem" + i,
                        "drawable", getPackageName());
                mEmoticons.add(emoticonsName);
                mEmoticons_Zem.add(emoticonsName);
                mEmoticonsId.put(emoticonsName, emoticonsId);
            }
            for (int i = 1; i < 59; i++) {
                String emoticonsName = "[zemoji" + i + "]";
                int emoticonsId = getResources().getIdentifier("zemoji_e" + i,
                        "drawable", getPackageName());
                mEmoticons.add(emoticonsName);
                mEmoticons_Zemoji.add(emoticonsName);
                mEmoticonsId.put(emoticonsName, emoticonsId);
            }
            initEmoticon1();
            initEmoticon2();

            //初始化网络请求
            finalHttp = new MyHttpClient();
            finalHttp.setCookieStore(new PersistentCookieStore(this));

            //初始化视频录制
            VideoUtil.getInstance(this).initVideoSdk(this);
        }
    }

    String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return null;
    }

    void initEmoticon1() {
        PListXMLParser parser = new PListXMLParser();
        PListXMLHandler handler = new PListXMLHandler();
        parser.setHandler(handler);

        try {
            parser.parse(getAssets().open("emoticon.plist"));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PList actualPList = ((PListXMLHandler) parser.getHandler()).getPlist();
        Dict root = (Dict) actualPList.getRootElement();

        Map<String, PListObject> map = root.getConfigMap();

        for (String key : map.keySet()) {
            PListObject o = map.get(key);
            mEmoticons1
                    .add(((com.longevitysoft.android.xml.plist.domain.String) o)
                            .getValue());
            mEmoticons1Id.put(
                    ((com.longevitysoft.android.xml.plist.domain.String) o)
                            .getValue(),
                    getResources().getIdentifier(key, "drawable",
                            getPackageName()));
        }
    }

    void initEmoticon2() {
        PListXMLParser parser = new PListXMLParser();
        PListXMLHandler handler = new PListXMLHandler();
        parser.setHandler(handler);

        try {
            parser.parse(getAssets().open("emoticon2.plist"));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PList actualPList = ((PListXMLHandler) parser.getHandler()).getPlist();
        Dict root = (Dict) actualPList.getRootElement();

        Map<String, PListObject> map = root.getConfigMap();

        for (String key : map.keySet()) {
            PListObject o = map.get(key);
            mEmoticons2
                    .add(((com.longevitysoft.android.xml.plist.domain.String) o)
                            .getValue());
            mEmoticons2Id.put(
                    ((com.longevitysoft.android.xml.plist.domain.String) o)
                            .getValue(),
                    getResources().getIdentifier(key, "drawable",
                            getPackageName()));
        }
    }

    public IQuanPushService remoteService = null;
    public CounterServiceConnection conn = null;

    public class CounterServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IQuanPushService.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName name) {
            remoteService = null;
        }
    }

    public boolean isConnectSocket() {
        try {
            if (remoteService != null && remoteService.getServerSocket()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void startServices() {
        stopServices();
        Intent i = new Intent(this, QuanPushService.class);
        i.setAction(com.quanliren.quan_one.util.BroadcastUtil.ACTION_CONNECT);
        startService(i);
        bindServices();
    }

    public void bindServices() {
        Intent i = new Intent(this, QuanPushService.class);
        if (conn == null)
            conn = new CounterServiceConnection();
        bindService(i, conn, Context.BIND_AUTO_CREATE);
    }

    public void stopServices() {
        try {
            Intent i = new Intent(this, QuanPushService.class);
            if (conn != null) {
                unbindService(conn);
                conn = null;
            }
            stopService(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        //清空用户表
        LoginUserDao.getInstance(this).clearTable();
        try {
            if (remoteService != null) {
                remoteService.closeAll();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        stopServices();
    }

    public LoginUser getUser() {
        return LoginUserDao.getInstance(this).getUser();
    }

    public User getUserInfo() {
        return LoginUserDao.getInstance(this).getUserInfo();
    }


    public static final DisplayImageOptions options_defalut = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.image_group_qzl)
            .showImageForEmptyUri(R.drawable.image_group_qzl)
            .showImageOnFail(R.drawable.image_group_load_f).cacheInMemory(true)
            .cacheOnDisk(true).build();
    public static final DisplayImageOptions options_no_defalut = new DisplayImageOptions.Builder().cacheInMemory(true)
            .cacheOnDisk(true).build();
    public static final DisplayImageOptions options_userlogo = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.defalut_logo)
            .showImageForEmptyUri(R.drawable.defalut_logo)
            .showImageOnFail(R.drawable.defalut_logo).cacheInMemory(true)
            .cacheOnDisk(true).build();
    public static final DisplayImageOptions options_group_userlogo = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.defalut_group_logo)
            .showImageForEmptyUri(R.drawable.defalut_group_logo)
            .showImageOnFail(R.drawable.defalut_group_logo).cacheInMemory(true)
            .cacheOnDisk(true).build();
    public static final DisplayImageOptions options_defalut_face = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.default_face)
            .showImageForEmptyUri(R.drawable.default_face)
            .showImageOnFail(R.drawable.default_face).cacheInMemory(true)
            .cacheOnDisk(true).build();
}
