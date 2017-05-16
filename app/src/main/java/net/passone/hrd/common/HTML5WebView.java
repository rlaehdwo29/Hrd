package net.passone.hrd.common;



import java.util.List;

import net.passone.hrd.LoginActivity;
import net.passone.hrd.R;
import net.passone.hrd.adapter.HttpHelper;
import net.passone.hrd.video.YoondiskPlayerActivity;

import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class HTML5WebView extends WebView {
        
        private Context                                                         mContext;
        private MyWebChromeClient                                       mWebChromeClient;
        private View                                                            mCustomView;
        private FrameLayout                                                     mCustomViewContainer;
        private WebChromeClient.CustomViewCallback      mCustomViewCallback;
        
        private FrameLayout                                                     mContentView;
        private LinearLayout                                                     mBrowserFrameLayout;
        private FrameLayout                                                     mLayout;
    	CookieManager cookieManager;
    	HttpHelper hh=new HttpHelper();
    	private ProgressDialog progressDialog;
    	private final Handler handler = new Handler();
    	RelativeLayout titlebar;

    static final String LOGTAG = "HTML5WebView";
            
        private void init(Context context) {
                mContext = context;             
                Activity a = (Activity) mContext;
                
                mLayout = new FrameLayout(context);
                
                mBrowserFrameLayout = (LinearLayout) LayoutInflater.from(a).inflate(R.layout.custom_screen, null);
                mContentView = (FrameLayout) mBrowserFrameLayout.findViewById(R.id.main_content);
                mCustomViewContainer = (FrameLayout) mBrowserFrameLayout.findViewById(R.id.fullscreen_custom_content);
                titlebar=(RelativeLayout)mBrowserFrameLayout.findViewById(R.id.titleBar);
                mLayout.addView(mBrowserFrameLayout, COVER_SCREEN_PARAMS);
                CookieSyncManager.createInstance(context);
                cookieManager=CookieManager.getInstance();
                if(CUser.mno.equals(""))
                {
                  cookieManager.removeAllCookie();
            		hh.clearCookie();	
                }
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
    			  cookieManager.setCookie(sessionInfo.getDomain(),cookieString);
    
    			  CookieSyncManager.getInstance().sync();
    			 }
    
    		    	CookieSyncManager.getInstance().startSync();
                mWebChromeClient = new MyWebChromeClient();

                addJavascriptInterface(new AndroidBridge(), "android");
          
               setHorizontalScrollBarEnabled(false); // ?�로 scroll ?�거
                setVerticalScrollBarEnabled(false); // �?�� scroll ?�거
               setWebChromeClient(mWebChromeClient);
            
            setWebViewClient(new MyWebViewClient());
               
            // Configure the webview
            WebSettings s = getSettings();
            s.setDomStorageEnabled(true); // I think you will need this one
            s.setPluginState(WebSettings.PluginState.ON);
            s.setBuiltInZoomControls(true);
            this.setInitialScale(1);
            s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            s.setUseWideViewPort(true);
            s.setLoadWithOverviewMode(true);
            s.setSaveFormData(true);
            s.setJavaScriptEnabled(true);
            s.setDefaultTextEncodingName("utf-8");
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN){
                try {
                    s.setAllowUniversalAccessFromFileURLs(true);
                    s.setAllowFileAccessFromFileURLs(true);
                } catch(NullPointerException e) {
                }
            }
            s.setMediaPlaybackRequiresUserGesture(false);
            s.setCacheMode(WebSettings.LOAD_DEFAULT);
            
            mContentView.addView(this);
        }

        public HTML5WebView(Context context) {
                super(context);
                init(context);
        }

        public HTML5WebView(Context context, AttributeSet attrs) {
                super(context, attrs);
                init(context);
        }

        public HTML5WebView(Context context, AttributeSet attrs, int defStyle) {
                super(context, attrs, defStyle);
                init(context);
        }
        
        public FrameLayout getLayout() {
                return mLayout;
        }
        
    public boolean inCustomView() {
                return (mCustomView != null);
        }
    
    public void hideCustomView() {
                mWebChromeClient.onHideCustomView();
        }
    


    private class MyWebChromeClient extends WebChromeClient {
                private Bitmap          mDefaultVideoPoster;
                private View            mVideoProgressView;
        
        @Override
                public void onShowCustomView(View view, CustomViewCallback callback)
                {
                        //Log.i(LOGTAG, "here in on ShowCustomView");
                HTML5WebView.this.setVisibility(View.GONE);
                titlebar.setVisibility(View.GONE);
                // if a view already exists then immediately terminate the new one
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                
                mCustomViewContainer.addView(view);
                mCustomView = view;
                mCustomViewCallback = callback;
                mCustomViewContainer.setVisibility(View.VISIBLE);
                }
                
                @Override
                public void onHideCustomView() {
                        
                        if (mCustomView == null)
                                return;        
                        
                        // Hide the custom view.
                        mCustomView.setVisibility(View.GONE);
                        
                        // Remove the custom view from its container.
                        mCustomViewContainer.removeView(mCustomView);
                        mCustomView = null;
                        mCustomViewContainer.setVisibility(View.GONE);
                        mCustomViewCallback.onCustomViewHidden();
                        titlebar.setVisibility(View.VISIBLE);
                        HTML5WebView.this.setVisibility(View.VISIBLE);
                        
                //Log.i(LOGTAG, "set it to webVew");
                }
                
                @Override
                public Bitmap getDefaultVideoPoster() {
                        //Log.i(LOGTAG, "here in on getDefaultVideoPoster");    
                        if (mDefaultVideoPoster == null) {
                                mDefaultVideoPoster = BitmapFactory.decodeResource(
                                                getResources(), R.drawable.ic_launcher);
                    }
                        return mDefaultVideoPoster;
                }
                
                @Override
                public View getVideoLoadingProgressView() {
                        //Log.i(LOGTAG, "here in on getVideoLoadingPregressView");
                        
                if (mVideoProgressView == null) {
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    mVideoProgressView = inflater.inflate(R.layout.video_loading_progress, null);
                }
                return mVideoProgressView; 
                }
        
         @Override
         public void onReceivedTitle(WebView view, String title) {
            ((Activity) mContext).setTitle(title);
         }

         @Override
         public void onProgressChanged(WebView view, int newProgress) {
                 ((Activity) mContext).getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress*100);
         }

         public boolean onJsAlert(WebView view, String url, final String message, JsResult result)
   	   {
   	    //if(null==view)return;
   	    final JsResult r = result;
   	    // return super.onJsAlert(view, url, message, result);
   	    if(null == mContext)return false;
   	    
   	        new AlertDialog.Builder(mContext)//mContext.getApplicationContext());
   	    /* .setTitle("AlertDialog") */
   	    .setMessage(message).setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener()
   	    {
   	     public void onClick(DialogInterface dialog, int which)
   	     {
   	    	if(message.equals("Session expired!"))
        	{
   	    		((Activity)mContext).finish();
   	    		CUser.mno="";
			    	
			    	cookieManager=CookieManager.getInstance();
			    	cookieManager.removeAllCookie();
			    	hh.clearCookie();
			    	mContext.startActivity(new Intent(mContext,LoginActivity.class));
        		
        	}
   	      r.confirm();
   	     }
   	    }).setCancelable(false).create().show();
   	    return true;
   	   }
   	    @Override
   	    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result)
   	    {
   	        new AlertDialog.Builder(mContext)
   	            .setTitle(Constants.app_name)
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
     
 
    }
        
        private class MyWebViewClient extends WebViewClient {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(LOGTAG, "shouldOverrideUrlLoading: "+url);
                ConnectivityManager cm = (ConnectivityManager) mContext
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();			// test for connection
                if(activeNetwork!=null ) {
                    if (activeNetwork.getType() != ConnectivityManager.TYPE_WIFI && activeNetwork.getType() != ConnectivityManager.TYPE_MOBILE) {
                        Util.showErrPage(mContext, url, view);
//					view.stopLoading();
                    } else {
                        if (url.startsWith("tel:")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            mContext.startActivity(intent);
                        } else {

                            Log.d("passone", url);

                            view.loadUrl(url);


                        }
                    }
                }
                else {
                    Util.showErrPage(mContext,url,view);

                }
                return true;
            }

	        public void onLoadResource (WebView view, String url) {
	        	try{
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(mContext);
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
                  Util.showErrPage(mContext,failingUrl,view);

	          }
        }
        
        static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS =
        new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		 private class AndroidBridge {
             @JavascriptInterface
             public void callAndroid(final String arg) { // must be final
	              handler.post(new Runnable() {
	                  public void run() {
	                   
	                    
	                  }
	              });
	          }
             @JavascriptInterface
             public void dl_share(final String site_id,final String cntnt_cd,final String serizno) { // must be final
	              handler.post(new Runnable() {
	                  public void run() {
//	                	  Intent intent = new Intent(mContext,DLShareActivity.class);
//	                	  Intent intent = new Intent(mContext,StreamWriteActivity.class);
//	                	  intent.putExtra("site_id",site_id);
//	                	  intent.putExtra("cntnt_cd",cntnt_cd);
//	                	  intent.putExtra("serizno",serizno);
//
//
//	                	  mContext.startActivity(intent);
//	                	  ((Activity)mContext).finish();

	                    
	                  }
	              });
	          }
             @JavascriptInterface
             public void dl_ebookview(final String url) { // must be final
	              handler.post(new Runnable() {
	                  public void run() {
	                	  mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));				


	                    
	                  }
	              });
	          }
			 public void dl_movieview(final String url) { // must be final
	              handler.post(new Runnable() {
	                  public void run() {
//	                	  Intent intent = new Intent(mContext,DLShareActivity.class);
	                	  Intent intent = new Intent(mContext,YoondiskPlayerActivity.class);
	                	  intent.putExtra("url",url);
	                	  intent.putExtra("currenttime", 0);


	                	  mContext.startActivity(intent);


//	                	  mContext.startActivity(intent);
//	                	  ((Activity)mContext).finish();

	                    
	                  }
	              });
	          }
	        }
}

