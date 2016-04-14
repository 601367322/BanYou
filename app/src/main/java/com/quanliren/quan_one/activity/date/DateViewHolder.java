package com.quanliren.quan_one.activity.date;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.user.PlayVideoFragment_;
import com.quanliren.quan_one.adapter.DatePicAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.DateBean;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.CircleImageView;
import com.quanliren.quan_one.custom.NoScrollGridView;
import com.quanliren.quan_one.custom.UserInfoLayout;
import com.quanliren.quan_one.custom.ZanLinearLayout;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;

public class DateViewHolder extends BaseHolder<DateBean> {

    @Bind(R.id.userinfo)
    UserInfoLayout userinfo;
    @Bind(R.id.time)
    TextView time;
    @Bind(R.id.user_info_ll)
    LinearLayout userInfoLl;
    @Bind(R.id.userlogo)
    CircleImageView userlogo;
    @Bind(R.id.date_time)
    TextView dateTime;
    @Bind(R.id.date_sex)
    TextView dateSex;
    @Bind(R.id.date_content)
    TextView dateContent;
    @Bind(R.id.tagcontainerLayout1)
    TagContainerLayout tagcontainerLayout1;
    @Bind(R.id.date_content_ll)
    LinearLayout dateContentLl;
    @Bind(R.id.date_money)
    TextView dateMoney;
    @Bind(R.id.date_phone)
    TextView datePhone;
    @Bind(R.id.date_remark)
    TextView dateRemark;
    @Bind(R.id.pic_gridview)
    NoScrollGridView picGridview;
    @Bind(R.id.location)
    TextView location;
    @Bind(R.id.reply_btn)
    TextView replyBtn;
    @Bind(R.id.reply_ll)
    LinearLayout replyLl;
    @Bind(R.id.zan)
    TextView zan;
    @Bind(R.id.zan_ll)
    ZanLinearLayout zanLl;
    @Bind(R.id.top)
    LinearLayout top;
    @Bind(R.id.content_rl)
    LinearLayout content_rl;
    @Bind(R.id.btm_space)
    View btmSpace;
    @Bind(R.id.video_img)
    ImageView videoImg;
    @Bind(R.id.loading)
    TextView loading;
    @Bind(R.id.video_content)
    View videoContent;
    @Bind(R.id.loading_img)
    ImageView loadingImg;

    DatePicAdapter adapter;
    LinearLayout.LayoutParams lp;

    String[] pays = {"面议", "100-500元/天", "500-1000元/天", "1000-2000元/天", "2000-3000元/天", "3000-4000元/天", "4000-5000元/天", "5000-10000元/天", "1万元以上/天"};

    Context context;
    int imgWidth;
    View.OnClickListener detailClick;
    boolean isDetail = false;
    boolean coin_switch = true;

    User user = null;

    public DateViewHolder(View view, Context context, View.OnClickListener detailClick, boolean coin) {
        super(view);

        this.context = context;
        this.imgWidth = (context.getResources().getDisplayMetrics().widthPixels - ImageUtil.dip2px(context, 12 * 2 + 4 + 4 + 4)) / 4;
        this.detailClick = detailClick;

        coin_switch = coin;

        adapter = new DatePicAdapter(context, new ArrayList<String>(), imgWidth);
        lp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, imgWidth);

        picGridview.setLayoutParams(lp);
        picGridview.setAdapter(adapter);

        user = ((AppClass) context.getApplicationContext()).getUserInfo();
    }

    public void bind(DateBean db, int position) {

        userinfo.setDate(db);

        if (db.getImglist() == null || db.getImglist().size() == 0) {
            picGridview.setVisibility(View.GONE);
        } else {
            picGridview.setVisibility(View.VISIBLE);
            adapter.setList(db.getImglist());
            adapter.notifyDataSetChanged();
            lp = (LinearLayout.LayoutParams) picGridview.getLayoutParams();
            lp.height = imgWidth * Util.getLines(db.getImglist().size(), 3);
            int num = (db.getImglist().size() > 3 ? 3 : db.getImglist().size());
            int lpwidth = ((num - 1) * ImageUtil.dip2px(context, 4)) + num * imgWidth;
            lp.width = lpwidth;
            lp.topMargin = ImageUtil.dip2px(context, 4);
            picGridview.setNumColumns(num);
            picGridview.setLayoutParams(lp);
        }

        ImageLoader.getInstance().displayImage(
                db.getAvatar() + StaticFactory._160x160, userlogo, AppClass.options_userlogo);

        time.setText(Util.getTimeDateChinaStr(db.getCtime()));
        if (!TextUtils.isEmpty(db.getRemark())) {
            dateRemark.setVisibility(View.VISIBLE);
            if (!isDetail) {
                dateRemark.setText(db.getRemark().replaceAll("\r|\n", ""));
            } else {
                dateRemark.setText(db.getRemark());
            }
        } else {
            dateRemark.setVisibility(View.GONE);
        }

        if (isDetail) {
            dateRemark.setSingleLine(false);
        }

        userlogo.setTag(db);
        location.setText(db.getArea());
        dateTime.setText(db.getDtime() + "　在　" + db.getAddress());
        replyBtn.setText(db.getCnum());
        if (detailClick != null) {
            content_rl.setTag(db);
            content_rl.setOnClickListener(detailClick);
            replyLl.setTag(db);
            replyLl.setOnClickListener(detailClick);
        }

        if ("0".equals(db.getObjsex())) {
            dateSex.setText("约一个　美女");
        } else if ("1".equals(db.getObjsex())) {
            dateSex.setText("约一个　帅哥");
        } else {
            dateSex.setText("约一个　美女 或者 帅哥");
        }

        zanLl.setBean(db);

        //视频
        if (db.video != null && db.video.videoType != 0) {
            videoContent.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoImg.getLayoutParams();
            if (picGridview.getVisibility() == View.GONE) {

                loadingImg.setImageResource(R.drawable.date_list_video_loading_big_icon);
                ImageLoader.getInstance().displayImage(db.video.videoImg + StaticFactory._320x320, videoImg);

                params.width = ImageUtil.dip2px(context, 124);
                params.height = ImageUtil.dip2px(context, 124);
            } else {

                loadingImg.setImageResource(R.drawable.date_list_video_loading_small_icon);
                ImageLoader.getInstance().displayImage(db.video.videoImg + StaticFactory._160x160, videoImg);

                params.width = imgWidth;
                params.height = imgWidth;
            }
            videoImg.setLayoutParams(params);
            videoImg.setTag(R.id.logo_tag, db);
            switch (db.video.videoType) {
                case 1:
                    loading.setVisibility(View.GONE);
                    loadingImg.setImageResource(R.drawable.date_list_video_start_big_icon);
                    break;
                case 2:
                    loading.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            videoContent.setVisibility(View.GONE);
        }

        if (!isDetail) {
            return;
        }

        btmSpace.setVisibility(View.VISIBLE);

        //薪酬
        dateMoney.setVisibility(View.VISIBLE);
        if (coin_switch) {
            dateMoney.setText("薪酬　" + pays[db.getPay()]);
        } else {
            dateMoney.setVisibility(View.GONE);
        }

        //手机号
        if (db.getDtstate() != null && !"0".equals(db.getDtstate())) {
            datePhone.setVisibility(View.GONE);
        } else {
            datePhone.setVisibility(View.VISIBLE);
            if (user.getIsvip() == 0 && !user.getId().equals(db.getUserid())) {
                datePhone.setText("我的联系方式　只对会员公开");
            } else {
                if (db.getPhoneMode() == 1) {
                    datePhone.setVisibility(View.GONE);
                } else if (db.getPhoneMode() == 0) {
                    datePhone.setText("我的联系方式　" + db.getMobile());
                }
            }
        }
        //约会内容
        if (db.getTypelist() == null || db.getTypelist().size() == 0) {
            dateContentLl.setVisibility(View.GONE);
        } else {
            dateContentLl.setVisibility(View.VISIBLE);
            tagcontainerLayout1.removeAllTags();
            tagcontainerLayout1.setTags(db.getTypelist());
        }
    }


    @OnClick(R.id.userlogo)
    public void userlogo_click(View view) {
        DateBean db = (DateBean) view.getTag();
        Util.startUserInfoActivity(context, db.getUserid());
    }

    @OnClick(R.id.video_img)
    public void videoImgClick(View view) {
        DateBean db = (DateBean) view.getTag(R.id.logo_tag);
        DateBean.Video video = db.video;
        if (video.videoType == 1) {
            if (!db.getUserid().equals(user.getId()) && user.getIsvip() == 0) {
                Util.goVip(context, 0);
                return;
            }
            PlayVideoFragment_.builder().bean(new DfMessage.VideoBean(video.video, video.videoImg)).build().show(((AppCompatActivity) context).getSupportFragmentManager(), "dialog");
        }
    }

    public void setIsDetail(boolean isDetail) {
        this.isDetail = isDetail;
    }
}