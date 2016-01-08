package com.quanliren.quan_one.util;

public class BroadcastUtil {
	static final String packagestr="com.quanliren.quan_one."; 
	public static final String UPDATE_USER_INFO=packagestr+"update_user_info";
	
	public static final String ACTION_KEEPALIVE = "com.quan.service.keep_alive";
	public static final String ACTION_RECONNECT = "com.quan.service.reconnect";
	public static final String ACTION_CONNECT="com.quan.service.connect";
	
	public static final String ACTION_CHECKCONNECT="com.quan.service.checkconnect";
	public static final String ACTION_OUTLINE="com.quan.service.outline";

	public static final int CHECKCONNECT = 15 * 1000;

    public static final String DOWNLOADEMOTICON = packagestr + "service.downloademoticon";
}
