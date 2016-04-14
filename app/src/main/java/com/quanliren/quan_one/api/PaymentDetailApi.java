package com.quanliren.quan_one.api;

import android.content.Context;

import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.util.URL;

public class PaymentDetailApi extends BaseApi {

    public PaymentDetailApi(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return URL.GET_PAYMENT_DETAIL;
    }

    @Override
    public void initParam(Object... obj) {
        super.initParam(obj);
    }
}
