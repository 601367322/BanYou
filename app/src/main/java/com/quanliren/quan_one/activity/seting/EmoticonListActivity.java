package com.quanliren.quan_one.activity.seting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.seting.emoticon.EmoticonDetailActivity_;
import com.quanliren.quan_one.activity.seting.emoticon.EmoticonHistoryListActivity_;
import com.quanliren.quan_one.adapter.EmoticonDownloadListAdapter;
import com.quanliren.quan_one.adapter.ShopAdapter;
import com.quanliren.quan_one.bean.BadgeBean;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean.EBanner;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean.EmoticonZip;
import com.quanliren.quan_one.custom.RoundProgressBar;
import com.quanliren.quan_one.custom.ScrollViewPager;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.listener.IProductGridListener;
import com.quanliren.quan_one.pull.swipe.SwipeRefreshLayout;
import com.quanliren.quan_one.service.DownLoadEmoticonService_;
import com.quanliren.quan_one.util.BroadcastUtil;
import com.quanliren.quan_one.util.LogUtil;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.emoticonlist)
public class EmoticonListActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener, ShopAdapter.IBuyListener {

    public static final String TAG = "EmoticonListActivity";
    public static final String EMOTICONDOWNLOAD_PROGRESS = "com.quanliren.quan_one.activity.seting.EmoticonListActivity.EMOTICONDOWNLOAD_PROGRESS";
    public static final String DELETE_EMOTICONDOWNLOAD = "com.quanliren.quan_one.activity.seting.EmoticonListActivity.DELETE_EMOTICONDOWNLOAD";

    @ViewById
    SwipeRefreshLayout swipe_layout;
    @ViewById
    ListView listview;

    ScrollViewPager viewpager;

    EmoticonPagerImageAdapter badapter;

    EmoticonDownloadListAdapter adapter;

    @Override
    public void init() {
        super.init();

        setTitleTxt("表情下载");
        setTitleRightTxt("管理");
        swipe_layout.setOnRefreshListener(this);

        try {
            EmoticonActivityListBean list = null;
            CacheBean cb = DBHelper.cacheDao.dao.queryForId(TAG);
            if (cb != null) {
                list = new Gson().fromJson(cb.getValue(),
                        new TypeToken<EmoticonActivityListBean>() {
                        }.getType());
            }
            initView(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        refresh();
    }

    @Receiver(actions = {DELETE_EMOTICONDOWNLOAD, EMOTICONDOWNLOAD_PROGRESS}, registerAt = Receiver.RegisterAt.OnResumeOnPause)
    public void receiver(Intent i) {
        String action = i.getAction();
        LogUtil.d(action);
        if (action.equals(EMOTICONDOWNLOAD_PROGRESS)) {
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
                    .getChildAt(position + 1).findViewById(R.id.buy);
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

        } else if (action.equals(DELETE_EMOTICONDOWNLOAD)) {
            int id = i.getExtras().getInt("id");
            List<EmoticonZip> list = adapter.getList();
            for (EmoticonZip emoticonZip : list) {
                if (emoticonZip.getId() == id) {
                    emoticonZip.setHave(false);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void rightClick(View v) {
        super.rightClick(v);
        EmoticonHistoryListActivity_.intent(this).start();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (adapter != null) {
            checkHave(adapter.getList());
        }
    }

    @UiThread(delay = 200)
    public void refresh() {
        swipe_layout.setRefreshing(true);
    }

    @ItemClick
    void listview(int position) {
        if (position > 0)
            EmoticonDetailActivity_.intent(this)
                    .bean(((EmoticonZip) adapter.getItem(position - 1)))
                    .start();
    }

    @Override
    public void onRefresh() {
        ac.finalHttp.post(URL.EMOTICON_DOWNLOAD_LIST, getAjaxParams(),
                new MyJsonHttpResponseHandler(mContext) {
                    @Override
                    public void onSuccessRetCode(JSONObject jo) throws Throwable {
                        CacheBean cb = new CacheBean(TAG, jo
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

    void initView(EmoticonActivityListBean bean) {
        if (bean == null) {
            return;
        }
        if (viewpager == null) {
            View view = View.inflate(this, R.layout.emoticonlist_head, null);
            viewpager = (ScrollViewPager) view.findViewById(R.id.viewpager);
            listview.addHeaderView(view);
            viewpager.setOnPageChangeListener(null);
            viewpager.setAdapter(badapter = new EmoticonPagerImageAdapter(bean
                    .getBlist(), new IProductGridListener() {

                @Override
                public void imgClick(View view) {
                    EBanner bean = (EBanner) view.getTag();
                    EmoticonZip zip = new EmoticonZip();
                    zip.setId(bean.getId());
                    EmoticonDetailActivity_.intent(EmoticonListActivity.this)
                            .bean(zip)
                            .start();
                }
            }));
            viewpager.startAutoSlide();
        } else {
            badapter.setList(bean.getBlist());
            badapter.notifyDataSetChanged();
        }

        LoginUser loginUser = ac.getUser();

        if (bean.getPlist() != null) {
            try {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("userId", loginUser.getId());
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
                adapter = new EmoticonDownloadListAdapter(this);
                adapter.setList(bean.getPlist());
                adapter.setListener(this);
                OnScrollListener listener = new PauseOnScrollListener(
                        ImageLoader.getInstance(), false, true);
                listview.setOnScrollListener(listener);
                listview.setAdapter(adapter);
            } else {
                adapter.setList(bean.getPlist());
                adapter.notifyDataSetChanged();
            }
        }

        BadgeBean badgeBean = DBHelper.badgeDao.getBadge(loginUser.getId());
        if (badgeBean != null) {
            badgeBean.getBean().setEmotionBadge(false);
            DBHelper.badgeDao.update(badgeBean);
        }
    }

    public void checkHave(List<EmoticonZip> plist) {
        if (plist != null) {
            try {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("userId", ac.getUser().getId());
                List<EmoticonZip> list = DBHelper.emoticonZipDao.dao.queryForFieldValues(map);

                for (EmoticonZip ez : plist) {
                    ez.setHave(false);
                }

                for (EmoticonZip ez : plist) {
                    for (EmoticonZip emoticonZip : list) {
                        if (ez.getId() == emoticonZip.getId()) {
                            ez.setHave(true);
                        }
                    }
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class EmoticonPagerImageAdapter extends PagerAdapter {

        List<EBanner> urllist = new ArrayList<EBanner>();

        IProductGridListener listener = null;

        public EmoticonPagerImageAdapter(List<EBanner> list,
                                         IProductGridListener listener) {
            this.urllist = list;
            this.listener = listener;
        }

        public void setList(List<EBanner> list) {
            this.urllist = list;
        }

        @Override
        public int getCount() {
            if (urllist.size() > 1) {
                return Integer.MAX_VALUE;
            }
            return urllist.size();
        }

        int height = 200;

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        private final DisplayImageOptions options_no_default = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();

        @Override
        public View instantiateItem(final ViewGroup container, int position) {
            View view = View.inflate(container.getContext(),
                    R.layout.emoticon_pager_image_item, null);
            final ImageView photoView = (ImageView) view.findViewById(R.id.img);
            final RoundProgressBar rp = (RoundProgressBar) view
                    .findViewById(R.id.progressBar);
            ImageLoader.getInstance().displayImage(
                    urllist.get(position % urllist.size()).getBannerUrl(),
                    photoView, options_no_default,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            float scale = (float) container.getContext()
                                    .getResources().getDisplayMetrics().widthPixels
                                    / (float) loadedImage.getWidth();
                            height = (int) (loadedImage.getHeight() * scale);
                            ((ImageView) view)
                                    .setLayoutParams(new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.MATCH_PARENT,
                                            height));
                            viewpager
                                    .setLayoutParams(new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.MATCH_PARENT,
                                            height));
                        }
                    }, new ImageLoadingProgressListener() {

                        @Override
                        public void onProgressUpdate(String imageUri,
                                                     View view, int current, int total) {
                            if (current == total) {
                                rp.setVisibility(View.GONE);
                            } else {
                                rp.setVisibility(View.VISIBLE);
                                rp.setMax(total);
                                rp.setProgress(current);
                            }
                        }
                    });
            photoView.setTag(urllist.get(position % urllist.size()));
            photoView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.imgClick(photoView);
                }
            });
            container.addView(view, LayoutParams.MATCH_PARENT, height);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    void doSuccess(Button mProgress) {
        EmoticonZip ez = (EmoticonZip) mProgress.getTag();
        ez.setHave(true);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void buyClick(Button progress) {
        if (Util.isFastDoubleClick()) {
            return;
        }
        final EmoticonZip ez = (EmoticonZip) progress.getTag();
        User user = ac.getUserInfo();
        if (user == null) {
            return;
        }
        if (ez.getType() == 1 && user.getIsvip() == 0) {// 会员
            Util.goVip(this, 0);
            return;
        }
        if (ez.getType() == 2 && ez.getIsBuy() == 0) {// 付费

            return;
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewpager != null) {
            viewpager.cancelAutoSlide();
        }
    }
}
