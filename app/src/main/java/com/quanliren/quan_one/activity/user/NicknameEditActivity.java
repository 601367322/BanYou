package com.quanliren.quan_one.activity.user;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity(R.layout.user_nickname_edit)
public class NicknameEditActivity extends BaseActivity {
    @ViewById(R.id.nickname_edit)
    EditText nickname_edit;
    @Extra
    public String str_nickname;
    @ViewById(R.id.tv_tip)
    TextView tv_tip;
    @Extra
    public int type;
    @Extra
    public String groupId;
    String nickname;
    @Override
    public void init() {
        super.init();
        setTitleRightTxt("保存");
        nickname_edit.setText(str_nickname);
        if(type==0){
            setTitleTxt("更改昵称");
            tv_tip.setText("请尽量使用真实姓名   请使用文明用语");
        }else if(type==1){
            setTitleTxt(getString(R.string.group_nickname));
            tv_tip.setText("请使用文明用语");
        }
    }
    @Override
    public void rightClick(View v) {
        super.rightClick(v);
        nickname = nickname_edit.getText().toString().trim();
        if (Util.hasSpecialByte(nickname)) {
            showCustomToast("昵称中不能包含特殊字符");
            return;
        }
        if(str_nickname.equals(nickname)){
            showCustomToast("修改昵称不能与当前昵称一样");
            return;
        }
        if (nickname.length() > 0) {
            if(type==0){
                RequestParams params = getAjaxParams();
                params.put("nickname", nickname);
                httpPost(URL.EDIT_USER_INFO, params);
            }else if(type==1){
                RequestParams params = getAjaxParams();
                params.put("groupId", groupId);
                params.put("groupName", nickname);
                httpPost(URL.EDIT_GROUP,params);
            }
        }else if(nickname.trim().length()==0){
			Util.toast(mContext, "请输入昵称");
		}

    }
    void httpPost(String url,RequestParams params){
        ac.finalHttp.post(url, params, new MyJsonHttpResponseHandler(mContext,Util.progress_arr[3]) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                showCustomToast("修改成功");
                Intent intent = new Intent();
                intent.putExtra("nickname", nickname);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}