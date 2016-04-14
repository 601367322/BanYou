package com.quanliren.quan_one.bean;

import android.content.Intent;

public class SetBean {
    public int icon;
    public String title;
    public Site site;
    public String source;
    public Intent clazz;
    public int img;
    public ItemType itemType;

    public enum Site {
        TOP, MID, BTM
    }

    public enum ItemType {
        NORMAL(0), NEW(1), CACHE(2), REDPACKET(3);

        public int getValue() {
            return value;
        }

        private int value;

        ItemType(int i) {
            this.value = i;
        }
    }

    public SetBean(int icon, String title, Site site, ItemType itemType, Intent clazz) {
        super();
        this.icon = icon;
        this.title = title;
        this.site = site;
        this.itemType = itemType;
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
