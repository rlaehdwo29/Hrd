package net.passone.hrd.adapter;
public class AlertCallbackContainer implements Runnable {

	private OnApiAlertListener _alertCallback;	
	private int _api;
	private Params _params;
	private ApiException _e;

	/**
	 * Constructor<br><br>
	 * @param callbackActivity
	 */
	public AlertCallbackContainer(OnApiAlertListener alertCallback, int api, Params params, ApiException e) {
		_alertCallback = alertCallback;
		_api = api;
		_params = params;
		_e = e;
	}

	public void run() {
		if (_alertCallback != null)
			_alertCallback.onApiAlert(_api, _params, _e);
	} 
	
}
