package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Shen on 2015/12/29.
 */
public class GroupMemberListAdapter extends BaseAdapter<User> {
    GroupMemberItemDealListener listener;
    ListType type;

    public GroupMemberListAdapter(Context context, GroupMemberItemDealListener listener, ListType type) {
        super(context);
        this.listener = listener;
        this.type = type;
    }

    public enum ListType {
        member, invite
    }

    boolean show = false;

    public void setShow(boolean show) {
        this.show = show;
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.group_member_list_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(getConvertView(position), parent, false);
            holder = getHolder(convertView);
        } else {
            holder = (BaseHolder) convertView.getTag();
        }
        holder.bind(getItem(position), position);
        return convertView;
    }

    @Override
    public void setList(List<User> list) {
        switch (type) {
            case invite:
                if (list.size() > 0) {
                    list.add(0, new User(3));
                }
                break;
        }
        super.setList(list);
    }

    class ViewHolder extends BaseHolder<User> {
        @Bind(R.id.userlayout)
        View userlayout;
        @Bind(R.id.userlogo)
        ImageView userlogo;
        @Bind(R.id.userinfo)
        UserInfoLayout userInfo;
        @Bind(R.id.signature)
        TextView signature;
        @Bind(R.id.delete)
        ImageView delete;
        @Bind(R.id.rb)
        CheckBox rb;
        @Bind(R.id.category_img)
        ImageView categoryImg;
        @Bind(R.id.member_tv)
        TextView memberTv;
        @Bind(R.id.groupheader)
        View groupheader;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(final User bean, int position) {
            if (bean.getTitle() > -1) {
                userlayout.setVisibility(View.GONE);
                groupheader.setVisibility(View.VISIBLE);
                if (bean.getTitle() == 0) {
                    categoryImg.setImageResource(R.drawable.group_member);
                    memberTv.setText(context.getString(R.string.group_member));
                } else if (bean.getTitle() == 1) {
                    categoryImg.setImageResource(R.drawable.group_master);
                    memberTv.setText(context.getString(R.string.group_master));
                } else if (bean.getTitle() == 3) {
                    categoryImg.setImageResource(R.drawable.group_member);
                    memberTv.setText(context.getString(R.string.care_friend));
                }
            } else {
                groupheader.setVisibility(View.GONE);
                userlayout.setVisibility(View.VISIBLE);
                if (Util.isStrNotNull(bean.getId())) {
                    userInfo.setUser(bean);
                    ImageLoader.getInstance().displayImage(bean.getAvatar() + StaticFactory._160x160, userlogo, ac.options_userlogo);
                    signature.setText(bean.getSignature());
                    if (type == ListType.member) {
                        rb.setVisibility(View.GONE);
                        if (position > 1 && show) {
                            delete.setVisibility(View.VISIBLE);
                        } else {
                            delete.setVisibility(View.GONE);
                        }
                        delete.setTag(bean);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.deal((User) v.getTag(), false);
                            }
                        });
                    } else if (type == ListType.invite) {
                        delete.setVisibility(View.GONE);
                        rb.setVisibility(View.VISIBLE);
                        rb.setChecked(bean.getChecked());
                        rb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bean.setChecked(rb.isChecked());
                                listener.deal(bean, rb.isChecked());
                            }
                        });
                        userlayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rb.performClick();
                            }
                        });
                    }
                }
            }
        }

    }

    public interface GroupMemberItemDealListener {

        /**
         * 踢出群成员
         *
         * @param tag
         */
        void deal(User tag, boolean checked);
    }
}
