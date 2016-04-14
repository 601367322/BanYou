package com.quanliren.quan_one.activity.seting;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.seting.wallet.PaymentDetailActivity_;
import com.quanliren.quan_one.activity.seting.wallet.TiXianlActivity_;
import com.quanliren.quan_one.bean.Account;
import com.quanliren.quan_one.util.PayUtil;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * Created by Kong on 2016/2/29.
 */
@EActivity(R.layout.activity_my_wallet)
public class MyWalletActivity extends BaseActivity {
    private static final int GETMONEY = 1;
    @ViewById
    TextView all_money;
    Account account = null;

    @Override
    public void init() {
        super.init();
        setTitleTxt("我的钱包");
        setTitleRightTxt("收支明细");
        if (ac.cs.getWALLET() == 0) {
            ac.cs.setWALLET(1);
        }
        getUserAccountPost();
    }

    public void getUserAccountPost() {

        ac.finalHttp.post(mContext, URL.GET_USER_ACCOUNT, Util.getRequestParams(mContext), new MyJsonHttpResponseHandler() {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                account = new Gson().fromJson(jo.getString(URL.RESPONSE), new TypeToken<Account>() {
                }.getType());

                if (account != null)
                    all_money.setText("￥" + account.total);

            }
        });
    }

    public void rightClick(View view) {
        PaymentDetailActivity_.intent(mContext).start();
    }

    @Click(R.id.chongzhi_btn)
    void recharge() {
        dialogChongZhi();
    }

    private void dialogChongZhi() {
        final Dialog dialog = new Dialog(mContext, R.style.red_line_dialog);
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_chong_zhi, null);
        final EditText editText = (EditText) convertView.findViewById(R.id.edittext);
        final RadioButton zhifubao = (RadioButton) convertView.findViewById(R.id.zhifubao);
        final RadioButton weixin = (RadioButton) convertView.findViewById(R.id.weixin);
        TextView button = (TextView) convertView.findViewById(R.id.ok);
        convertView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.closeSoftKeyboard(editText);
                dialog.dismiss();
            }
        });
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()){
                    case R.id.zhifubao:
                        if (isChecked) {
                            weixin.setChecked(false);
                        } else {
                            weixin.setChecked(true);
                        }
                        break;
                    case R.id.weixin:
                        if (isChecked) {
                            zhifubao.setChecked(false);
                        } else {
                            zhifubao.setChecked(true);
                        }
                        break;
                }
            }
        };
        zhifubao.setOnCheckedChangeListener(listener);
        weixin.setOnCheckedChangeListener(listener);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                    showCustomToast("请输入充值金额");
                    return;
                }
                if(Double.valueOf(editText.getText().toString().trim())<1){
                    showCustomToast("充值金额不能少于1元");
                    return;
                }
                if (account.total + Double.valueOf(editText.getText().toString().trim()) > 2000) {
                    showCustomToast("现有金额加充值金额不能超过2000元");
                    return;
                }
                Utils.closeSoftKeyboard(editText);
                dialog.dismiss();
                if (zhifubao.isChecked()) {
                    BigDecimal bd = new BigDecimal(editText.getText().toString().trim());
                    bd = bd.add(new BigDecimal(Math.ceil(bd.doubleValue() * 0.015 * 100) / 100));
                    final double total = bd.doubleValue();
                    new AlertDialog.Builder(mContext).setTitle("支付宝支付提醒").setMessage("根据支付宝条款，需额外支付1.5%手续费，实际支付金额为" + total + "元。").setPositiveButton("支付", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startBuy(PayUtil.PayType.ZHIFUBAO,Double.valueOf(editText.getText().toString().trim()), total);
                        }
                    }).setNegativeButton(R.string.cancel, null).create().show();
//                    startBuy(PayUtil.PayType.ZHIFUBAO, Double.valueOf(editText.getText().toString().trim()),Double.valueOf(editText.getText().toString().trim()));
                } else {
                    BigDecimal bd = new BigDecimal(editText.getText().toString().trim());
                    bd = bd.add(new BigDecimal(Math.ceil(bd.doubleValue() * 0.02 * 100) / 100));
                    final double total = bd.doubleValue();
                    new AlertDialog.Builder(mContext).setTitle("微信支付提醒").setMessage("根据微信条款，需额外支付2%手续费，实际支付金额为" + total + "元。").setPositiveButton("支付", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startBuy(PayUtil.PayType.WEIXIN,Double.valueOf(editText.getText().toString().trim()), total);
                        }
                    }).setNegativeButton(R.string.cancel, null).create().show();
                }
            }
        });
        dialog.setContentView(convertView);

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    @Click(R.id.tixian_btn)
    void withdraw() {
        if (account == null) {
            TiXianlActivity_.intent(mContext).total(0.0 + "").startForResult(GETMONEY);
        } else {
            TiXianlActivity_.intent(mContext).total(account.total + "").startForResult(GETMONEY);
        }
    }

    @OnActivityResult(GETMONEY)
    void onActivityResult(int resultCode) {
        if (resultCode == RESULT_OK) {
            getUserAccountPost();
        }
    }

    public void startBuy(final PayUtil.PayType payType, double redTotal,double total) {
        RequestParams ap = getAjaxParams();
        ap.put("gnumber", "202");
        ap.put("redTotal", redTotal);
        ap.put("total", total);
        PayUtil.getInstance().buy(mContext, payType, ap, "钱包充值", new PayUtil.IMyPayListener() {
            @Override
            public void onPaySuccess() {
                showCustomToast("充值成功，刷新钱包");
                getUserAccountPost();
            }

            @Override
            public void onPayFail(String str) {
                if(TextUtils.isEmpty(str)){
                    return;
                }
                showCustomToast("充值失败");
            }
        });
    }
}
