package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.bean.Payment;

import butterknife.Bind;

public class PaymentDetailAdapter extends BaseAdapter<Payment> {

    public PaymentDetailAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.payment_detail_item;
    }


    class ViewHolder extends BaseHolder<Payment> {
        @Bind(R.id.payment_type)
        TextView payment_type;
        @Bind(R.id.state)
        TextView state;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.money)
        TextView money;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(Payment payment, int position) {
            if (payment.getIncomeType() == 0) {
                money.setText("+￥" + payment.getMoney());
                money.setTextColor(context.getResources().getColor(R.color.color3));
            } else if (payment.getIncomeType() == 1) {
                money.setText("-￥" + payment.getMoney());
                money.setTextColor(context.getResources().getColor(R.color.red_number));
            }
            if (payment.getOverType() == 0) {
                state.setVisibility(View.GONE);
            } else {
                state.setVisibility(View.VISIBLE);
                if (payment.getOverType() == 1) {
                    state.setText("(审核中)");
                } else if (payment.getOverType() == 2) {
                    state.setText("(审核通过，待打款)");
                }else if (payment.getOverType() == 3) {
                    state.setText("(审核不通过)");
                }

            }
            payment_type.setText(payment.getBillType());
            time.setText(payment.getCtime());
        }
    }
}
