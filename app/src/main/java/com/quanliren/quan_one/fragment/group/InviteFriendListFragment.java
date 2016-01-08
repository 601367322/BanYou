package com.quanliren.quan_one.fragment.group;

import android.view.View;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.GroupMemberListAdapter;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.api.BlackListApi;
import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.fragment.base.BaseListFragment;
import com.quanliren.quan_one.fragment.message.FriendListFragment;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kong on 2015/12/31.
 */
@EFragment(R.layout.fragment_list)
public class InviteFriendListFragment extends BaseListFragment<User> implements GroupMemberListAdapter.GroupMemberItemDealListener {
    @FragmentArg
    GroupBean group;
    int inviteCount = 0;
    List<String> selectList = new ArrayList<String>();

    @Override
    public void init() {
        super.init();
        setTitleTxt("邀请好友(0)");
        setTitleRightTxt(getString(R.string.ok));
    }

    @Override
    public Class<?> getClazz() {
        return User.class;
    }

    @Override
    public BaseAdapter<User> getAdapter() {
        return new GroupMemberListAdapter(getActivity(), this, GroupMemberListAdapter.ListType.invite);
    }

    @Override
    public BaseApi getApi() {
        return new BlackListApi(getActivity(), FriendListFragment.FriendType.invite);
    }

    @Override
    public void initParams() {
        api.initParam(group.getId());
    }

    @Override
    public void setJsonData(JSONObject jo, boolean cache) {
        List<User> list = (List<User>) Util.jsonToList(jo.optJSONObject(URL.RESPONSE).optString(URL.LIST), getClazz());
        for (User user : list) {
            if (selectList.contains(user.getId())) {
                user.setChecked(true);
            }
        }
        onSuccessRefreshUI(jo, list, cache);
    }

    @Override
    public void deal(User bean, boolean checked) {
        if (checked) {
            addUser(bean);
        } else {
            removeUser(bean);
        }
    }

    private void removeUser(User bean) {
        if (selectList.contains(bean.getId())) {
            selectList.remove(bean.getId());
            setTitleTxt("邀请好友(" + (--inviteCount) + ")");
        }
    }

    private void addUser(User bean) {
        if (selectList.contains(bean.getId())) {
            return;
        }
        selectList.add(bean.getId());
        setTitleTxt("邀请好友(" + (++inviteCount) + ")");
    }

    @Override
    public void rightClick(View v) {
        if (adapter.getCount() < 2) {
            return;
        }
        if (selectList.size() > 0) {
            dealInvite();
        }
    }

    private void dealInvite() {
        String json = new Gson().toJson(selectList);
        RequestParams params = getAjaxParams();
        params.put("type", 1);
        params.put("groupId", group.getId());
        params.put("otherId", json);
        ac.finalHttp.post(getActivity(), URL.GROUP_MANAGER_USER, params, new MyJsonHttpResponseHandler(getActivity(), Util.progress_arr[4]) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                getActivity().finish();
            }
        });
    }
}
