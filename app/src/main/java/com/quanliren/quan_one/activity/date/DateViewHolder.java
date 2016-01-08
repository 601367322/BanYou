package com.quanliren.quan_one.activity.date;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.DatePicAdapter;
import com.quanliren.quan_one.adapter.base.BaseHolder;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.DateBean;
import com.quanliren.quan_one.custom.UserInfoLayout;
import com.quanliren.quan_one.custom.ZanLinearLayout;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

public class DateViewHolder extends BaseHolder<DateBean>{

    @Bind(R.id.userinfo)
    UserInfoLayout userinfo;
    @Bind(R.id.userlogo)
    ImageView userlogo;
    @Bind(R.id.dian_point)
    ImageView dian_point;
    @Bind(R.id.pic_gridview)
    GridView gridView;
    DatePicAdapter adapter;
    @Bind(R.id.time)
    TextView time;
    @Bind(R.id.signature)
    TextView signature;
    @Bind(R.id.reply_btn)
    TextView reply_btn;
    @Bind(R.id.location)
    TextView location;
    @Bind(R.id.tv_address)
    TextView tv_address;
    @Bind(R.id.tv_dtime)
    TextView tv_dtime;
    @Bind(R.id.tv_objsex)
    TextView tv_objsex;
    @Bind(R.id.tv_pay)
    TextView tv_pay;
    RelativeLayout.LayoutParams lp;
    @Bind(R.id.content_rl)
    View content_rl;
    @Bind(R.id.zan_ll)
    ZanLinearLayout zan_ll;
    @Bind(R.id.reply_ll)
    View reply_ll;

    String[] pays = {"面议", "100-500元/天", "500-1000元/天", "1000-2000元/天", "2000-3000元/天", "3000-4000元/天", "4000-5000元/天", "5000-10000元/天", "1万元以上/天"};

    Context context;
    int imgWidth;
    View.OnClickListener detailClick;
    boolean isDetail = false;
    boolean coin_switch = true;

    public DateViewHolder(View view, Context context, View.OnClickListener detailClick,boolean coin) {
        super(view);

        this.context = context;
        this.imgWidth = (context.getResources().getDisplayMetrics().widthPixels - ImageUtil.dip2px(context, 104)) / 3;
        this.detailClick = detailClick;

        coin_switch = coin;

        adapter = new DatePicAdapter(context, new ArrayList<String>(), imgWidth);
        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, imgWidth);
        lp.addRule(RelativeLayout.BELOW, R.id.signature);

        gridView.setLayoutParams(lp);
        gridView.setAdapter(adapter);
    }

    public void bind(DateBean db, int position) {

        userinfo.setDate(db);

        if (db.getImglist() == null || db.getImglist().size() == 0) {
            gridView.setVisibility(View.GONE);
        } else {
            gridView.setVisibility(View.VISIBLE);
            adapter.setList(db.getImglist());
            adapter.notifyDataSetChanged();
            lp = (RelativeLayout.LayoutParams) gridView.getLayoutParams();
            lp.height = imgWidth * Util.getLines(db.getImglist().size(), 3);
            int num = (db.getImglist().size() > 3 ? 3 : db.getImglist().size());
            int lpwidth = ((num - 1) * ImageUtil.dip2px(context, 4)) + num * imgWidth;
            lp.width = lpwidth;
            gridView.setNumColumns(num);
            gridView.setLayoutParams(lp);
        }
        ImageLoader.getInstance().displayImage(
                db.getAvatar() + StaticFactory._160x160, userlogo, AppClass.options_userlogo);
        time.setText(Util.getTimeDateChinaStr(db.getCtime()));
        if (!TextUtils.isEmpty(db.getRemark())) {
            signature.setVisibility(View.VISIBLE);
            dian_point.setVisibility(View.VISIBLE);
            if(!isDetail) {
                signature.setText(db.getRemark().replaceAll("\r|\n", ""));
            }else{
                signature.setText(db.getRemark());
            }
        } else {
            signature.setText("无");
            dian_point.setVisibility(View.VISIBLE);
            signature.setVisibility(View.VISIBLE);
        }

        if(isDetail){
            signature.setSingleLine(false);
        }

        userlogo.setTag(db);
        location.setText(db.getArea());
        tv_address.setText(db.getAddress());
        tv_dtime.setText(db.getDtime());
        reply_btn.setText(db.getCnum());
        if (detailClick != null) {
            content_rl.setTag(db);
            content_rl.setOnClickListener(detailClick);
            reply_ll.setTag(db);
            reply_ll.setOnClickListener(detailClick);
        }
        if ("0".equals(db.getObjsex())) {
            tv_objsex.setText("美女");
        } else if ("1".equals(db.getObjsex())) {
            tv_objsex.setText("帅哥");
        } else {
            tv_objsex.setText("不限");
        }

        if(coin_switch) {
            ((View)tv_pay.getParent()).setVisibility(View.VISIBLE);
            tv_pay.setText(pays[db.getPay()]);
        }else{
            ((View)tv_pay.getParent()).setVisibility(View.GONE);
        }
        zan_ll.setBean(db);
    }


    @OnClick(R.id.userlogo)
    public void userlogo_click(View view) {
        DateBean db = (DateBean) view.getTag();
        Util.startUserInfoActivity(context, db.getUserid());
    }

    public boolean isDetail() {
        return isDetail;
    }

    public void setIsDetail(boolean isDetail) {
        this.isDetail = isDetail;
    }
}