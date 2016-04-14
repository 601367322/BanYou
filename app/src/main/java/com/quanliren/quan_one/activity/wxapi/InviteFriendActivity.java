package com.quanliren.quan_one.activity.wxapi;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity
public class InviteFriendActivity extends BaseActivity {
	private final UMSocialService mController = UMServiceFactory
			.getUMSocialService(Constants.DESCRIPTOR);
	@Extra
	String inviteCode;
	@ViewById
	TextView invite_code;
	@ViewById
	ImageView top;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.fx);
		setTitleTxt("邀请好友");
		// 配置需要分享的相关平台,并设置分享内容
		configPlatforms(mController, INVITE);
		invite_code.setText(inviteCode);
		Bitmap loadedImage = ((BitmapDrawable) top.getDrawable()).getBitmap();
		int swidth = getResources().getDisplayMetrics().widthPixels;
		float widthScale = (float) swidth / (float) loadedImage.getWidth();
		int height = (int) (widthScale * loadedImage.getHeight());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(swidth, height);
		top.setLayoutParams(lp);
		top.setImageBitmap(loadedImage);
	}
	@Click(R.id.copy)
	void copyInviteCode(View view){
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText("伴游注册邀请码："+inviteCode);
		showCustomToast("邀请码复制成功");
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
