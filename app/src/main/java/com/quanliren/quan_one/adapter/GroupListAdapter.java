package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.custom.CircleImageView;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

import butterknife.Bind;

/**
 * Created by Shen on 2015/12/24.
 */
public class GroupListAdapter extends BaseAdapter<GroupBean> {

    public GroupListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.group_list_item;
    }

    class ViewHolder extends BaseHolder<GroupBean> {

        @Bind(R.id.group_logo)
        CircleImageView groupLogo;
        @Bind(R.id.group_name)
        TextView groupName;
        @Bind(R.id.group_vip)
        ImageView groupVip;
        @Bind(R.id.group_people_number)
        TextView groupPeopleNumber;
        @Bind(R.id.group_juli)
        TextView groupJuli;
        @Bind(R.id.group_desc)
        TextView groupDesc;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(GroupBean bean, int position) {
            ImageLoader.getInstance().displayImage(bean.getAvatar() + StaticFactory._320x320, groupLogo, AppClass.options_group_userlogo);
            groupName.setText(bean.getGroupName());
            if (Integer.valueOf(bean.getGroupType()) == 0) {
                groupVip.setVisibility(View.VISIBLE);
                groupVip.setImageResource(R.drawable.vip_1);
            } else if (Integer.valueOf(bean.getGroupType()) == 1) {
                groupVip.setVisibility(View.VISIBLE);
                groupVip.setImageResource(R.drawable.vip_2);
            } else {
                groupVip.setVisibility(View.GONE);
            }

            groupPeopleNumber.setText(bean.getMemberNum() + "/" + bean.getMemberSum());
            groupJuli.setText(bean.getArea());

            if (Double.valueOf(bean.getLatitude()) != 0 && Double.valueOf(bean.getLongitude()) != 0
                    && !ac.cs.getLat().equals("")) {
                groupJuli.setText(Util.getDistance(
                        Double.valueOf(ac.cs.getLng()),
                        Double.valueOf(ac.cs.getLat()), Double.valueOf(bean.getLongitude()),
                        Double.valueOf(bean.getLatitude()))
                        + "km");
            } else {
                groupJuli.setText("");
            }

            groupDesc.setText(bean.getGroupInt());
        }
    }
}
