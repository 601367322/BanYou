package com.quanliren.quan_one.activity.seting.emoticon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.seting.EmoticonListActivity;
import com.quanliren.quan_one.adapter.EmoteLargeAdapter;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean.EmoticonZip;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean.EmoticonZip.EmoticonImageBean;
import com.quanliren.quan_one.custom.CustomScrollView;
import com.quanliren.quan_one.custom.NumberProgressBar;
import com.quanliren.quan_one.custom.emoji.EmoteGridView;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.pull.smoothprogressbar.SmoothProgressBar;
import com.quanliren.quan_one.service.DownLoadEmoticonService_;
import com.quanliren.quan_one.util.BroadcastUtil;
import com.quanliren.quan_one.util.DrawableCache;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifDrawable;

@EActivity
public class EmoticonDetailActivity extends BaseActivity implements EmoteGridView.EmoticonListener {

    @Extra
    EmoticonZip bean;
    @ViewById(R.id.gridview)
    EmoteGridView gridview;
    @ViewById(R.id.banner)
    ImageView banner;
    @ViewById(R.id.name)
    TextView name;
    @ViewById(R.id.size)
    TextView size;
    @ViewById(R.id.price)
    TextView price;
    @ViewById(R.id.buyBtn)
    Button buyBtn;
    @ViewById(R.id.child)
    View child;
    @ViewById(R.id.remark)
    TextView remark;
    @ViewById(R.id.scrollview)
    CustomScrollView scrollview;
    @ViewById(R.id.number_progress)
    NumberProgressBar number_progress;
    @ViewById(R.id.smooth_progress)
    SmoothProgressBar smooth_progress;
    EmoteLargeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emoticon_detail);
        initView();
        onRefreshStarted();
        receiveBroadcast(EmoticonListActivity.EMOTICONDOWNLOAD_PROGRESS, handler);
        Util.umengCustomEvent(mContext, "emotion_detail_view");
    }

    Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            Intent i = (Intent) msg.obj;
            String action = i.getAction();
            if (action.equals(EmoticonListActivity.EMOTICONDOWNLOAD_PROGRESS)) {
                int state = i.getExtras().getInt("state");
                EmoticonZip temp = (EmoticonZip) i.getSerializableExtra("bean");
                if (bean.getId() == temp.getId()) {
                    switch (state) {
                        case 0:
                            buyBtn.setVisibility(View.GONE);
                            smooth_progress.setVisibility(View.GONE);
                            number_progress.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            buyBtn.setVisibility(View.GONE);
                            smooth_progress.setVisibility(View.GONE);
                            number_progress.setVisibility(View.VISIBLE);
                            int gress = i.getExtras().getInt("progress");
                            int total = i.getExtras().getInt("total");
                            int percent = (int) (((float) gress / (float) total) * 100);
                            if (percent > 0 && percent < 100) {
                                number_progress.setMax(total);
                                number_progress.setProgress(gress);
                            }
                            break;
                        case 2:
                            smooth_progress.setVisibility(View.GONE);
                            number_progress.setVisibility(View.GONE);
                            number_progress.setProgress(0);
                            buyBtn.setVisibility(View.VISIBLE);
                            buyBtn.setEnabled(false);
                            buyBtn.setText("已下载");
                            break;
                        case -1:
                            smooth_progress.setVisibility(View.GONE);
                            number_progress.setVisibility(View.GONE);
                            number_progress.setProgress(0);
                            buyBtn.setVisibility(View.VISIBLE);
                            buyBtn.setEnabled(true);
                            buyBtn.setText("下载");
                            break;
                    }
                }
            }
            super.dispatchMessage(msg);
        }
    };

    @Click(R.id.buyBtn)
    public void buyBtn(View view) {
        User user = ac.getUserInfo();
        if (user == null) {
            return;
        }
        if (bean.getType() == 1 && user.getIsvip() == 0) {// 会员
            goVip();
            return;
        }
        if (bean.getType() == 2 && bean.getIsBuy() == 0) {// 付费

            return;
        }

        boolean isExists = false;
        try {
            EmoticonZip ezb = DBHelper.emoticonZipDao.getEmoticonById(ac.getUser().getId(), bean.getId());
            if (ezb != null) {
                isExists = true;
            }
        } catch (Exception e1) {

            e1.printStackTrace();
        }

        try {
            Intent i = new Intent(this, DownLoadEmoticonService_.class);
            i.setAction(BroadcastUtil.DOWNLOADEMOTICON);
            i.putExtra("bean", bean);
            startService(i);
            if ((bean.getType() == 0 || bean.getIsBuy() == 1 || (bean.getType() == 1 && user
                    .getIsvip() > 0)) && !isExists) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        smooth_progress.setInterpolator(new AccelerateInterpolator());
        smooth_progress.setIndeterminate(true);
        smooth_progress.setVisibility(View.VISIBLE);
        buyBtn.setVisibility(View.GONE);
    }

    void initView() {
        child.setVisibility(View.GONE);
        EmoticonZip be = DBHelper.emoticonZipDao.getEmoticonById(ac.getUser().getId(), bean.getId());
        if (be != null) {
            buyBtn.setEnabled(false);
            buyBtn.setText("已下载");
        }

    }

    @Override
    protected void onResume() {

        super.onResume();
        if (bean.getName() != null && !"".equals(bean.getName())) {
            setTitleTxt(bean.getName());
        } else {
            setTitleTxt("表情");
        }
    }

    public void onRefreshStarted() {
        RequestParams ap = getAjaxParams();
        ap.put("id", bean.getId() + "");
        ac.finalHttp.post(URL.EMOTICON_DETAIL, ap,
                new MyJsonHttpResponseHandler(mContext, Util.progress_arr[1]) {

                    @Override
                    public void onSuccessRetCode(JSONObject jo) throws Throwable {
                        init((EmoticonZip) new Gson().fromJson(
                                jo.getString(URL.RESPONSE),
                                new TypeToken<EmoticonZip>() {
                                }.getType()));
                    }

                });
    }

    public void init(EmoticonZip bean) {
        if (bean == null) {
            return;
        }
        this.bean = bean;
        child.setVisibility(View.VISIBLE);
        setTitle(bean.getName());
        ImageLoader.getInstance().displayImage(bean.getBannerUrl(), banner, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                float scale = (float) getApplicationContext()
                        .getResources().getDisplayMetrics().widthPixels
                        / (float) loadedImage.getWidth();
                int height = (int) (loadedImage.getHeight() * scale);
                ((ImageView) view)
                        .setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                height));
            }
        });

        name.setText(bean.getName());
        remark.setText(bean.getRemark());
        size.setText("大小：" + bean.getSize());
        switch (bean.getType()) {
            case 0:
                price.setText("免费");
                break;
            case 1:
                price.setText("会员专属");
                break;
            case 2:
                price.setText("¥" + bean.getPrice());
                break;
        }

        List<EmoticonImageBean> imgs = bean.getImglist();

        if (adapter != null) {
            adapter.setList(imgs);
            adapter.notifyDataSetChanged();
        } else {
            gridview.setListener(this);
            gridview.setAdapter(adapter = new EmoteLargeAdapter(this, imgs));
        }

    }

    @Override
    public void onEmoticonClick(EmoticonImageBean bean) {


    }

    class EmoticonPreview {

        @Bind(R.id.gif)
        ImageView gif;
        @Bind(R.id.progress)
        View progress;

        View view;

        public EmoticonPreview() {
            ButterKnife.bind(this, view = View.inflate(EmoticonDetailActivity.this, R.layout.emoticon_preview, null));
        }
    }

    EmoticonImageBean loadedBean = null;
    PopupWindow pop;
    EmoticonPreview ep;

    @Override
    public void onEmoticonLongPress(EmoticonImageBean bean, int[] xy, int[] wh) {

        scrollview.setEnableTouchScroll(false);
        setSwipeBackEnable(false);
        if (ep == null) {
            ep = new EmoticonPreview();
        }
        if ((loadedBean == null || (bean != null && !loadedBean.equals(bean)))
                && bean != null) {
            if (loadedBean != null) {
                GifDrawable gd = (GifDrawable) ep.gif.getDrawable();
                if (gd != null)
                    gd.stop();
                ep.gif.setImageDrawable(null);
            }

            loadedBean = bean;

            ImageLoader.getInstance().loadImage(bean.getGifUrl(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    ep.progress.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view,
                                              Bitmap loadedImage) {
                    ep.progress.setVisibility(View.GONE);
                    DrawableCache.getInstance().displayDrawable(ep.gif, ImageLoader.getInstance().getDiskCache().get(imageUri).getPath());
                }
            });

            if (pop == null) {
                pop = new PopupWindow(ep.view, ImageUtil.dip2px(this, 120),
                        ImageUtil.dip2px(this, 120));
                pop.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.emoticon_popup_bg));
            } else {
                pop.dismiss();
            }
            int x = (int) ((float) (pop.getWidth() - wh[0]) / 2);
            x = xy[0] - x;
            if (x < 0) {
                x = 0;
            } else if (x + pop.getWidth() > getResources().getDisplayMetrics().widthPixels) {
                x = getResources().getDisplayMetrics().widthPixels
                        - pop.getWidth();
            }

            int y = xy[1] - pop.getHeight()
                    - ImageUtil.dip2px(this, 20);

            if (y < 0) {
                y = xy[1] + wh[1]
                        + ImageUtil.dip2px(this, 20);
            }

            pop.showAtLocation(
                    scrollview,
                    Gravity.NO_GRAVITY,
                    x,
                    y);
        }
    }

    @Override
    public void onEmoticonLongPressCancle() {

        scrollview.setEnableTouchScroll(true);
        setSwipeBackEnable(true);
        if (loadedBean != null) {
            GifDrawable gd = (GifDrawable) ep.gif.getDrawable();
            if (gd != null)
                gd.stop();
            ep.gif.setImageDrawable(null);
        }
        loadedBean = null;
        if (pop != null) {
            pop.dismiss();
        }
    }
}
