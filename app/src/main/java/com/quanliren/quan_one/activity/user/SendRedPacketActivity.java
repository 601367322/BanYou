package com.quanliren.quan_one.activity.user;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.Account;
import com.quanliren.quan_one.bean.ChatListBean;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.message.ChatListFragment;
import com.quanliren.quan_one.service.SocketManage;
import com.quanliren.quan_one.util.PayUtil;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * Created by Shen on 2016/3/1.
 */
@EActivity(R.layout.activity_send_packet)
public class SendRedPacketActivity extends BaseActivity {

    @ViewById(R.id.content_ll)
    View content_ll;
    Account account = null;
    @ViewById
    EditText packet_number, packet_total, packet_content;
    @ViewById
    View packet_number_ll;
    @Extra
    ChatActivity.ChatType chatType = ChatActivity.ChatType.friend;
    @ViewById
    TextView total;

    /**
     * 好友或群组
     */
    @Extra
    public User friend;
    /**
     * 自己
     */
    @Extra
    public User user;

    @Override
    public void init() {
        super.init();

        setSwipeBackEnable(false);

        setTitleTxt(getString(R.string.send_packet));

        getUserAccountPost();

        packet_total.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (packet_total.getText().length() > 0) {
                    total.setVisibility(View.VISIBLE);
                    total.setText("￥" + Double.valueOf(packet_total.getText().toString()).toString());
                } else {
                    total.setVisibility(View.GONE);
                }
            }
        });
    }

    @UiThread
    public void total_request() {
        packet_total.setFocusable(true);
        packet_total.requestFocus();
        Utils.openSoftKeyboard(this, packet_total);
    }

    @UiThread
    public void number_request() {
        packet_number.setFocusable(true);
        packet_number.requestFocus();
        Utils.openSoftKeyboard(this, packet_number);
    }

    public void getUserAccountPost() {

        ac.finalHttp.post(mContext, URL.GET_USER_ACCOUNT, Util.getRequestParams(mContext), new MyJsonHttpResponseHandler(this, Util.progress_arr[1]) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                account = new Gson().fromJson(jo.getString(URL.RESPONSE), new TypeToken<Account>() {
                }.getType());

                if (account != null) {
                    content_ll.setVisibility(View.VISIBLE);

                    if (chatType == ChatActivity.ChatType.friend) {
                        packet_number_ll.setVisibility(View.GONE);
                        packet_number.setText("1");

                        total_request();
                    } else {
                        number_request();
                    }
                }

            }
        });
    }

    @Click(R.id.send_packet_btn)
    public void sendPacketBtnClick() {
        String packetNumber = packet_number.getText().toString().trim();
        String packetTotal = packet_total.getText().toString().trim();

        if (TextUtils.isEmpty(packetNumber)) {
            showCustomToast("请输入红包个数");
            return;
        }
        if (TextUtils.isEmpty(packetTotal)) {
            showCustomToast("请输入红包总金额");
            return;
        }
        if (Double.valueOf(packetTotal) < 1) {
            showCustomToast("总金额最少1元");
            return;
        }
        if (Double.valueOf(packetTotal) > 200) {
            showCustomToast("总金额最多200元");
            return;
        }
        if (Double.valueOf(packetTotal) / Double.valueOf(packetNumber) < 0.01) {
            showCustomToast("单个红包不能少于0.01元");
            return;
        }
        new AlertDialog.Builder(mContext).setItems(new String[]{"我的钱包(￥" + account.total + ")", "支付宝", "微信"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        dialogPassWord();
                        break;
                    case 1:
                        BigDecimal bd = new BigDecimal(packet_total.getText().toString().trim());
                        bd = bd.add(new BigDecimal(Math.ceil(bd.doubleValue() * 0.015 * 100) / 100));
                        new AlertDialog.Builder(mContext).setTitle("支付宝支付提醒").setMessage("根据支付宝条款，需额外支付1.5%手续费，实际支付金额为" + bd.doubleValue() + "元。").setPositiveButton("支付", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pay_thrid(PayUtil.PayType.ZHIFUBAO);
                            }
                        }).setNegativeButton(R.string.cancel, null).create().show();
                        break;
                    case 2:
                        wxDialog();
                        break;
                }
            }
        }).create().show();
    }

    public void dialogPassWord() {
        final Dialog dialog = new Dialog(mContext, R.style.red_line_dialog);
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_pay_password, null);
        final EditText editText = (EditText) convertView.findViewById(R.id.edittext);
        TextView textView = (TextView) convertView.findViewById(R.id.total_text);
        textView.setText("￥" + packet_total.getText().toString().trim());
        TextView button = (TextView) convertView.findViewById(R.id.ok);
        convertView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.closeSoftKeyboard(editText);
                dialog.dismiss();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                    showCustomToast("请输入密码");
                    return;
                }
                RequestParams params = Util.getRequestParams(mContext);
                params.put("pwd", editText.getText().toString());
                params.put("total", Double.valueOf(packet_total.getText().toString().trim()));
                params.put("count", Integer.valueOf(packet_number.getText().toString().trim()));
                params.put("content", TextUtils.isEmpty(packet_content.getText().toString().trim()) ? packet_content.getHint().toString() : packet_content.getText().toString().trim());
                params.put("toId", friend == null ? "" : friend.getId());
                switch (chatType) {
                    case group:
                        params.put("redType", 1);
                        break;
                    default:
                        params.put("redType", 0);
                        break;
                }
                PayUtil.getInstance().buy(mContext, PayUtil.PayType.WALLET, params, "红包", new PayUtil.IMyWalletPayListener() {
                    @Override
                    public void onPaySuccess(JSONObject jos) {
                        try {
                            Utils.closeSoftKeyboard(editText);
                            dialog.dismiss();

                            /**
                             * 写在这里为了保证消息保存到数据库，否则在特定情况下，chatactivity无法收到result
                             */
                            onSuccessJson(jos.getJSONObject(URL.RESPONSE));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPaySuccess() {
                    }

                    @Override
                    public void onPayFail(String str) {
                        if (!TextUtils.isEmpty(str)) {
                            showCustomToast(str);
                        }
                    }
                });
            }
        });
        dialog.setContentView(convertView);

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    private void onSuccessJson(JSONObject jos) throws JSONException {
        JSONObject msg = DfMessage.getMessage(user, jos.toString(), friend, DfMessage.PACKET, 0);
        try {
            switch (chatType) {
                case group:
                    JSONObject my = new JSONObject();
                    my.put("avatar", user.getAvatar());
                    my.put("nickname", user.getNickname());
                    my.put("id", user.getId());
                    msg.put("friend", my);
                    break;
            }

            final JSONObject jo = new JSONObject();
            jo.put(SocketManage.ORDER, SocketManage.ORDER_SENDMESSAGE);
            jo.put(SocketManage.SEND_USER_ID, user.getId());
            jo.put(SocketManage.RECEIVER_USER_ID, friend.getId());
            jo.put(SocketManage.MESSAGE, msg);
            jo.put(SocketManage.MESSAGE_ID,
                    msg.getString(SocketManage.MESSAGE_ID));

            final DfMessage msgs = new Gson().fromJson(msg.toString(),
                    new TypeToken<DfMessage>() {
                    }.getType());

            msgs.setUserid(user.getId());

            msgs.setDownload(SocketManage.D_downloaded);

            ChatListBean cb = new ChatListBean(user, msgs, friend);

            DBHelper.dfMessageDao.saveMessage(msgs, cb);

            Intent broad = new Intent(ChatListFragment.ADDMSG);
            broad.putExtra("bean", cb);
            sendBroadcast(broad);

            Intent intent = new Intent();
            intent.putExtra("bean", msgs);
            setResult(RESULT_OK, intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void wxDialog() {
        BigDecimal bd = new BigDecimal(packet_total.getText().toString().trim());
        bd = bd.add(new BigDecimal(Math.ceil(bd.doubleValue() * 0.02 * 100) / 100));
        new AlertDialog.Builder(mContext).setTitle("微信支付提醒").setMessage("根据微信条款，需额外支付2%手续费，实际支付金额为" + bd.doubleValue() + "元。").setPositiveButton("支付", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pay_thrid(PayUtil.PayType.WEIXIN);
            }
        }).setNegativeButton(R.string.cancel, null).create().show();
    }

    public void pay_thrid(PayUtil.PayType payType) {
        RequestParams params = Util.getRequestParams(mContext);
        params.put("gnumber", 201);
        BigDecimal bd = new BigDecimal(packet_total.getText().toString().trim());
        params.put("redTotal", bd.doubleValue());
        if (payType == PayUtil.PayType.WEIXIN) {
            bd = bd.add(new BigDecimal(Math.ceil(bd.doubleValue() * 0.02 * 100) / 100));
        } else if (payType == PayUtil.PayType.ZHIFUBAO) {
            bd = bd.add(new BigDecimal(Math.ceil(bd.doubleValue() * 0.015 * 100) / 100));
        }
        params.put("total", bd.doubleValue());
        params.put("count", Integer.valueOf(packet_number.getText().toString().trim()));
        params.put("content", TextUtils.isEmpty(packet_content.getText().toString().trim()) ? packet_content.getHint().toString() : packet_content.getText().toString().trim());
        params.put("toId", friend == null ? "" : friend.getId());
        switch (chatType) {
            case group:
                params.put("redType", 1);
                break;
            default:
                params.put("redType", 0);
                break;
        }
        PayUtil.getInstance().buy(mContext, payType, params, "伴游红包", new PayUtil.IMyRedPacketPayListener() {

            int rId = 0;

            @Override
            public void onGetOrder(JSONObject jo) {
                try {
                    rId = jo.getJSONObject(URL.RESPONSE).getInt("rId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPaySuccess() {
                try {
                    JSONObject jo = new JSONObject();
                    jo.put("rId", rId);
                    jo.put("content", TextUtils.isEmpty(packet_content.getText().toString().trim()) ? packet_content.getHint().toString() : packet_content.getText().toString().trim());
                    onSuccessJson(jo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPayFail(String str) {
                if (!TextUtils.isEmpty(str)) {
                    showCustomToast(str);
                }
            }
        });
    }
}
