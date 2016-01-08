package com.quanliren.quan_one.bean;

import android.content.Intent;

public class SetBean {
	public int icon;
	public String title;
	public boolean isFirst;
	public int isEmotion;
    public String source;
	public Intent clazz;
    public int img;
	public SetBean() {
		super();

	}
	public SetBean(int icon, String title,  int isEmotion,boolean isFirst, Intent clazz) {
		super();
		this.icon = icon;
		this.title = title;
		this.isFirst = isFirst;
		this.isEmotion = isEmotion;
		this.clazz = clazz;
	}
    public String getSource() {
        if (source == null || source.equals("")) {
            return "0MB";
        }
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
