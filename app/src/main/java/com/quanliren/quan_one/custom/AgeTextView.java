package com.quanliren.quan_one.custom;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.util.Util;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AgeTextView extends TextView {

    Context context;

    public AgeTextView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public AgeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void init() {
        setOnClickListener(ageClick);
    }

    Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);

    class dialog implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, day);

            updateLabel();
        }
    }

    OnClickListener ageClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Calendar cal = Calendar.getInstance(Locale.CHINA);
            if (!TextUtils.isEmpty(getText().toString())) {
                try {
                    Date time = Util.fmtDate.parse(getText().toString());
                    cal.setTime(time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            DatePickerDialog dialog = DatePickerDialog.newInstance(new dialog(), cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dialog.setMaxDate(Calendar.getInstance(Locale.CHINA));
            Calendar min = Calendar.getInstance(Locale.CHINA);
            min.set(Calendar.YEAR, dialog.getMaxDate().get(Calendar.YEAR) - 80);
            dialog.setMinDate(min);
            dialog.setAccentColor(getResources().getColor(R.color.nav_press_txt));
            dialog.show(((BaseActivity)getContext()).getFragmentManager(),"");
        }
    };


    private void updateLabel() {
        try {
            String age = Util.fmtDate.format(dateAndTime.getTime());
            setText(age);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
