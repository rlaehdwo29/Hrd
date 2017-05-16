package net.passone.hrd.adapter;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.passone.hrd.common.Constants;
import net.passone.hrd.container.ApiResult;
import net.passone.hrd.container.Curriculum;
import net.passone.hrd.container.Download;
import net.passone.hrd.container.LectureInfo;
import net.passone.hrd.container.PlayStatus;
import net.passone.hrd.container.QnaWrite;
import net.passone.hrd.container.Sign;
import net.passone.hrd.container.Site;
import net.passone.hrd.container.Version;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;



public class AdapterManager extends Api {

	HttpHelper _httpHelper = new HttpHelper();
	ObjectMapper _objectMapper = new ObjectMapper();	
	ExecutorService _threadExecutor = Executors.newCachedThreadPool();

	public AdapterManager() {
		super.setBaseUrl(Constants.baseUrl);
	}

	/**
	 * HTTP를 이용하여 API 요청
	 * 쓰레드를 이용하여 비동기로 처리.
	 * @param lmParams 
	 */
	public void request(Params lmParams) {							

		try {
			/* Set parameters for Asynchronous sender. */
			lmParams.setUri(getUri(lmParams.getApi()));
			lmParams.setValueTypeRef(getTypeReference(lmParams.getApi()));

			/*
			 * Execute asynchronous sender thread.
			 */
			_threadExecutor.execute(
					new AsyncSender(_httpHelper, _objectMapper, lmParams));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * API 명령별 컨테이너 클래스형을 리턴한다.<br><br>
	 * @param api
	 * @return TypeReference<?>
	 */	
	private TypeReference<?> getTypeReference(int api) {
		switch(api) {
		case Api.SIGN_IN 					: return new TypeReference<Sign>() {};					// 로그인
		case Api.SIGN_OUT 					: return new TypeReference<Sign>() {};					// 로그아웃(사용안함)
		case Api.ID_SEARCH 					: return new TypeReference<List<Site>>() {};					// 아이디로 회사찾기
		case Api.VERSION 					: return new TypeReference<Version>() {};					// 아이디로 회사찾기
		case Api.QNA_WRITE 					: return new TypeReference<QnaWrite>() {};					// 아이디로 회사찾기
		case Api.CURRICULUM 					: return new TypeReference<Curriculum>() {};					
		case Api.LECTURE 					: return new TypeReference<List<LectureInfo>>() {};					
		case Api.DOWNLOAD 					: return new TypeReference<Download>() {};	
		case Api.STATUS				: return new TypeReference<PlayStatus>() {};
			case Api.PLAYMOVE				: return new TypeReference<PlayStatus>() {};

		}
		return null;
	}

	/**
	 * API의 URI를 구한다.
	 * @param api
	 * @return URI
	 */
	private String getUri(int api) {	
		if(api>=5 || api==3)
		{
			return getPath(api);

		}
			return (getBaseUrl() + getPath(api));
	}	

	/**
	 * Encode UTF-8
	 * @param params
	 * @return UTF8 Map<String, String>
	 */
	private Map<String, String> toUTF8(Map<String, String> params) {	
		Map<String, String> utf8Map = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : params.entrySet())			 
			utf8Map.put(entry.getKey(), toUTF8(entry.getValue()));
		return utf8Map;				
	}	

	/**
	 * Encode UTF-8
	 * @param String
	 * @return UTF8 String ( or Empty string )
	 */
	private String toUTF8(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch(Exception e) {
			return "";
		}
	}

	/**
	 * Close thread.
	 */
	public void close() {
		if (_threadExecutor != null)
			_threadExecutor.shutdownNow();
	}

	/**
	 * Shutdown connection manager.
	 */
	public void shutdownConnectionManager() {
		if (_httpHelper != null)
			_httpHelper.shutdownConnectionManager();
	}
}