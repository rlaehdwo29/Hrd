package net.passone.hrd.adapter;


public interface OnResponseListener {

	/**
	 * onResponseReceived<br><br>
	 * API 처리 결과리턴하기 위한 콜백함수<br><br>
	 * @param api - API Command.
	 * @param obj -
	 */
	public void onResponseReceived(int api, Object obj);		
}
