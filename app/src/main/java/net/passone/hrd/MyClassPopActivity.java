package net.passone.hrd;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.passone.hrd.adapter.DBmanager;
import net.passone.hrd.adapter.HttpHelper;
import net.passone.hrd.adapter.WaitDialog;
import net.passone.hrd.common.CUser;
import net.passone.hrd.common.Constants;
import net.passone.hrd.common.Environments;
import net.passone.hrd.common.HTML5WebView;
import net.passone.hrd.common.IntentModelActivity;
import net.passone.hrd.common.Util;
import net.passone.hrd.video.YoondiskPlayerActivity;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyClassPopActivity extends IntentModelActivity {
	Cursor cursor;
	String uid,_url;
	HTML5WebView mWebView;
	private final Handler handler = new Handler();
	ImageButton btn_write,btn_help,btn_close;
	AlertDialog alert;
	private Map<String, Object> map = new HashMap<String, Object>();
	protected static WaitDialog dialog;
	private ProgressDialog progressDialog;
	MyClassPopActivity self=null;
	String str_url="",title="",vurl="",classcount="",part="",page="",contentskey="",studykey="";
	CookieManager cookieManager;
	HttpHelper hh=new HttpHelper();
	RelativeLayout titlebar;
	boolean isFinish=true;
	Context context;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self=this;
		context=this;
		Intent i=getIntent();
		Bundle bun = getIntent().getExtras();
		if(bun != null)
		{
			if(bun.getString("url").length()>0)
				str_url= bun.getString("url");
			if(bun.getString("title").length()>0)
				title= bun.getString("title");
			classcount = bun.getString("classcount");
			part=bun.getString("part");
			page=bun.getString("page");
			contentskey=bun.getString("contentskey");
			studykey=bun.getString("studykey");
		}
		_url=str_url;

		mWebView = new HTML5WebView(this);
//		if (savedInstanceState != null) {
//			mWebView.restoreState(savedInstanceState);
//		} else {
//			Util.debug(_url);
//			mWebView.loadUrl(_url);
//		}
		setContentView(mWebView.getLayout());
		titlebar=(RelativeLayout)findViewById(R.id.titleBar);
		titlebar.setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.titleText)).setText(title);
		btn_close = (ImageButton)findViewById(R.id.rightImageButton);
		setTitleButton(btn_close,R.drawable.btn_close);
		CookieSyncManager.createInstance(this);
		cookieManager=CookieManager.getInstance();

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

//		mWebView.setInitialScale(1);
//		mWebView.getSettings().setLoadWithOverviewMode(true);
//		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setJavaScriptEnabled(true);

		//		mWebView.clearCache(true);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebView.getSettings().setJavaScriptEnabled(true);  // ���信�� �ڹٽ�ũ��Ʈ���డ��
		mWebView.addJavascriptInterface(new AndroidBridge(), "android");
		// Bridge ?�스?�스 ?�록
		//    mWebView.addJavascriptInterface(new AndroidBridge(), "CSAcademy");
		mWebView.setHorizontalScrollBarEnabled(true); // ?�로 scroll ?�거
		mWebView.setVerticalScrollBarEnabled(false); // 가�?scroll ?�거
		mWebView.getSettings().setBuiltInZoomControls(false);
		mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
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
						{//?�션??만료?�을 경우 쿠키?�션 초기??????종료
							CUser.mno="";
							cookieManager=CookieManager.getInstance();
							cookieManager.removeAllCookie();
							hh.clearCookie();
							mWebView.stopLoading();
							finish();
						}
						r.confirm();
					}
				}).setCancelable(false).create().show();
				return true;
			}
			public void onProgressChanged(WebView view, int newProgress) {


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
						//
						Log.d("passone", "pop view:"+url);
						isFinish = true;
						CUser.finish = false;
						CUser.currenttime = 0;
						vurl = "";
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
			//			(new Handler()).postDelayed(new Runnable() {
			//
			//				public void run() {		
			//
			//					try{
			//						if (progressDialog != null&&progressDialog.isShowing()) {
			//							progressDialog.dismiss();
			//							progressDialog = null;
			//						}
			//					}catch(Exception e){}
			//				}
			//			}, 2000);	/* 2 seconds */
		}

		public void onPageFinished(WebView view, String url) {
			try{
				if (progressDialog != null&&progressDialog.isShowing()) {
					progressDialog.dismiss();
					progressDialog = null;
				}

				//				if(vurl.length()>0)
				//
				//				{
				//					if(url.endsWith(".mp4")) {   
				//						Intent i = new Intent(Intent.ACTION_VIEW);     
				//						Uri uri = Uri.parse(url);             
				//						i.setDataAndType(uri, "video/mp4");          
				//						startActivity(i);        
				//						Log.d("passone",url);
				//						vurl="";
				//					}
				//					else
				//					{
				//						mWebView.loadUrl(vurl);
				//					}
				//
				//				}

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
	protected void onDestroy() {
		mWebView.loadUrl("");
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(alert!=null)
			alert.dismiss();
		super.onResume();
		if(CUser.lasttime!=0)
		{
			Log.d("passone","javascript:fnsavesec1('"+CUser.lasttime+"')");
//			loadWebView("javascript:fnsavesec1('"+CUser.lasttime+"')");

		}
		//			mWebView.loadUrl("javascript:fnsavesec1('"+CUser.lasttime+"')");
		Log.d("passone","resume:"+CUser.currenttime);
		if(!isFinish) //finish가 N일 경우
		{
			String str_finish="N";
			if(CUser.finish){
				str_finish="Y";
				isFinish=true;
			}
			else
				str_finish="N";

//			loadWebView("javascript:fnMarkerWrite(0,'"+str_finish+"','"+CUser.currenttime+"')");
			
		}
		//			mWebView.loadUrl("javascript:fnsavesec1('"+CUser.lasttime+"')");
		CUser.lasttime=0;
		if(CUser.callbackUrl.length()>0)
			mWebView.loadUrl(CUser.callbackUrl);
		CUser.callbackUrl="";
		//		mWebView=new WebView(self);
		//		loadWebView(_url);
		// TODO Auto-generated method stub

		super.onResume();
	}
	private class AndroidBridge {
		@JavascriptInterface
		public void callAndroid(final String arg) { // must be final
			handler.post(new Runnable() {
				public void run() {
					Log.d("passone", "setMessage("+arg+")");
					if(arg.equals("close"))
					{
						finish();
					}
				}
			});
		}
		@JavascriptInterface
		public void goVideo(final String url, final String title, final String current, final String total, final String classkey,final String str_url) { // must be final
			Log.d("passone","여기야2?");

			final String filename=classkey+"_"+part+"_"+page+".mp4";

			handler.post(new Runnable() {
				public void run() {
					Log.d("passone", "setMessage("+url+")");
					if(vurl.equals(url))
					{
						Intent intent=new Intent(self,YoondiskPlayerActivity.class);
						intent.putExtra("url", vurl.replace("m06", "s06"));
						intent.putExtra("title", title);
						intent.putExtra("current", current);
						intent.putExtra("total", total);
						intent.putExtra("furl",str_url);
						intent.putExtra("classkey", classkey);
						intent.putExtra("classcount", classcount);
						intent.putExtra("part", part);
						intent.putExtra("page", page);
						intent.putExtra("contentskey", contentskey);
						intent.putExtra("studykey", studykey);

						File dfile = new File(Util.getDownloadPath(filename));
						if(dfile.isFile() && dfile.exists())
						{
							intent.putExtra("url", Util.getDownloadPath(filename));
							startActivity(intent);
						}
						else //스트리밍
						{
							if(Util.isWifiConnected(self))
							{
								startActivity(intent);
							}
							else
							{
								if(Environments.ALLOW_3G)
								{
									startActivity(intent);

								}
								else
								{
									Util.alertYes(self, R.string.video_3g_alert);
								}
							}
						}
				
						vurl="";
					}
					else
					{
						vurl=url;
						mWebView.reload();

					}
					
					

				}
			});
			
		}
		
		//현재사용중임
		@JavascriptInterface
		public void goVideo(final String url, final String title, final String current, final String total, final String classkey,final String str_url,
				final String marker, final String finish, final String markerTime, final String control) { // must be final
			Log.d("passone","여기야?");
			handler.post(new Runnable() {
				public void run() {
					Log.d("passone", "setMessage("+url+" / "+finish+" / "+markerTime+" / "+control+")");
//					if(vurl.equals(url))
//					{
						Intent intent=new Intent(self,YoondiskPlayerActivity.class);
						intent.putExtra("url", url.replace("m06", "s06"));
						intent.putExtra("title", title);
						intent.putExtra("current", current);
						intent.putExtra("total", total);
						intent.putExtra("furl",str_url);
						intent.putExtra("classkey", classkey);
						intent.putExtra("finish", finish);
						intent.putExtra("markerTime", markerTime);

						intent.putExtra("classcount", classcount);
						intent.putExtra("part", part);
						intent.putExtra("page", page);
						intent.putExtra("contentskey", contentskey);
						intent.putExtra("studykey", studykey);
						
							Log.d("passone","CUerCurrent"+CUser.currenttime);
//							if(CUser.currenttime>0)
//							{
//								intent.putExtra("markerTime", String.valueOf(CUser.currenttime));
								CUser.currenttime=0;
//							}
//							else
//							{
								intent.putExtra("markerTime", markerTime);
								
//							}
						
						intent.putExtra("control", control);
						if(finish.equals("Y") )
						{
							isFinish=true;
						}
						else
						{
							if(!CUser.finish)
								isFinish=false;
						}
						String filename=classkey+"_"+part+"_"+page+".mp4";
						File dfile = new File(Util.getDownloadPath(filename));
						if(dfile.isFile() && dfile.exists())
						{
							intent.putExtra("url", Util.getDownloadPath(filename));
							startActivity(intent);
						}
						else //스트리밍
						{
							if(Util.isWifiConnected(self))
							{
								startActivity(intent);
								CUser.currenttime=0;
							}
							else
							{
								if(Environments.ALLOW_3G)
								{
									Util.ToastMessage(self, "3G/LTE 네트워크에서 재생합니다.");
									startActivity(intent);
									CUser.currenttime=0;
								}
								else
								{
									Util.alertYes(self, R.string.video_3g_alert);
								}
							}
						}
					
						vurl="";
//					}
//					else
//					{
//						vurl=url;
//						mWebView.reload();
//					}
					
				}
			});
		}
		
		@JavascriptInterface
		public void goVideo(final String url, final String title, final String current, final String total, final String classkey,final String classcount,
				final String studykey, final String sns , final String marker , final String finish, final String markerTime, final String control,final String part,final String page,final String sampleyn) { // must be final
			Log.d("passone","여기야3?");
			final String filename=classkey+"_"+part+"_"+page+".mp4";
			DBmanager db_manager = new DBmanager(self,"UserInfo.db");
			SQLiteDatabase db=db_manager.getReadableDatabase();
			Cursor mDownloadCursor = db.rawQuery("select filename from download where filename=?",new String[]{filename});
			final int nDownloadCnt = mDownloadCursor.getCount();
			Log.d("passone","nDownloadCnt="+nDownloadCnt);
			mDownloadCursor.close();
			if(db.isOpen()) db.close();
			if(db_manager!=null){
				db_manager.close();
			}
			handler.post(new Runnable() {
				public void run() {
					Log.d("passone", "setMessage("+url+" / "+studykey+" / "+markerTime+" / "+control+")");
//					if(vurl.equals(url.replace("m06", "s06")))
//					{
						Intent intent=new Intent(self,YoondiskPlayerActivity.class);
						intent.putExtra("title", title);
						intent.putExtra("current", current);
						intent.putExtra("total", total);
						intent.putExtra("classkey", classkey);
						intent.putExtra("studykey", studykey);
						intent.putExtra("part", part);
						intent.putExtra("page", page);
						intent.putExtra("classcount", classcount);
						intent.putExtra("contentskey", contentskey);
						intent.putExtra("studykey", studykey);
						intent.putExtra("finish", finish);
						intent.putExtra("sampleyn", sampleyn);

						Log.d("passone","CUerCurrent"+CUser.currenttime);
						//							if(CUser.currenttime>0)
						//							{
						//								intent.putExtra("markerTime", String.valueOf(CUser.currenttime));
						CUser.currenttime=0;
						//							}
						//							else
						//							{
						intent.putExtra("markerTime", markerTime);

						//							}

						intent.putExtra("control", control);
						if(finish.equals("Y") )
						{
							isFinish=true;
						}
						else
						{
							if(!CUser.finish)
								isFinish=false;
						}

						File dfile = new File(Util.getDownloadPath(filename));
					Log.d("passone","nDownloadCnt=333"+nDownloadCnt);
						if(dfile.isFile() && dfile.exists() && nDownloadCnt>0)
						{
							Log.d("passone","nDownloadCnt=444"+nDownloadCnt);
							intent.putExtra("url", "file://"+Util.getDownloadPath(filename));
							startActivity(intent);
						}
						else //스트리밍
						{
							vurl=url.replace("m06", "s06");
							Log.d("passone","nDownloadCnt=555"+nDownloadCnt);
							intent.putExtra("url", vurl.trim());
							if(Util.isWifiConnected(self))
							{
								startActivity(intent);
								CUser.currenttime=0;
							}
							else
							{
								if(Environments.ALLOW_3G)
								{
									Util.ToastMessage(self, "3G/LTE 네트워크에서 재생합니다.");
									startActivity(intent);
									CUser.currenttime=0;
								}
								else
								{
									Util.alertYes(self, R.string.video_3g_alert);
								}
							}

						}
						vurl="";
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
		//		XActivityGroup parent = ((XActivityGroup)getParent());
		//		parent.group.back();
		finish();

		return;
	}
}
