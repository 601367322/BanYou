package com.quanliren.quan_one.activity.rank;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.RankingAdapter;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.api.RankApi;
import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.fragment.base.BaseListFragment;
import com.quanliren.quan_one.util.Util;

import org.androidannotations.annotations.EFragment;

/**
 * Created by Shen on 2016/4/6.
 */
@EFragment(R.layout.fragment_list)
public class RankListFragment extends BaseListFragment<User> {

    @Override
    public void init() {
        super.init();

        setTitleTxt("人气排行");
    }

    @Override
    public BaseAdapter<User> getAdapter() {
        return new RankingAdapter(getActivity());
    }

    @Override
    public BaseApi getApi() {
        return new RankApi(getActivity());
    }

    @Override
    public Class<?> getClazz() {
        return User.class;
    }

    @Override
    public void listview(int position) {
        super.listview(position);
        Util.startUserInfoActivity(getActivity(), adapter.getItem(position));
    }
}
