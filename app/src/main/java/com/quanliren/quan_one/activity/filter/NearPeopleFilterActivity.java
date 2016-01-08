package com.quanliren.quan_one.activity.filter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.CustomFilterBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.dao.LoginUserDao;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity
public class NearPeopleFilterActivity extends BaseActivity implements View.OnClickListener {

    @ViewById(R.id.sex_btn)
    RadioGroup sex_btn;
    @ViewById(R.id.everyone)
    RadioButton everyone;
    @ViewById(R.id.girl)
    RadioButton girl;
    @ViewById(R.id.boy)
    RadioButton boy;

    @ViewById(R.id.all_time)
    LinearLayout all_time;
    @ViewById(R.id.one_day)
    LinearLayout one_day;
    @ViewById(R.id.three_day)
    LinearLayout three_day;
    @ViewById(R.id.one_week)
    LinearLayout one_week;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.near_people_filter);
        setTitleRightTxt(R.string.ok);
        setTitleTxt(R.string.filter);
        setListener();

        List<CustomFilterBean> listCB= DBHelper.customFilterBeanDao.getAllFilter();
        if(listCB!=null)
            for (CustomFilterBean cfb : listCB) {
                if("sex".equals(cfb.key)){
                    sexIndex=cfb.id;
                }
                if(ac.getUserInfo().getIsvip()>0){
                    if("actime".equals(cfb.key)){
                        timeIndex=cfb.id;
                    }
                }
            }
        switch (sexIndex){
            case -1:
                everyone.setChecked(true);
                break;
            case 0:
                girl.setChecked(true);
                break;
            case 1:
                boy.setChecked(true);
                break;
        }
        switch (timeIndex){
            case -1:
                all_time.performClick();
                break;
            case 1:
                one_day.performClick();
                break;
            case 2:
                three_day.performClick();
                break;
            case 3:
                one_week.performClick();
                break;
        }
    }

    private void setListener() {
        all_time.setOnClickListener(this);
        one_day.setOnClickListener(this);
        three_day.setOnClickListener(this);
        one_week.setOnClickListener(this);
    }

    int sexIndex = -1;
    int timeIndex = -1;

    @Override
    public void rightClick(View v) {
        int sexButtonId = sex_btn.getCheckedRadioButtonId();
        switch (sexButtonId) {
            case R.id.everyone:
                sexIndex = -1;
                break;
            case R.id.girl:
                sexIndex = 0;
                break;
            case R.id.boy:
                sexIndex = 1;
                break;
        }
        Intent intent=new Intent();
        intent.putExtra("sexIndex", sexIndex);
        intent.putExtra("timeIndex",timeIndex);
        setResult(11, intent);
        finish();
    }
    User user;
    @Override
    public void onClick(View v) {
        user= LoginUserDao.getInstance(getApplicationContext()).getUserInfo();
        if(user!=null){
            if(user.getIsvip()>0){
                switch (v.getId()) {
                    case R.id.all_time:
                        timeIndex = -1;
                        break;
                    case R.id.one_day:
                        timeIndex = 1;
                        break;
                    case R.id.three_day:
                        timeIndex = 2;
                        break;
                    case R.id.one_week:
                        timeIndex = 3;
                        break;
                }
                showIcon(v);
            }else{
                if(v.getId()==R.id.all_time){
                    timeIndex = -1;
                    showIcon(v);
                }else{
                    goVip();
                }
            }
        }

    }
    public void showIcon(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View icon = ((ViewGroup) parent.getChildAt(i)).getChildAt(1);
            icon.setVisibility(View.GONE);
        }
        View icon = ((ViewGroup) view).getChildAt(1);
        icon.setVisibility(View.VISIBLE);
    }
}
