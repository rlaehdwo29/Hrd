package net.passone.hrd.adapter;

import java.util.HashMap;
import java.util.Map;

import net.passone.hrd.R;
import net.passone.hrd.common.IntentModelActivity;
import net.passone.hrd.common.Util;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;



/**
 * Adapter 클래스<br>
 * 함수를 호출하면 HTTP 서버로 부터 API 결과를 가져온 후<br>
 * OnResponseListener 인터페이스의 onResponseReceived 콜백함수로 결과를 리턴한다. <br>
 */
public class Adapter implements OnApiAlertListener {

	// Adapter Manager.
	private AdapterManager _am;
	private Handler _handler = new Handler();
	/**
	 * Constructor.
	 */
	public Adapter() {
		// Create an adapter manager.
		_am = new AdapterManager();		
	}

	public void close() {
		// Close an adapter manager.
		_am.close();		
	}

	/**
	 * shutdownConnection<br><br>
	 * DefaultHttpClient 의 ConnectionManager 를 종료한다.<br>
	 * 모든 연결이 끊어진다.<br><br>
	 */
	public void shutdownConnection() { 
		_am.shutdownConnectionManager();	
	}	

	/**
	 *  �⺻ URL ����<br><br>
	 *  @param url
	 */
	public void setBaseUrl(String url) {
		_am.setBaseUrl(url);
	}

	/**
	 * 로그인<br><br> 
	 * 사용자 계정 입력을 할때 한 번만 호출한다.<br>
	 * 콜백함수인 onResponseReceived 의 두번째 인자를
	 * <b>Sign</b> 클래스로 형변환하여 사용해야한다.<br><br>
	 * @param userid - 사용자 아이디
	 * @param passwd - 비밀번호
	 * @param company_cd - 회사 id
	 * @param lp - 사이트 id
	 * @param devicetoken - 푸시용 토큰값(사용안함)
	 * @param udid - 기기고유값(사용안함)
	 * @param context
	 * @param callback - onResponseReceived 함수
	 */
	public void signIn(String userid, String passwd, String siteid, String udid, String devicetoken,
			Context context, OnResponseListener callback) 
	{		 
		Map<String, String> params = new HashMap<String, String>();
		params.put("userid", userid);
		params.put("userpw", passwd);
		params.put("siteid", siteid);
		params.put("platform", "android");
		params.put("udid", udid);
		params.put("devicetoken", devicetoken);
		_am.request(new Params(Api.SIGN_IN, params, context, callback, this, _handler));
	}	

	/**
	 * 로그아웃<br><br>
	 * 로그아웃할 때 한 번만 호출한다.<br>
	 * 콜백함수인 onResponseReceived 의 두번째 인자를
	 * <b>Sign</b> 클래스로 형변환하여 사용해야한다.<br><br>
	 * @param uid - 회원코드 
	 * @param context
	 * @param callback - onResponseReceived 함수
	 */
	public void signOut(String uid, Context context, OnResponseListener callback) {		 
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", uid);
		_am.request(new Params(Api.SIGN_OUT, params, context, callback, this, _handler));
	}

	/**
	 * 아이디로 회사 리스트 검색<br><br>
	 * 로그아웃할 때 한 번만 호출한다.<br>
	 * 콜백함수인 onResponseReceived 의 두번째 인자를
	 * <b>Sign</b> 클래스로 형변환하여 사용해야한다.<br><br>
	 * @param userid - 회원id 
	 * @param context
	 * @param callback - onResponseReceived 함수
	 */
	public void idSearch(String userid, Context context, OnResponseListener callback) {		 
		Map<String, String> params = new HashMap<String, String>();
		params.put("userid", userid);
		_am.request(new Params(Api.ID_SEARCH, params, context, callback, this, _handler));
	}
	public void qnaWrite(String flag, String subject, String contents, Context context, OnResponseListener callback) {		 
		Map<String, String> params = new HashMap<String, String>();
		params.put("flag", flag);
		params.put("subject", subject);
		params.put("contents", contents);

		_am.request(new Params(Api.QNA_WRITE, params, context, callback, this, _handler));
	}
	public void version(Context context, OnResponseListener callback) {		 
		Map<String, String> params = new HashMap<String, String>();
		params.put("platform", "android");
		params.put("api", "smartcampus"); // 스마트연수원: smartcampus / 벽산: byucksan / 코오롱: kolon
		_am.request(new Params(Api.VERSION, params, context, callback, this, _handler));
	}
	public void getCurriculum(String classkey,String classcount,String studykey,Context context, OnResponseListener callback) {		 
		Map<String, String> params = new HashMap<String, String>();
		params.put("classkey", classkey);
		params.put("classcount", classcount);
		params.put("studykey", studykey);

		_am.request(new Params(Api.CURRICULUM, params, context, callback, this, _handler));
	}
	public void getLecture(String classkey,String part,String studykey, String classcount, Context context, OnResponseListener callback) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("classkey", classkey);
		params.put("Part", part);
		params.put("studykey", studykey);
		params.put("classcount", classcount);

		_am.request(new Params(Api.LECTURE, params, context, callback, this, _handler));
	}
	public void playDownload(String classkey,String classcount,String studykey,String part,String page,Context context, OnResponseListener callback) {		 
		Map<String, String> params = new HashMap<String, String>();
		params.put("classkey", classkey);
		params.put("classcount", classcount);
		params.put("studykey", studykey);
		params.put("part", part);
		params.put("page", page);

		_am.request(new Params(Api.DOWNLOAD, params, context, callback, this, _handler));
	}
	public void play_state(String classkey,String classcount,String studykey,String part,String page,String finish,int markertime, int duration,Context context, OnResponseListener callback) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("classkey", classkey);
		params.put("classcount", classcount);
		params.put("studykey", studykey);
		params.put("Finish", finish);
		params.put("MarkerTime", String.valueOf(markertime));
		params.put("Duration", String.valueOf(duration));
		params.put("part", part);
		params.put("page", page);

		_am.request(new Params(Api.STATUS, params, context, callback, this, _handler));
	}
	public void play_move(String classkey,String classcount,String studykey,String part,String page,Context context, OnResponseListener callback) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("classkey", classkey);
		params.put("classcount", classcount);
		params.put("studykey", studykey);
		params.put("part", part);
		params.put("page", page);

		_am.request(new Params(Api.PLAYMOVE, params, context, callback, this, _handler));
	}
	/**
	 * 통신장애 Alert 콜백
	 * API 요청 후 통신 장애가 발생하면 이 함수가 호출이 된다.<br><br>
	 * @param api - 요청한 API 명령
	 * @param params - 요청한 API 파라메터
	 */
	public void onApiAlert(int api, Params params, ApiException ex) {
		if (params != null) {
			try {

				alertYesNo(api, params, ex);

				Util.debug(" >> HTTP response exception code = " + ex.getErrorCode());
				Util.debug(" >> HTTP response exception message = " + ex.getMessage());

			} catch(Exception e) {;}
		}
	}

	/**
	 * 통신장애 Alert 메시지를 출력한다.<br><br>
	 * @param api - 요청한 API 명령
	 * @param params - 요청한 API 파라메터
	 * @throws Exception
	 */
	public void alertYesNo(final int api, final Params params, ApiException ex) throws Exception {

		final Context context = params.getContext();
		String errorstr="";
		if(ex.getErrorCode()==1002)
		{
			errorstr="인터넷 연결을 확인해 주세요.";
		}
		else
		{
			errorstr=Integer.toString(ex.getErrorCode());
		}
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);		
		dialog.setTitle(context.getResources().getString(R.string.app_name))
		.setMessage(ex.getMessage() + "\n" + errorstr)	// error message
		.setCancelable(false)
		.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {		
				dialog.dismiss();
//				_handler.postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						_am.request(params);
//					}
//				}, 5000);	// 통신상태가 변화할 시간을 준다.
				IntentModelActivity.closeActivity(context);
			}
		}).setCancelable(false)	
//		.setNegativeButton("취소", new DialogInterface.OnClickListener() {		
//			@Override
//			public void onClick(DialogInterface dialog, int id) {					
//				dialog.cancel();
//				/*
//				 * 
//				 *  
//				 *  현재 임시로 강제종료.... 절차 밟아 종료하도록 기능 추가..요망
//				 *  
//				 *  
//				 */					
//				IntentModelActivity.closeActivity(context);
//			}		
//		})
		.show();
	}
}