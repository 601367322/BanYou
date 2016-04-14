package com.quanliren.quan_one.api;

import android.content.Context;

import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.util.URL;

/**
 * Created by Shen on 2016/4/6.
 */
public class RankApi extends BaseApi {

    public RankApi(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return URL.HOT_USER_LIST;
    }
}
