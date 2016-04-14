package com.quanliren.quan_one.activity.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.shop.ShopVipDetailActivity_;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;

@EActivity
public abstract class BaseUserActivity extends BaseActivity {

    @ViewById(R.id.nickname)
    public TextView nickname;

    @ViewById(R.id.id_num)
    public TextView id_num;

    @ViewById(R.id.signature)
    public TextView signature;
    @ViewById(R.id.sex)
    public TextView sex;
    @ViewById(R.id.income)
    public TextView income;
    @ViewById(R.id.work)
    public TextView work;
    @ViewById(R.id.emotion)
    public TextView emotion;
    @ViewById(R.id.userlogo)
    public ImageView userlogo;

    @ViewById(R.id.identity)
    public TextView identity;
    @ViewById(R.id.payment)
    public TextView payment;
    @ViewById(R.id.pay)
    public TextView pay;
    @ViewById(R.id.phone)
    public TextView phone;
    @ViewById(R.id.go_vip)
    public TextView go_vip;
    @ViewById(R.id.introduce)
    public TextView introduce;

    public User user = null;

    @Click(R.id.go_vip)
    public void goVip() {
        ShopVipDetailActivity_.intent(mContext).start();
    }

    String[] pays = {"100-500元/天", "500-1000元/天", "1000-2000元/天", "2000-3000元/天", "3000-4000元/天", "4000-5000元/天", "5000-10000元/天", "1万元以上/天"};

    public void initViewByUser() {
        try {
            if (Util.isStrNotNull(user.getAvatar())) {
                ImageLoader.getInstance().displayImage(
                        user.getAvatar() + StaticFactory._160x160, userlogo, ac.options_userlogo);
            }
            nickname.setText(user.getNickname());
            if (Util.isStrNotNull(user.getIncome())) {
                income.setText(user.getIncome());
            }

            if (Util.isStrNotNull(user.getJob())) {
                work.setText(user.getJob());
            }
            if (Util.isStrNotNull(user.getEmotion())) {
                emotion.setText(user.getEmotion());
            }
            if (Util.isStrNotNull(user.getSignature())) {
                signature.setText(user.getSignature());
            } else {
                signature.setText("无");
            }

            id_num.setText(user.getUsernumber());

            if (user.getIdentity() == 0) {
                identity.setText("游客");
                payment.setText("支付能力");
            } else {
                identity.setText("伴游");
                payment.setText("薪酬");
            }
            pay.setText(pays[user.getPay()]);

            if (Util.isStrNotNull(user.getIntroduce())) {
                introduce.setText(user.getIntroduce());
            } else {
                if (user.getId().equals(ac.getUser().getId())) {
                    introduce.setHint("您还没有自我介绍哦");
                } else {
                    introduce.setText("这个人有点懒，什么都没有留下");
                }
            }
            if (go_vip != null) {
                go_vip.setVisibility(View.GONE);
            }

            if (user.getShowState() == 1) {
                phone.setText("不公开");
            } else if (user.getShowState() == 0) {
                if (user.getId().equals(ac.getUser().getId())) {
                    phone.setText("只对会员公开");
                } else {
                    if (ac.getUserInfo().getIsvip() > 0) {
                        phone.setText(user.getMobile());
                    } else {
                        phone.setText("只对会员公开");
                        if (go_vip != null) {
                            go_vip.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> getxingzuoMap() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        return map;
    }

    public void pro_btn(View v) {
    }

}