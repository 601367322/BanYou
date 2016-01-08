package com.quanliren.quan_one.api;

import android.content.Context;

import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.util.URL;

/**
 * Created by Shen on 2015/11/10.
 */
public class VisitorListApi extends BaseApi {

    @Override
    public String getUrl() {
        return URL.VISITLIST;
    }

    public VisitorListApi(Context context) {
        super(context);
    }
}
