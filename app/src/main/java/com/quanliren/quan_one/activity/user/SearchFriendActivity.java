package com.quanliren.quan_one.activity.user;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.group.GroupDetailActivity_;
import com.quanliren.quan_one.adapter.BlackPeopleAdapter;
import com.quanliren.quan_one.adapter.GroupListAdapter;
import com.quanliren.quan_one.adapter.NearPeopleTwoAdapter;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.application.AM;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.pull.XListView;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.List;

@EActivity(R.layout.search_friend)
public class SearchFriendActivity extends BaseActivity implements XListView.IXListViewListener {

    @ViewById(R.id.search_input)
    EditText search_input;
    @ViewById(R.id.mayuse)
    TextView mayuse;
    @ViewById(R.id.cancle_tv)
    TextView cancleTv;
    @ViewById(R.id.tv_user)
    TextView userTv;
    @ViewById(R.id.tv_group)
    TextView groupTv;
    @ViewById(R.id.et_clear)
    ImageButton et_clear;
    @ViewById(R.id.search_ll)
    RelativeLayout searchLl;
    @ViewById(R.id.actionbar)
    View actionbar;
    @ViewById(R.id.reminder)
    View reminder;
    @ViewById(R.id.search_select)
    View searchSelect;
    @ViewById
    public XListView listview;
    User user;
    NearPeopleTwoAdapter adapter;
    BlackPeopleAdapter sf_adapter;
    GroupListAdapter sgAdapter;
    LinearLayout.LayoutParams lp = null;
    RelativeLayout.LayoutParams rlp = null;
    int type = 0;

    @Override
    public void init() {
        super.init();
        setTitleTxt(R.string.search_friend);
        mayuse.setVisibility(View.GONE);
        adapter = new NearPeopleTwoAdapter(this);
        sf_adapter = new BlackPeopleAdapter(this);
        sgAdapter = new GroupListAdapter(this);
        listview.setAdapter(adapter);
        listview.setXListViewListener(this);
        cancleTv.setVisibility(View.GONE);
        lp = (LinearLayout.LayoutParams) actionbar.getLayoutParams();
        rlp = (RelativeLayout.LayoutParams) search_input.getLayoutParams();
        user = ac.getUserInfo();
        if (user == null) {
            return;
        }
        tuijUser();
    }

    void tuijUser() {
        RequestParams rp = getAjaxParams();
        rp.put("sex", user.getSex());
        ac.finalHttp.post(URL.MAYUSE_FRIEND, rp, new MyJsonHttpResponseHandler(this, Util.progress_arr[1]) {

            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                if (!jo.isNull(URL.RESPONSE)) {
                    List<User> list = Util.jsonToList(jo.optJSONObject(URL.RESPONSE).optString(URL.LIST), User.class);
                    if (list.size() > 0) {
                        mayuse.setVisibility(View.VISIBLE);
                    }
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    ;
    int p = 0;
    RequestParams rp = null;
    RequestParams params = null;

    public void goSearch() {
        Util.umengCustomEvent(mContext, "search_user");
        type = 0;
        listview.setAdapter(sf_adapter);
        if (searchSelect.getVisibility() == View.VISIBLE) {
            searchSelect.setVisibility(View.GONE);
        }
        rp = getAjaxParams();
        closeInput();
        if (search_input.getText().toString().trim().equals("")) {
            showCustomToast(getString(R.string.no_input));
            return;
        }
        p = 0;

        rp.put("p", p);
        rp.put("keyword", search_input.getText().toString().trim());
        httpPost();
    }

    @Override
    public void onLoadMore() {
        if (type == 0) {
            rp.put("p", p);
            httpPost();
        } else if (type == 1) {
            params.put("p", p);
            groupHttpPost();
        }
    }

    void httpPost() {
        ac.finalHttp.post(URL.SEARCH_FRIEND, rp, new MyJsonHttpResponseHandler(this, getString(R.string.searching)) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                if (jo.isNull(URL.RESPONSE)) {
                    showCustomToast(getString(R.string.no_find_friend));
                } else {
                    List<User> list = Util.jsonToList(jo.optJSONObject(URL.RESPONSE).optString(URL.LIST), User.class);
                    setData(list, sf_adapter, Util.getPage(jo));
                }
            }


        });
    }

    void setData(List list, BaseAdapter adapter, int page) {
        if (list.size() > 0) {
            mayuse.setVisibility(View.GONE);
            if (p == 0) {
                adapter.setList(list);
                adapter.notifyDataSetChanged();
                listview.setSelection(0);
            } else {
                adapter.add(list);
                adapter.notifyDataSetChanged();
            }
            p = page;
            listview.setPage(page);
        } else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
            listview.setPage(-1);
            showCustomToast(getString(R.string.no_find_friend));
        }
    }

    /**
     * 搜索框父控件监听
     * @param view
     */
    @Click(R.id.search_ll)
    void searchChange(View view) {
        search_input.requestFocus();
        Utils.openSoftKeyboard(mContext, search_input);
    }

    /**
     * 搜索还是取消
     * @param view
     */
    @Click(R.id.cancle_tv)
    void cancleSearch(View view) {
        if ("搜索".equals(cancleTv.getText().toString())) {
            goSearch();
        } else if ("取消".equals(cancleTv.getText().toString())) {
            back();
        }
    }

    /**
     * 监听输入框焦点变化
     * @param v
     * @param hasFocus
     */
    @FocusChange(R.id.search_input)
    void focusChanged(View v, boolean hasFocus) {
        if (hasFocus) {
            if (search_input.getText().toString().length() == 0) {
                reminder.setVisibility(View.VISIBLE);
            }
            listview.setAdapter(sf_adapter);
            cancleTv.setVisibility(View.VISIBLE);
            mayuse.setVisibility(View.GONE);
            rlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            search_input.setLayoutParams(rlp);
            if (lp.topMargin == -ImageUtil.dip2px(this, 48)) {
                return;
            }
            ValueAnimator animator = ObjectAnimator.ofInt(0, ImageUtil.dip2px(this, 48));
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = Integer.parseInt(animation.getAnimatedValue().toString());
                    lp.topMargin = -value;
                    actionbar.setLayoutParams(lp);
                }
            });
            animator.setDuration(200).start();
        }
    }

    /**
     * 按键监听（点完成，就开始查找好友）
     *
     * @param hello
     * @param actionId
     * @param keyEvent
     */
    @EditorAction(R.id.search_input)
    void onEditorActions(TextView hello, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            goSearch();
        }
    }

    @AfterTextChange(R.id.search_input)
    void afterTextChanged(Editable s) {
        if (s.toString().length() > 0) {
            et_clear.setVisibility(View.VISIBLE);
            cancleTv.setText("搜索");
            clearData();
            searchSelect.setVisibility(View.VISIBLE);
            reminder.setVisibility(View.GONE);
            userTv.setText(s.toString());
            groupTv.setText(s.toString());
        } else {
            et_clear.setVisibility(View.GONE);
            cancleTv.setText("取消");
            clearData();
            searchSelect.setVisibility(View.GONE);
            reminder.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.et_clear)
    void etClear(View view) {
        search_input.setText("");
    }

    @Override
    public void onBackPressed() {
        if (cancleTv.getVisibility() == View.GONE) {
            finish();
            return;
        }
        back();
    }

    @ItemClick
    public void listview(int position) {
        if (type == 0) {
            if (position <= sf_adapter.getCount()) {
                User user = sf_adapter.getItem(position);
                Util.startUserInfoActivity(mContext, user);
            }
        } else if (type == 1) {
            AM.getActivityManager().popActivity(GroupDetailActivity_.class);
            GroupDetailActivity_.intent(this).bean(sgAdapter.getItem(position)).start();
        }

    }

    void back() {
        clearData();
        search_input.setText("");
        listview.setAdapter(adapter);
        if (adapter.getList().size() > 0) {
            mayuse.setVisibility(View.VISIBLE);
        }
        Utils.closeSoftKeyboard(mContext);
        lp.topMargin = ImageUtil.dip2px(SearchFriendActivity.this, 0);
        actionbar.setLayoutParams(lp);
        rlp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        search_input.setLayoutParams(rlp);
        cancleTv.setVisibility(View.GONE);
        search_input.clearFocus();
        if (reminder.getVisibility() == View.VISIBLE) {
            reminder.setVisibility(View.GONE);
        }
    }

    void clearData() {
        sf_adapter.clear();
        sf_adapter.notifyDataSetChanged();
        sgAdapter.clear();
        sgAdapter.notifyDataSetChanged();
        listview.setPage(-1);
    }

    /**
     * 查找用户
     */
    @Click(R.id.search_user)
    void searchUser() {
        goSearch();
    }

    /**
     * 查找群组
     */
    @Click(R.id.search_group)
    void searchGroup() {
        closeInput();
        type = 1;
        if (searchSelect.getVisibility() == View.VISIBLE) {
            searchSelect.setVisibility(View.GONE);
        }
        listview.setAdapter(sgAdapter);
        params = getAjaxParams();
        params.put("groupName", search_input.getText().toString().trim());
        params.put("p", 0);
        groupHttpPost();
        Util.umengCustomEvent(mContext, "search_group");
    }

    void groupHttpPost() {
        ac.finalHttp.post(URL.GROUP_LIST, params, new MyJsonHttpResponseHandler(this, getString(R.string.searching)) {

            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                if (jo.isNull(URL.RESPONSE)) {
                    showCustomToast(getString(R.string.no_find_friend));
                } else {
                    List<GroupBean> list = Util.jsonToList(jo.optJSONObject(URL.RESPONSE).optString(URL.LIST), GroupBean.class);
                    setData(list, sgAdapter, Util.getPage(jo));
                }
            }
        });
    }

    ;
}
