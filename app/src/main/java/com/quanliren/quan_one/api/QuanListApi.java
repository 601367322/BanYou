package com.quanliren.quan_one.api;

import android.content.Context;

import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.bean.CustomFilterQuanBean;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.date.DateListFragment;
import com.quanliren.quan_one.util.URL;

import java.util.List;

/**
 * Created by Shen on 2015/11/10.
 */
public class QuanListApi extends BaseApi {

    int type;

    @Override
    public String getUrl() {
        switch (type) {
            case DateListFragment.ALL:
                return URL.DONGTAI;
            case DateListFragment.ONCE:
            case DateListFragment.MY:
                return URL.PERSONALDONGTAI;
            case DateListFragment.COLLECT:
                return URL.COLLECTLIST;
            case DateListFragment.CARE:
                return URL.MY_CARE_DATE;
            case DateListFragment.HOT:
                return URL.HOT_DATE_LIST;
        }
        return "";
    }

    public QuanListApi(Context context, int type) {
        super(context);
        this.type = type;
    }

    @Override
    public void initParam(Object... obj) {
        super.initParam(obj);

        switch (type) {
            case DateListFragment.ALL:
                List<CustomFilterQuanBean> listCB = DBHelper.customFilterBeanQuanDao.getAllFilter();

                if (listCB != null) {
                    for (CustomFilterQuanBean cfb : listCB) {
                        getParams().put(cfb.key, cfb.id + "");
                    }
                }
                if (!ac.cs.getChoseLocationID().equals("-1")) {
                    getParams().put("cityid", ac.cs.getChoseLocationID());
                }
                getParams().put("longitude", ac.cs.getLng());
                getParams().put("latitude", ac.cs.getLat());
                break;
            case DateListFragment.ONCE:
                getParams().put("otherid", obj[0]);
                break;
        }

    }
}
