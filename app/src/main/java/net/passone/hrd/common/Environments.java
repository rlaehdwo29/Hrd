package net.passone.hrd.common;


import android.content.Context;

public class Environments {	

	/**
	 * ȯ�漳�� ��������
	 */
	public static boolean PREFERENCE_EXIST = false;
	/**
	 * �ڵ��α���
	 */
	public static boolean AUTO_LOGIN = false;
	/**
	 * �������� ����
	 */
	public static boolean SAVE_ACCOUNT_INFO = true;
	
	/**
	 * 3G�ٿ�ε�?���?
	 */
	public static boolean ALLOW_3G = true;
	public static boolean ALLOW_ALRAM=false;

	/**
	 * 3G�ٿ�ε�?���?
	 */
	public static boolean ALERT_WHEN_DOWNLOAD_ON_MOBILE_3G = true;
	public static float DOWNLOAD_CAPACITY = 4;

	/**
	 * �������Ͽ� ���� ���࿩��
	 */
	public static boolean ISAGREE = true;
	/**
	 * �������� �ε���
	 */
	public static int NOTICE_IDX = 0;

	/**
	 * ?�시 ?�림
	 */

	public static boolean PUSH_A01 = true;
	public static boolean PUSH_A02 = true;
	public static boolean PUSH_A03 = true;
	public static boolean PUSH_A04 = true;
	public static boolean PUSH_A05 = true;
	public static boolean PUSH_A06 = true;
	public static boolean PUSH_A07 = true;
	public static boolean PUSH_A08 = true;
	public static boolean PUSH_A09 = true;
	public static boolean PUSH_A10 = true;
	public static boolean PUSH_A11 = true;


	
	/**
	 * Save configuration information to preference file.<br>
	 * @param context
	 */
	public static void save(Context ctx) {
		Preference prefs = new Preference(ctx, Constants.PREFERENCE_FILE);
		prefs.write("PREFERENCE_EXIST", true);
		prefs.write("AUTO_LOGIN", AUTO_LOGIN);
		prefs.write("ALLOW_3G", ALLOW_3G);
		prefs.write("ALLOW_ALRAM", ALLOW_ALRAM);
		prefs.write("SAVE_ACCOUNT_INFO", SAVE_ACCOUNT_INFO);
		prefs.write("ALLOW_DOWNLOAD_ON_MOBILE_3G", ALLOW_3G);
		prefs.write("ALERT_WHEN_DOWNLOAD_ON_MOBILE_3G", ALERT_WHEN_DOWNLOAD_ON_MOBILE_3G);
		prefs.write("DOWNLOAD_CAPACITY", DOWNLOAD_CAPACITY);
		prefs.write("PUSH_A01", PUSH_A01);
		prefs.write("PUSH_A02", PUSH_A02);
		prefs.write("PUSH_A03", PUSH_A03);
		prefs.write("PUSH_A04", PUSH_A04);
		prefs.write("PUSH_A05", PUSH_A05);
		prefs.write("PUSH_A06", PUSH_A06);
		prefs.write("PUSH_A07", PUSH_A07);
		prefs.write("PUSH_A08", PUSH_A08);
		prefs.write("PUSH_A09", PUSH_A09);
		prefs.write("PUSH_A10", PUSH_A10);
		prefs.write("PUSH_A11", PUSH_A11);

		prefs.write("ISFIRST", ISAGREE);
		prefs.write("NOTICE_IDX", NOTICE_IDX);
	}

	/**
	 * Load configuration information from preference file.<br>
	 * @param context
	 */
	public static void load(Context ctx) {
		Preference prefs = new Preference(ctx, Constants.PREFERENCE_FILE);
		PREFERENCE_EXIST = prefs.read("PREFERENCE_EXIST", false);
		AUTO_LOGIN = prefs.read("AUTO_LOGIN", false);
		SAVE_ACCOUNT_INFO = prefs.read("SAVE_ACCOUNT_INFO", true);
		ALLOW_3G = prefs.read("ALLOW_3G", false);
		ALLOW_ALRAM = prefs.read("ALLOW_ALRAM", false);
		ALERT_WHEN_DOWNLOAD_ON_MOBILE_3G = prefs.read("ALERT_WHEN_DOWNLOAD_ON_MOBILE_3G", true);
		DOWNLOAD_CAPACITY = prefs.read("DOWNLOAD_CAPACITY", 4.0f);
		PUSH_A01 = prefs.read("PUSH_A01", true);
		PUSH_A02 = prefs.read("PUSH_A02", true);
		PUSH_A03 = prefs.read("PUSH_A03", true);
		PUSH_A04 = prefs.read("PUSH_A04", true);
		PUSH_A05 = prefs.read("PUSH_A05", true);
		PUSH_A06 = prefs.read("PUSH_A06", true);
		PUSH_A07 = prefs.read("PUSH_A07", true);
		PUSH_A08 = prefs.read("PUSH_A08", true);
		PUSH_A09 = prefs.read("PUSH_A09", true);
		PUSH_A10 = prefs.read("PUSH_A10", true);
		PUSH_A11 = prefs.read("PUSH_A11", true);

		ISAGREE = prefs.read("ISAGREE", true);
		NOTICE_IDX = prefs.read("NOTICE_IDX", 0);
	}	 
}

