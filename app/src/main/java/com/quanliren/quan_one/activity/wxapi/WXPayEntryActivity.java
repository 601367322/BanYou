package com.quanliren.quan_one.activity.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.quanliren.quan_one.post.UpdateUserPost;
import com.quanliren.quan_one.util.*;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if(resp.errCode == BaseResp.ErrCode.ERR_OK){
			com.quanliren.quan_one.util.Util.toast(this, "购买成功，正在刷新用户信息。");
		}else{
			com.quanliren.quan_one.util.Util.toast(this,"购买失败");
		}
		new UpdateUserPost(this,null);

		this.finish();
	}
}