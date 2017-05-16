package net.passone.hrd.adapter;


public class CallbackContainer implements Runnable {

	private OnResponseListener _callback;

	private int _api;
	private Object _result;		

	/**
	 * Constructor<br><br>
	 * @param callback
	 */
	public CallbackContainer(OnResponseListener callback, int api, Object result) {
		_callback = callback;
		_api = api;
		_result = result;
	}

	public void run() {
		if (_callback != null)
			_callback.onResponseReceived(_api, _result);
	}
}