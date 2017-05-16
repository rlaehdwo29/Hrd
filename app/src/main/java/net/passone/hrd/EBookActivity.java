package net.passone.hrd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.passone.hrd.adapter.HttpHelper;
import net.passone.hrd.adapter.WaitDialog;
import net.passone.hrd.common.CUser;
import net.passone.hrd.common.Constants;
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
import android.widget.RelativeLayout;

public class EBookActivity extends IntentModelActivity {
	Cursor cursor;
	String uid,_url;
	WebView mWebView;
	private final Handler handler = new Handler();
	ImageButton btn_write,btn_help,btn_close;
	AlertDialog alert;
	private Map<String, Object> map = new HashMap<String, Object>();
	protected static WaitDialog dialog;
	private ProgressDialog progressDialog;
	EBookActivity self=null;
	String mode="e";
	CookieManager cookieManager;
	HttpHelper hh=new HttpHelper();
	RelativeLayout titlebar;
	Context context;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self=this;
		context=this;
		Intent i=getIntent();
		Bundle bun = getIntent().getExtras();
		if(bun != null)
		{
			if(bun.getString("mode").length()>0)
				mode= bun.getString("mode");
		}
		setContentView(R.layout.wview);
		
		CookieSyncManager.createInstance(this);
		cookieManager=CookieManager.getInstance();
		if(mode.equals("e"))
		{
			_url=Constants.baseUrl+Constants.ebookUrl;
		}
		else
		{
			_url=Constants.baseUrl+Constants.basicbookUrl;

		}
		//	setScrap();
		initialize();

		initWebview();


	}
	@Override
	public void onClick(View view) {
		int id=view.getId();
		finish();



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
		mWebView.getSettings().setJavaScriptEnabled(true);  // ���信�� �ڹٽ�ũ��Ʈ���డ��
		mWebView.addJavascriptInterface(new AndroidBridge(), "android");
		// Bridge 인스턴스 등록
		//    mWebView.addJavascriptInterface(new AndroidBridge(), "CSAcademy");
		mWebView.setHorizontalScrollBarEnabled(true); // 세로 scroll 제거
		mWebView.setVerticalScrollBarEnabled(false); // 가로 scroll 제거
		mWebView.getSettings().setBuiltInZoomControls(false);
		mWebView.setWebViewClient(new webViewClient());  // WebViewClient ����         
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsConfirm(WebView view, String url, String message, final JsResult result)
			{
				new AlertDialog.Builder(self)
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
	      	    
	      	        new AlertDialog.Builder(self)
	      	    .setMessage(message).setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener()
	      	    {
	      	     public void onClick(DialogInterface dialog, int which)
	      	     {
	      	    	if(message.contains("로그인")|| message.contains("Session"))
		           	{//세션이 만료됬을 경우 쿠키세션 초기화 후 앱 종료
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
					progressDialog = new ProgressDialog(self);
					progressDialog.setMessage("로딩중입니다...");
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
						startActivity(new Intent(self,MainActivity.class));

					}
				}
			});
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		startActivity(new Intent(self,MainActivity.class));

	}
}
