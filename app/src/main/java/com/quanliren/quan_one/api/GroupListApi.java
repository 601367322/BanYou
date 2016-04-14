package com.quanliren.quan_one.api;

import android.content.Context;

import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.fragment.group.GroupListFragment;
import com.quanliren.quan_one.util.URL;

/**
 * Created by Shen on 2015/12/24.
 */
public class GroupListApi extends BaseApi {

    GroupListFragment.GroupType type;

    public GroupListApi(Context context, GroupListFragment.GroupType type) {
        super(context);
        this.type = type;
    }

    @Override
    public String getUrl() {
        switch (type) {
            case near:
                return URL.GROUP_LIST;
            default:
                return URL.My_GROUP_LIST;
        }

    }

    @Override
    public void initParam(Object... obj) {
        super.initParam(obj);
        if (type == GroupListFragment.GroupType.near) {
            getParams().put("longitude", ac.cs.getLng());
            getParams().put("latitude", ac.cs.getLat());
        } else if (type == GroupListFragment.GroupType.other) {
            getParams().put("otherId", obj[0]);
        }
    }
}
