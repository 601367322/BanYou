package com.quanliren.quan_one.activity.through;

import android.content.Intent;
import android.view.View;

import com.amap.api.maps2d.model.LatLng;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.filter.ThroughFilterActivity_;
import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.bean.CustomFilterBean;
import com.quanliren.quan_one.dao.CustomFilterBeanDao;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.base.BaseListFragment;
import com.quanliren.quan_one.adapter.NearPeopleTwoAdapter;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.api.ThroughListApi;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.fragment.near.NearPeopleFragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OnActivityResult;

/**
 * Created by Shen on 2015/11/10.
 */
@EFragment(R.layout.fragment_list)
public class ThroughListFragment extends BaseListFragment<User> {

    CustomFilterBeanDao filterDao;
    @Override
    public Class<?> getClazz() {
        return User.class;
    }

    @Override
    public void init() {
        super.init();
        title.setText("会员漫游");
        setTitleRightIcon(R.drawable.filter);
        filterDao = DBHelper.customFilterBeanDao;
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

    @Override
    public void rightClick(View v) {
        ThroughFilterActivity_.intent(this).startForResult(NearPeopleFragment.NEAR_PEOPLE_FILTER);
    }
    @OnActivityResult(value = NearPeopleFragment.NEAR_PEOPLE_FILTER)
    public void onFilterResult(Intent data) {
        if (data != null) {
            CustomFilterBean cfb_sex;
            CustomFilterBean cfb_time;
            int sexIndex = data.getIntExtra("sexIndex", -1);
            int timeIndex = data.getIntExtra("timeIndex", -1);
            if (sexIndex != -1) {
                cfb_sex = new CustomFilterBean("性别", "", "sex_through", sexIndex);
                filterDao.deleteById("sex_through");
                filterDao.create(cfb_sex);
            } else {
                filterDao.deleteById("sex_through");
            }
            if (timeIndex != -1) {
                cfb_time = new CustomFilterBean("时间", "", "actime_through", timeIndex);
                filterDao.deleteById("actime_through");
                filterDao.create(cfb_time);
            } else {
                filterDao.deleteById("actime_through");
            }
            swipeRefresh();
        }
    }
}
