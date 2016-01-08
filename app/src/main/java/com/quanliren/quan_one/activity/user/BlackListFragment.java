package com.quanliren.quan_one.activity.user;

import android.content.Intent;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.fragment.base.BaseListFragment;
import com.quanliren.quan_one.adapter.BlackPeopleAdapter;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.api.BlackListApi;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.LoginUserDao;
import com.quanliren.quan_one.fragment.message.FriendListFragment;
import com.quanliren.quan_one.util.Util;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;

import java.util.Date;
import java.util.List;

/**
 * Created by Shen on 2015/7/9.
 */
@EFragment(R.layout.fragment_list)
public class BlackListFragment extends BaseListFragment<User> {

    public static final String CANCLEBLACKLIST = "com.quanliren.quan_one.activity.user.BlackListActivity.CANCLEBLACKLIST";
    public static final String ADDEBLACKLIST = "com.quanliren.quan_one.activity.user.BlackListActivity.ADDBLACKLIST";


    @Override
    public void init() {
        super.init();
        setTitleTxt("黑名单");
    }

    @Override
    public Class<?> getClazz() {
        return User.class;
    }

    @Override
    public BaseAdapter<User> getAdapter() {
        return new BlackPeopleAdapter(getActivity());
    }

    @Override
    public BaseApi getApi() {
        return new BlackListApi(getActivity(), FriendListFragment.FriendType.black);
    }

    @Override
    public boolean needCache() {
        return true;
    }

    @Override
    public String getCacheKey() {
        return super.getCacheKey() + LoginUserDao.getInstance(getActivity()).getUser().getId();
    }

    @Override
    public int getEmptyView() {
        return R.layout.my_black_list_empty;
    }

    @Receiver(actions = {CANCLEBLACKLIST, ADDEBLACKLIST})
    public void receiver(Intent i) {
        String action = i.getAction();
        if (action.equals(CANCLEBLACKLIST)) {
            String id = i.getExtras().getString("id");
            List<User> user = adapter.getList();
            User temp = null;
            for (User user2 : user) {
                if (user2.getId().equals(id)) {
                    temp = user2;
                }
            }
            if (temp != null) {
                adapter.remove(temp);
                adapter.notifyDataSetChanged();
            }
        } else if (action.equals(ADDEBLACKLIST)) {
            User user = (User) i.getExtras().getSerializable("bean");
            user.setCtime(Util.fmtDateTime.format(new Date()));
            adapter.add(0, user);
            adapter.notifyDataSetChanged();
        }
    }

    public void listview(int position) {
        if (position <= adapter.getCount()) {
            User user = adapter.getItem(position);
            Util.startUserInfoActivity(getActivity(), user);
        }
    }

}
