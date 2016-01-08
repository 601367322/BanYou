package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class NearPeopleTwoAdapter extends BaseAdapter<User> {

    public NearPeopleTwoAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.near_people_grid;
    }

    private int mColumn = 2;

    AdapterView.OnItemClickListener logoClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            List<User> users = (List<User>) adapterView.getTag();
            User user = users.get(i);
            Util.startUserInfoActivity(context, user);
        }
    };

    class ViewHolder extends BaseHolder<User> {

        @Bind(R.id.gridview)
        GridView gridView;
        NearPeopleGridAdapter adapter;

        public ViewHolder(View view) {
            super(view);
            adapter = new NearPeopleGridAdapter(context);
            gridView.setAdapter(adapter);
        }

        @Override
        public void bind(User bean, int position) {
            List<User> users = new ArrayList<>();
            for (int i = 0; i < mColumn; i++) {
                int n = position * mColumn + i;
                if (n < list.size()) {
                    users.add(getItem(n));
                }
            }

            adapter.setList(users);
            gridView.setTag(users);
            gridView.setOnItemClickListener(logoClick);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        int count = list.size() / mColumn;
        if (list.size() % mColumn > 0) {
            count++;
        }
        return count;
    }
}
