package com.quanliren.quan_one.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.DateBean;
import com.quanliren.quan_one.bean.DateReplyBean;
import com.quanliren.quan_one.bean.User;

public class UserInfoLayout extends RelativeLayout {

    private TextView nickname, sex, constell, auth_icon;
    private ImageView vip_icon;
    private boolean need_vip = true;
    private int nickNameSize = 0;
    private int nickNameColor = Color.BLACK;

    public UserInfoLayout(Context context, AttributeSet attrs,
                          int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UserInfoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        try {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.UserInfoLayout);
            if (a != null) {
                int layout = a.getResourceId(R.styleable.UserInfoLayout_info_layout, 0);
                inflate(context, layout, this);
                nickname = (TextView) findViewById(R.id.nickname);
                sex = (TextView) findViewById(R.id.sex);
                vip_icon = (ImageView) findViewById(R.id.vip);
                constell = (TextView) findViewById(R.id.constell);
                auth_icon = (TextView) findViewById(R.id.auth_icon);
                need_vip = a.getBoolean(R.styleable.UserInfoLayout_info_vip, true);
                nickNameSize = a.getInteger(R.styleable.UserInfoLayout_info_nickname_size, 16);
                nickNameColor = a.getColor(R.styleable.UserInfoLayout_info_nickname_color, getResources().getColor(R.color.username));
                a.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserInfoLayout(Context context) {
        super(context);
    }

    public void setDate(DateBean date) {
        setUser(date.getNickname(), Integer.valueOf(date.getSex()), date.getAge(), date.getIsvip(), date.getConstell(), date.getConfirmType());
    }

    public void setDateReply(DateReplyBean date) {
        setUser(date.getNickname(), Integer.valueOf(date.getSex()), date.getAge(), date.getIsvip(), "", 0);
    }

    public void setUser(User user) {
        setUser(user.getNickname(), Integer.valueOf(user.getSex()), user.getAge(), user.getIsvip(), user.getConstell(), user.getConfirmType());
    }

    public void setUser(String nickname, int sex, String age, int vip, String cons, int confirm) {
        this.nickname.setText(nickname);
        this.nickname.setTextSize(nickNameSize);
        this.nickname.setTextColor(nickNameColor);
        switch (sex) {
            case 0:
                this.sex.setBackgroundResource(R.drawable.group_girl_number_bg);
                break;
            case 1:
                this.sex.setBackgroundResource(R.drawable.group_boy_number_bg);
                break;
            default:
                break;
        }
        if (TextUtils.isEmpty(age)) {
            this.sex.setText("0");
        } else {
            this.sex.setText(age);
        }
        this.sex.setTextSize(11);

        if (need_vip) {
            if (vip == 1) {
                vip_icon.setVisibility(View.VISIBLE);
                vip_icon.setImageResource(R.drawable.vip_1);
            } else if (vip == 2) {
                vip_icon.setVisibility(View.VISIBLE);
                vip_icon.setImageResource(R.drawable.vip_2);
            } else {
                vip_icon.setVisibility(View.GONE);
            }
        }
        if (constell != null) {
            constell.setText(cons);
        }
        if (auth_icon != null) {
            if (confirm == 2) {
                auth_icon.setVisibility(VISIBLE);
            } else {
                auth_icon.setVisibility(GONE);
            }
        }
    }

}
