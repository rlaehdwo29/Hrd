package net.passone.hrd;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.passone.hrd.adapter.Api;
import net.passone.hrd.adapter.DBmanager;
import net.passone.hrd.adapter.HttpHelper;
import net.passone.hrd.adapter.OnResponseListener;
import net.passone.hrd.adapter.WaitDialog;
import net.passone.hrd.common.CUser;
import net.passone.hrd.common.CVersion;
import net.passone.hrd.common.Constants;
import net.passone.hrd.common.Environments;
import net.passone.hrd.common.IntentModelActivity;
import net.passone.hrd.common.Util;
import net.passone.hrd.container.Version;
import net.passone.hrd.video.YoondiskPlayerActivity;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

public class MainActivity extends IntentModelActivity implements OnClickListener, OnResponseListener{
	Cursor cursor;
	String uid,_url;
	WebView mWebView;
	private final Handler handler = new Handler();
	ImageButton btn_write,btn_help,btn_list;
	AlertDialog alert;
	private Map<String, Object> map = new HashMap<String, Object>();
	protected static WaitDialog dialog;
	MainActivity self=null;
	String menu;
	CookieManager cookieManager;
	private ProgressDialog progressDialog;
	long backKeyPressedTime=0;
	private String[] szUrlArr = {Constants.baseUrl+"/board/notice/list.asp",Constants.baseUrl+"/board/faq/list.asp",Constants.baseUrl+"/board/qa/list.asp",Constants.baseUrl+"/menu/info.asp"};

	Context context;
	DBmanager db_manager;	
	SQLiteDatabase db;
	OnResponseListener callback;  
	HttpHelper hh=new HttpHelper();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self=this;
		context=this;
		callback=this;

		Intent i=getIntent();
		View viewToLoad = LayoutInflater.from(this).inflate(R.layout.wview, null); //탭내 웹뷰의 <select>활성화를 위해
		this.setContentView(viewToLoad);  
		btn_list = (ImageButton)findViewById(R.id.leftImageButton);
		btn_write=(ImageButton)findViewById(R.id.rightImageButton);
		//		setTitleButton(btn_write,R.drawable.write_off);
		//		setTitleButton(btn_list,R.drawable.back);

		//	url=Constants.baseUrl+"index.php";
		_url=Constants.baseUrl+Constants.mainUrl;
		Log.d("passone","url="+_url);

		CookieSyncManager.createInstance(this);

		//	setScrap();
		initialize();

		initWebview();


	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
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
			Log.d("passone",_url);
		//    	String url="http://www.naver.com";
		loadWebView(_url);

	}
	public void initialize() {
		// TODO Auto-generated method stub
		DefaultHttpClient client = new DefaultHttpClient();
		_api.version(context, callback);

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
						.setCancelable(false)
				.create()
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
				if(activeNetwork.getType()!= ConnectivityManager.TYPE_WIFI && activeNetwork.getType()!= ConnectivityManager.TYPE_MOBILE)
				{
					Util.showErrPage(context,url,view);
//					view.stopLoading();
				}
				else {

					if (url.startsWith("tel:")) {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(intent);
					} else {
						Log.d("passone", "url2=" + _url);
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
					progressDialog = new ProgressDialog(getParent());
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
				//urls.add(0, url);
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
		Util.debug("onResume-MainActivity");
		super.onResume();
		// TODO Auto-generated method stub

	}
	private class AndroidBridge {
		@JavascriptInterface
		public void callAndroid(final String arg) { // must be final
			handler.post(new Runnable() {
				public void run() {
					Log.d("passone", "call android setMessage(" + arg + ")");
					Intent i = new Intent();
					if (arg.equals("setting")) {
						i = new Intent(self, SettingActivity.class);
						startActivity(i);
						finish();
					}
					else if(arg.equals("tab3"))
					{
						startActivity(new Intent(self,DownloadActivity.class));

					}else {
						mWebView.loadUrl(Constants.baseUrl+"/"+arg);
					}
//					if(arg.equals("tab1"))
//					{
//						i.putExtra("menu", 0);
//					}
//					else if(arg.equals("tab2"))
//					{
//						i.putExtra("menu", 2);
//
//					}
//					else if(arg.equals("tab3"))
//					{
//						i.putExtra("menu", 3);
//
//					}
//					else if(arg.equals("tab4"))
//					{
//						i.putExtra("menu", 4);
//
//					}
//					else if(arg.equals("tab5"))
//					{
//						i.putExtra("menu", 2);
//
//					}
//					//	                     else if(arg.equals("tab7"))
//					//	                     {
//					//	                    	 i.putExtra("menu", 3);
//					//	                     }
//					else if(arg.equals("ebook"))
//					{
//						i = new Intent(self,EBookActivity.class).putExtra("mode", "e");
//					}
//					else if(arg.equals("basicbook"))
//					{
//						i = new Intent(self,EBookActivity.class).putExtra("mode", "b");
//					}

				}
			});
		}
		@JavascriptInterface
		public void goMyclass(final String arg) { // must be final
			handler.post(new Runnable() {
				public void run() {
					Log.d("passone", "go myclass setMessage(" + arg + ")");
					Intent i = new Intent(self, MyClassActivity.class);
					i.putExtra("url", Constants.baseUrl+"/"+arg);
					startActivity(i);
				}
			});
		}
		@JavascriptInterface
		public void fnLaunchExApp (final String scheme,final String url_install) { // must be final
			handler.post(new Runnable() {
				public void run() {
					Intent intent = new Intent();
					Util.debug("url_install:" + url_install);

					String url_scheme = scheme.replace("intent:", "");
					try {

						intent.setAction(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url_scheme));
						self.startActivity(intent);

					} catch (Exception e) {
						Util.ToastMessage(self, "앱을 먼저 설치해주세요.");
						intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_install));
						self.startActivity(intent);
					}
				}
			});
		}
		@JavascriptInterface
		public void goVideo(final String url, final String title, final String current, final String total, final String classkey,final String classcount,
							final String studykey, final String sns , final String marker , final String finish, final String markerTime, final String control,final String part,final String page,final String sampleyn) { // must be final
			handler.post(new Runnable() {
				public void run() {
					Log.d("passone", "setMessage(" + url + " / " + title + " / " + markerTime + " / " + control + ")");
//					if(vurl.equals(url.replace("m06", "s06")))
//					{
					String vTitle="";
					try {
						 vTitle = URLDecoder.decode(title, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(self, YoondiskPlayerActivity.class);

					intent.putExtra("title", title);

					intent.putExtra("current", current);
					intent.putExtra("total", total);
					intent.putExtra("classkey", classkey);
					intent.putExtra("studykey", studykey);
					intent.putExtra("part", part);
					intent.putExtra("page", page);
					intent.putExtra("classcount", classcount);
					intent.putExtra("studykey", studykey);
					intent.putExtra("finish", finish);
					intent.putExtra("sampleyn", sampleyn);

					Log.d("passone", "CUerCurrent" + CUser.currenttime);
					//							if(CUser.currenttime>0)
					//							{
					//								intent.putExtra("markerTime", String.valueOf(CUser.currenttime));
					CUser.currenttime = 0;
					//							}
					//							else
					//							{
					intent.putExtra("markerTime", markerTime);

					//							}

					intent.putExtra("control", control);

					String filename = classkey + "_" + part + "_" + page + ".mp4";
					File dfile = new File(Util.getDownloadPath(filename));
					if (dfile.isFile() && dfile.exists()) {
						intent.putExtra("url", "file://" + Util.getDownloadPath(filename));
						startActivity(intent);
					} else //스트리밍
					{
						intent.putExtra("url", url.trim());
						if (Util.isWifiConnected(self)) {
							startActivity(intent);
							CUser.currenttime = 0;
						} else {
							if (Environments.ALLOW_3G) {
								Util.ToastMessage(self, "3G/LTE 네트워크에서 재생합니다.");
								startActivity(intent);
								CUser.currenttime = 0;
							} else {
								Util.alertYes(self, R.string.video_3g_alert);
							}
						}
					}
//					}
//					else
//					{
//						vurl=url.replace("m06", "s06");
//						mWebView.reload();
//					}

				}
			});
		}

		
	}

	@Override
	public void onBackPressed() 
	{


		boolean bBack = false;
		for(int i=0; i<szUrlArr.length; i++){
			if(mWebView.getUrl().equals(szUrlArr[i])) {
				bBack = true;
				break;

			}
		}
		if(bBack) mWebView.goBack();
		else{
			if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
				backKeyPressedTime = System.currentTimeMillis();
				Util.ToastMessage(this, "'뒤로'버튼을 한번만 더 누르시면 종료됩니다.");
				return;
			}
			if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
				finish();
			}
		}


		return;
	}
	public void onResponseReceived(int api, Object result) {
		// TODO Auto-generated method stub
		Util.debug("Login result    :   "+result);	
		switch(api) {

		case Api.VERSION :
			if (result != null) {
				final Version ver = (Version)result;	    		
				if(ver.servercheck>0)
				{
					String serverStr="";
					if(ver.serverstr.length()>0)
					{
						serverStr=ver.serverstr;
					}
					else 
						serverStr="서버 점검중입니다.";
					Util.alert(self, "New Version", serverStr, "확인", null, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {					
							finish();
						}
					}, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {					
						}
					});
				}
				else {

					if (ver.version.trim().length()>0) {  			
						String str_current=Util.getVersion(self);
						if(Double.parseDouble(ver.version.trim())>Double.parseDouble(str_current))
						{
							String alert_str="현재버전: "+str_current+"\n최신버전: "+ver.version.trim()+"\n\n- What's New -\n"+ver.newcontent;

							Util.alert(self, "New Version", alert_str, "업데이트하기", "취소", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {					
									if(!ver.link.equals(""))
									{
										startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ver.link.replace("\\/", "/"))));		
									}
								}
							}, new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {					
								}
							});

						}


					} else {    			
						Util.ToastMessage(this, "버전 정보 로딩 실패");

					}  
				}
			} else {
				// 수신, 파싱 오류
				Util.PopupMessage(this, getResources().getString(R.string.api_http_alert));
			}			
			break; 



		}		
	}
}
