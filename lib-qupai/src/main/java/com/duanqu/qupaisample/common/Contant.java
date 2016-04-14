package com.duanqu.qupaisample.common;


public class Contant {

    /**
     * 默认时长
     */
    public static  int DEFAULT_DURATION_LIMIT = 10;
    /**
     * 默认码率
     */
    public static  int DEFAULT_BITRATE = 2000 * 1000;
    /**
     * 默认Video保存路径，请开发者自行指定
     */
    public static  String VIDEOPATH = "";
    /**
     * 默认缩略图保存路径，请开发者自行指定
     */
    public static  String THUMBPATH =  VIDEOPATH + ".jpg";
    /**
     * 水印本地路径，文件必须为rgba格式的PNG图片
     */
    public static  String WATER_MARK_PATH ="assets://Qupai/watermark/qupai-logo.png";
}
