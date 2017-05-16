package net.passone.hrd.adapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.passone.hrd.common.CUser;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;


public class HttpHelper {

	private static final int POST_TYPE = 1;
	private static final int GET_TYPE = 2;	

	public static final String MIME_FORM_ENCODED = "application/x-www-form-urlencoded";
	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String HTTP_RESPONSE = "HTTP_RESPONSE";
	public static final String HTTP_RESPONSE_ERROR = "HTTP_RESPONSE_ERROR";
	CookieManager cookieManager;

	public static final DefaultHttpClient client;	
	static {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
		params.setParameter(CoreProtocolPNames.USER_AGENT, "Apache-HttpClient/Android");
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(600000));
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, new Integer(600000));		
		params.setParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
		params.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); // Cookie.
		
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		client = new DefaultHttpClient(cm, params);
//		if (!CUser.uid.equals("")) {
//			BasicClientCookie cookie1 = new BasicClientCookie("PASSONE_IDX", CUser.idx);
//			cookie1.setDomain("passone.net");
//			cookie1.setPath("/");
//			client.getCookieStore().addCookie(cookie1);
//
//			BasicClientCookie cookie2 = new BasicClientCookie("PASSONE_UID", CUser.uid);
//			cookie2.setDomain("passone.net");
//			cookie2.setPath("/");
//			client.getCookieStore().addCookie(cookie2);
//			BasicClientCookie cookie3 = new BasicClientCookie("PASSONE_PLATFORM", CUser.platform);
//			cookie3.setDomain("passone.net");
//			cookie3.setPath("/");
//			client.getCookieStore().addCookie(cookie3);
//
//		}
	}			

	/**
	 * Shutdown HTTP client.
	 * Shuts down this connection manager and releases allocated resources. 
	 * This includes closing all connections, whether they are currently used or not.
	 */
	public void shutdownConnectionManager() {
		client.getConnectionManager().shutdown();
	}

	/**
	 * Clear cookie.
	 */
	public void clearCookie() {
		HttpHelper.client.getCookieStore().clear();
	}

	/**
	 * Method: GET
	 * 
	 * @param url
	 * @return The Source code read from this URL (or null)
	 */
	public HttpResponse requestGet(final String url) 
			throws ClientProtocolException, IOException
			{		
		return httpRequest(null, url, null, HttpHelper.GET_TYPE);
			}

	/**
	 * Method: POST
	 * 
	 * @param url
	 * @return The Source code read from this URL (or null)
	 */
	public HttpResponse requestPost(final String url, final Map<String, String> params) 
			throws ClientProtocolException, IOException 
			{
		return httpRequest(HttpHelper.MIME_FORM_ENCODED, url, params, HttpHelper.POST_TYPE);
			}

	/**
	 * 
	 * @param contentType
	 * @param url
	 * @param params
	 * @param requestType
	 * @return
	 */
	private HttpResponse httpRequest(final String contentType, final String url,		
			final Map<String, String> params, final int requestType) 
					throws ClientProtocolException, IOException
					{
		// handle POST or GET request respectively.
		HttpRequestBase method = null;

		if (requestType == HttpHelper.POST_TYPE) {
			method = new HttpPost(url);

			List<NameValuePair> nvps = null;
			if ((params != null) && (params.size() > 0)) {
				nvps = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			if (nvps != null) {
				try {
					HttpPost methodPost = (HttpPost) method;
					methodPost.setEntity(new UrlEncodedFormEntity(nvps,	HTTP.UTF_8));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("Error peforming HTTP request: "	+ e.getMessage(), e);
				}
			}
		} else if (requestType == HttpHelper.GET_TYPE) {
			method = new HttpGet(url);
		}
		
		// execute request
		return execute(method);
					}

	/**
	 * Execute HTTP client.
	 * @param method
	 * @return HttpResponse - Response data.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	private synchronized HttpResponse execute(final HttpRequestBase method) 
			throws ClientProtocolException, IOException	
			{			
        HttpResponse responsePOST = client.execute(method);  
        List<Cookie> cookies = getCookies();

		cookieManager=CookieManager.getInstance();
		CookieSyncManager.getInstance().startSync();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("mno")) CUser.mno=cookie.getValue();

		}
//		Cookie sessionInfo = null;
//		 for (Cookie cookie : cookies ) {
//		  sessionInfo = cookie;
//		  String cookieString = sessionInfo.getName() + "="
//		   + sessionInfo.getValue() + "; path="
//		   + sessionInfo.getPath()+"; domain="+sessionInfo.getDomain();
//		  Log.d("passone","s"+cookieString);
//		  cookieManager.setCookie(Constants.cookieUrl,cookieString);
//
//		  CookieSyncManager.getInstance().sync();
//		 }
		
		return responsePOST;
			}
	public List<Cookie> getCookies() {
		return client.getCookieStore().getCookies();
	}
}
