package com.quanliren.quan_one.fragment;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alipay.Keys;
import com.alipay.PayResult;
import com.alipay.Rsa;
import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.fragment.base.BaseViewPagerChildFragment;
import com.quanliren.quan_one.activity.shop.ShopVipIntroduce_;
import com.quanliren.quan_one.activity.user.LoginActivity_;
import com.quanliren.quan_one.activity.wxapi.MD5;
import com.quanliren.quan_one.bean.OrderBean;
import com.quanliren.quan_one.post.UpdateUserPost;
import com.quanliren.quan_one.util.Constants;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

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
        if(needBack){
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

    public void startBao(final String gnumber) {
        RequestParams ap = getAjaxParams();
        ap.put("gnumber", gnumber);
        ac.finalHttp.post(URL.GETALIPAY, ap, new MyJsonHttpResponseHandler(getActivity()) {

                    @Override
                    public void onSuccessRetCode(JSONObject jo) throws Throwable {
                        if ("101".equals(gnumber)) {
                            buy(jo.toString(), "普通会员");
                        } else if ("102".equals(gnumber)) {
                            buy(jo.toString(), "富豪会员");
                        }
                    }
                }
        );

    }

    public void buy(String t, String name) {
        try {
            String info = getNewOrderInfo(t, name);
            String sign = Rsa.sign(info, Keys.PRIVATE);
            try {
                // 仅需对sign 做URL编码
                sign = URLEncoder.encode(sign, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            info += "&sign=\"" + sign + "\"&" + getSignType();

            final String orderInfo = info;
            new Thread() {
                public void run() {
                    PayTask alipay = new PayTask(getActivity());
                    // 设置为沙箱模式，不设置默认为线上环境
//                     alipay.setSandBox(true);

                    String result = alipay.pay(orderInfo);

                    Message msg = new Message();
                    msg.what = RQF_PAY;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            }.start();

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "remote_call_failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RQF_PAY:
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
//                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    if (TextUtils.equals(resultStatus, "9000")) {
                        showCustomToast("购买成功，正在刷新用户信息。");
                        new UpdateUserPost(getActivity(), null);
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            showCustomToast("支付结果确认中");
                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            showCustomToast("取消购买");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            showCustomToast("购买失败");
                        }
                    }
                    break;
                case RQF_LOGIN: {
                    Toast.makeText(getActivity(), msg.obj.toString(),
                            Toast.LENGTH_SHORT).show();
                }
                break;
                default:
                    break;
            }
        }

        ;
    };

    OrderBean ob = null;

    private String getNewOrderInfo(String t, String name) {
        try {
            JSONObject jo = new JSONObject(t);
            ob = new Gson().fromJson(jo.getString(URL.RESPONSE), new TypeToken<OrderBean>() {
            }.getType());
        } catch (JSONException e) {

            e.printStackTrace();
        }

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + Keys.DEFAULT_PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + Keys.DEFAULT_SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + ob.getOrder_no() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + name + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + name + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + ob.getPrice() + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + ob.getNotify_url()
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
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
                        startBao(gnumber);
                        break;
                    case 1:
                        startWXin(gnumber);
                        break;
                }
            }
        }).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void startWXin(final String gnumber) {
        RequestParams ap = getAjaxParams();
        ap.put("gnumber", gnumber);
        ac.finalHttp.post(URL.GETWXPAY, ap,
                new MyJsonHttpResponseHandler(getActivity(), "正在加载") {

                    @Override
                    public void onSuccessRetCode(JSONObject jo) throws Throwable {
                        try {
                            wXinBuy(jo);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                });
    }

    private void wXinBuy(JSONObject jo) throws Throwable {
        String respense = jo.getString(URL.RESPONSE);
        JSONObject resp = new JSONObject(respense);
        req.appId = resp.getString("appid");
        req.partnerId = resp.getString("partnerid");
        req.prepayId = resp.getString("prepayid");
        req.packageValue = "Sign=WXPay";
        req.nonceStr = resp.getString("noncestr");
        req.timeStamp = resp.getString("timestamp");
        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        req.sign = genAppSign(resp.getString("appkey"), signParams);
        sendPayReq();
    }

    private void sendPayReq() {
        msgApi.registerApp(Constants.APP_ID);
        msgApi.sendReq(req);
        customDismissDialog();
    }

    /**
     * app 签名
     *
     * @param params
     * @return
     */
    private String genAppSign(String appkey, List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(appkey);

        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return appSign;
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
                break;
            case R.id.rich_vip:
                buyClick("102");
                break;
        }
    }
}
