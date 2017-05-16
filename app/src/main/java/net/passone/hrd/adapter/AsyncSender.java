package net.passone.hrd.adapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import net.passone.hrd.R;
import net.passone.hrd.common.Util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.os.Handler;


public class AsyncSender extends Thread {

	private HttpHelper _httpHelper;	// For HTTP
	private ObjectMapper _objectMapper; // For JSON
	private Params _params;
	private Handler _handler;
	//protected static ProgressDialog dialog;
	protected static WaitDialog dialog;
	protected static int dialogCount = 0;


	/**
	 * Constructor<br><br> 
	 * @param httpHelper
	 * @param objectMapper
	 * @param params
	 */
	public AsyncSender(HttpHelper httpHelper, ObjectMapper objectMapper, Params params)
	{	
		_httpHelper = httpHelper;
		_objectMapper = objectMapper;
		_params = params;
		_handler = params.getHandler();
	} 

	@Override
	public void run() {

		showDialog();

		callback();

		hideDialog();
	}

	private void showDialog() {		

		try {			
			// Show delayed dialog. 
			_handler.postDelayed(new Runnable() {
				public void run() {


					try {
						if (dialogCount++ == 0) {
							if (AsyncSender.dialog != null)
								AsyncSender.dialog.dismiss();
							AsyncSender.dialog = WaitDialog.show(_params.getContext(), "", "");
						} 
					} catch(Exception e) {;}
				}
			}, 0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void callback() {

		try {

			/**
			 * API 처리
			 */
			apiCallback();			

		} catch (ApiException e) {

			/**
			 * 통신장애
			 */
			alertCallback(e);
		}

	}

	private void hideDialog() {

		try {		
			// Close dialog.
			_handler.post(new Runnable() {
				public void run() {

					try {
						if (dialogCount-- == 1 && AsyncSender.dialog != null)	{
							AsyncSender.dialog.dismiss();
							AsyncSender.dialog = null;
						}
					} catch (Exception e) {;}
				}
			});	

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * API 리턴 콜백
	 * @throws Exception
	 */
	private synchronized void apiCallback() throws ApiException {		
		int api =  _params.getApi();
		Object result = requestHttp(_params.getContext(), _params.getUri(), _params.getParams());
		CallbackContainer callback = new CallbackContainer(_params.getCallback(), api, result);
		if (callback != null) {
			_handler.post(callback);
		}
	}

	/**
	 * 통신 장애 콜백
	 */
	private void alertCallback(ApiException ex) {
		try {
			int api =  _params.getApi();
			AlertCallbackContainer alert = 
					new AlertCallbackContainer(_params.getAlertCallback(), api, _params, ex);			
			_handler.post(alert);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * HTTP 서버에 요청<br><br>
	 * 리턴되는  값은 Object 형이지만 호출한 곳에서는 
	 * 컨데이너 클래스형으로 형변환 하여 사용하여야 한다.<br><br>
	 * @return Object 	    
	 */
	private Object requestHttp(final Context context, 
			final String url, final Map<String, String> params) throws ApiException 
			{		
		/* Response type is JSON string or plain-text. */
		String response = execute(context, url, params);		
		try {	
			Util.debug(">>> HTTP Response = " + response);
			response = response.replace("\r", "");
			response = response.replace("\n", "");
			response = response.replace("/", "\\/");


			//			Log.d("passone", "objectMapper : "+_objectMapper.readValue(response, _params.getValueTypeRef()));
			if (response != null) {
				// Decoding utf-8
				//				response = URLDecoder.decode(response, "utf-8");		

				//				Util.debug(">>> HTTP Response (Decoding UTF-8) = " + response);

				/* Deserialize JSON string and bind to adapter container class. */

				return _objectMapper.readValue(response, _params.getValueTypeRef());
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();			
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
		}

		return null;
			}

	/**
	 * Execute HTTP client.
	 * @param method
	 * @return String - Response data.
	 */
	private synchronized String execute(final Context context, 
			final String url, final Map<String, String> params) throws ApiException 
			{			
		HttpResponse response = null;

		try {

			Util.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			Util.debug(">>> HTTP Request Url = " + url);
			Map<String, String> p = new HashMap<String, String>();
			String request_url=url+"?";
			int cnt=0;
			for (Map.Entry<String, String> entry : params.entrySet())
			{

				Util.debug(">>> HTTP Request Param = " + entry.getKey() + " = " + entry.getValue());
				request_url=request_url+entry.getKey()+"="+entry.getValue();
				if(cnt<params.entrySet().size()-1)
				{	cnt++;
					request_url=request_url+"&";
				}
			}
			Util.debug(request_url);
			if(url.contains("login.asp")) {
				response = _httpHelper.requestPost(url,params);

			}
			else
			{
				response = _httpHelper.requestGet(request_url);

			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new ApiException(1001, context.getString(R.string.api_http_alert));
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApiException(1002, context.getString(R.string.api_http_alert));
		}		

		int httpStatus = response.getStatusLine().getStatusCode();

		Util.debug(">>> HTTP Response status = " + Integer.toString(httpStatus));

		// Status 200 : OK
		if (httpStatus == HttpStatus.SC_OK) {				
			try {

				return EntityUtils.toString(response.getEntity());
			} catch (ParseException e) {
				e.printStackTrace();
				return (null);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}				
		} 
		// HTTP 오류 처리
		else if (httpAlert(httpStatus)) {
			throw new ApiException(httpStatus, context.getString(R.string.api_http_alert));		
		}		

		return (null);
			}


	private boolean httpAlert(int httpStatus) {

		/**
		 * switch 문에 포함된 HTTP 상태는 통신장애로 처리되어 alert 창을 출력하게 됩니다.
		 */	
		switch(httpStatus) 
		{
		case HttpStatus.SC_CONTINUE :		  
			// 101
		case HttpStatus.SC_SWITCHING_PROTOCOLS :		  
			// 102
		case HttpStatus.SC_PROCESSING :
			// 201
		case HttpStatus.SC_CREATED :
			// 202
		case HttpStatus.SC_ACCEPTED :
			// 203
		case HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION :
			// 204
		case HttpStatus.SC_NO_CONTENT :
			// 205
		case HttpStatus.SC_RESET_CONTENT :
			// 206
		case HttpStatus.SC_PARTIAL_CONTENT :
			// 207
		case HttpStatus.SC_MULTI_STATUS :
			// 300
		case HttpStatus.SC_MULTIPLE_CHOICES :
			// 301
		case HttpStatus.SC_MOVED_PERMANENTLY :
			// 302
		case HttpStatus.SC_MOVED_TEMPORARILY :
			// 303
		case HttpStatus.SC_SEE_OTHER :
			// 304
		case HttpStatus.SC_NOT_MODIFIED :
			// 305
		case HttpStatus.SC_USE_PROXY :
			// 307
		case HttpStatus.SC_TEMPORARY_REDIRECT :
			// 400
		case HttpStatus.SC_BAD_REQUEST :
			// 401
		case HttpStatus.SC_UNAUTHORIZED :
			// 402
		case HttpStatus.SC_PAYMENT_REQUIRED :		  
			// 403
		case HttpStatus.SC_FORBIDDEN :
			// 404
		case HttpStatus.SC_NOT_FOUND :
			// 405
		case HttpStatus.SC_METHOD_NOT_ALLOWED :
			// 406
		case HttpStatus.SC_NOT_ACCEPTABLE :
			// 407
		case HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED :
			// 408
		case HttpStatus.SC_REQUEST_TIMEOUT :
			// 409
		case HttpStatus.SC_CONFLICT :
			// 410
		case HttpStatus.SC_GONE :
			// 411
		case HttpStatus.SC_LENGTH_REQUIRED :
			// 412
		case HttpStatus.SC_PRECONDITION_FAILED :
			// 413
		case HttpStatus.SC_REQUEST_TOO_LONG :
			// 414
		case HttpStatus.SC_REQUEST_URI_TOO_LONG :
			// 415
		case HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE :
			// 416
		case HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE :
			// 417
		case HttpStatus.SC_EXPECTATION_FAILED :
			// 419
		case HttpStatus.SC_INSUFFICIENT_SPACE_ON_RESOURCE :
			// 420
		case HttpStatus.SC_METHOD_FAILURE :
			// 422
		case HttpStatus.SC_UNPROCESSABLE_ENTITY :
			// 423
		case HttpStatus.SC_LOCKED :
			// 424
		case HttpStatus.SC_FAILED_DEPENDENCY :
			// 500
		case HttpStatus.SC_INTERNAL_SERVER_ERROR :
			// 501
		case HttpStatus.SC_NOT_IMPLEMENTED :
			// 502
		case HttpStatus.SC_BAD_GATEWAY : 
			// 503
		case HttpStatus.SC_SERVICE_UNAVAILABLE :
			// 504
		case HttpStatus.SC_GATEWAY_TIMEOUT :
			// 505
		case HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED :
			// 507
		case HttpStatus.SC_INSUFFICIENT_STORAGE :
			return (true);
		}

		return (false);
	}
}