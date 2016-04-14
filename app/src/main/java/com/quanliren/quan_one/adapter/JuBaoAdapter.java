package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.bean.JuBao;
import com.quanliren.quan_one.util.Util;

import butterknife.Bind;

/**
 * Created by Shen on 2016/4/7.
 */
public class JuBaoAdapter extends BaseAdapter<JuBao> {

    public JuBaoAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.jubao_checkbox_item;
    }

    class ViewHolder extends BaseHolder<JuBao> {

        @Bind(R.id.checkbox)
        CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(JuBao bean, int position) {
            checkBox.setText(bean.rpType);
            if (bean.isChecked) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            checkBox.setTag(R.id.logo_tag, bean);
            checkBox.setOnCheckedChangeListener(changeListener);
        }

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isChecked) {
                        num++;
                    }
                }
                if (num >= 3) {
                    ((CheckBox) v).setChecked(false);
                }
            }
        };

        CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int num = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isChecked) {
                        num++;
                    }
                }
                if (num >= 3 && isChecked) {
                    buttonView.setChecked(false);
                    Util.toast(context, "最多选三个");
                } else {
                    ((JuBao) buttonView.getTag(R.id.logo_tag)).isChecked = isChecked;
                }
            }
        };
    }
}
