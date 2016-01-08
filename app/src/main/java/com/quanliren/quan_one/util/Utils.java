package com.quanliren.quan_one.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.umeng.onlineconfig.OnlineConfigAgent;

import java.util.ArrayList;
import java.util.List;

public class Utils {
	public static final String TAG = "PushDemoActivity";
	public static final String RESPONSE_METHOD = "method";
	public static final String RESPONSE_CONTENT = "content";
	public static final String RESPONSE_ERRCODE = "errcode";
	protected static final String ACTION_LOGIN = "com.baidu.pushdemo.action.LOGIN";
	public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
	public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
	public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
	protected static final String EXTRA_ACCESS_TOKEN = "access_token";
	public static final String EXTRA_MESSAGE = "message";

	public static String logStringCache = "";

	// 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
        	return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
            	apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {

        }
        return apiKey;
    }

    // 用share preference来实现是否绑定的开关。在ionBind且成功时设置true，unBind且成功时设置false
    public static boolean hasBind(Context context) {
    	SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		String flag = sp.getString("bind_flag", "");
		if ("ok".equalsIgnoreCase(flag)) {
			return true;
		}
		return false;
    }

    public static void setBind(Context context, boolean flag) {
    	String flagStr = "not";
    	if (flag) {
    		flagStr = "ok";
    	}
    	SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString("bind_flag", flagStr);
		editor.commit();
    }

	public static List<String> getTagsList(String originalText) {
		if (originalText == null || originalText.equals("")) {
			return null;
		}
		List<String> tags = new ArrayList<String>();
		int indexOfComma = originalText.indexOf(',');
		String tag;
		while (indexOfComma != -1) {
			tag = originalText.substring(0, indexOfComma);
			tags.add(tag);

			originalText = originalText.substring(indexOfComma + 1);
			indexOfComma = originalText.indexOf(',');
		}

		tags.add(originalText);
		return tags;
	}

	public static String getLogText(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString("log_text", "");
	}

	public static void setLogText(Context context, String text) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString("log_text", text);
		editor.commit();
	}

	/**
	 * 开启软键盘
	 */
	public static void openSoftKeyboard(Context context,EditText et) {
		if(et!=null) {
			InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(et, 0);
		}else{
			InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.toggleSoftInput(0,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 关闭软键盘
	 */
	public static void closeSoftKeyboard(Context context) {
		closeSoftKeyboard(context,null);
	}

	/**
	 * 关闭软键盘
	 */
	public static void closeSoftKeyboard(Context context, View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null && ((Activity) context).getCurrentFocus() != null) {
			inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} else {
			closeSoftKeyboard(view);
		}
	}
	/**
	 * 关闭软键盘
	 */
	public static void closeSoftKeyboard(View view) {
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	private static final String EXTRA_DEF_KEYBOARDHEIGHT = "DEF_KEYBOARDHEIGHT";
	/** 键盘默认高度 (dp) */
	private static int sDefKeyboardHeight = 300;
	private static final String MAX_RESIZE_LAYOUT_HEIGHT = "MAX_RESIZE_LAYOUT_HEIGHT";

	public static int getDefKeyboardHeight(Context context) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		int height = settings.getInt(EXTRA_DEF_KEYBOARDHEIGHT, 0);
		if (height > 0 && sDefKeyboardHeight != height) {
			Utils.setDefKeyboardHeight(context, height);
		}
		return sDefKeyboardHeight;
	}

	public static void setDefKeyboardHeight(Context context,int height) {
		if(sDefKeyboardHeight != height){
			final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
			settings.edit().putInt(EXTRA_DEF_KEYBOARDHEIGHT, height).commit();
		}
		Utils.sDefKeyboardHeight = height;
	}

	public static int getMaxResizeLayoutHeight(Context context) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		int height = settings.getInt(MAX_RESIZE_LAYOUT_HEIGHT, 0);
		return height;
	}

	public static void setMaxResizeLayoutHeight(Context context,int height) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		settings.edit().putInt(MAX_RESIZE_LAYOUT_HEIGHT, height).commit();
	}

	public static boolean showCoin(Context context){
		if (OnlineConfigAgent.getInstance().getConfigParams(context, "coin").equals("off")) {
			return false;
		}else{
			return true;
		}
	}
}
