package com.quanliren.quan_one.fragment;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.shop.ShopVipIntroduce_;
import com.quanliren.quan_one.activity.user.LoginActivity_;
import com.quanliren.quan_one.fragment.base.BaseViewPagerChildFragment;
import com.quanliren.quan_one.post.UpdateUserPost;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.PayUtil;
import com.quanliren.quan_one.util.Util;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

@EFragment
public class ShopVipFrament extends BaseViewPagerChildFragment implements View.OnTouchListener, View.OnClickListener {

    private static final int RQF_PAY = 1;
    private static final int RQF_LOGIN = 2;

    @ViewById(R.id.banner)
    ImageView banner;
    @ViewById(R.id.common_vip)
    ImageView common_vip;
    @ViewById(R.id.rich_vip)
    ImageView rich_vip;
    @ViewById(R.id.help_vip)
    ImageView help_vip;
    PayReq req = new PayReq();
    IWXAPI msgApi;

    @FragmentArg
    public boolean needBack = false;

    @Override
    public int getConvertViewRes() {
        return R.layout.vip_frament;
    }

    @Override
    public void lazyInit() {
        super.init();
        setTitleTxt(R.string.shop);
        initView();
        msgApi = WXAPIFactory.createWXAPI(getActivity(), null);
    }

    @Override
    public void init() {
        if (needBack) {
            lazyInit();
        }
    }

    @Override
    public boolean needBack() {
        return needBack;
    }

    void initView() {
        Bitmap loadedImage = ((BitmapDrawable) banner.getDrawable()).getBitmap();
        int swidth = getResources().getDisplayMetrics().widthPixels;
        float widthScale = (float) swidth / (float) loadedImage.getWidth();
        int height = (int) (widthScale * loadedImage.getHeight());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(swidth, height);
        banner.setLayoutParams(lp);
        banner.setImageBitmap(loadedImage);

        common_vip.setImageResource(R.drawable.common_vip);
        Bitmap loadedImageCV = ((BitmapDrawable) common_vip.getDrawable()).getBitmap();
        int swidthVip = getResources().getDisplayMetrics().widthPixels - ImageUtil.dip2px(getActivity(), 16);
        float widthScaleVip = (float) swidthVip / (float) loadedImageCV.getWidth();
        int heightVip = (int) (widthScaleVip * loadedImageCV.getHeight());
        LinearLayout.LayoutParams lpVip = new LinearLayout.LayoutParams(swidthVip, heightVip);
        int margin = ImageUtil.dip2px(getActivity(), 8);
        lpVip.setMargins(margin, margin, margin, 0);
        common_vip.setLayoutParams(lpVip);
        common_vip.setImageBitmap(loadedImageCV);

        rich_vip.setImageResource(R.drawable.rich_vip);
        Bitmap loadedImageRV = ((BitmapDrawable) rich_vip.getDrawable()).getBitmap();
        rich_vip.setLayoutParams(lpVip);
        rich_vip.setImageBitmap(loadedImageRV);

        help_vip.setImageResource(R.drawable.help_vip);
        Bitmap loadedImageHV = ((BitmapDrawable) help_vip.getDrawable()).getBitmap();
        help_vip.setLayoutParams(lpVip);
        help_vip.setImageBitmap(loadedImageHV);

        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopVipIntroduce_.intent(getActivity()).start();
            }
        });

        common_vip.setOnTouchListener(this);
        rich_vip.setOnTouchListener(this);
        common_vip.setOnClickListener(this);
        rich_vip.setOnClickListener(this);

    }

    public void buyClick(final String gnumber) {
        if (ac.getUser() == null) {
            LoginActivity_.intent(this).start();
            return;
        }
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle("支付方式").setItems(new String[]{"支付宝支付", "微信支付"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        startBuy(PayUtil.PayType.ZHIFUBAO, gnumber);
                        break;
                    case 1:
                        startBuy(PayUtil.PayType.WEIXIN, gnumber);
                        break;
                }
            }
        }).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void startBuy(final PayUtil.PayType payType, String gnumber) {
        RequestParams ap = getAjaxParams();
        ap.put("gnumber", gnumber);

        PayUtil.getInstance().buy(getActivity(), payType, ap, "101".equals(gnumber) ? "普通会员" : "富豪会员", new PayUtil.IMyPayListener() {
            @Override
            public void onPaySuccess() {
                switch (payType){
                    case WEIXIN:
                        Util.umengCustomEvent(getActivity(), "android_weixin");
                        break;
                    case ZHIFUBAO:
                        Util.umengCustomEvent(getActivity(), "android_zhifubao");
                        break;
                }
                showCustomToast("购买成功，正在刷新用户信息。");
                new UpdateUserPost(getActivity(), null);
            }

            @Override
            public void onPayFail(String str) {
                if(!TextUtils.isEmpty(str)) {
                    showCustomToast(str);
                }
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setAlpha(0.5f);
                break;
            case MotionEvent.ACTION_UP:
                v.setAlpha(1);
                break;
            case MotionEvent.ACTION_CANCEL:
                v.setAlpha(1);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_vip:
                buyClick("101");
                Util.umengCustomEvent(getActivity(), "normal_vip_btn");
                break;
            case R.id.rich_vip:
                buyClick("102");
                Util.umengCustomEvent(getActivity(), "rich_vip_btn");
                break;
        }
    }
}
