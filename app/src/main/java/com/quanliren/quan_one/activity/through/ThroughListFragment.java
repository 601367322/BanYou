package com.quanliren.quan_one.activity.through;

import com.amap.api.maps2d.model.LatLng;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.fragment.base.BaseListFragment;
import com.quanliren.quan_one.adapter.NearPeopleTwoAdapter;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.api.ThroughListApi;
import com.quanliren.quan_one.bean.User;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

/**
 * Created by Shen on 2015/11/10.
 */
@EFragment(R.layout.fragment_list)
public class ThroughListFragment extends BaseListFragment<User> {

    @Override
    public Class<?> getClazz() {
        return User.class;
    }

    @Override
    public void init() {
        super.init();
        title.setText("会员漫游");
    }

    @FragmentArg
    public LatLng ll;

    @Override
    public BaseAdapter<User> getAdapter() {
        return new NearPeopleTwoAdapter(getActivity());
    }

    @Override
    public BaseApi getApi() {
        return new ThroughListApi(getActivity(),ll);
    }

    @Override
    public void initParams() {
        super.initParams();
        api.initParam();
    }
}
