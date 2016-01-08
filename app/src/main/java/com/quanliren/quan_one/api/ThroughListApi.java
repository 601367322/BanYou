package com.quanliren.quan_one.api;

import android.content.Context;

import com.amap.api.maps2d.model.LatLng;
import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.util.URL;

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
        getParams().put("longitude",String.valueOf(ll.longitude));
        getParams().put("latitude", String.valueOf(ll.latitude));
    }
}
