package com.quanliren.quan_one.fragment.message;

import android.content.Intent;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.BlackPeopleAdapter;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.api.BlackListApi;
import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.bean.BadgeBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.base.BaseListFragment;
import com.quanliren.quan_one.util.Util;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.Receiver;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Shen on 2015/12/8.
 */
@EFragment(R.layout.fragment_list)
public class FriendListFragment extends BaseListFragment<User> {

    public static final String ADDCARE = "com.quanliren.quan_one.fragment.message.FriendListFragment.addcare";
    public static final String REMOVECARE = "com.quanliren.quan_one.fragment.message.FriendListFragment.removecare";

    @FragmentArg
    FriendType friendType;

    public enum FriendType {
        care, funs, black, invite
    }

    @Override
    public void init() {
        super.init();

        switch (friendType) {
            case care:
                setTitleTxt(getString(R.string.care));
                break;
            case funs:
                setTitleTxt(getString(R.string.funs));
                break;
        }

    }

    @Override
    public BaseAdapter getAdapter() {
        return new BlackPeopleAdapter(getActivity());
    }

    @Override
    public BaseApi getApi() {
        return new BlackListApi(getActivity(), friendType);
    }

    @Override
    public Class<?> getClazz() {
        return User.class;
    }

    @Override
    public String getCacheKey() {
        LoginUser loginUser = ac.getUser();
        switch (friendType) {
            case care:
                return super.getCacheKey() + loginUser.getId() + "care";
            case funs:
                return super.getCacheKey() + loginUser.getId() + "funs";
        }
        return super.getCacheKey();
    }

    @Override
    public void initParams() {
        api.initParam();
    }

    public void listview(int position) {
        if (position <= adapter.getCount()) {
            User user = adapter.getItem(position);
            Util.startUserInfoActivity(getActivity(), user);
        }
    }

    @Override
    public boolean needCache() {
        return true;
    }

    @Receiver(actions = {ADDCARE, REMOVECARE})
    public void onReceiver(Intent intent, @Receiver.Extra("user") User user) {
        if (friendType == FriendType.care) {
            if (intent.getAction().equals(REMOVECARE)) {
                if (adapter != null) {
                    List<User> list = adapter.getList();
                    User temp = null;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getId().equals(user.getId())) {
                            temp = list.get(i);
                        }
                    }
                    if (temp != null) {
                        list.remove(temp);
                        adapter.notifyDataSetChanged();
                    }
                    showHideEmptyView();
                }
            } else if (intent.getAction().equals(ADDCARE)) {
                if (adapter != null) {
                    List<User> list = adapter.getList();
                    User temp = null;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getId().equals(user.getId())) {
                            temp = list.get(i);
                        }
                    }
                    if (temp == null) {
                        list.add(0, user);
                        adapter.notifyDataSetChanged();
                    }
                    showHideEmptyView();
                }
            }
        }
    }

    @Override
    public void onSuccessCallBack(JSONObject jo) {
        super.onSuccessCallBack(jo);
        if (friendType == FriendType.funs) {
            LoginUser loginUser = ac.getUser();
            BadgeBean badgeBean = DBHelper.badgeDao.getBadge(loginUser.getId());
            if (badgeBean != null) {
                badgeBean.getBean().setFunsBadge(false);
                DBHelper.badgeDao.update(badgeBean);
            }
        }
    }
}
