package com.quanliren.quan_one.adapter;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.group.GroupDetailActivity_;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Shen on 2015/7/22.
 */
public class MessageHelperHolder extends MessageBaseHolder {

    @Bind(R.id.helper)
    View helper;
    @Bind(R.id.send_type)
    TextView send_type;
    @Bind(R.id.send_comment)
    TextView send_comment;
    @Bind(R.id.see_detail)
    TextView see_detail;
    @Bind(R.id.confirm_btn)
    View confirmBtn;
    @Bind(R.id.agree)
    TextView agree;
    @Bind(R.id.disagree)
    TextView disagree;
    @Bind(R.id.chat_context_tv)
    TextView context_tv;
    @Bind(R.id.result_txt)
    TextView result_txt;
    @Bind(R.id.btn_selector)
    LinearLayout btn_selector;

    public MessageHelperHolder(View view) {
        super(view);
    }

    @Override
    public void bind(DfMessage bean, int position) {
        super.bind(bean, position);
        helper.setVisibility(View.VISIBLE);
        context_tv.setVisibility(View.GONE);
        helper.setTag(bean);
        helper.setOnLongClickListener(long_click);
        helper.setOnClickListener(null);
        btn_selector.setBackgroundResource(R.drawable.see_detail);
        final DfMessage.OtherHelperMessage msg = bean.getOtherHelperContent();
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(msg.getNickname())) {
            sb.append("<font color='#ee3e3e'>" + msg.getNickname() + "</font>");
        }
        switch (msg.getInfoType()) {
            case DfMessage.OtherHelperMessage.INFO_TYPE_COMMIT:
                sb.append(context.getString(R.string.info_type_0));
                break;
            case DfMessage.OtherHelperMessage.INFO_TYPE_PAST_DUE:
                sb.append(context.getString(R.string.info_type_1));
                break;
            case DfMessage.OtherHelperMessage.INFO_TYPE_REPLY_COMMIT:
                sb.append(context.getString(R.string.info_type_2));
                break;
            case DfMessage.OtherHelperMessage.INFO_TYPE_APPLY_JOIN_GROUP:
                sb.append(String.format(context.getString(R.string.apply_your_group), msg.getGroupName()));
                break;
            case DfMessage.OtherHelperMessage.INFO_TYPE_AGREE_APPLY:
            case DfMessage.OtherHelperMessage.INFO_TYPE_KICK_OUT:
            case DfMessage.OtherHelperMessage.INFO_TYPE_JIE_SAN:
                helper.setVisibility(View.GONE);
                context_tv.setVisibility(View.VISIBLE);
                context_tv.setText(msg.getText());
                return;
            case DfMessage.OtherHelperMessage.INFO_TYPE_INVITE_JOIN_GROUP:
                sb.append(String.format(context.getString(R.string.invite_your_group), msg.getGroupName()));
                break;
        }
        send_type.setTag(bean);
        send_type.setText(Html.fromHtml(sb.toString()));
        send_comment.setVisibility(View.VISIBLE);
        send_comment.setText(msg.getText());

        confirmBtn.setVisibility(View.GONE);
        see_detail.setVisibility(View.GONE);
        result_txt.setVisibility(View.GONE);

        disagree.setTag(null);
        agree.setTag(null);

        switch (msg.getInfoType()) {
            case DfMessage.OtherHelperMessage.INFO_TYPE_COMMIT:
            case DfMessage.OtherHelperMessage.INFO_TYPE_PAST_DUE:
            case DfMessage.OtherHelperMessage.INFO_TYPE_REPLY_COMMIT:
                see_detail.setVisibility(View.VISIBLE);
                see_detail.setTag(bean);
                see_detail.setOnClickListener(detailClick);
                break;
            case DfMessage.OtherHelperMessage.INFO_TYPE_APPLY_JOIN_GROUP:
            case DfMessage.OtherHelperMessage.INFO_TYPE_INVITE_JOIN_GROUP:
                switch (msg.getIsAgree()) {
                    case 0:
                        confirmBtn.setVisibility(View.VISIBLE);
                        result_txt.setVisibility(View.GONE);
                        agree.setTag(bean);
                        disagree.setTag(bean);
                        break;
                    case 1:
                    case 2:
                        confirmBtn.setVisibility(View.GONE);
                        result_txt.setVisibility(View.VISIBLE);
                        switch (msg.getIsAgree()) {
                            case 1:
                                result_txt.setText(context.getResources().getString(R.string.agreed));
                                break;
                            case 2:
                                result_txt.setText(context.getResources().getString(R.string.disagreed));
                                break;
                        }
                        btn_selector.setBackgroundResource(R.drawable.see_detail_disenable);
                        break;
                }
                break;
        }
    }

    @OnClick(R.id.send_type)
    public void nickNameClick(View view) {
        final DfMessage msg = (DfMessage) view.getTag();
        final DfMessage.OtherHelperMessage helperMessage = msg.getOtherHelperContent();
        if (helperMessage.getInfoType() == DfMessage.OtherHelperMessage.INFO_TYPE_APPLY_JOIN_GROUP) {
            Util.startUserInfoActivity(context, helperMessage.getuId());
        } else if (helperMessage.getInfoType() == DfMessage.OtherHelperMessage.INFO_TYPE_INVITE_JOIN_GROUP) {
            GroupBean group = new GroupBean();
            group.setId(helperMessage.getgId());
            group.setNickname(helperMessage.getGroupName());
            GroupDetailActivity_.intent(context).bean(group).start();
        }
    }


    @OnClick(R.id.agree)
    public void agreeClick(View view) {
        if (view.getTag() == null) {
            return;
        }
        final DfMessage msg = (DfMessage) view.getTag();
        final DfMessage.OtherHelperMessage helperMessage = msg.getOtherHelperContent();
        int type = -1;
        switch (helperMessage.getInfoType()) {
            case DfMessage.OtherHelperMessage.INFO_TYPE_APPLY_JOIN_GROUP:
                type = 0;
                break;
            case DfMessage.OtherHelperMessage.INFO_TYPE_INVITE_JOIN_GROUP:
                type = 1;
                break;
        }
        doClick(view, type, 1, URL.AGREE_GROUP_REQUEST);
    }

    @OnClick(R.id.disagree)
    public void disAgreeClick(View view) {
        if (view.getTag() == null) {
            return;
        }
        final DfMessage msg = (DfMessage) view.getTag();
        final DfMessage.OtherHelperMessage helperMessage = msg.getOtherHelperContent();
        int type = -1;
        switch (helperMessage.getInfoType()) {
            case DfMessage.OtherHelperMessage.INFO_TYPE_APPLY_JOIN_GROUP:
                type = 6;
                break;
            case DfMessage.OtherHelperMessage.INFO_TYPE_INVITE_JOIN_GROUP:
                type = 3;
                break;
        }
        doClick(view, type, 2, URL.GROUP_MANAGER_USER);
    }

    /**
     * 发送同意或拒绝请求
     *
     * @param view  按钮
     * @param type  申请、邀请
     * @param agree 同意拒绝
     * @param url   地址
     */
    public void doClick(View view, int type, final int agree, String url) {
        final DfMessage msg = (DfMessage) view.getTag();
        final DfMessage.OtherHelperMessage helperMessage = msg.getOtherHelperContent();
        RequestParams params = Util.getRequestParams(context);
        params.put("type", type);
        params.put("grId", helperMessage.getGrId());
        ac.finalHttp.post(url, params, new MyJsonHttpResponseHandler(context, Util.progress_arr[4]) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                helperMessage.setIsAgree(agree);
                msg.setOtherHelperContent(helperMessage);
                DBHelper.dfMessageDao.update(msg);
                getAdapter().notifyDataSetChanged();
            }
        });
    }

    View.OnClickListener detailClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getHandler().obtainMessage(0, v.getTag()).sendToTarget();
        }
    };
}
