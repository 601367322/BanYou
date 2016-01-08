package com.quanliren.quan_one.api;

import android.content.Context;

import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.util.URL;

/**
 * Created by Shen on 2015/12/29.
 */
public class GroupMemberListApi extends BaseApi {

    @Override
    public String getUrl() {
        return URL.GROUP_MEMBER_LIST;
    }

    public GroupMemberListApi(Context context) {
        super(context);
    }

    @Override
    public void initParam(Object... obj) {
        super.initParam(obj);
        getParams().put("groupId",obj[0]);
    }
}
