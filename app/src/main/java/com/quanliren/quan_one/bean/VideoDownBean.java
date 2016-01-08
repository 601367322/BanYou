package com.quanliren.quan_one.bean;

import java.io.Serializable;

public  class VideoDownBean implements Serializable {
        public DfMessage msg;
        public int current;
        public int total;

        public VideoDownBean() {
            super();
        }

        public VideoDownBean(DfMessage msg, int current, int total) {
            this.msg = msg;
            this.current = current;
            this.total = total;
        }
    }