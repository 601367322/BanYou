package com.quanliren.quan_one.fragment.group;

import android.view.View;

import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.GroupMemberListAdapter;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.api.GroupMemberListApi;
import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.fragment.base.BaseListFragment;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shen on 2015/12/29.
 */
@EFragment(R.layout.fragment_list)
public class GroupMemberListFragment extends BaseListFragment<User> implements GroupMemberListAdapter.GroupMemberItemDealListener {

    @FragmentArg
    GroupBean group;

    boolean isOk = false;

    @Override
    public void init() {
        super.init();
        setTitleTxt(getString(R.string.group_member));
        if (group != null && ac.getUser().getId().equals(group.getUser().getId())) {
            setTitleRightTxt(R.string.edit);
        }
    }

    @Override
    public BaseAdapter<User> getAdapter() {
        return new GroupMemberListAdapter(getActivity(), this, GroupMemberListAdapter.ListType.member);
    }

    @Override
    public BaseApi getApi() {
        return new GroupMemberListApi(getActivity());
    }

    @Override
    public Class<?> getClazz() {
        return User.class;
    }

    @Override
    public void initParams() {
        super.initParams();
        api.initParam(group.getId());
    }

    @Override
    public void rightClick(View v) {
        if (!ac.getUser().getId().equals(group.getUser().getId())) {
            return;
        }
        if (isOk == false) {
            setTitleRightTxt(R.string.ok);
            editOpen();
        } else {
            setTitleRightTxt(R.string.edit);
            editClose();
        }
        isOk = !isOk;
    }

    public void editOpen() {
        ((GroupMemberListAdapter) adapter).setShow(true);
        adapter.notifyDataSetChanged();
    }

    public void editClose() {
        ((GroupMemberListAdapter) adapter).setShow(false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void deal(final User user, boolean checked) {
        RequestParams params = getAjaxParams();
        params.put("type", 4);
        params.put("groupId", group.getId());
        params.put("otherId", user.getId());
        ac.finalHttp.post(getActivity(), URL.GROUP_MANAGER_USER, params, new MyJsonHttpResponseHandler(getActivity(), Util.progress_arr[4]) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                dataList.remove(user);
                adapter.setList(updateList(dataList));
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void listview(int position) {
        if (position <= adapter.getCount()) {
            User user = adapter.getItem(position);
            if (Util.isStrNotNull(user.getId())) {
                Util.startUserInfoActivity(getActivity(), user);
            }
        }
    }

    List dataList = new ArrayList();

    @Override
    public void onSuccessRefreshUI(JSONObject jo, List list, boolean cache) {
        dataList.clear();
        dataList.addAll(list);
        super.onSuccessRefreshUI(jo, updateList(list), cache);
    }

    List updateList(List<User> list) {
        List<User> master = new ArrayList<User>();
        List<User> member = new ArrayList<User>();
        for (User user : list) {
            if ("1".equals(user.getMemberType())) {
                master.add(user);
            } else if ("0".equals(user.getMemberType())) {
                member.add(user);
            }
        }
        if (master.size() > 0) {
            master.add(0, new User(1));
        }
        if (member.size() > 0) {
            member.add(0, new User(0));
        }
        master.addAll(member);
        return master;
    }
}
