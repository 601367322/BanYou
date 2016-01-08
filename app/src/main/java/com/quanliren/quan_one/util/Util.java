package com.quanliren.quan_one.util;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.shop.ShopVipDetailActivity_;
import com.quanliren.quan_one.activity.user.UserInfoActivity_;
import com.quanliren.quan_one.activity.user.UserOtherInfoActivity_;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.dao.LoginUserDao;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static final SimpleDateFormat fmtDateTime = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat fmtDtTime = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat fmtDate = new SimpleDateFormat(
            "yyyy-MM-dd");
    public static final String FILE = "file://";


    public static boolean isMobileNO(String mobiles) {
        if (mobiles == null) {
            return false;
        }
        Pattern p = Pattern.compile("^[1]+\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean hasSpecialByte(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static int exceedByteLength(String str) {
        byte[] buff = str.getBytes();
        int f = buff.length;
        return f;
    }

    public static long locationTime;

    public static boolean isFastLocation() {
        long time = System.currentTimeMillis();
        long timeD = time - locationTime;
        if (0 < timeD && timeD < 15000) {
            return true;
        }
        return false;
    }

    public static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static long counterTime;

    public static boolean isFastCounter() {
        long time = System.currentTimeMillis();
        long timeD = time - counterTime;
        if (0 < timeD && timeD < 1 * 60 * 1000) {
            return true;
        }
        counterTime = time;
        return false;
    }

    public static int getLines(int sum, int num) {
        int lines = (int) (sum / num);
        if (sum % num > 0) {
            lines++;
        }
        return lines;
    }

    public static final boolean isPassword(String password) {
        if (password.length() > 16 || password.length() < 6) {
            return false;
        } else if (!password.matches("^[a-zA-Z0-9 -]+$")) {
            return false;
        }
        return true;
    }

    public static void shareMsg(Context context, String activityTitle,
                                String msgTitle, String msgText, String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/png");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getChannel(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (Exception e) {
        }
        return "QUANONE";
    }

    public static String getAge(Date date) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        if (cal.getTime().before(date)) {
            return "0";
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(date);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                // monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                // monthNow>monthBirth
                age--;
            }
        }

        if (age < 0) {
            age = 0;
        }

        return age + "";
    }

    // 判断手机有无存储卡
    public static boolean existSDcard() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return true;
        } else
            return false;
    }

    public long getSDFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        // return freeBlocks * blockSize; //单位Byte
        // return (freeBlocks * blockSize)/1024; //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    public static int getLengthString(String str) {
        try {
            byte[] b = str.getBytes("gb2312");
            return b.length;
        } catch (Exception ex) {
            return 0;
        }
    }

    public static boolean isStrNotNull(String str) {
        if (str != null && str.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String getTimeDateStr(String dates) {
        String str1 = "";
        try {
            Date date = fmtDateTime.parse(dates);
            long result = Math.abs(new Date().getTime() - date.getTime());
            if (result < 60000)// 一分钟内
            {
                str1 = "刚刚";
            } else if (result >= 60000 && result < 3600000)// 一小时内
            {
                long seconds = result / 60000;
                str1 = seconds + "分钟前";
            } else if (result >= 3600000 && result < 86400000)// 一天内
            {
                long seconds = result / 3600000;
                str1 = seconds + "小时前";
            } else if (result >= 86400000 && result < 604800000l)// 日期格式
            {
                String[] temp = dates.split(" ");
                String o = temp[0];
                str1 = o.substring(o.indexOf("-") + 1, o.length());
            } else {
                String[] temp = dates.split(" ");
                String o = temp[0];
                str1 = o;
            }
        } catch (Exception e) {
        }
        return str1;
    }

    public static String getTimeDateChinaStr(String dates) {
        String str1 = "";
        try {
            Date date = fmtDateTime.parse(dates);
            long result = Math.abs(new Date().getTime() - date.getTime());
            if (result < 60000)// 一分钟内
            {
                str1 = "刚刚";
            } else if (result >= 60000 && result < 3600000)// 一小时内
            {
                long seconds = result / 60000;
                str1 = seconds + "分钟前";
            } else if (result >= 3600000 && result < 86400000)// 一天内
            {
                long seconds = result / 3600000;
                str1 = seconds + "小时前";
            } else if (result >= 86400000 && result < 259200000)// 三天内
            {
                long seconds = result / 86400000;
                str1 = seconds + "天前";
            } else {                                                //三天以后
                str1 = "3天前";
            }
        } catch (Exception e) {
        }
        return str1;
    }

    public static void doCopyFile(File from, File to) throws IOException {
        FileInputStream input = new FileInputStream(from);
        try {
            FileOutputStream output = new FileOutputStream(to);
            try {
                byte[] buffer = new byte[1024];
                int n = 0;
                while (-1 != (n = input.read(buffer))) {
                    output.write(buffer, 0, n);
                }
            } finally {
                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException ioe) {
                    // ignore
                }
            }
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ioe) {
                // ignore
            }
        }
    }

    public static void setAlarmTime(Context context, long timeInMillis, String action, int time) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(action);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        int interval = time;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setWindow(AlarmManager.RTC, timeInMillis, interval, sender);
        } else {
            am.setRepeating(AlarmManager.RTC, timeInMillis, interval, sender);
        }
    }

    public static void canalAlarm(Context context, String action) {
        Intent intent = new Intent(action);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }

    public static String getDistance(double lng1, double lat1, double lng2,
                                     double lat2) {
        // System.out.println(lng1+"--------"+lat1+"--------"+lng2+"--------"+lat2);
        double a = 2 * 6378.137;
        double b = Math.PI / 360;
        double c = Math.PI / 180;
        double s = a
                * Math.asin(Math.sqrt(Math.pow(Math.sin(b * (lat1 - lat2)), 2)
                + Math.cos(c * lat1) * Math.cos(lat2 * c)
                * Math.pow(Math.sin(b * (lng1 - lng2)), 2)));
        if (s * 1000 < 1) {
            s = 0.00;
        }
        return RoundOf(String.valueOf(s), 2);
    }

    public static String RoundOf(String str) {
        return String
                .valueOf((double) Math.round(Double.valueOf(str) * 100) / 100);
    }

    public static String RoundOf(String str, int num) {
        return String
                .valueOf((double) Math.round(Double.valueOf(str) * 100) / 100);
    }

    public static String getDistance(String distance) {
        try {
            return RoundOf(String.valueOf(Double.valueOf(distance) / 1000), 2)
                    + "km";
        } catch (NumberFormatException e) {

            e.printStackTrace();
        }
        return "";
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static String getChatTime(String time) {
        try {
            Calendar c = Calendar.getInstance(Locale.CHINA);
            Calendar msgDate = Calendar.getInstance(Locale.CHINA);
            msgDate.setTime(Util.fmtDateTime.parse(time));

            if (msgDate.get(Calendar.YEAR) == c.get(Calendar.YEAR)) {
                if (msgDate.get(Calendar.DATE) == c.get(Calendar.DATE)) {
                    return (getNum(msgDate.get(Calendar.HOUR_OF_DAY)) + ":" + getNum(msgDate
                            .get(Calendar.MINUTE)));
                } else {
                    return (getNum(msgDate.get(Calendar.MONTH) + 1) + "-"
                            + getNum(msgDate.get(Calendar.DATE)) + " "
                            + getNum(msgDate.get(Calendar.HOUR_OF_DAY)) + ":" + getNum(msgDate
                            .get(Calendar.MINUTE)));
                }
            } else {
                return (getNum(msgDate.get(Calendar.YEAR)) + "-"
                        + getNum(msgDate.get(Calendar.MONTH) + 1) + "-"
                        + getNum(msgDate.get(Calendar.DATE)) + " "
                        + getNum(msgDate.get(Calendar.HOUR_OF_DAY)) + ":" + getNum(msgDate
                        .get(Calendar.MINUTE)));
            }
        } catch (ParseException e) {

            e.printStackTrace();
        }
        return "";
    }

    public static String getNum(Integer num) {
        return num < 10 ? "0" + num : num + "";
    }

    public static int daysBetween(String strDate) {
        try {
            Date bdate = fmtDate.parse(fmtDate.format(new Date()));
            Date smdate = fmtDate.parse(strDate);
            Calendar cal = Calendar.getInstance(Locale.CHINA);
            cal.setTime(smdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate);
            long time2 = cal.getTimeInMillis();
            long between_days = (time1 - time2) / (1000 * 3600 * 24);
            return Integer.parseInt(String.valueOf(between_days));
        } catch (Exception e) {

            e.printStackTrace();
        }
        return 0;
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    /**
     * 获取Assets目录下ban.properties中的属性
     *
     * @param key
     * @return
     */
    public static String getAssetsProperties(String key) {

        Context context = AppClass.getContext();
        String result = "";
        try {
            Properties properties = new Properties();
            InputStream is = context.getAssets().open(getPathName());
            properties.load(is);
            if (properties != null) {
                if (properties.containsKey(key)) {
                    result = properties.get(key).toString();
                }
            }
        } catch (Exception e) {
            LogUtil.d("getAssetsProperties e[" + e + "]");
        }
        return result;
    }

    /**
     * 读取本地SD卡文件下ban.properties中的属性
     *
     * @param
     * @return
     */
    private static String getSDcardProperties(String key) {

        Context context = AppClass.getContext();
        String result = "";
        try {
            Properties properties = new Properties();
            InputStream is = new FileInputStream(getBaseDir(context) + getPathName());
            properties.load(is);
            if (properties != null) {
                if (properties.containsKey(key)) {
                    result = properties.get(key).toString();
                }
            }
        } catch (Exception e) {

        }
        return result;
    }


    /**
     * 初始化 ban.properties中的属性
     *
     * @param key
     * @return
     */
    public static String getPropertiesValue(String key) {

        Context context = AppClass.getContext();
        String result = "";

        boolean settingopen = false;
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            String msg = appInfo.metaData.getString("TEST_SETTING");
            if (msg.equals("open")) {
                settingopen = true;
            } else {
                settingopen = false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (checkSaveLocationExists() && fileIsExists(getBaseDir(context) + getPathName()) && settingopen) {
            result = getSDcardProperties(key);

            if (result.equals("")) {
                result = getAssetsProperties(key);
            }
        } else {
            result = getAssetsProperties(key);
        }
        return result;
    }


    /**
     * 判断文件是否存在
     *
     * @return
     */
    public static boolean fileIsExists(String pathName) {
        try {
            File f = new File(pathName);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }

    /**
     * 初始属性的文件名
     *
     * @return
     */
    public static String getPathName() {
        return "ban.properties";
    }

    /**
     * 检查是否安装SD卡
     *
     * @return
     */
    public static boolean checkSaveLocationExists() {
        String sDCardStatus = Environment.getExternalStorageState();
        boolean status;
        if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
            status = true;
        } else
            status = false;
        return status;
    }

    /**
     * 初始化文件保存基本目录
     *
     * @param context
     * @return
     */
    public static String getBaseDir(Context context) {
        return StaticFactory.APKCardPath;
    }


    public static void toast(Context context, String str) {
        if (context != null && str != null) {
            Toast tempToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
            tempToast.show();
        }
    }

    public static ProgressDialog progress(Context context, String str) {
        if (context != null && str != null) {
            return progress(context, str, true);
        } else {
            return null;
        }
    }

    public static ProgressDialog progress(Context context, String str, boolean cancleable) {
        if (context != null && str != null) {
            ProgressDialog dialog = new ProgressDialog(context, R.style.no_dark_background);
            dialog.setMessage(str);
            dialog.setCancelable(cancleable);
            dialog.setIndeterminate(false);
            return dialog;
        } else {
            return null;
        }
    }

    public static RequestParams getRequestParams(Context context) {
        LoginUser user = DBHelper.loginUserDao.getUser();
        RequestParams ap = new RequestParams();
        ap.put("pid", String.valueOf(((AppClass) context.getApplicationContext()).cs.getVersionCode()));
        ap.put("clientType", "android");
        if (user != null) {
            ap.put("token", user.getToken());
        }
        return ap;
    }

    public static void goVip(final Context context, int type) {
        String str = type == 0 ? "只有成为会员之后才可以使用哦~" : "只有成为富豪会员之后才可以使用哦~";
        new AlertDialog.Builder(context)
                .setMessage(str)
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                            }
                        })
                .setPositiveButton("成为会员",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                ShopVipDetailActivity_.intent(context).start();
                            }
                        }).create().show();
    }

    public static <T> ArrayList<T> jsonToList(String json, Class<T> classOfT) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjs = new Gson().fromJson(json, type);
        ArrayList<T> listOfT = null;
        try {
            listOfT = new ArrayList<>();
            for (JsonObject jsonObj : jsonObjs) {
                listOfT.add(new Gson().fromJson(jsonObj, classOfT));
            }
            return listOfT;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int getPage(JSONObject jo) {
        if (jo != null) {
            if (jo.has(URL.RESPONSE)) {
                JSONObject page = jo.optJSONObject(URL.RESPONSE);
                if (page.has(URL.PAGEINDEX)) {
                    return page.optInt(URL.PAGEINDEX);
                }
            }
        }
        return 1;
    }

    public static String[] progress_arr = new String[]{"", "正在获取数据", "正在登录", "正在提交", "请稍等"};

    private static final String TYPE_NAME_PREFIX = "class ";

    public static String getClassName(Type type) {
        if (type == null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(TYPE_NAME_PREFIX)) {
            className = className.substring(TYPE_NAME_PREFIX.length());
        }
        return className;
    }

    public static Class<?> getClass(Type type)
            throws ClassNotFoundException {
        String className = getClassName(type);
        if (className == null || className.isEmpty()) {
            return null;
        }
        return Class.forName(className);
    }

    public static void startUserInfoActivity(Context context, String userId) {
        if (context != null) {
            Intent i = new Intent(context, userId.equals(LoginUserDao.getInstance(context).getUser().getId()) ? UserInfoActivity_.class : UserOtherInfoActivity_.class);
            i.putExtra("id", userId);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    public static void startUserInfoActivity(Context context, User user) {
        if (context != null && user != null) {
            startUserInfoActivity(context, user.getId());
        }
    }
}
