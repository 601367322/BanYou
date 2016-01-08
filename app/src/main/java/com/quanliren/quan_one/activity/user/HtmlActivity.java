package com.quanliren.quan_one.activity.user;

import android.webkit.WebSettings;
import android.webkit.WebView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.banyou_html)
public class HtmlActivity extends BaseActivity{

	@ViewById(R.id.webview)
	WebView webview;
	@Extra
	public String url;
	@Extra
	public String title_txt;

	@Override
	public void init() {
		super.init();
		setTitleTxt(title_txt);
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		if(!"".equals(url)&&url!=null){
			webview.loadUrl(url);
		}
	}
}
