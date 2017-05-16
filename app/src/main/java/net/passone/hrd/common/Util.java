package net.passone.hrd.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import net.passone.hrd.LoginActivity;
import net.passone.hrd.R;
import net.passone.hrd.adapter.DBmanager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.webkit.WebView;
import android.widget.Toast;



public class Util {

	public static final long KiloBytes = 1024;
	public static final long MegaBytes = KiloBytes * KiloBytes;
	public static final long GigaBytes = MegaBytes * KiloBytes;
	public static final String PREF_SET = "set";
	public static final String PREF_USEAUTOLOGIN = "useAutoLogin";
	public static final String PREF_USEALARMNETCHANGE = "useAlarmNetChanege";
	public static SharedPreferences pref;
	public static String MT_PREFS = "MY_PREFS";
	static DBmanager db_manager;	
	static SQLiteDatabase db;	

	////video play�� ���� API

	public static Handler handler;
	//public static com.inka.ncg.passone.sdk.NCG_Agent ncg;

	//////////////////////////////////////////////////////////////////////////////////////////
	// UI
	//////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * �ȵ���̵� ���ø����̼� ����
	 */
	public static void runApplication(Context context, String packageName) 
			throws NameNotFoundException, Exception 
			{		
		PackageManager pm = context.getPackageManager();

		/**
		 * Package�� ���� ���� �˻�
		 * ������ �������� ������ NameNotFoundException �߻�		
		 */
		pm.getApplicationInfo(packageName, 0);

		/**
		 * ���ø����̼� ������ ����� Exception ���� ó��
		 */
		try {
			Intent intent = pm.getLaunchIntentForPackage(packageName);				
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			throw new Exception(e);
		}
			}

	/**
	 * Toast �޽��� ���� <br><br>
	 * @param context
	 * @param msg - �޽���
	 */
	public static void ToastMessage(final Context context, String msg)	{
		Toast toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/**
	 * Toast �޽��� �˾� ����<br><br>
	 * @param context
	 * @param msg - �޽���
	 */
	public static void PopupMessage(final Context context, String msg) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(msg);
		builder.setPositiveButton("확인", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
			} 
		});
		builder.show();
	}
//////////////////////////////////////////////////////////////////////////////////////////
// UI
//////////////////////////////////////////////////////////////////////////////////////////

public static void alertYes(final Context context, int message) {
alert(context, 0, message, "확인", null);
}

public static void alertYes(final Context context, int message, OnClickListener onClickListener) {
alert(context, 0, message, "확인", onClickListener);
}

public static void alertYes(final Context context, int title, int message, OnClickListener onClickListener) {
alert(context, title, message, "확인", onClickListener);
}

public static void alertOk(final Context context, int message) {
alert(context, 0, message, "확인", null);
}	

public static void alertOk(final Context context, int message, OnClickListener onClickListener) {
alert(context, 0, message, "확인", onClickListener);
}

public static void alertOk(final Context context, int title, int message, OnClickListener onClickListener) {
alert(context, title, message, "확인", onClickListener);
}

public static void alertYesNo(final Context context, int message, 
OnClickListener onDissmissClickListener, OnClickListener onCancelClickListener) 
{
alert(context, 0, message, "예", "아니오", onDissmissClickListener, onCancelClickListener);
}

public static void alertYesNo(final Context context, int title, int message, 
OnClickListener onDissmissClickListener, OnClickListener onCancelClickListener) 
{
alert(context, title, message, "예", "아니오", onDissmissClickListener, onCancelClickListener);
}

public static void alertOkCancel(final Context context, int message, 
OnClickListener onDissmissClickListener, OnClickListener onCancelClickListener) 
{
alert(context, 0, message, "확인", "취소", onDissmissClickListener, onCancelClickListener);
}

public static void alertOkCancel(final Context context, int title, int message, 
OnClickListener onDissmissClickListener, OnClickListener onCancelClickListener) 
{
alert(context, title, message, "확인", "취소", onDissmissClickListener, onCancelClickListener);
}

/**
* alert
* 
* @param context - The context.
* @param message - The resource name.
* @param buttonText - The text to display in the button.
* @param listener - The DialogInterface.OnDismissListener to use.
*/
public static void alert(final Context context, int title, int message, String buttonText, OnClickListener onClickListener) {
AlertDialog dialog = new AlertDialog.Builder(context).create();
if (title > 0)
dialog.setTitle(context.getString(title));
dialog.setMessage(context.getString(message));
dialog.setButton(buttonText, onClickListener);
dialog.show();
}

/**
* alert
* 
* @param context - The context.
* @param message - The resource name.
* @param positiveButton - The text to display in the positive button.
* @param negativeButton - The text to display in the negative button.
* @param onDissmissClickListener - The DialogInterface.OnDismissListener to use.
* @param onCancelClickListener - The DialogInterface.OnCancelListener to use.
*/
public static void alert(final Context context, int title, int message, String positiveButtonText, String negativeButtonText, 
OnClickListener onDissmissClickListener, OnClickListener onCancelClickListener) 
{	
AlertDialog.Builder builder = new AlertDialog.Builder(context);
builder.setMessage(context.getString(message));
if (title > 0)
builder.setTitle(context.getString(title));
builder.setPositiveButton(positiveButtonText, onDissmissClickListener);					
builder.setNegativeButton(negativeButtonText, onCancelClickListener);
AlertDialog dialog = builder.create();
dialog.show();
}

/**
* alert
* 
* @param context - The context.
* @param message - The resource name.
* @param positiveButton - The text to display in the positive button.
* @param negativeButton - The text to display in the negative button.
* @param onDissmissClickListener - The DialogInterface.OnDismissListener to use.
* @param onCancelClickListener - The DialogInterface.OnCancelListener to use.
*/
public static void alert(final Context context, String title, String message, String positiveButtonText, String negativeButtonText, 
OnClickListener onDissmissClickListener, OnClickListener onCancelClickListener) 
{	
AlertDialog.Builder builder = new AlertDialog.Builder(context);
builder.setMessage(message);
if (title != null && !title.equals(""))
builder.setTitle(title);
builder.setPositiveButton(positiveButtonText, onDissmissClickListener);					
builder.setNegativeButton(negativeButtonText, onCancelClickListener);
AlertDialog dialog = builder.create();
dialog.show();
}	
	/**
	 * ���ڿ��� �ؽ��ڵ带 ����.<br><br>
	 * @param values
	 * @return int
	 */
	public static int getHahscode(String... values) {
		String str = "";
		for (String v : values)
			str += v;	
		return (str.hashCode());
	}

	/**
	 * ���ڿ��� MD5 HEX ���ڿ��� ��ȯ
	 * @param s
	 * @return
	 */
	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i=0; i<messageDigest.length; i++) {
				String hex = Integer.toHexString(0xFF & messageDigest[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);				
			}
			return hexString.toString();			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	  public static byte[] encodeBase64(byte [] binaryData) {
	    byte [] buf = null;
	        
	    try {
	        Class Base64 = Class.forName("org.apache.commons.codec.binary.Base64");
	        Class[] parameterTypes = new Class[] { byte[].class };  
	        Method encodeBase64 = Base64.getMethod("encodeBase64", parameterTypes);
	        buf = (byte[])encodeBase64.invoke(Base64, binaryData);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }        
	        
	    return buf;
	}
	/**
	 * URL ���� ���ϸ� ��ȯ <br><br>
	 * @param url
	 * @return filename
	 */
	public static String getFileName(String url) {
		return (url.substring(url.lastIndexOf('/') + 1)); 
	}	

	/**
	 * src �� URL ���� ���ϸ��� dest ����ο� �����Ͽ� ��ȯ . <br><br>
	 * @param src
	 * @param dest
	 */
	private String getFileName(String src, String dest) {    	
		return (Util.absolutePath(dest) + "/" + src.substring(src.lastIndexOf('/') + 1));  
	}

	/**
	 * ���� �ٿ�ε� ũ�⸦  0/0 MB ������ ���ڿ��� ��ȯ<br><br>
	 * @param current - Byte(s)
	 * @param total - Byte(s)
	 * @return
	 */
	public static String toDisplayMB(long current, long total) {
		return Long.toString(current/MegaBytes) + "/" + Long.toString(total/MegaBytes) + " MB"; 
	}

	/**
	 * ����� ��ȯ<br><br>
	 * @param current - Byte(s)
	 * @param total - Byte(s)
	 * @return
	 */
	public static int getProgress(long current, long total) {
		return (total != 0) ? ((int)((double)current / (double)total * 100.0)) : (0);
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	// Network & Device
	//////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * WI-FI ���� ����.<br><br>
	 * @param context
	 * @return WifiInfo
	 * @see WifiInfo
	 */
	public static WifiInfo getWifiInfo(final Context context) {

		// Connectivity Service Manager
		WifiManager wManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wManager.getConnectionInfo();
		// WI-FI is enabled.
		if (wManager.isWifiEnabled() && wifiInfo.getSSID() != null)
			return (wifiInfo);

		// disabled
		return (null);
	}

	/**
	 * Mobile ���� ����.<br><br>
	 * @param context
	 * @return true-����
	 */
	public static boolean isMobileConnected(final Context context) {
		ConnectivityManager connectivityManager = 
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// Mobile
		boolean mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
		return (mobile);
	}

	/**
	 * WI-FI ���� ����.<br><br>
	 * @param context
	 * @return true-����
	 */
	public static boolean isWifiConnected(final Context context) {		
		ConnectivityManager connectivityManager = 
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// WI-FI
		boolean wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
		return (wifi);
	}

	/**
	 * Mobile (3G) ���� ����.<br><br>
	 * @param context
	 * @return WifiInfo
	 * @see WifiInfo
	 */
	public static WifiInfo getMobileInfo(final Context context) {

		// Connectivity Service Manager
		WifiManager wManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wManager.getConnectionInfo();
		// WI-FI is enabled.
		if (wManager.isWifiEnabled() && wifiInfo.getSSID() != null)
			return (wifiInfo);

		// disabled
		return (null);
	}

	/**
	 * ����̽� ���� ��������.<br><br>
	 * @param context
	 * @return ����̽� ID
	 */
	public static String getDeviceID(final Context context) {
			String uid = "";
			try{
				TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
				uid = tManager.getDeviceId();
				
			}catch(Exception e) {;}
					
			if(uid == null || uid.equals(""))
			{
				uid = "35" + //we make this look like a valid IMEI
				Build.BOARD.length()%10+ Build.BRAND.length()%10 + 
				Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 + 
				Build.DISPLAY.length()%10 + Build.HOST.length()%10 + 
				Build.ID.length()%10 + Build.MANUFACTURER.length()%10 + 
				Build.MODEL.length()%10 + Build.PRODUCT.length()%10 + 
				Build.TAGS.length()%10 + Build.TYPE.length()%10 + 
				Build.USER.length()%10 ; //13 digits
				
			}
			return uid;
	
	}

	/**
	 * ���ķ����� ���� �˻�<br><br>
	 * @return true:enulator, false:device
	 */
	public static boolean isEmulator(final Context context) {
		String id = Util.getDeviceID(context);
		if (id == null)
			return (true);
		return (id.equals("000000000000000"));
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	// File 
	//////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * SD Card ����Ʈ ���� Ȯ���ϱ�.<br><br> 
	 * @return true-����, false-����ȵ�
	 */
	public static boolean isMountSDCard() {		
		String sDCardStatus = Environment.getExternalStorageState();        
		return (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) ? (true) : (false);
	}

	/**
	 * SD Card ��� ��θ� ���Ѵ�.<br><br>
	 * @return "mnt/sdcard"
	 */
	public static String absolutePath() {
		return (Environment.getExternalStorageDirectory().getAbsolutePath());
	}	

	/**
	 * SD Card ��� ��θ� ���Ѵ�.<br><br> 
	 * @return "mnt/sdcard"
	 */
	public static String absolutePath(String path) {
		return (Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path);
	}

	/**
	 * ���丮 �� �Ϻ��� ���丮, ��� ��� �����Ѵ�.<br><br>
	 * @param 
	 * @return 
	 */
	public static boolean deleteData(String directory) {
		if (!(isMountSDCard()) || (directory.equals("")))
			return false;    		 
		String absDir = absolutePath() + "/" + directory;
		return deleteTree(new File(absDir), true);
	}

	private static boolean deleteTree(File base, boolean deleteBase) {
		boolean result = true;
		if (base.isDirectory()) {
			for (File child : base.listFiles()) {            	
				result &= deleteTree(child, true);
			}
		}
		if (deleteBase) {
			result &= base.delete();
		}
		return (result);
	}

	/**
	 * ������ ���� ������ �˻��Ѵ�.<br><br>
	 * @param path
	 * @return
	 */
	public static boolean fileExists (String path) {
		if (!(isMountSDCard()) || (path.equals("")))
			return false; 
		String absPath = absolutePath() + "/" + path;
		return ((new File(absPath)).exists());
	}

	/**
	 * ������ ũ�⸦ ���Ѵ�.<br><br>
	 * @param path
	 * @return
	 */
	public static long getFileSize (String path) {
		if (!(isMountSDCard()) || (path.equals("")))
			return 0;       
		String absPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path;
		return ((new File(absPath)).length());
	}

	/**
	 * ������ �����Ѵ�.<br><br>
	 * @param file
	 * @return
	 */
	public static Boolean deleteFile(String path, String file) {
		if (!(isMountSDCard()) || (path.equals("")))
			return false;
		String absPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path;    	
		return ((new File(absPath, file)).delete());
	}


	/////////////////////////////////////////////////////////////////////
	////  video play�� ���� API
	/////////////////////////////////////////////////////////////////////
	// 외부 메모리가 사용가능한지 판단
		public static boolean canUseExternalMemory() {
			String state = Environment.getExternalStorageState();
			return state.equals(Environment.MEDIA_MOUNTED);
		}
	/**
	 * DRM �ٿ�ε� ��� ����
	 */
	public static String getDownloadPath(String filename) {

		if(canUseExternalMemory())
		{
			new File(Constants.rootDirectory).mkdirs();
			return Constants.rootDirectory+filename;
		}
		else{
			new File(Constants.InternalrootDirectory).mkdirs();
			return Constants.InternalrootDirectory+filename;
		}
	}
	/**
	 * Return the size of a directory in bytes
	 */
	public static long dirSize(File dir) {

		if (dir.exists()) {
			long result = 0;
			File[] fileList = dir.listFiles();
			for(int i = 0; i < fileList.length; i++) {
				// Recursive call if it's a directory
				if(fileList[i].isDirectory()) {
					result += dirSize(fileList [i]);
				} else {
					// Sum the file size in bytes
					result += fileList[i].length();
				}
			}
			return result; // return the file size
		}
		return 0;
	}
	/**
	 * DRM �ٿ�ε� ��� ����
	 */
	public static String getDownloadPath() {

		if(canUseExternalMemory())
		{
			new File(Constants.rootDirectory).mkdirs();
			return Constants.rootDirectory;
		}
		else{
			new File(Constants.InternalrootDirectory).mkdirs();
			return Constants.InternalrootDirectory;
		}
	}

	/**
	 * DRM ���ϸ� ��
	 * @param url
	 * @return
	 */
	public static String getFilename(String url) {
		String str = url.substring(url.lastIndexOf("/")+1, url.length());
		return str;
	}		    

	/**
	 * ���� ���� Ȯ��
	 * @param path
	 * @return
	 */
	public static boolean isDownFile(String path) {
		File file = new File(path);		
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ���丮 ���� Ȯ��
	 * @param path
	 * @return
	 */
	public static boolean isDirectory(String path) {
		File file = new File(path);		
		if (file.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}

	// DEBUG log print
	public static void debug(String log) {
		if (Constants.debuggable)
			Log.d(Constants.TAG, log);	
			
	}

	// SDī�� ��뷮
	static final int ERROR = -1; 

	/**
	 * ��밡���� �ܺ� �޸� ũ��
	 * @return - long
	 */
	static public long getAvailableExternalMemorySize() {   
		if (isMountSDCard()) {   
			File path = Environment.getExternalStorageDirectory();   
			StatFs stat = new StatFs(path.getPath());   
			long blockSize = stat.getBlockSize();   
			long availableBlocks = stat.getAvailableBlocks();   
			return availableBlocks * blockSize;   
		} else {   
			return ERROR;   
		}   
	}   

	/**
	 * �ܺ� �޸� ��ü ũ��
	 * @return - long
	 */
	static public long getTotalExternalMemorySize() {   
		if (isMountSDCard()) {   
			File path = Environment.getExternalStorageDirectory();   
			StatFs stat = new StatFs(path.getPath());   
			long blockSize = stat.getBlockSize();   
			long totalBlocks = stat.getBlockCount();   
			return totalBlocks * blockSize;   
		} else {   
			return ERROR;   
		}   
	}

	/**
	 * PREFERENCE ������ �ҷ�����
	 * @param context
	 * @param prefcase
	 * @return
	 */
	public static boolean getPref(final Context context, String prefcase){
		pref = context.getSharedPreferences(MT_PREFS, Context.MODE_PRIVATE);
		return pref.getBoolean(prefcase, true);
	}

	/** 
	 * DB ���� ���̵� �ҷ�����
	 */
	public static String getUserid(Context context, String _uid) {
		db_manager = new DBmanager(context,"UserInfo.db");
		String userid = "";
		String sql = "Select id from userinfo where uid="+_uid;
		Util.debug("query="+sql);
		if (_uid.equals("")) {
			//			userid = context.getResources().getString(R.string.config_no_userid);
		} else {
			db = db_manager.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				userid = cursor.getString(0);
			} else {
				//				userid = context.getResources().getString(R.string.config_no_userid);
			}
			cursor.close();
			db.close();
		}
		return userid;
	}

	/**
	 * ������ ��������
	 * @return
	 */
	public static String getModel() {
		return Build.MODEL.replace(" ", "_");
	}
	
	 public static Options getBitmapSize(Options options)
	 {
		 int targetWidth = 0;
	        int targetHeight = 0;
	         
	        if(options.outWidth > options.outHeight){    
	            targetWidth = (int)(600 * 1.3);
	            targetHeight = 600;
	        }else{
	            targetWidth = 600;
	            targetHeight = (int)(600 * 1.3);
	        }
	 
	        Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math.abs(options.outWidth - targetWidth);
	        if(options.outHeight * options.outWidth * 2 >= 16384){
	            double sampleSize = scaleByHeight
	                ? options.outHeight / targetHeight
	                : options.outWidth / targetWidth;
	            options.inSampleSize = (int) Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
	        }
	        options.inJustDecodeBounds = false;
	        options.inTempStorage = new byte[16*1024];
	         
	        return options;
	 }
	 public byte[] readBytes(InputStream inputStream) throws IOException
	 {       
	     MyByteArrayOutputStream byteBuffer = new MyByteArrayOutputStream(); // note the change!
	     int bufferSize = 1024;     
	     byte[] buffer = new byte[bufferSize];    
	     int len = 0;    
	     while ((len = inputStream.read(buffer)) != -1) 
	     {         
	        byteBuffer.write(buffer, 0, len);   
	     }
	     inputStream.close(); // hopefully this will release some more memory
	     return byteBuffer.toByteArray();
	 }
	 public static String getVersion(Context context) {
			String version="";
			try {
			   PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			   version = i.versionName;
			} catch(NameNotFoundException e) { }
			return version;	
		}
	 public static boolean saveProfile(String saveName, String urlPath, Context context)
	 {
		 String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+Constants.profileDirectory;
			try {
				(new File(fileName)).mkdirs();	
				InputStream inputStream = new URL(urlPath).openStream();
			    
				File file = new File(fileName+"/"+saveName);
				OutputStream out = new FileOutputStream(file);
				  int c = 0;
				     while((c = inputStream.read()) != -1)
				    	 out.write(c);
				     out.flush();

				out.close();

				 return true;

			} catch (Exception e) {
				return false;
			}

	 }
	 public static boolean saveImageStore(String saveName, String urlPath, Context context)
	 {
		 String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+Constants.imageDirectory;
		 File f = new File(fileName);
			File[] fList = f.listFiles();
	 
			if (fList != null) {
				for (int i = 0; i < fList.length; i++) {
					String sName = fList[i].getName();
					if(sName.equals(saveName))
						return false;
				}
			
			}
			try {
				(new File(fileName)).mkdirs();	
				InputStream inputStream = new URL(urlPath).openStream();
			    
				File file = new File(fileName+"/"+saveName);
				OutputStream out = new FileOutputStream(file);
				  int c = 0;
				     while((c = inputStream.read()) != -1)
				    	 out.write(c);
				     out.flush();

				out.close();

				 return true;

			} catch (Exception e) {
				return false;
			}

	 }
	 public static void FinishAllActivity(Context context)
	 {
		 ActivityManager am = (ActivityManager)context.getSystemService(Activity.ACTIVITY_SERVICE);
		 String name = context.getPackageName();
		 List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();

		    for(ActivityManager.RunningAppProcessInfo i : list){

		        if(i.processName.equals(name) == true){
		        	((Activity)context).moveTaskToBack(true);
		        	((Activity)context).finish();
//		        	android.os.Process.killProcess(android.os.Process.myPid()); 
//		            i.importance = ActivityManager.RunningAppProcessInfo.IMPORTANCE_EMPTY;
//		            am.killBackgroundProcesses(i.processName);
		        }
		    }
		 context.startActivity(new Intent(context,LoginActivity.class));
	 }
	 public static Bitmap ResizeBitmap(Bitmap bm)
	 {
		 int height = bm.getHeight();
		    int width = bm.getWidth();
		    // Toast.makeText(this, width + " , " + height, Toast.LENGTH_SHORT).show();
		    Bitmap resized = bm;
		    if(height<=800)
		    {
		    	resized=bm;
		    }
		    else
		    {
		    	while (height > 800) {
		    		resized = Bitmap.createScaledBitmap(bm, (width * 800) / height, 800, true);
		    		height = resized.getHeight();
		    		width = resized.getWidth();
		    	}
		    }
		    if(width>800)
		    {
		    	while (width > 800) {
		    		resized = Bitmap.createScaledBitmap(bm, 800,(height * 800) / width, true);
		    		height = resized.getHeight();
		    		width = resized.getWidth();
		    	}
		    }
		   
		    try {
		    	  File copyFile = new File(Constants.tempImage);
		    	copyFile.createNewFile();
		    	OutputStream out = new FileOutputStream(copyFile);

		    	resized.compress(CompressFormat.JPEG, 50, out);
		    	out.close();
		    } catch (Exception e) {        
		    	e.printStackTrace();
		    } 
		    
		    
	        return resized;
	 }
	//To check whether network connection is available on device or not
     public static boolean checkInternetConnection(Activity _activity) {
         ConnectivityManager conMgr = (ConnectivityManager) _activity.getSystemService(Context.CONNECTIVITY_SERVICE);
         if (conMgr.getActiveNetworkInfo() != null
                 && conMgr.getActiveNetworkInfo().isAvailable()
                 && conMgr.getActiveNetworkInfo().isConnected()) 
             return true;
         else
             return false;
     }//checkInternetConnection()
     public synchronized static int GetExifOrientation(String filepath) 
     {
         int degree = 0;
         ExifInterface exif = null;
         
         try 
         {
             exif = new ExifInterface(filepath);
         } 
         catch (IOException e) 
         {
             e.printStackTrace();
         }
         
         if (exif != null) 
         {
             int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
             
             if (orientation != -1) 
             {
                 // We only recognize a subset of orientation tag values.
                 switch(orientation) 
                 {
                     case ExifInterface.ORIENTATION_ROTATE_90:
                         degree = 90;
                         break;
                         
                     case ExifInterface.ORIENTATION_ROTATE_180:
                         degree = 180;
                         break;
                         
                     case ExifInterface.ORIENTATION_ROTATE_270:
                         degree = 270;
                         break;
                 }

             }
         }
         
         return degree;
     }
     public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees) 
     {
         if ( degrees != 0 && bitmap != null ) 
         {
             Matrix m = new Matrix();
             m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
             try 
             {
                 Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                 if (bitmap != b2) 
                 {
                 	bitmap.recycle();
                 	bitmap = b2;
                 }
             } 
             catch (OutOfMemoryError ex) 
             {
                 // We have no memory to rotate. Return the original bitmap.
             }
         }
         
         return bitmap;
     }
	public static void  showErrPage(Context context, String url, WebView view)
	{
		ToastMessage(context, context.getResources().getString(R.string.api_http_alert));
		String err_msg="<html>\n" +
				"<head>\n" +
				"<script>\n" +
				"function refresh_url() {\n" +
				"    window.location.assign(\""+url+"\")\n" +
				"}\n" +
				"</script>\n" +
				"</head>\n" +
				"<body align=\"center\">인터넷 연결을 확인하거나 잠시 후 이용해주세요.<br><br>\n" +
				"\n" +
				"<input type=\"button\" value=\"새로고침\" onclick=\"refresh_url()\">\n" +
				"\n" +
				"</body>\n" +
				"</html>";
		view.loadData(err_msg,  "text/html; charset=UTF-8", null);

	}
}
