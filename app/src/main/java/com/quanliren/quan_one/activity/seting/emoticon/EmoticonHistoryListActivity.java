package com.quanliren.quan_one.activity.seting.emoticon;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.seting.EmoticonListActivity;
import com.quanliren.quan_one.adapter.EmoticonDownloadHistoryListAdapter;
import com.quanliren.quan_one.adapter.ShopAdapter;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean.EmoticonZip;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.pull.swipe.SwipeRefreshLayout;
import com.quanliren.quan_one.service.DownLoadEmoticonService_;
import com.quanliren.quan_one.util.BroadcastUtil;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.emoticonlist)
public class EmoticonHistoryListActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener, ShopAdapter.IBuyListener {

    public static final String TAG = "EmoticonHistoryListActivity";
    public String CACHEKEY = TAG;

    @ViewById
    TextView empty;
    @ViewById
    SwipeRefreshLayout swipe_layout;
    @ViewById
    ListView listview;

    EmoticonDownloadHistoryListAdapter adapter;

    User user;

    @Override
    public void init() {
        super.init();
        user = ac.getUserInfo();
        CACHEKEY += user.getId();

        setTitleTxt("表情管理");
    }

    @Receiver(actions = EmoticonListActivity.EMOTICONDOWNLOAD_PROGRESS, registerAt = Receiver.RegisterAt.OnResumeOnPause)
    public void receiver(Intent i) {
        String action = i.getAction();
        if (action.equals(EmoticonListActivity.EMOTICONDOWNLOAD_PROGRESS)) {
            int state = i.getExtras().getInt("state");
            EmoticonZip bean = (EmoticonZip) i.getSerializableExtra("bean");

            List<EmoticonZip> list = adapter.getList();
            int position = -1;
            for (EmoticonZip emoticonZip : list) {
                if (emoticonZip.getId() == bean.getId()) {
                    position = list.indexOf(emoticonZip);
                }
            }
            if (position < 0) {
                return;
            }
            Button progress = (Button) listview
                    .getChildAt(position).findViewById(R.id.buy);
            switch (state) {
                case 0:
                    showCustomToast("正在下载");
                    progress.setEnabled(false);
                    break;
                case 1:
                    progress.setEnabled(false);
                    break;
                case 2:
                    progress.setEnabled(true);
                    doSuccess(progress);
                    break;
                case -1:
                    progress.setEnabled(true);
                    showCustomToast("下载失败");
                    break;
            }
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @UiThread(delay = 200)
    public void refresh(){
        swipe_layout.setRefreshing(true);
    }
    @AfterViews
    void initView() {
        swipe_layout.setOnRefreshListener(this);
        try {
            EmoticonActivityListBean list = null;
            CacheBean cb = DBHelper.cacheDao.dao.queryForId(CACHEKEY);
            if (cb != null) {
                list = new Gson().fromJson(cb.getValue(),
                        new TypeToken<EmoticonActivityListBean>() {
                        }.getType());
            }
            initView(list);
        } catch (Exception e) {
            e.printStackTrace();
        }refresh();
    }

    @ItemClick
    void listview(int position) {
        EmoticonDetailActivity_.intent(this)
                .bean(((EmoticonZip) adapter.getItem(position))).start();
    }

    @Override
    public void onRefresh() {
        ac.finalHttp.post(URL.EMOCTION_MANAGE, getAjaxParams(),
                new MyJsonHttpResponseHandler(mContext) {
                    @Override
                    public void onSuccessRetCode(JSONObject jo) throws Throwable {
                        CacheBean cb = new CacheBean(CACHEKEY, jo
                                .getString(URL.RESPONSE), new Date()
                                .getTime());
                        DBHelper.cacheDao.delete(cb);
                        DBHelper.cacheDao.create(cb);
                        EmoticonActivityListBean list = new Gson().fromJson(
                                jo.getString(URL.RESPONSE),
                                new TypeToken<EmoticonActivityListBean>() {
                                }.getType());
                        initView(list);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        swipe_layout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void buyClick(Button progress) {
        final EmoticonZip ez = (EmoticonZip) progress.getTag();
        User user = ac.getUserInfo();
        if (user == null) {
            return;
        }
        if (ez.getType() == 1 && user.getIsvip() == 0) {// 会员
            Util.goVip(this,0);
            return;
        }
        if (ez.getType() == 2 && ez.getIsBuy() == 0) {// 付费
            return;
        }

        if (ez.isHave()) {
            try {
                DBHelper.emoticonZipDao.delete(ez);
                ez.setHave(false);
                Intent i = new Intent(
                        EmoticonListActivity.DELETE_EMOTICONDOWNLOAD);
                i.putExtra("id", ez.getId());
                sendBroadcast(i);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            boolean isExists = false;
            try {
                EmoticonZip ezb = DBHelper.emoticonZipDao.dao.queryForId(ez.getId());
                if (ezb != null && ezb.getUserId().equals(ac.getUser().getId())) {
                    isExists = true;
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            try {
                if ((ez.getType() == 0 || ez.getIsBuy() == 1 || (ez.getType() == 1 && user
                        .getIsvip() > 0)) && !isExists) {
                    Intent i = new Intent(this, DownLoadEmoticonService_.class);
                    i.setAction(BroadcastUtil.DOWNLOADEMOTICON);
                    i.putExtra("bean", ez);
                    startService(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void doSuccess(Button mProgress) {
        EmoticonZip ez = (EmoticonZip) mProgress.getTag();
        ez.setHave(true);
        adapter.notifyDataSetChanged();
    }

    void initView(EmoticonActivityListBean bean) {
        if (bean == null) {
            empty.setVisibility(View.VISIBLE);
            return;
        }else{
            empty.setVisibility(View.GONE);
        }

        if (bean.getPlist() != null) {
            try {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("userId", ac.getUser().getId());
                List<EmoticonZip> list = DBHelper.emoticonZipDao.dao.queryForFieldValues(map);

                for (EmoticonZip ez : bean.getPlist()) {
                    for (EmoticonZip emoticonZip : list) {
                        if (ez.getId() == emoticonZip.getId()) {
                            ez.setHave(true);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (adapter == null) {
                adapter = new EmoticonDownloadHistoryListAdapter(this,
                        bean.getPlist(), this);
                OnScrollListener listener = new PauseOnScrollListener(
                        ImageLoader.getInstance(), false, true);
                listview.setOnScrollListener(listener);
                listview.setAdapter(adapter);
            } else {
                adapter.setList(bean.getPlist());
                adapter.notifyDataSetChanged();
            }
        }
    }

}
