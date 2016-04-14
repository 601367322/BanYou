package com.quanliren.quan_one.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

import co.lujun.androidtagview.TagBean;

public class DateBean implements Serializable{
	private String dyid;
	private String ctime;
	private String content;
	private String userid;
	private String nickname;
	private String mobile;
	private int phoneMode = 1;
	private String age;
	private String sex;
	private String objsex;
	private String address;
	private String dtime;
    private String remark;
	private String avatar;
	private String cnum;
	private String iscollect;
	private String constell;;
	private String area;
	private String dtstate;
	private int isvip;
	private int pay;
	public int zambia;
	public String zambiastate;
	public Video video;

	public List<TagBean> typelist;

	public List<TagBean> getTypelist() {
		return typelist;
	}

	public void setTypelist(List<TagBean> typelist) {
		this.typelist = typelist;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getPhoneMode() {
		return phoneMode;
	}

	public void setPhoneMode(int phoneMode) {
		this.phoneMode = phoneMode;
	}

	public int getConfirmType() {
		return confirmType;
	}

	public void setConfirmType(int confirmType) {
		this.confirmType = confirmType;
	}

	public int confirmType;

	public int getPay() {
		return pay;
	}

	public void setPay(int pay) {
		this.pay = pay;
	}

	public String getDtstate() {
        return dtstate;
    }

    public void setDtstate(String dtstate) {
        this.dtstate = dtstate;
    }

    public String getConstell() {
        return constell;
    }

    public void setConstell(String constell) {
        this.constell = constell;
    }

    public int getIsvip() {
		return isvip;
	}
	public void setIsvip(int isvip) {
		this.isvip = isvip;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getCnum() {
		if(cnum==null||cnum.equals("")){
			return "0";
		}
		return cnum;
	}

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

	public String getIscollect() {
        return iscollect;
    }

    public void setIscollect(String iscollect) {
        this.iscollect = iscollect;
    }

    public void setCnum(String cnum) {
		this.cnum = cnum;
	}
	public String getDyid() {
		return dyid;
	}
	public void setDyid(String dyid) {
		this.dyid = dyid;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getSex() {
		if(sex.equals("")){
			return "0";
		}
		return sex;
	}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getObjsex() {
        return objsex;
    }

    public void setObjsex(String objsex) {
        this.objsex = objsex;
    }

    public String getDtime() {
        return dtime;
    }

    public void setDtime(String dtime) {
        this.dtime = dtime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setSex(String sex) {
		this.sex = sex;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	private String imgs;
	public String getImgs() {
		return new Gson().toJson(imglist);
	}
	public void setImgs(String imgs) {
		this.imglist = new Gson().fromJson(imgs,
				new TypeToken<List<ImageBean>>() {
				}.getType());
		this.imgs = imgs;
	}
	private List<ImageBean> imglist;
	public List<ImageBean> getImglist() {
		return imglist;
	}
	public void setImglist(List<ImageBean> imglist) {
		this.imglist = imglist;
	}
	private List<DateReplyBean> commlist;
	public List<DateReplyBean> getCommlist() {
		return commlist;
	}
	public void setCommlist(List<DateReplyBean> commlist) {
		this.commStr=new Gson().toJson(commlist);
		this.commlist = commlist;
	}
	private String commStr;
	public String getCommStr() {
		return new Gson().toJson(commlist);
	}
	public void setCommStr(String commStr) {
		this.commlist = new Gson().fromJson(commStr,
				new TypeToken<List<DateReplyBean>>() {
				}.getType());
		this.commStr = commStr;
	}

	public class Video implements Serializable{
		public String videoImg;
		public String video;
		public int videoType;
	}
}
