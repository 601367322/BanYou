package com.quanliren.quan_one.activity.user;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.seting.MyWalletActivity_;
import com.quanliren.quan_one.bean.RedPacketDetail;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.CircleImageView;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Shen on 2016/3/3.
 */
@EActivity(R.layout.activity_red_packet_detail)
public class RedPacketDetailActivity extends BaseActivity {

    @Extra
    RedPacketDetail bean;
    @ViewById
    ListView listview;

    HeadHolder holder;

    User user = null;

    @Override
    public void init() {

        setTitleTxt(getString(R.string.red_packet_detail));

        user = ac.getUserInfo();

        View head = View.inflate(this, R.layout.activity_red_packet_detail_head, null);

        holder = new HeadHolder(head);
        holder.bind(bean.myRed);

        listview.addHeaderView(head);
        RedPacketDetailAdapter adapter = new RedPacketDetailAdapter(mContext);
        adapter.setList(bean.otherRed);
        listview.setAdapter(adapter);
    }

    class HeadHolder {

        @Bind(R.id.userlogo)
        CircleImageView userlogo;
        @Bind(R.id.master_username)
        TextView masterUsername;
        @Bind(R.id.master_content)
        TextView masterContent;
        @Bind(R.id.money)
        TextView money;
        @Bind(R.id.go_wallet)
        TextView goWallet;
        @Bind(R.id.get_money_ll)
        LinearLayout getMoneyLl;
        @Bind(R.id.no_body)
        TextView noBody;
        @Bind(R.id.history_text)
        TextView historyText;

        public HeadHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void bind(RedPacketDetail.OtherUser otherUser) {
            ImageLoader.getInstance().displayImage(otherUser.avatar + StaticFactory._320x320, userlogo, ac.options_userlogo);
            userlogo.setTag(R.id.logo_tag, otherUser.userId);
            masterUsername.setText(otherUser.nickname + "发的红包");
            masterContent.setText(otherUser.content);
            if (otherUser.userId.equals(user.getId()) && bean.redType == 0) {//如果是自己发的红包
                goWallet.setVisibility(View.GONE);
                getMoneyLl.setVisibility(View.GONE);
                noBody.setVisibility(View.VISIBLE);
                if (bean.remainCount == 0) {//如果无人领取
                    noBody.setText("暂时无人领取");
                } else {
                    noBody.setText("红包已被抢完");
                }
            } else {
                getMoneyLl.setVisibility(View.VISIBLE);
                noBody.setVisibility(View.GONE);

                boolean exists_me = false;
                for (int i = 0; i < bean.otherRed.size(); i++) {
                    RedPacketDetail.OtherUser ot = bean.otherRed.get(i);
                    if (user.getId().equals(ot.userId)) {
                        money.setText("￥" + ot.money);
                        exists_me = true;
                    }
                }

                if (!exists_me) {
                    getMoneyLl.setVisibility(View.GONE);
                    noBody.setVisibility(View.VISIBLE);
                    noBody.setText("红包已被抢完");
                }
            }
            String historyStr = "领取" + bean.remainCount + "/" + bean.totalCount + "个";
            if (otherUser.userId.equals(user.getId())) {
                historyStr += "，剩余金额￥" + bean.surMoney;
            }
            historyText.setText(historyStr);
        }

        @OnClick(R.id.go_wallet)
        public void goWallet() {
            MyWalletActivity_.intent(mContext).start();
        }

        @OnClick(R.id.userlogo)
        public void userLogoClick(View view) {
            Util.startUserInfoActivity(mContext, view.getTag(R.id.logo_tag).toString());
        }
    }
}
