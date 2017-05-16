package net.passone.hrd.adapter;

import java.util.Map;

import org.codehaus.jackson.type.TypeReference;

import android.content.Context;
import android.os.Handler;
/**
 */
public class Params {

	private int _api;
	private String _uri;
	private Map<String, String> _params;
	private Context _context;
	private OnResponseListener _callback;
	private OnApiAlertListener _alertCallback;
	private TypeReference<?> _valueTypeRef;
	private Handler _handler;

	/**
	 * Constructor
	 * @param api
	 * @param params
	 * @param context
	 * @param callback
	 */
	public Params(int api, Map<String, String> params, Context context, 
			OnResponseListener callback, OnApiAlertListener alertCallback, Handler handler) 
	{		
		_api = api;
		_params = params;
		_context = context;
		_callback = callback;
		_alertCallback = alertCallback;
		_handler = handler;
	}	

	public int getApi() {
		return _api;
	}

	public void setApi(int api) {
		_api = api;
	}

	public String getUri() {
		return _uri;
	}

	public void setUri(String uri) {
		_uri = uri;
	}

	public Map<String, String> getParams() {
		return _params;
	}

	public void setParams(Map<String, String> params) {
		_params = params;		
	}

	public Context getContext() {
		return _context;
	}

	public Handler getHandler() {
		return _handler;
	}

	public void setHandler(Handler handler) {
		_handler = handler;
	}	

	public TypeReference<?> getValueTypeRef() {
		return _valueTypeRef;
	}

	public void setValueTypeRef(TypeReference<?> valueTypeRef) {
		_valueTypeRef = valueTypeRef;
	}

	public OnResponseListener getCallback() {
		return _callback;
	}

	public OnApiAlertListener getAlertCallback() {
		return _alertCallback;
	}
}
