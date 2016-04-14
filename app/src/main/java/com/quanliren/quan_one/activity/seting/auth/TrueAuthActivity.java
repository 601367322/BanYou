package com.quanliren.quan_one.activity.seting.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.date.DateEmoticonsKeyBoardBar;
import com.quanliren.quan_one.adapter.DateDetailReplyAdapter;
import com.quanliren.quan_one.bean.DateReplyBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.VideoBean;
import com.quanliren.quan_one.custom.PopupMenu;
import com.quanliren.quan_one.custom.emoji.EmoticonsEditText;
import com.quanliren.quan_one.fragment.SetingMoreFragment;
import com.quanliren.quan_one.pull.XListView;
import com.quanliren.quan_one.pull.swipe.SwipeRefreshLayout;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

@EActivity(R.layout.activity_true_auth)
public class TrueAuthActivity extends BaseActivity implements XListView.IXListViewListener, SwipeRefreshLayout.OnRefreshListener, DateDetailReplyAdapter.IQuanDetailReplyAdapter, DateEmoticonsKeyBoardBar.KeyBoardBarViewListener {
    @ViewById
    SwipeRefreshLayout swipeLayout;
    @ViewById(R.id.listview)
    XListView listview;
    @ViewById(R.id.bottom_btns)
    View bottomBtns;
    @ViewById(R.id.bottom_btn)
    View bottomBtn;
    @ViewById(R.id.keyboard)
    DateEmoticonsKeyBoardBar keyBoardBar;
    @ViewById
    EmoticonsEditText et_chat;
    @ViewById(R.id.layout_bottom)
    View layoutBottom;
    VideoViewHolder viewHolder;
    @Extra
    String otherId;
    View headView = null;
    public VideoBean bean;
    int p = 0;
    DateDetailReplyAdapter adapter;
    PopupMenu popupMenu = null;

    @Override
    public void init() {
        super.init();
        setTitleTxt(getString(R.string.true_auth));
        listview.addHeaderView(headView = View.inflate(this, R.layout.activity_true_auth_head, null));
        viewHolder = new VideoViewHolder(headView, this);
        headView.setVisibility(View.GONE);
        layoutBottom.setVisibility(View.GONE);
        bottomBtns.setVisibility(View.GONE);
        bottomBtn.setVisibility(View.GONE);
        listview.setAdapter(adapter = new DateDetailReplyAdapter(this));
        adapter.setListener(this);
        listview.setXListViewListener(this);
        swipeLayout.setOnRefreshListener(this);
        keyBoardBar.setOnKeyBoardBarViewListener(this);
        if (ac.cs.getTRUE_NAME() == 0) {
            ac.cs.setTRUE_NAME(1);
        }
        refresh();

    }

    @Override
    public void rightClick(View v) {
        if (bean != null && bean.getVideoType() == 1 && bean.getUserId().equals(ac.getUser().getId())) {//自己的视频
            if (popupMenu == null) {
                popupMenu = new PopupMenu(mContext, v);
                popupMenu.inflate(R.menu.video_deal_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem arg0) {
                        switch (arg0.getItemId()) {
                            case R.id.re_auth:
                                //  重新认证
                                TrueNoAuthActivity_.intent(mContext).start();
                                finish();
                                break;
                            case R.id.del_auth:
                                //  删除认证
                                ac.finalHttp.post(mContext, URL.DELETEVIDEOAUTH, getAjaxParams(), new MyJsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccessRetCode(JSONObject jo) throws Throwable {
                                        Intent intent = new Intent(SetingMoreFragment.UPDATE_USERINFO);
                                        sendBroadcast(intent);
                                        finish();
                                    }
                                });
                                break;
                        }
                        return false;
                    }
                });

            }
            popupMenu.show();
        }
    }

    @UiThread(delay = 200l)
    public void refresh() {
        swipeLayout.setRefreshing(true);
    }

    @Override
    public void onLoadMore() {
        if (p > 0) {
            RequestParams rp = Util.getRequestParams(mContext);
            rp.put("uvId", bean.getUvId());
            rp.put("p", p);
            ac.finalHttp.post(URL.VIDEOREPLY, rp, new callBack());
        }
    }

    @Override
    public void onRefresh() {
        p = 0;
        RequestParams rp = Util.getRequestParams(mContext);
        if (!TextUtils.isEmpty(otherId)) {
            rp.put("otherId", otherId);
        }
        rp.put("p", 0);
        ac.finalHttp.post(URL.VIDEODETAIL, rp, new callBack());
    }

    public void setHeadSource() {
        viewHolder.bind(bean, 0);
        if (bean.getVideoType() == 2) {
            keyBoardBar.hideAutoView();
            layoutBottom.setVisibility(View.GONE);
            bottomBtn.setVisibility(View.VISIBLE);
        } else if (bean.getVideoType() == 1) {
            //  判断是不是自己的视频认证，是则点击右上角，弹出重新录制和删除认证
            if (bean.getUserId().equals(ac.getUser().getId())) {
                setTitleRightIcon(R.drawable.more);
            }
            layoutBottom.setVisibility(View.VISIBLE);
        }
        adapter.setList(bean.getCommlist());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void contentClick(DateReplyBean bean) {
        if (!bean.getUserid().equals(ac.getUser().getId())) {
            et_chat.setTag(bean);
            et_chat.setHint("回复 " + bean.getNickname() + " :");
        } else {
            et_chat.setHint("");
        }
        et_chat.setFocusable(true);
        et_chat.setFocusableInTouchMode(true);
        et_chat.requestFocus();
        Utils.openSoftKeyboard(this, et_chat);
    }

    @Override
    public void delete_contentClick(final DateReplyBean bean) {
        //  删除评论
        if (bean != null && bean.getUserid().equals(ac.getUser().getId())) {
            final RequestParams rp = Util.getRequestParams(mContext);
            rp.put("replyid", bean.getId());
            AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setMessage("你确定要删除这条评论吗？")
                    .setPositiveButton(
                            "确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    ac.finalHttp.post(URL.DELETEVIDEOAUTHREPLY, rp, new MyJsonHttpResponseHandler(mContext, Util.progress_arr[3]) {
                                        @Override
                                        public void onSuccessRetCode(JSONObject jo) throws Throwable {
                                            showCustomToast("删除成功");
                                            List<DateReplyBean> beans = adapter.getList();
                                            int position = -1;
                                            for (DateReplyBean b : beans) {
                                                if (b.getId() == (bean.getId())) {
                                                    position = beans.indexOf(b);
                                                    break;
                                                }
                                            }
                                            if (position != -1)
                                                deleteAnimate(position);
                                            viewHolder.commentNum.setText((Integer.valueOf(viewHolder.commentNum.getText()
                                                    .toString()) - 1) + "");
                                        }

                                    });
                                }
                            })
                    .setNegativeButton(
                            "取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                }
                            }).create();
            dialog.show();
        }
    }

    public void deleteAnimate(final int position) {
        adapter.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void logoCick(DateReplyBean bean) {
        Util.startUserInfoActivity(this, bean.getUserid());
    }

    @Override
    public void OnKeyBoardStateChange(int state, int height) {

    }

    @Override
    public void OnSendBtnClick(String msg) {
        sendPost(msg);
    }

    class callBack extends MyJsonHttpResponseHandler {

        public callBack() {
            super(mContext);
        }

        @Override
        public void onSuccessRetCode(JSONObject jo) throws Throwable {
            if (p == 0) {
                bean = new Gson().fromJson(jo.getString(URL.RESPONSE),
                        new TypeToken<VideoBean>() {
                        }.getType());
                setHeadSource();
                jo = jo.getJSONObject(URL.RESPONSE);
            } else {
                jo = jo.getJSONObject(URL.RESPONSE);
                List<DateReplyBean> commList = new Gson().fromJson(jo.getString("commlist"),
                        new TypeToken<List<DateReplyBean>>() {
                        }.getType());
                bean.getCommlist().addAll(commList);
            }
            p = jo.getInt("p");
            listview.setPage(p);
            headView.setVisibility(View.VISIBLE);
            listview.stop();

        }

        @Override
        public void onFailRetCode(JSONObject jo) {
            super.onFailRetCode(jo);
            swipeLayout.setRefreshing(false);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            swipeLayout.setRefreshing(false);
        }
    }

    void sendPost(String content) {

        Utils.closeSoftKeyboard(this);
        keyBoardBar.hideAutoView();

        RequestParams ap = Util.getRequestParams(mContext);
        ap.put("uvId", bean.getUvId());
        ap.put("content", content);
        ap.put("nickname", ac.getUserInfo().getNickname());

        DateReplyBean replayBean = new DateReplyBean();

        Object obj = et_chat.getTag();
        if (obj != null) {
            DateReplyBean rb = (DateReplyBean) obj;
            ap.put("replyuid", rb.getUserid());
            replayBean.setReplyuid(rb.getUserid());
            replayBean.setReplyuname(rb.getNickname());
        }
        replayBean.setContent(content);
        User user = ac.getUserInfo();
        replayBean.setAvatar(user.getAvatar());
        replayBean.setNickname(user.getNickname());
        replayBean.setUserid(user.getId());
        replayBean.setCtime(Util.fmtDateTime.format(new Date()));
        if ("男".equals(user.getSex()) || "1".equals(user.getSex())) {
            replayBean.setSex("1");
        } else {
            replayBean.setSex("0");
        }
        replayBean.setBirthday(user.getBirthday());
        replayBean.setAge(user.getAge());
        replayBean.setIsvip(user.getIsvip());
        ac.finalHttp.post(URL.REPLYVIDEODETAIL, ap, new replyCallBack(replayBean));
    }

    class replyCallBack extends MyJsonHttpResponseHandler {
        DateReplyBean replayBean;

        public replyCallBack(DateReplyBean replayBean) {
            super(mContext, "正在发表评论");
            this.replayBean = replayBean;
        }

        public void onStart() {
            super.onStart();
            bean.getCommlist().add(0, replayBean);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onSuccessRetCode(JSONObject jo) throws Throwable {
            String id = jo.getJSONObject(URL.RESPONSE).getString("id");
            replayBean.setId(id);
            viewHolder.commentNum.setText((Integer.valueOf(viewHolder.commentNum.getText().toString()) + 1) + "");
            if (!ac.getUser().getId().equals(bean.getUserId())) {
                closeInput();
            }
            et_chat.setText("");
            et_chat.setHint("");
            et_chat.setTag(null);
            Utils.closeSoftKeyboard(mContext);
        }

        @Override
        public void onFailRetCode(JSONObject jo) {
            super.onFailRetCode(jo);
            bean.getCommlist().remove(replayBean);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        Utils.closeSoftKeyboard(mContext);
        keyBoardBar.hideAutoView();
        super.onDestroy();
    }
}
