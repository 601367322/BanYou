package com.quanliren.quan_one.api;

import android.content.Context;

import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.util.URL;

/**
 * Created by Shen on 2016/4/12.
 */
public class PopularValueApi extends BaseApi {

    public PopularValueApi(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return URL.MY_POPULAR_VALUE;
    }
}
