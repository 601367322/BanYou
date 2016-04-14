package com.quanliren.quan_one.fragment.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.friend.FriendActivity_;
import com.quanliren.quan_one.activity.group.GroupListActivity_;
import com.quanliren.quan_one.bean.BadgeBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.base.BaseViewPagerChildFragment;
import com.quanliren.quan_one.pull.swipe.SwipeRefreshLayout;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.Serializable;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shen on 2015/12/22.
 */
@EFragment
public class RelationFragment extends BaseViewPagerChildFragment implements SwipeRefreshLayout.OnRefreshListener {

    @ViewById(R.id.swipe_layout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.care_count)
    TextView careCount;
    @Bind(R.id.funs_count)
    TextView funsCount;
    @Bind(R.id.group_count)
    TextView groupCount;
    @Bind(R.id.funs_badge)
    View funsBadge;
    @Bind(R.id.group_badge)
    View groupBadge;

    @Override
    public int getConvertViewRes() {
        return R.layout.fragment_relation;
    }

    @Override
    public void lazyInit() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setRefreshing(true);
    }

    @Click({R.id.fans_tab, R.id.care_tab, R.id.group_tab})
    public void tabClick(View view) {
        switch (view.getId()) {
            case R.id.fans_tab:
                FriendActivity_.intent(this).friendType(FriendListFragment.FriendType.funs).start();
                break;
            case R.id.care_tab:
                FriendActivity_.intent(this).friendType(FriendListFragment.FriendType.care).start();
                break;
            case R.id.group_tab:
                GroupListActivity_.intent(this).start();
                break;
        }
    }

    @Override
    public void onVisible() {
        super.onVisible();

        if (funsBadge != null) {
            LoginUser loginUser = ac.getUser();
            BadgeBean badgeBean = DBHelper.badgeDao.getBadge(loginUser.getId());
            if (badgeBean != null) {
                if (badgeBean.getBean().isFunsBadge()) {
                    funsBadge.setVisibility(View.VISIBLE);
                } else {
                    funsBadge.setVisibility(View.GONE);
                }
                if (badgeBean.getBean().isGroupBadge()) {
                    groupBadge.setVisibility(View.VISIBLE);
                } else {
                    groupBadge.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onVisible();
    }

    @Override
    public void onRefresh() {
        ac.finalHttp.post(URL.GET_RELATION_CNT, Util.getRequestParams(getActivity()), new MyJsonHttpResponseHandler(getActivity()) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                Cnt cnt = new Gson().fromJson(jo.getString(URL.RESPONSE), new TypeToken<Cnt>() {
                }.getType());
                careCount.setText(cnt.acut);
                funsCount.setText(cnt.fcut);
                groupCount.setText(cnt.gcut);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class Cnt implements Serializable {
        public String acut;
        public String fcut;
        public String gcut;
    }
}
