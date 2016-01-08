package com.quanliren.quan_one.activity.date;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.fragment.base.BaseListFragment;
import com.quanliren.quan_one.adapter.BlackPeopleAdapter;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.api.VisitorListApi;
import com.quanliren.quan_one.bean.CounterBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemLongClick;
import org.json.JSONObject;

/**
 * Created by Shen on 2015/11/10.
 */
@EFragment(R.layout.fragment_list)
public class VisitorListFragment extends BaseListFragment<User> {


    @Override
    public void init() {
        super.init();
        setTitleTxt("访客记录");
    }

    @Override
    public Class<?> getClazz() {
        return User.class;
    }

    @Override
    public boolean needCache() {
        return true;
    }

    @Override
    public BaseAdapter<User> getAdapter() {
        BlackPeopleAdapter adapter = new BlackPeopleAdapter(getActivity());
        adapter.setNeedTime(true);
        return adapter;
    }

    @Override
    public BaseApi getApi() {
        return new VisitorListApi(getActivity());
    }


    @Override
    public void onSuccessCallBack(JSONObject jo) {
        super.onSuccessCallBack(jo);
        CounterBean bean = DBHelper.counterDao.getCounter(ac.getUser().getId());
        if(bean!=null) {
            bean.getBean().setVcnt("0");
            DBHelper.counterDao.update(bean);
        }
    }

    @Override
    public void listview(int position) {
        super.listview(position);
        User user = adapter.getItem(position);
        Util.startUserInfoActivity(getActivity(),user);
    }

    @ItemLongClick(R.id.listview)
    public void listview_longClick(final int position){
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setItems(new String[]{"删除这条记录"},
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                menuClick(position);
                            }
                        }).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void menuClick(int position) {
        RequestParams ap = getAjaxParams();
        ap.put("uvid", adapter.getItem(position).getUvid());
        ac.finalHttp.post(URL.DELETE_VISITLIST, ap, new setLogoCallBack(position));
    }

    class setLogoCallBack extends MyJsonHttpResponseHandler {

        int position;

        public setLogoCallBack(int position) {
            super(getActivity(), Util.progress_arr[1]);
            this.position = position;
        }

        @Override
        public void onSuccessRetCode(JSONObject jo) throws Throwable {
            deleteAnimate(position);
            showCustomToast("删除成功");
        }
    }

    public void deleteAnimate(final int position) {
        adapter.remove(position);
        adapter.notifyDataSetChanged();
    }
}
