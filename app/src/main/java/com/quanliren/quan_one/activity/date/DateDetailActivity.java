package com.quanliren.quan_one.activity.date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.jubao.JuBaoActivity_;
import com.quanliren.quan_one.adapter.DateDetailReplyAdapter;
import com.quanliren.quan_one.adapter.DateDetailReplyAdapter.IQuanDetailReplyAdapter;
import com.quanliren.quan_one.bean.DateBean;
import com.quanliren.quan_one.bean.DateReplyBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.PopupMenu;
import com.quanliren.quan_one.custom.emoji.EmoticonsEditText;
import com.quanliren.quan_one.pull.XListView;
import com.quanliren.quan_one.pull.swipe.SwipeRefreshLayout;
import com.quanliren.quan_one.util.Constants;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.sso.UMSsoHandler;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

@EActivity(R.layout.date_detail)
public class DateDetailActivity extends BaseActivity implements IQuanDetailReplyAdapter, DateEmoticonsKeyBoardBar.KeyBoardBarViewListener, XListView.IXListViewListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "DongTaiDetailActivity";

    @ViewById
    SwipeRefreshLayout swipeLayout;

    @ViewById(R.id.listview)
    XListView listview;

    @ViewById(R.id.keyboard)
    DateEmoticonsKeyBoardBar keyBoardBar;

    @ViewById
    EmoticonsEditText et_chat;

    @Extra
    public DateBean bean;

    @ViewById(R.id.layout_bottom)
    View layoutBottom;

    DateViewHolder viewHolder;

    View headView = null;

    DateDetailReplyAdapter adapter;

    PopupMenu popupMenu = null;

    @Override
    public void init() {
        super.init();

        setTitleTxt(getString(R.string.date_detail));
//        headView = View.inflate(this, R.layout.date_item, null);
        listview.addHeaderView(headView = View.inflate(this, R.layout.date_item, null));

        viewHolder = new DateViewHolder(headView, this, null, Utils.showCoin(this));

        viewHolder.setIsDetail(true);

        headView.setVisibility(View.GONE);

        layoutBottom.setVisibility(View.GONE);

        listview.setXListViewListener(this);

        listview.setAdapter(adapter = new DateDetailReplyAdapter(this));

        adapter.setListener(this);

        swipeLayout.setOnRefreshListener(this);

        keyBoardBar.setOnKeyBoardBarViewListener(this);

        // 配置需要分享的相关平台,并设置分享内容
        configPlatforms(mController, DATE);

        refresh();

        Util.umengCustomEvent(mContext, "date_detail_view");
    }

    @Override
    public void OnKeyBoardStateChange(int state, int height) {

    }

    @Override
    public void OnSendBtnClick(String msg) {
        sendPost(msg);
    }


    @UiThread(delay = 200l)
    public void refresh() {
        swipeLayout.setRefreshing(true);
    }

    public void collectionText() {
        if (bean != null && bean.getIscollect() != null) {
            if (bean.getIscollect().equals("0")) {
                collection.setTitle(R.string.collection);
            } else {
                collection.setTitle(R.string.cancle_collection);
            }
        }
    }

    public void setHeadSource() {
        viewHolder.bind(bean, 0);
        isMy();
        adapter.setList(bean.getCommlist());
        adapter.notifyDataSetChanged();
    }

    MenuItem collection;

    public void rightClick(View v) {
        keyBoardBar.hideAutoView();
        Utils.closeSoftKeyboard(this);
        RequestParams ap = Util.getRequestParams(DateDetailActivity.this);
        if (bean != null && bean.getUserid().equals(ac.getUser().getId())) {//自己的约会
            if (getString(R.string.over).equals(title_right_txt.getText().toString())) { //结束约会
                ap.put("dyid", bean.getDyid());
                dealClick(URL.DELETE_DONGTAI, "您确定要结束这条约会吗？", ap);
            } else if (getString(R.string.refresh).equals(title_right_txt.getText().toString())) {//约会延长15天结束
                ap.put("dyId", bean.getDyid());
                dealClick(URL.DATEDELAY, "刷新后，约会时间将延长15天", ap);
            }
        } else {//他人约会
            if (popupMenu == null) {
                popupMenu = new PopupMenu(DateDetailActivity.this, v);
                popupMenu.inflate(R.menu.near_dongtai_menu);
                collection = popupMenu.getMenu().findItem(R.id.collection);
                collectionText();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem arg0) {
                        switch (arg0.getItemId()) {
                            case R.id.collection:
                                collection();
                                break;
                            case R.id.share:
                                shareDateDetail();
                                break;
                            case R.id.jubao:
                                report();
                                break;
                        }
                        return false;
                    }
                });

            }
            popupMenu.show();
        }

    }

    void dealClick(final String url, String message, final RequestParams ap) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ac.finalHttp.post(url, ap,
                                new MyJsonHttpResponseHandler(DateDetailActivity.this, Util.progress_arr[3]) {
                                    @Override
                                    public void onSuccessRetCode(JSONObject jo) throws Throwable {
                                        showCustomToast("操作成功");
                                        exchangeRT();
                                    }
                                });
                    }
                }).create().show();

    }

    ;

    void exchangeRT() {
        if (getString(R.string.over).equals(title_right_txt.getText().toString())) { //结束约会
            setTitleRightTxt(getString(R.string.refresh));
        } else if (getString(R.string.refresh).equals(title_right_txt.getText().toString())) {//约会延长15天结束
            setTitleRightTxt(getString(R.string.over));
        }
    }

    ;
    private final UMSocialService mController = UMServiceFactory
            .getUMSocialService(Constants.DESCRIPTOR);

    public void shareDateDetail() {
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA);
        mController.openShare(DateDetailActivity.this, new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {
                Utils.closeSoftKeyboard(mContext);
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                Utils.closeSoftKeyboard(mContext);
            }
        });
    }

    class callBack extends MyJsonHttpResponseHandler {

        public callBack() {
            super(DateDetailActivity.this.mContext);
        }

        @Override
        public void onSuccessRetCode(JSONObject jo) throws Throwable {
            if (isRefresh == 0) {
                bean = Util.jsonToBean(jo.getString(URL.RESPONSE),DateBean.class);

                setHeadSource();
                if (Integer.valueOf(bean.getCnum()) < 21) {
                    listview.setPage(-1);
                } else {
                    listview.setPage(0);
                }

            } else if (isRefresh == 1) {
                jo = jo.getJSONObject(URL.RESPONSE);
                p = jo.getString("p");
                List<DateReplyBean> commList = new Gson().fromJson(jo.getString("commlist"),
                        new TypeToken<List<DateReplyBean>>() {
                        }.getType());
                bean.getCommlist().addAll(commList);
                listview.setPage(Integer.valueOf(p));
            }
            headView.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);
            listview.stop();
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
        ap.put("dyid", bean.getDyid());
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
        ac.finalHttp.post(URL.REPLY_DONGTAI, ap, new replyCallBack(replayBean));
    }

    int isRefresh = 0;

    @Override
    public void onRefresh() {
        isRefresh = 0;
        RequestParams rp = Util.getRequestParams(mContext);
        if (bean != null) {
            rp.put("dyid", bean.getDyid());
            ac.finalHttp.post(URL.GETDONGTAI_DETAIL, rp, new callBack());
        }
    }

    String p = "1";

    @Override
    public void onLoadMore() {
        if (isRefresh == 0) {
            p = "1";
            isRefresh = 1;
        }
        if (Integer.valueOf(p) > 0) {
            RequestParams rp = Util.getRequestParams(mContext);
            rp.put("dyid", bean.getDyid());
            rp.put("p", p);
            ac.finalHttp.post(URL.COMMETLIST, rp, new callBack());
        }
    }

    class replyCallBack extends MyJsonHttpResponseHandler {
        DateReplyBean replayBean;

        public replyCallBack(DateReplyBean replayBean) {
            super(DateDetailActivity.this.mContext, "正在发表评论");
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
            viewHolder.replyBtn.setText((Integer.valueOf(viewHolder.replyBtn.getText().toString()) + 1) + "");
            if (!ac.getUser().getId().equals(bean.getUserid())) {
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

    public void isMy() {
        if (bean != null) {
            if (bean.getUserid().equals(ac.getUser().getId())) {
                title_right_icon.setVisibility(View.GONE);
                if (bean.getDtstate() != null && !"0".equals(bean.getDtstate())) {
                    setTitleRightTxt(getString(R.string.refresh));
                } else {
                    setTitleRightTxt(getString(R.string.over));
                }
            } else {
                setTitleRightIcon(R.drawable.more);
                setTitleRightTxt("");
            }
        }
    }

    @Override
    public void delete_contentClick(final DateReplyBean bean) {
        if (bean != null && bean.getUserid().equals(ac.getUser().getId())) {
            final RequestParams rp = Util.getRequestParams(mContext);
            rp.put("replyid", bean.getId());
            AlertDialog dialog = new AlertDialog.Builder(DateDetailActivity.this)
                    .setMessage("你确定要删除这条评论吗？")
                    .setPositiveButton(
                            "确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    ac.finalHttp.post(URL.DELETE_REPLY, rp, new MyJsonHttpResponseHandler(DateDetailActivity.this, Util.progress_arr[3]) {
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
                                            viewHolder.replyBtn.setText((Integer.valueOf(viewHolder.replyBtn.getText()
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

    public void report() {
        if (bean == null) {
            return;
        }
        LoginUser my = ac.getUser();
        if (my == null) {
            return;
        }
        if (my.getId().equals(bean.getUserid())) {
            showCustomToast("这是自己的哟~");
            return;
        }
        User friend = new User();
        friend.setId(bean.getUserid());
        friend.setNickname(bean.getNickname());
        JuBaoActivity_.intent(mContext).other(friend).date(bean).startForResult(20001);
    }

    @Override
    public void logoCick(DateReplyBean bean) {
        Util.startUserInfoActivity(this, bean.getUserid());
    }


    public void collection() {
        if (ac.getUser() == null) {
            return;
        }
        RequestParams ap = Util.getRequestParams(mContext);
        ap.put("dyid", bean.getDyid());
        ap.put("type", bean.getIscollect());
        ac.finalHttp.post(URL.COLLECTDATE, ap, new MyJsonHttpResponseHandler(mContext, Util.progress_arr[3]) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                bean.setIscollect(bean.getIscollect().equals("0") ? "1"
                        : "0");
                if (bean.getIscollect().equals("1")) {
                    showCustomToast("收藏成功");
                } else {
                    showCustomToast("取消收藏成功");
                }
                collectionText();
            }
        });
    }

    public void deleteAnimate(final int position) {
        adapter.remove(position);
        adapter.notifyDataSetChanged();
    }

    //SSO授权回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.closeSoftKeyboard(mContext);
    }

    @Override
    protected void onDestroy() {
        Utils.closeSoftKeyboard(mContext);
        keyBoardBar.hideAutoView();
        super.onDestroy();
    }
}
