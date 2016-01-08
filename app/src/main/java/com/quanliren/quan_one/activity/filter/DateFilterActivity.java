package com.quanliren.quan_one.activity.filter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.CustomFilterQuanBean;
import com.quanliren.quan_one.dao.DBHelper;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.date_filter)
public class DateFilterActivity extends BaseActivity {


    @ViewById
    LinearLayout all, item1, item2, item3, item4;

    @Override
    public void init() {
        super.init();

        setTitleRightTxt(R.string.ok);
        setTitleTxt(getString(R.string.filter));

        List<CustomFilterQuanBean> listCB = DBHelper.customFilterBeanQuanDao.getAllFilter();
        int usex = -1;
        int sex = -1;
        if (listCB != null) {
            for (CustomFilterQuanBean cfb : listCB) {
                if ("usex".equals(cfb.key)) {
                    usex = cfb.id;
                } else if ("sex".equals(cfb.key)) {
                    sex = cfb.id;
                }
            }
        }
        if (usex == 1 && sex == 0) {
            showIcon(item1);
        } else if (usex == 0 && sex == 1) {
            showIcon(item2);
        } else if (usex == 1 && sex == 1) {
            showIcon(item3);
        } else if (usex == 0 && sex == 0) {
            showIcon(item4);
        } else if (usex == -1 && sex == -1) {
            showIcon(all);
        }
    }

    int usex = -1;
    int sex = -1;

    @Override
    public void rightClick(View v) {
        if (usex == -1 && sex == -1) {
            DBHelper.customFilterBeanQuanDao.deleteById("usex");
            DBHelper.customFilterBeanQuanDao.deleteById("sex");
        } else {
            saveFilter("usex", usex);
            saveFilter("sex", sex);
        }
        setResult(RESULT_OK);
        finish();
    }

    public void saveFilter(String key, int sex) {
        CustomFilterQuanBean cfb = new CustomFilterQuanBean("性别", sex == 0 ? "女" : "男", key, sex);
        DBHelper.customFilterBeanQuanDao.deleteById(key);
        DBHelper.customFilterBeanQuanDao.create(cfb);
    }

    @Click({R.id.all, R.id.item1, R.id.item2, R.id.item3, R.id.item4})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all:
                usex = -1;
                sex = -1;
                break;
            case R.id.item1:
                usex = 1;
                sex = 0;
                break;
            case R.id.item2:
                usex = 0;
                sex = 1;
                break;
            case R.id.item3:
                usex = 1;
                sex = 1;
                break;
            case R.id.item4:
                usex = 0;
                sex = 0;
                break;
        }
        showIcon(v);
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
