package com.quanliren.quan_one.api;

import android.content.Context;

import com.amap.api.maps2d.model.LatLng;
import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.bean.CustomFilterBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.util.URL;

import java.util.List;

/**
 * Created by Shen on 2015/11/10.
 */
public class ThroughListApi extends BaseApi {

    LatLng ll;

    public ThroughListApi(Context context) {
        super(context);
    }

    public ThroughListApi(Context context, LatLng ll) {
        super(context);
        this.ll = ll;
    }

    @Override
    public String getUrl() {
        return URL.ROAMUSERLIST;
    }

    @Override
    public void initParam(Object... obj) {
        super.initParam(obj);
        List<CustomFilterBean> listCB = DBHelper.customFilterBeanDao.getAllFilter();
        if (listCB != null)
            for (CustomFilterBean cfb : listCB) {
                if ("sex_through".equals(cfb.key)) {
                    getParams().put("sex", cfb.id + "");
                }
                if ("actime_through".equals(cfb.key)) {
                    getParams().put("actime", cfb.id + "");
                }
            }
        getParams().put("longitude",String.valueOf(ll.longitude));
        getParams().put("latitude", String.valueOf(ll.latitude));
    }
}
