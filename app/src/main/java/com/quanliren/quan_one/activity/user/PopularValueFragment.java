package com.quanliren.quan_one.activity.user;

import android.view.View;
import android.widget.TextView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.PopularValueAdapter;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.api.PopularValueApi;
import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.bean.PopularValue;
import com.quanliren.quan_one.fragment.base.BaseListFragment;
import com.quanliren.quan_one.util.URL;

import org.androidannotations.annotations.EFragment;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Shen on 2016/4/12.
 */
@EFragment(R.layout.fragment_list)
public class PopularValueFragment extends BaseListFragment<PopularValue> {

    //人气总数
    int allPuplarNumber;
    TextView textView;

    @Override
    public void init() {
        super.init();

        setTitleTxt("我的人气");
        setTitleRightIcon(R.drawable.title_back_icon);
    }

    @Override
    public void initListView() {
        super.initListView();

        View view = View.inflate(getActivity(), R.layout.popular_detail_header, null);
        textView = (TextView) view.findViewById(R.id.my_popular_value);
        listview.addHeaderView(view);
    }

    @Override
    public BaseAdapter<PopularValue> getAdapter() {
        return new PopularValueAdapter(getActivity());
    }

    @Override
    public BaseApi getApi() {
        return new PopularValueApi(getActivity());
    }

    @Override
    public Class<?> getClazz() {
        return PopularValue.class;
    }

    @Override
    public void onSuccessRefreshUI(JSONObject jo, List<PopularValue> list, boolean cache) {
        super.onSuccessRefreshUI(jo, list, cache);

        try {
            allPuplarNumber = jo.getJSONObject(URL.RESPONSE).getInt("popNum");
            textView.setText(allPuplarNumber + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
