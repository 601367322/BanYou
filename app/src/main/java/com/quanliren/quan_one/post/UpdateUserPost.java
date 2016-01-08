package com.quanliren.quan_one.post;

import android.content.Context;
import android.content.Intent;

import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.SetingMoreFragment;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.json.JSONObject;

/**
 * Created by Shen on 2015/12/7.
 */
public class UpdateUserPost {

    public UpdateUserPost(final Context context, final MyJsonHttpResponseHandler callBack) {
        final AppClass ac = (AppClass) context.getApplicationContext();
        ac.finalHttp.post(URL.GET_USER_INFO, Util.getRequestParams(context),
                new MyJsonHttpResponseHandler() {
                    @Override
                    public void onSuccessRetCode(JSONObject jo) throws Throwable {
                        User user = DBHelper.userTableDao.updateUser(jo);
                        Intent intent = new Intent(SetingMoreFragment.UPDATE_USERINFO_);
                        intent.putExtra("user",user);
                        context.sendBroadcast(intent);
                        if (callBack != null) {
                            callBack.onSuccessRetCode(jo);
                        }
                    }
                });
    }
}
