package net.passone.hrd.adapter;


public interface OnApiAlertListener {

	/**
	 * onResponseReceived<br><br>
	 * API 처리 결과를 리턴하기 위한 콜백함수<br><br> 
	 * @param api - API Command.
	 * @param params - Parameter
	 */
	public void onApiAlert(int api, Params params, ApiException e);
}
