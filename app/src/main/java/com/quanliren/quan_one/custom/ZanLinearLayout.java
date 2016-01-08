package com.quanliren.quan_one.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.DateBean;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class ZanLinearLayout extends LinearLayout {

    private DateBean bean;
    public TextView zan;

    public ZanLinearLayout(Context context) {
        super(context);
    }


    public ZanLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZanLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    public void init() {
        zan = (TextView) findViewById(R.id.zan);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bean != null) {
                    RequestParams params = Util.getRequestParams(getContext());
                    params.put("dynamicId", bean.getDyid());
                    if (bean.zambiastate.equals("1")) {
                        params.put("type", 1+"");
                    } else {
                        params.put("type", 0+"");
                    }
                    ((AppClass) getContext().getApplicationContext()).finalHttp.post(URL.DONGTAIZAN, params,new MyJsonHttpResponseHandler(getContext(), Util.progress_arr[3]){
                                @Override
                                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                                    if (bean.zambiastate.equals("0")) {
                                        bean.zambiastate = "1";
                                        bean.zambia++;
                                        setSelected(true);
                                    } else {
                                        bean.zambiastate = "0";
                                        bean.zambia--;
                                        setSelected(false);
                                    }
                                    zan.setText(bean.zambia + "");
                                    EventBus.getDefault().post(bean);
                                }
                            }
                    );
                }
            }
        });
    }


    public DateBean getBean() {
        return bean;
    }

    public void setBean(DateBean bean) {
        this.bean = bean;
        zan.setText(bean.zambia + "");
        if ("1".equals(bean.zambiastate)) {
            setSelected(true);
        } else {
            setSelected(false);
        }
    }
}
