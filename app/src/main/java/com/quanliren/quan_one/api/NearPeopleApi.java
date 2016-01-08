package com.quanliren.quan_one.api;

import android.content.Context;

import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.bean.CustomFilterBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.util.URL;

import java.util.List;

/**
 * Created by Shen on 2015/11/9.
 */
public class NearPeopleApi extends BaseApi {

    public NearPeopleApi(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return URL.NearUserList;
    }

    public void initParam(Object... obj) {
        User user = ac.getUserInfo();
        List<CustomFilterBean> listCB = DBHelper.customFilterBeanDao.getAllFilter();
        if (listCB != null)
            for (CustomFilterBean cfb : listCB) {
                if (user != null) {
                    if (user.getIsvip() > 0) {
                        getParams().put(cfb.key, cfb.id + "");
                    } else {
                        if (!"actime".equals(cfb.key)) {
                            getParams().put(cfb.key, cfb.id + "");
                        }
                    }
                }
            }
        getParams().put("longitude", ac.cs.getLng());
        getParams().put("latitude", ac.cs.getLat());
    }

}
