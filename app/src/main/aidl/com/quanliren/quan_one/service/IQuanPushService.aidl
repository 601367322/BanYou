package com.quanliren.quan_one.service;
interface IQuanPushService { 
    void sendMessage(String str); 
    boolean getServerSocket();
    void closeAll();
}