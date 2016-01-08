package com.quanliren.quan_one.post;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.BadgeBean;
import com.quanliren.quan_one.bean.CounterBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.json.JSONObject;

/**
 * Created by Shen on 2015/12/7.
 */
public class CounterPost {

    public CounterPost(Context context, final MyJsonHttpResponseHandler callBack) {
        if (Util.isFastCounter()) {
            return;
        }
        final AppClass ac = (AppClass) context.getApplicationContext();
        RequestParams rp = Util.getRequestParams(context);
        ac.finalHttp.post(URL.STATISTIC, rp, new MyJsonHttpResponseHandler() {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                LoginUser loginUser = ac.getUser();

                CounterBean counterBean = new CounterBean();
                counterBean.setDetail(jo.getJSONObject(URL.RESPONSE).toString());
                counterBean.setUserId(loginUser.getId());
                CounterBean.Counter remoteCounter = counterBean.getBean();

                CounterBean localBean = DBHelper.counterDao.getCounter(loginUser.getId());
                CounterBean.Counter localCounter = localBean.getBean();

                BadgeBean badgeBean = DBHelper.badgeDao.getBadge(loginUser.getId());
                if (badgeBean == null) {
                    badgeBean = new BadgeBean();
                }
                badgeBean.setUserId(loginUser.getId());
                BadgeBean.Badge badge = badgeBean.getBean();

                if (!localCounter.getAttdycnt().equals(remoteCounter.getAttdycnt())) {
                    badge.setDateBadge(true);
                }
                if (!localCounter.getPhizcnt().equals(remoteCounter.getPhizcnt())) {
                    badge.setEmotionBadge(true);
                }
                if (!localCounter.getFuncnt().equals(remoteCounter.getFuncnt())) {
                    badge.setFunsBadge(true);
                }
                if (!localCounter.getGroupcnt().equals(remoteCounter.getGroupcnt())) {
                    badge.setGroupBadge(true);
                }

                DBHelper.counterDao.delete(localBean);
                DBHelper.counterDao.create(counterBean);
                DBHelper.badgeDao.dao.createOrUpdate(badgeBean);

                if (callBack != null) {
                    callBack.onSuccessRetCode(jo);
                }
            }
        });
    }
}
