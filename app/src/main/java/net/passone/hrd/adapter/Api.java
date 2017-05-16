package net.passone.hrd.adapter;

public class Api {

	/*
	 * API Command.
	 * 
	 * 상수의 값은 0부터 순차적이어야 한다.
	 * Path 배열의 인덱스로 사용되므로 상호 순서가 올바른지 확인하여야 한다.
	 * 신규로 추가 시는 뒤에 저장한다.
	 */	
	public static final int SIGN_IN 					= 0;	// 로그인
	public static final int SIGN_OUT 					= 1;	// 
	public static final int ID_SEARCH 					= 2;	// 
	public static final int VERSION 					= 3;	// 
	public static final int QNA_WRITE 					= 4;	// 
	public static final int CURRICULUM 					= 5;	// 
	public static final int LECTURE 					= 6;	// 
	public static final int DOWNLOAD 					= 7;	// 
	public static final int STATUS					= 8;	//
	public static final int PLAYMOVE					= 9;	//





	// Base URL
	private static String BaseUrl;	

	// Path
	private static final String Path[] = {
		"/api_v1/login.asp",					
		"/logout.asp",					
		"/siteid.asp",
		"http://api.passone.net/version.php",
		"/counsel/counselWrite_Proc.asp",
		"http://mobileclass.campus21.co.kr/api_v5/Curriculum.asp",
		"http://mobileclass.campus21.co.kr/api_v5/lecture.asp",
		"http://mobileclass.campus21.co.kr/api_v5/play_download.asp",
		"http://mobileclass.campus21.co.kr/api_v5/play_state.asp",
		"http://mobileclass.campus21.co.kr/api_v5/play_state.asp"


	};	

	/**
	 * 기본 URL 구함.<br><br>
	 * @return URL
	 */
	protected String getBaseUrl() {
		return BaseUrl;
	}	

	/**
	 * 기본 URL 설정.<br><br>
	 * @param url
	 */
	protected void setBaseUrl(String url) {
		BaseUrl = url;
	}	

	/**
	 * API에 대한 Path를 구함.<br><br>
	 * @param api
	 * @return 값이 존재하지 않는 경우 공백 &quot;&quot; 리턴.
	 */
	protected String getPath(int api) {
		try {
			return Path[api];
		} catch(Exception e) {
			return "";
		}
	}
}
