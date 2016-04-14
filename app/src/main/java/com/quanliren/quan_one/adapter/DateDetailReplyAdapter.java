package com.quanliren.quan_one.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.bean.DateReplyBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.custom.UserInfoLayout;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

import butterknife.Bind;

public class DateDetailReplyAdapter extends BaseAdapter<DateReplyBean> {

    LoginUser user = null;
    IQuanDetailReplyAdapter listener;

    public DateDetailReplyAdapter(Context context) {
        super(context);
        user = ac.getUser();
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.date_reply_item;
    }

    class ViewHolder extends BaseHolder<DateReplyBean> {
        @Bind(R.id.userinfo)
        UserInfoLayout userinfo;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.content)
        TextView content;
        @Bind(R.id.delete_content)
        TextView delete_content;
        @Bind(R.id.content_rl)
        View content_rl;
        @Bind(R.id.delete_ll)
        View delete_ll;
        @Bind(R.id.userlogo)
        ImageView userlogo;
        @Bind(R.id.reply_icon)
        ImageView reply_icon;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(DateReplyBean bean, int position) {

            userinfo.setDateReply(bean);

            if (bean.getReplyuid() != null && !bean.getReplyuid().equals("") && !bean.getReplyuid().equals("-1")) {
                content.setText("回复 " + bean.getReplyuname() + " : " + bean.getContent());
            } else {
                content.setText(bean.getContent());
            }
            if (user.getId().equals(bean.getUserid())) {
                delete_ll.setVisibility(View.VISIBLE);
                delete_content.setTag(bean);
                delete_content.setOnClickListener(viewClick);

            } else {
                delete_ll.setVisibility(View.GONE);
            }
            content_rl.setTag(bean);
            content_rl.setOnClickListener(viewClick);
            userlogo.setTag(R.id.logo_tag, bean);
            userlogo.setOnClickListener(viewClick);
            reply_icon.setTag(bean);
            reply_icon.setOnClickListener(viewClick);
            time.setText(Util.getTimeDateChinaStr(bean.getCtime()));
            ImageLoader.getInstance().displayImage(
                    bean.getAvatar() + StaticFactory._160x160, userlogo, ac.options_userlogo);
        }
    }

    public IQuanDetailReplyAdapter getListener() {
        return listener;
    }

    public void setListener(IQuanDetailReplyAdapter listener) {
        this.listener = listener;
    }

    public interface IQuanDetailReplyAdapter {
        void contentClick(DateReplyBean bean);

        void delete_contentClick(DateReplyBean bean);

        void logoCick(DateReplyBean bean);
    }

    OnClickListener viewClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.userlogo:
                    listener.logoCick((DateReplyBean) v.getTag(R.id.logo_tag));
                    break;
                case R.id.content_rl:
                case R.id.reply_icon:
                    listener.contentClick((DateReplyBean) v.getTag());
                    break;
                case R.id.delete_content:
                    listener.delete_contentClick((DateReplyBean) v.getTag());
                    break;

            }
        }
    };
}
