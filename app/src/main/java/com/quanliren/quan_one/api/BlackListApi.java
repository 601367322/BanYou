package com.quanliren.quan_one.api;

import android.content.Context;

import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.fragment.message.FriendListFragment;
import com.quanliren.quan_one.util.URL;

/**
 * Created by Shen on 2015/11/9.
 */
public class BlackListApi extends BaseApi {

    FriendListFragment.FriendType friendType;

    public BlackListApi(Context context, FriendListFragment.FriendType ft) {
        super(context);
        this.friendType = ft;
    }

    @Override
    public String getUrl() {
        switch (friendType) {
            case funs:
            case care:
                return URL.MYCAREANDFUNS;
            case invite:
                return URL.GROUPMYCARE;
            case black:
                return URL.BLACKLIST;
        }
        return "";
    }

    @Override
    public void initParam(Object... obj) {
        super.initParam(obj);
        switch (friendType) {
            case funs:
                getParams().put("type", 2);
                break;
            case care:
                getParams().put("type", 1);
                break;
            case black:
                break;
            case invite:
                getParams().put("groupId", obj[0].toString());
                break;
        }
    }
}
