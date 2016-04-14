package com.quanliren.quan_one.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.alipay.Keys;
import com.alipay.PayResult;
import com.alipay.Rsa;
import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.wxapi.MD5;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.OrderBean;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Shen on 2016/3/1.
 */
public class PayUtil {

    public enum PayType {
        WEIXIN,
        ZHIFUBAO,
        WALLET
    }

    private static PayUtil instance;

    public PayUtil() {
    }

    public static synchronized PayUtil getInstance() {
        if (instance == null) {
            instance = new PayUtil();
        }
        return instance;
    }

    public IMyPayListener listener;

    /**
     * 支付统一方法
     *
     * @param context
     * @param payType  支付方式
     * @param params   请求伴游服务器获取订单的参数
     * @param name     商品名称
     * @param listener 监听事件
     */
    public final void buy(final Context context, final PayType payType, final RequestParams params, final String name, final IMyPayListener listener) {

        if (context == null) {
            return;
        }
        if (payType == null) {
            return;
        }
        if (params == null) {
            return;
        }
        if (listener == null) {
            return;
        }
        if (TextUtils.isEmpty(name)) {
            return;
        }

        this.listener = listener;

        AppClass ac = (AppClass) context.getApplicationContext();

        String url = null;

        switch (payType) {
            case WEIXIN:
                url = URL.GETWXPAY;
                break;
            case ZHIFUBAO:
                url = URL.GETALIPAY;
                break;
            case WALLET:
                url = URL.GETWALLETPAY;
                break;
        }

        ac.finalHttp.post(context, url, params, new MyJsonHttpResponseHandler(context, "正在获取订单") {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                if (listener instanceof IMyRedPacketPayListener) {
                    ((IMyRedPacketPayListener) listener).onGetOrder(jo);
                }
                switch (payType) {
                    case ZHIFUBAO:
                        buy_zhifubao(jo, name, (Activity) context, listener);
                        break;
                    case WEIXIN:
                        buy_weixin(jo, context);
                        break;
                    case WALLET:
                        ((IMyWalletPayListener) listener).onPaySuccess(jo);
                        break;
                }
            }

            @Override
            public void onFailRetCode(JSONObject jo) {
                super.onFailRetCode(jo);
                listener.onPayFail("");
            }

            @Override
            public void onFailure() {
                super.onFailure();
                listener.onPayFail("");
            }
        });
    }

    private void buy_weixin(JSONObject jo, Context context) throws JSONException {
        String respense = jo.getString(URL.RESPONSE);
        JSONObject resp = new JSONObject(respense);
        PayReq req = new PayReq();
        IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);
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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < signParams.size(); i++) {
            sb.append(signParams.get(i).getName());
            sb.append('=');
            sb.append(signParams.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(resp.getString("appkey"));
        req.sign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        msgApi.registerApp(Constants.APP_ID);
        msgApi.sendReq(req);
    }

    private void buy_zhifubao(JSONObject jo, String name, final Activity context, final IMyPayListener listener) {
        try {
            String info = getNewOrderInfo(jo.toString(), name);
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
                    PayTask alipay = new PayTask(context);

                    final String result = alipay.pay(orderInfo);

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PayResult payResult = new PayResult(result);

                            String resultStatus = payResult.getResultStatus();

                            if (TextUtils.equals(resultStatus, "9000")) {
                                listener.onPaySuccess();
                            } else {
                                // 判断resultStatus 为非“9000”则代表可能支付失败
                                // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                                if (TextUtils.equals(resultStatus, "8000")) {
                                    listener.onPayFail("支付结果确认中");
                                } else if (TextUtils.equals(resultStatus, "6001")) {
                                    listener.onPayFail("取消购买");
                                } else {
                                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                    listener.onPayFail("购买失败");
                                }
                            }
                        }
                    });
                }
            }.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            listener.onPayFail("系统错误");
        }
    }

    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    private String getNewOrderInfo(String t, String name) {
        OrderBean ob = null;
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

    public interface IMyPayListener {

        public void onPaySuccess();

        public void onPayFail(String str);
    }

    public interface IMyWalletPayListener extends IMyPayListener {

        public void onPaySuccess(JSONObject jo);
    }

    public interface IMyRedPacketPayListener extends IMyPayListener {

        public void onGetOrder(JSONObject jo);
    }
}
