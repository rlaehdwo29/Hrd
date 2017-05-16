package net.passone.hrd.common;

import android.os.Environment;

public class Constants {

	/**
	 * 오픈라이센스 사용허가권 표시 설정
	 */
	public static final boolean Eula = false;
	/**
	 * 파일을 저장할 루트 디렉토리
	 */
	public static final String rootDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/woongjin/passone/campus/";
	public static final String InternalrootDirectory = Environment.getRootDirectory().getAbsolutePath()+"/woongjin/passone/campus/";
	public static final String imageDirectory = rootDirectory+"/files/image";
	public final static String profileDirectory = rootDirectory+"/files/profile";
	public final static String tempImage = Environment.getExternalStorageDirectory().getAbsolutePath()+"/temp";

	/**
	 * API URL
	 */
	public static String baseUrl = "http://smartclass.campus21.co.kr";

	public static String mainUrl="/default.asp";
	public static String myclassUrl = "/myClass/default.asp"; 
	public static String learnplusUrl = "/running/list01.asp"; 
	public static String noticeUrl = "/notice/noticeList.asp"; 
	public static String supportUrl = "/counsel/default.asp"; 
	public static String blearningUrl="/booklearning/";
	public static String studyDataUrl="/counsel/dataList.asp";
	public static String ebookUrl="/ebook/ebook.asp";
	public static String basicbookUrl="/ebook/basicBook.asp";
	public static String cookieUrl="smartclass.campus21.co.kr";
	public static final String DATABASE_NAME = "UserInfo.db";

	public static String libraryKey = "06ddb900079042a3978ffe990f8dcd87"; 

	public static String rdpIP="211.110.159.132"; // rdp server ip	//passone 실 - 211.110.159.130 //passone 고도화 - 211.110.159.132 //aracomm - 112.216.84.162

	/**
	 * LOGCAT TAG
	 */
	public static String TAG = "passone";
	/**
	 * Debuggable
	 */
	public static boolean debuggable = true;

	public static final String PREFERENCE_FILE = "GLSMOBILE";

	public static String app_name="스마트 연수원";
	public static String Call="tel:0262966562";
	public static String HOMEPAGE="http://hrd.hyundaimotorgroup.com/";
	public static String ESCHOOL_URL="http://m.cyber.ybmsisa.com/hdkia";
	public static String SPEP_URL="http://me2.do/xsJUhKB";
	public static String FRESH_URL="http://hkjunior.com";

	/**
	 *  동영상 이동 텀(초)
	 */
	public static final int MOVIE_MOVE_TERM = 10;

}
