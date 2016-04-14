package com.quanliren.quan_one.activity.seting.wallet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.adapter.ParentsAdapter;
import com.quanliren.quan_one.bean.AccountUser;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kong on 2016/2/29.
 */
@EActivity(R.layout.activity_ti_xian)
public class TiXianlActivity extends BaseActivity {
    @ViewById
    EditText account;
    @ViewById
    EditText account_name;
    @ViewById
    EditText money;
    @ViewById
    LinearLayout acounts_ll;
    @ViewById
    TextView total_tv;
    @ViewById
    ImageButton more_account;
    @Extra
    String total;
    boolean isShow = false; // 更多账户是否展开
    private PopupWindow pop;
    private PopupAdapter adapter;
    private ListView listView;
    private List<AccountUser> accounts = new ArrayList<AccountUser>();

    @Override
    public void init() {
        super.init();
        setTitleTxt("提现");
        total_tv.setText(total);
    }

    @Click(R.id.more_account)
    public void more_account(View v) {
        List<AccountUser> accounts = Util.jsonToList(ac.cs.getZHIFUBAO(), AccountUser.class);
        if (accounts == null || accounts.size() == 0) {
            return;
        }
        if (!isShow) {
            isShow = true;
            initAccountPop();
        } else {
            isShow = false;
            initAccountPop();
        }
    }

    static class ViewHolder {
        TextView tv;
        ImageView iv;
        LinearLayout ll;
    }

    class PopupAdapter extends ParentsAdapter {

        public PopupAdapter(Context c, List list) {
            super(c, list);
        }

        public View getView(final int position, View convertView, ViewGroup arg2) {

            ViewHolder holder = null;
            final String name = ((AccountUser) list.get(position)).getName();
            final String accountCode = ((AccountUser) list.get(position)).getAccount();
            if (convertView == null) {
                convertView = View.inflate(c, R.layout.username_popup, null);
                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.more_user);
                holder.iv = (ImageView) convertView
                        .findViewById(R.id.more_clear);
                holder.ll = (LinearLayout) convertView
                        .findViewById(R.id.more_user_ll);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(accountCode);
            holder.ll.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    initAccountPop();
                    account.setText(accountCode);
                    account_name.setText(name);
                }
            });

            holder.iv.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    accounts.remove(position);
                    ac.cs.setZHIFUBAO(new Gson().toJson(accounts));
                    DBHelper.moreLoginUserDao.delete(name);
                    adapter.notifyDataSetChanged();
                    if (accounts.size() == 0) {
                        initAccountPop();
                    }
                }
            });
            if (position == (list.size() - 1)) {
                holder.ll.setBackgroundResource(R.drawable.input_btm_btn);
            } else {
                holder.ll.setBackgroundResource(R.drawable.input_mid_btn);
            }
            return convertView;
        }

    }

    public void initAccountPop() {
        if (pop == null) {
            if (adapter == null) {
                // 获取更多账户信息
                accounts = Util.jsonToList(ac.cs.getZHIFUBAO(), AccountUser.class);
                adapter = new PopupAdapter(getApplicationContext(), accounts);
                listView = new ListView(mContext);
                int width = account.getWidth()+more_account.getWidth();
                pop = new PopupWindow(listView, width,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                pop.setOutsideTouchable(true);
                listView.setItemsCanFocus(false);
                listView.setDivider(null);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listView.setAdapter(adapter);
                pop.setFocusable(false);
                pop.showAsDropDown(more_account, 0, ImageUtil.dip2px(mContext,1));
                isShow = true;
                more_account.animate().setDuration(200).rotation(180)
                        .start();
            }
        } else if (pop.isShowing()) {
            pop.dismiss();
            isShow = false;
            more_account.animate().setDuration(200).rotation(0).start();
        } else if (!pop.isShowing()) {
            // 获取更多账户信息
            accounts = Util.jsonToList(ac.cs.getZHIFUBAO(), AccountUser.class);
            adapter.setList(accounts);
            adapter.notifyDataSetChanged();
            pop.showAsDropDown(more_account ,0, ImageUtil.dip2px(mContext,1));
            isShow = true;
            more_account.animate().setDuration(200).rotation(180).start();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isShow && pop != null) {
                initAccountPop();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    public void onBackPressed() {

        if (isShow && pop != null) {
            initAccountPop();
            return;
        }
        super.onBackPressed();
    }

    @Click(R.id.tixian_btn)
    void commit(View v) {
        closeInput();
        if (TextUtils.isEmpty(account.getText().toString().trim())) {
            showCustomToast("请输入支付宝账号");
            return;
        }
        if (TextUtils.isEmpty(account_name.getText().toString().trim())) {
            showCustomToast("请输入支付宝姓名");
            return;
        }
        if (TextUtils.isEmpty(money.getText().toString().trim())) {
            showCustomToast("请输入提现金额");
            return;
        }
        if (Float.parseFloat(money.getText().toString().trim()) < 100.00) {
            showCustomToast("提现金额至少为100元");
            return;
        }
        //计算手续费
        BigDecimal bd = new BigDecimal(money.getText().toString().trim());
        if (bd.doubleValue() >= 100 && bd.doubleValue() <= 200) {
            //提现金额大于100小于200，手续费小于1元，暂扣1元
            bd = bd.subtract(new BigDecimal(1));
        } else if (bd.doubleValue() > 200 && bd.doubleValue() < 5000) {
            //提现金额大于200小于5000，手续费按0.5%计算
            BigDecimal temp = new BigDecimal(Math.ceil(bd.doubleValue() * 0.005 * 100) / 100);
            bd = bd.subtract(temp);
        } else if (bd.doubleValue() >= 5000) {
            //提现金额大于5000，手续费25元计算
            bd = bd.subtract(new BigDecimal(25));
        }
        final double realMoney = bd.doubleValue();
        new AlertDialog.Builder(mContext).setTitle("确认信息").setMessage("支付宝账号：" + account.getText().toString().trim() + "\n支付宝姓名：" + account_name.getText().toString().trim() + "\n提现金额：￥" + money.getText().toString().trim() + "\n到账金额：￥" + bd.doubleValue()).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogPassWord(realMoney);
            }
        }).setNegativeButton(R.string.cancel, null).create().show();
    }

    void updateZhiFuBao(AccountUser accountU) {
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        if (accounts.size() == 0) {
            accounts.add(accountU);
            ac.cs.setZHIFUBAO(new Gson().toJson(accounts));
        } else {
            int i = -1;
            int j = -1;
            for (AccountUser accountUser : accounts) {
                if (accountUser.getAccount().equals(accountU.getAccount())) {
                    if (!accountUser.getName().equals(accountU.getName())) {
                        i = accounts.indexOf(accountUser);
                    }
                    j = 0;
                }
            }
            if (i > -1) {
                accounts.get(i).setName(accountU.getName());
                ac.cs.setZHIFUBAO(new Gson().toJson(accounts));
            }
            if (j == -1) {
                accounts.add(0,accountU);
                ac.cs.setZHIFUBAO(new Gson().toJson(accounts));
            }
        }

    }

    public void dialogPassWord(final double realMoney) {
        final Dialog dialog = new Dialog(mContext, R.style.red_line_dialog);
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_pay_password, null);
        final EditText editText = (EditText) convertView.findViewById(R.id.edittext);
        TextView textView = (TextView) convertView.findViewById(R.id.total_text);
        textView.setText("￥" + money.getText().toString().trim());
        TextView tip = (TextView) convertView.findViewById(R.id.tip);
        tip.setText("提现");
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
                Utils.closeSoftKeyboard(editText);
                dialog.dismiss();
                RequestParams params = getAjaxParams();
                params.put("pwd", editText.getText().toString());
                params.put("zfbNumber", account.getText().toString().trim());
                params.put("zfbName", account_name.getText().toString().trim());
                params.put("money", Double.valueOf(money.getText().toString().trim()));
                params.put("realMoney", realMoney);
                final AccountUser accountUser = new AccountUser(account_name.getText().toString().trim(), account.getText().toString().trim());
                ac.finalHttp.post(mContext, URL.MONEY_TI_XIAN, params, new MyJsonHttpResponseHandler(mContext, Util.progress_arr[3]) {
                    @Override
                    public void onSuccessRetCode(JSONObject jo) throws Throwable {
                        updateZhiFuBao(accountUser);
                        AlertDialog understand = new AlertDialog.Builder(mContext).setMessage("提现申请已完成，72小时内审核通过后到账。").setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        }).create();
                        understand.setCanceledOnTouchOutside(false);
                        understand.show();
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
}
