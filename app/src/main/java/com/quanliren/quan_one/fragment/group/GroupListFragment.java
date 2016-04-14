package com.quanliren.quan_one.fragment.group;

import android.content.Intent;
import android.view.View;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.group.CreateGroupActivity_;
import com.quanliren.quan_one.activity.group.EditGroupActivity;
import com.quanliren.quan_one.activity.group.GroupDetailActivity_;
import com.quanliren.quan_one.adapter.GroupListAdapter;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.api.GroupListApi;
import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.bean.BadgeBean;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.custom.ShakeImageView;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.base.BaseListFragment;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Shen on 2015/12/24.
 */
@EFragment
public class GroupListFragment extends BaseListFragment<GroupBean> {

    @FragmentArg
    GroupType groupType;

    @ViewById(R.id.add_group)
    View addGroup;

    @FragmentArg
    String otherId;
    @FragmentArg
    String title_str;

    public enum GroupType {
        near, my, other
    }

    @Override
    public int getConvertViewRes() {
        return R.layout.near_group;
    }

    @Override
    public BaseAdapter getAdapter() {
        return new GroupListAdapter(getActivity());
    }

    @Override
    public BaseApi getApi() {
        return new GroupListApi(getActivity(), groupType);
    }

    @Override
    public Class<?> getClazz() {
        return GroupBean.class;
    }

    @Override
    public String getCacheKey() {
        switch (groupType) {
            case near:
                return super.getCacheKey();
            case other:
                return super.getCacheKey() + otherId;
            default:
                return super.getCacheKey() + ac.getUser().getId();
        }
    }

    @Override
    public boolean needLocation() {
        switch (groupType) {
            case near:
                return true;
            default:
                return super.needLocation();
        }
    }

    @Override
    public boolean needTitle() {
        switch (groupType) {
            case near:
                return false;
            default:
                return super.needTitle();
        }
    }

    @Override
    public boolean needCache() {
        return true;
    }

    /**
     * 删除已解散的群组
     *
     * @param intent
     * @param bean
     */
    @Receiver(actions = {EditGroupActivity.DISSOLVEGROUP})
    public void onReceiver(Intent intent, @Receiver.Extra("group") GroupBean bean) {
        if (intent.getAction().equals(EditGroupActivity.DISSOLVEGROUP)) {
            if (adapter != null) {
                List<GroupBean> list = adapter.getList();
                GroupBean temp = null;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId().equals(bean.getId())) {
                        temp = list.get(i);
                    }
                }
                if (temp != null) {
                    list.remove(temp);
                    adapter.notifyDataSetChanged();
                }
                showHideEmptyView();
            }
        }
    }

    @Override
    public void init() {
        if (getActivity() != null && init.compareAndSet(false, true)) {
            super.init();
            switch (groupType) {
                case near:
                    if (maction_bar != null)
                        maction_bar.setVisibility(View.GONE);
                    break;
                case other:
                    addGroup.setVisibility(View.GONE);
                    setTitleTxt(title_str);
                    break;
                default:
                    setTitleTxt(getString(R.string.my_group));
                    break;
            }
        }
    }

    @Override
    public void initParams() {
        super.initParams();
        api.initParam(otherId);
    }

    @Override
    public void initListView() {
        super.initListView();
        switch (groupType) {
            case near:
                ShakeImageView adImg = new ShakeImageView(getActivity());
                adImg.addToListView(listview);
                break;
        }
    }

    @Override
    public void listview(int position) {
        switch (groupType) {
            case near:
                position--;
                break;
        }
        AM.getActivityManager().popActivity(GroupDetailActivity_.class);
        GroupDetailActivity_.intent(this).bean(adapter.getItem(position)).start();
    }


    @Click(R.id.add_group)
    public void add_group(View view) {
        if (ac.getUserInfo().getIsvip() < 1) {
            Util.goVip(getActivity(), 0);
            return;
        } else if (ac.getUserInfo().getIsvip() > 0) {
            judgeCreat();
        }
    }

    /**
     * 是否可以创建群
     */
    void judgeCreat() {
        ac.finalHttp.post(URL.CAN_CREATE_GROUP, getAjaxParams(), new MyJsonHttpResponseHandler(getActivity(), Util.progress_arr[4]) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                String ratify = jo.getJSONObject(URL.RESPONSE).getString("ratify");
                if ("0".equals(ratify)) {
                    CreateGroupActivity_.intent(getActivity()).start();
                } else if ("1".equals(ratify)) {
                    showCustomToast("您只能创建一个群");
                }
            }
        });
    }

    @Override
    public void onSuccessCallBack(JSONObject jo) {
        super.onSuccessCallBack(jo);
        if (groupType == GroupType.my) {
            LoginUser loginUser = ac.getUser();
            BadgeBean badgeBean = DBHelper.badgeDao.getBadge(loginUser.getId());
            if (badgeBean != null) {
                badgeBean.getBean().setGroupBadge(false);
                DBHelper.badgeDao.update(badgeBean);
            }
        }
    }

}
