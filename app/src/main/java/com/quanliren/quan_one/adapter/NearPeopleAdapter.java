package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.UserInfoLayout;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

import butterknife.Bind;

public class NearPeopleAdapter extends BaseAdapter<User> {

    public NearPeopleAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.near_people_list_item;
    }

    class ViewHolder extends BaseHolder<User> {
        @Bind(R.id.userlogo)
        ImageView userlogo;
        @Bind(R.id.userinfo)
        UserInfoLayout userinfo;
        @Bind(R.id.signature)
        TextView signature;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.location)
        TextView location;
        @Bind(R.id.tourists)
        TextView tourists;
        @Bind(R.id.escort)
        TextView escort;
        @Bind(R.id.imgConut)
        TextView imgConut;

        @Override
        public void bind(User user, int position) {
            userinfo.setUser(user);
            tourists.setVisibility(View.GONE);
            escort.setVisibility(View.GONE);

            ImageLoader.getInstance().displayImage(
                    user.getAvatar() + StaticFactory._320x320, userlogo,
                    ac.options_userlogo);
            if (Util.isStrNotNull(user.getSignature())) {
                signature.setText(user.getSignature());
            } else {
                signature.setText(context.getResources().getString(R.string.lazy));
            }
            if (user.getActionTime() == null || "".equals(user.getActionTime())) {
                time.setVisibility(View.GONE);
            } else {
                time.setVisibility(View.VISIBLE);
                time.setText(Util.getTimeDateChinaStr(user.getActionTime()));
            }

            if (Double.valueOf(user.getLatitude()) != 0 && Double.valueOf(user.getLongitude()) != 0
                    && !ac.cs.getLat().equals("")) {
                location.setText(Util.getDistance(
                        Double.valueOf(ac.cs.getLng()),
                        Double.valueOf(ac.cs.getLat()), Double.valueOf(user.getLongitude()),
                        Double.valueOf(user.getLatitude()))
                        + "km");
            } else {
                location.setText("");
            }
            if (user.getIdentity() == 0) {
                tourists.setVisibility(View.VISIBLE);
            } else {
                escort.setVisibility(View.VISIBLE);
            }
            imgConut.setText(user.getImgCount() + "");
        }

        public ViewHolder(View view) {
            super(view);
        }
    }
}
