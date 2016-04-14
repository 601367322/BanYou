package com.quanliren.quan_one.activity.seting.wallet;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.PaymentDetailAdapter;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.api.PaymentDetailApi;
import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.bean.Payment;
import com.quanliren.quan_one.fragment.base.BaseListFragment;

import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.fragment_list)
public class PaymentDetailFragment extends BaseListFragment<Payment> {

    @Override
    public void init() {
        super.init();
        setTitleTxt("收支明细");
    }

    @Override
    public Class<?> getClazz() {
        return Payment.class;
    }

    @Override
    public BaseAdapter<Payment> getAdapter() {
        return new PaymentDetailAdapter(getActivity());
    }

    @Override
    public BaseApi getApi() {
        return new PaymentDetailApi(getActivity());
    }

}
