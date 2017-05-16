package net.passone.hrd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.passone.hrd.adapter.HttpHelper;
import net.passone.hrd.adapter.WaitDialog;
import net.passone.hrd.common.CUser;
import net.passone.hrd.common.Constants;
import net.passone.hrd.common.Environments;
import net.passone.hrd.common.IntentModelActivity;
import net.passone.hrd.common.Util;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class BLearningActivity extends IntentModelActivity {
	Cursor cursor;
	String uid,_url;
	WebView mWebView;
	private final Handler handler = new Handler();
	ImageButton btn_write,btn_help,btn_list;
	AlertDialog alert;
	private Map<String, Object> map = new HashMap<String, Object>();
	protected static WaitDialog dialog;
	private ProgressDialog progressDialog;
	BLearningActivity self=null;
	String menu;
	CookieManager cookieManager;
	HttpHelper hh=new HttpHelper();
	Context context;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self=this;
		context=this;
		Intent i=getIntent();
		setContentView(R.layout.titlewebview);
		btn_list = (ImageButton)findViewById(R.id.leftImageButton);
		btn_write=(ImageButton)findViewById(R.id.rightImageButton);
		//		setTitleButton(btn_write,R.drawable.write_off);
		//		setTitleButton(btn_list,R.drawable.back);

		//		_url="http://api.passone.net/cyber/01.mp4";
		_url=Constants.baseUrl+Constants.blearningUrl;
		setTitle("");

		CookieSyncManager.createInstance(this);
		cookieManager=CookieManager.getInstance();

		initialize();

		initWebview();


	}
	@Override
	public void onClick(View view) {
		int id=view.getId();




	}

	public void initWebview() {
		// TODO Auto-generated method stub

		cookieManager = CookieManager.getInstance();
		List<Cookie> cookies=hh.getCookies();
		Cookie sessionInfo = null;
		Log.d("passone","uid="+CUser.mno);

		for (Cookie cookie : cookies ) {
			sessionInfo = cookie;
			String cookieString = sessionInfo.getName() + "="
					+ sessionInfo.getValue() + "; path="
					+ sessionInfo.getPath()+"; domain="+sessionInfo.getDomain();
			Log.d("passone","s"+cookieString);
			cookieManager.setCookie(Constants.cookieUrl,cookieString);

			CookieSyncManager.getInstance().sync();
		}

		CookieSyncManager.getInstance().startSync();
		//	Log.d("passone",url);
		//    	String url="http://www.naver.com";
		loadWebView(_url);

	}
	public void initialize() {
		// TODO Auto-generated method stub
		DefaultHttpClient client = new DefaultHttpClient();

	}



	public void putAll(Map<? extends String, ? extends Object> arg0) {
		map.putAll(arg0);
	}
	private void loadWebView(String url) {
		// TODO Auto-generated method stub
		mWebView = (WebView)findViewById(R.id.webview); 


		//		mWebView.clearCache(true);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebView.getSettings().setJavaScriptEnabled(true);  // ???��?? ??????????????
		mWebView.addJavascriptInterface(new AndroidBridge(), "android");
		// Bridge �ν��Ͻ� ���
		//    mWebView.addJavascriptInterface(new AndroidBridge(), "CSAcademy");
		mWebView.setHorizontalScrollBarEnabled(true); // ���� scroll ����
		mWebView.setVerticalScrollBarEnabled(false); // ���� scroll ����
		mWebView.getSettings().setBuiltInZoomControls(false);
		mWebView.setWebViewClient(new webViewClient());  // WebViewClient ????         
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsConfirm(WebView view, String url, String message, final JsResult result)
			{
				new AlertDialog.Builder(getParent())
				.setTitle(getText(R.string.app_name))
				.setMessage(message)
				.setPositiveButton(android.R.string.ok,
						new AlertDialog.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						result.confirm();
					}
				})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						result.cancel();
					}
				})
						.setCancelable(false).create()
				.show();

				return true;
			};
			public boolean onJsAlert(WebView view, String url, final String message, JsResult result)
			{
				final JsResult r = result;
				if(null == self)return false;

				new AlertDialog.Builder(getParent())
				.setMessage(message).setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						if(message.contains("�α���")|| message.contains("Session"))
						{//������ ����?�� ��� ��Ű���� �ʱ�ȭ �� �� ����
							CUser.mno="";
							cookieManager=CookieManager.getInstance();
							cookieManager.removeAllCookie();
							hh.clearCookie();
							mWebView.stopLoading();
							logout();

						}
						r.confirm();
					}
				}).setCancelable(false).create().show();
				return true;
			}
			public void onProgressChanged(WebView view, int newProgress) {


			}
		});
		mWebView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivity(intent);

			}
		});

		mWebView.loadUrl(url);  

	}
	private class webViewClient extends WebViewClient { 
		@Override 
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();			// test for connection
			if(activeNetwork!=null ) {
				if (activeNetwork.getType() != ConnectivityManager.TYPE_WIFI && activeNetwork.getType() != ConnectivityManager.TYPE_MOBILE) {
					Util.showErrPage(context, url, view);
//					view.stopLoading();
				} else {
					if (url.startsWith("tel:")) {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(intent);
					} else {
						_url = url;
						if (url.endsWith(".mp4")) {

							Intent i = new Intent(Intent.ACTION_VIEW);
							Uri uri = Uri.parse(url);
							i.setDataAndType(uri, "video/mp4");
							if (Util.isWifiConnected(self)) {
								startActivity(i);
							} else {
								if (Environments.ALLOW_3G) {
									startActivity(i);

								} else {
									Util.alertYes(getParent(), R.string.video_3g_alert);
								}
							}
						} else
							view.loadUrl(url);

					}
				}
			}
			else {
				Util.showErrPage(context,url,view);

			}
			return true; 
		} 

		public void onLoadResource (WebView view, String url) {
			try{
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(getParent());
					progressDialog.setMessage("�ε����Դϴ�...");
					progressDialog.show();
				}
			}catch(Exception e){}
			(new Handler()).postDelayed(new Runnable() {

				public void run() {		

					try{
						if (progressDialog != null&&progressDialog.isShowing()) {
							progressDialog.dismiss();
							progressDialog = null;
						}
					}catch(Exception e){}
				}
			}, 2000);	/* 2 seconds */
		}

		public void onPageFinished(WebView view, String url) {
			try{
				if (progressDialog != null&&progressDialog.isShowing()) {
					progressDialog.dismiss();
					progressDialog = null;
				}

			}catch(Exception e){}
			CookieSyncManager.getInstance().sync();

		}


		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Util.showErrPage(context,failingUrl,view);



		}

	}

	public void onStart() { 

		super.onStart(); 

		CookieSyncManager.createInstance(this);

	}   

	@Override

	public void onPause(){

		super.onPause();

		CookieSyncManager.getInstance().stopSync();

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(alert!=null)
			alert.dismiss();
		super.onResume();

		// TODO Auto-generated method stub

		super.onResume();
	}
	private class AndroidBridge {
		@JavascriptInterface
		public void callAndroid(final String arg) { // must be final
			handler.post(new Runnable() {
				public void run() {
					Log.d("passone", "setMessage("+arg+")");
					if(arg.equals("home"))
					{
						finish();
						((Activity)getParent()).startActivity(new Intent(self,MainActivity.class));

					}
				}
			});
		}
		@JavascriptInterface
		public void goStudy(final String url,final String title) { // must be final
			handler.post(new Runnable() {
				public void run() {
					Log.d("passone", "setMessage("+url+","+title+")");
					if(Util.isWifiConnected(self))
					{
						goPlayVideo(url,title);
					}
					else
					{
						if(Environments.ALLOW_3G)
						{
							goPlayVideo(url,title);

						}
						else
						{
							Util.alertYes(getParent(), R.string.video_3g_alert);
						}
					}


				}
			});
		}
	}
	@Override
	public void onBackPressed() 
	{
		//				XActivityGroup parent = ((XActivityGroup)getParent());
		//				parent.group.back();
		finish();
		((Activity)getParent()).startActivity(new Intent(self,MainActivity.class));

		return;
	}
	@Override
	public void onConfigurationChanged (Configuration newConfig)
	{

		super.onConfigurationChanged(newConfig);

	}
	public void goPlayVideo(String url, String title)
	{
		Intent i=new Intent(self,MyClassPopActivity.class);
		i.putExtra("url", url);
		i.putExtra("title", title);
		((Activity)getParent()).startActivity(i);
	}
}
