package com.quanliren.quan_one.activity.wxapi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.util.Constants;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.sso.UMSsoHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@EActivity
public class InviteFriendActivity extends BaseActivity {
	private final UMSocialService mController = UMServiceFactory
			.getUMSocialService(Constants.DESCRIPTOR);
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.fx);
		setTitleTxt("邀请好友");
		// 配置需要分享的相关平台,并设置分享内容
		configPlatforms(mController,INVITE);
	}
	@Click(R.id.fx_sina_btn)
	public void shareToSina(View view){
		postShare(SHARE_MEDIA.SINA);

	}
	@Click(R.id.fx_qq_btn)
	public void shareToQQ(View view){
		postShare(SHARE_MEDIA.QQ);
	}
	@Click(R.id.fx_qzone_btn)
	public void shareToQZone(View view){
		postShare(SHARE_MEDIA.QZONE);
	}
	@Click(R.id.fx_msg_btn)
	public void shareToMsg(View view){
		Uri smsToUri = Uri.parse("smsto:");
		Intent mIntent = new Intent(Intent.ACTION_SENDTO,
				smsToUri);
		mIntent.putExtra("sms_body", Constants.shareContent);
		startActivity(mIntent);
	}
	@Click(R.id.fx_weixin_btn)
	public void shareToWeiXin(View view){
		postShare(SHARE_MEDIA.WEIXIN);
	}
	@Click(R.id.fx_friend_btn)
	public void shareToWXCircle(View view){
		postShare(SHARE_MEDIA.WEIXIN_CIRCLE);
	}
	/**
	 * 直接分享，底层分享接口。如果分享的平台是新浪、腾讯微博、豆瓣、人人，则直接分享，无任何界面弹出； 其它平台分别启动客户端分享</br>
	 */
	private void postShare(SHARE_MEDIA mPlatform) {
		mController.postShare(InviteFriendActivity.this, mPlatform, new SocializeListeners.SnsPostListener() {

			@Override
			public void onStart() {
			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
			}
		});
	}
	//SSO授权回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
}
