package com.quanliren.quan_one.bean;

import java.io.Serializable;

public class DateReplyBean implements Serializable{

	private String id;
	private String content;
	private String ctime;
	private String userid;
	private String nickname;
	private String avatar;
	private String replyuid;
	private String replyuname;
	private String age;
	private String sex;
	private int isvip;
	private String birthday;

	public void setAge(String age) {
		this.age = age;
	}

	public String getAge() {
		return age;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSex() {
		if (sex == null || sex.equals("男")) {
			return "1";
		}
		if (sex.equals("女") || "0".equals(sex)) {
			return "0";
		}
		return sex;
	}

	public void setIsvip(int isvip) {
		this.isvip = isvip;
	}

	public int getIsvip() {
		return isvip;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getBirthday() {
		return birthday;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getReplyuid() {
		return replyuid;
	}
	public void setReplyuid(String replyuid) {
		this.replyuid = replyuid;
	}
	public String getReplyuname() {
		return replyuname;
	}
	public void setReplyuname(String replyuname) {
		this.replyuname = replyuname;
	}
	public DateReplyBean(String id, String content, String ctime,
						 String userid, String nickname, String avatar, String replyuid,
						 String replyuname) {
		super();
		this.id = id;
		this.content = content;
		this.ctime = ctime;
		this.userid = userid;
		this.nickname = nickname;
		this.avatar = avatar;
		this.replyuid = replyuid;
		this.replyuname = replyuname;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public DateReplyBean() {
		super();

	}
	
}
