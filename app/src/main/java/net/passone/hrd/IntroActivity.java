package net.passone.hrd;
import net.passone.hrd.adapter.Api;
import net.passone.hrd.adapter.DBmanager;
import net.passone.hrd.adapter.HttpHelper;
import net.passone.hrd.adapter.OnResponseListener;
import net.passone.hrd.common.CUser;
import net.passone.hrd.common.Constants;
import net.passone.hrd.common.Environments;
import net.passone.hrd.common.IntentModelActivity;
import net.passone.hrd.common.StaticVars;
import net.passone.hrd.common.Util;
import net.passone.hrd.container.Sign;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
/**
 * <b>Intro �� </b><br>
 * �� ���۽� ����Ǵ� ���ĺ�<br>
 * @author boram han
 */
public class IntroActivity extends IntentModelActivity implements OnResponseListener{
	/** Called when the activity is first created. */
	DBmanager db_manager;	
	SQLiteDatabase db;	
	String id="",passwd="",device_token="",siteid="";
	Context context;
	OnResponseListener callback;
	HttpHelper hh= new HttpHelper();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);
		CookieSyncManager.createInstance(this);
		if(Environments.PUSH_A01)
			startPushService();
		initialize();
		start();
	}
	public void start() {

		(new Handler()).postDelayed(new Runnable() {
			public void run() {		
				Log.d("passone",id);
			
				if(Environments.SAVE_ACCOUNT_INFO && (!id.equals("")))
				{
					
					_api.signIn(id, passwd, CUser.siteid,Util.getDeviceID(context),CUser.device_token, context, callback);


				}
				else
				{
					cookieManager= CookieManager.getInstance();
					cookieManager.removeAllCookie();
					hh.clearCookie();
					goLogin();
				}
			}
		}, 2000);	/* 2 seconds */

	}

	public void goLogin() {
		Intent i=new Intent(this,LoginActivity.class);
		startActivity(i);
		finish();
	}
	public void goMain() {
		Intent i=new Intent(this,MainActivity.class);
		startActivity(i);
		finish();
	}
	public void initialize() {

		initializeDebuggable();
		context=this;
		callback=this;
		HttpHelper hh=new HttpHelper();

		loadEnvironments();
		loadDatabase();
	}
	public void startPushService()
	{
		//C2DM ���ID �߱�
		Log.d("passone","start push");

		Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
		registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0)); // ���ø����̼�ID
		registrationIntent.putExtra("sender", "bbo0824@gmail.com"); //������ID
		registrationIntent.setPackage("net.passone.hrd");
		startService(registrationIntent); //���� ����(���ID�߱޹ޱ�)
		// ������ ������ "app"�� "sender"�� ����� �����Ͻô°� �ƴ϶� ���ۿ��� �ʿ��� ��������Դϴ�.
		//	getUserInfo(self);


	}
	public void stopPushService()
	{
		//Android C2DM�� Push�޽����� �׸� �ްڴٴ� �޽����� ������ Intent
		Intent unregIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
		unregIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
		startService(unregIntent);
	}
	/**
	 * ����� ������ ����<br>
	 * Manifaest�� debuggable ���¸� ���<br>
	 */
	public void initializeDebuggable() {
		Constants.debuggable = 
				(0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE));
	}

	/**
	 * �ɼ� ���� ������ �ҷ��´�. �������� �ʴ� ��� �ʱⰪ�� ����<br>
	 */
	public void loadEnvironments() {
		Environments.load(this);    	
		if (!Environments.PREFERENCE_EXIST) {
			Environments.save(this);
		}
	}

	/**
	 * ������ ���̽��� �ҷ��´�. �ڵ��α����� ���� �������� �ε�.<br>
	 */
	public void loadDatabase() {

		db_manager = new DBmanager(this,"UserInfo.db");
		db=db_manager.getReadableDatabase();
		//db에서 버전정보 쿼리
		Cursor appinfo_cursor = db.query("version", new String[] {"version, bright"}, "version="+Util.getVersion(this), null, null, null, null);
//		Cursor appinfo_cursor = db.query("version", new String[] {"version"}, "version="+Util.getVersion(this), null, null, null, null);
		Log.d("passone","db couccnt="+appinfo_cursor.getCount());

		try {
			if (appinfo_cursor.getCount()!= 0) {
				appinfo_cursor.moveToFirst();
				CUser.bright=appinfo_cursor.getFloat(1);

			}
			else
			{ //현재 버전정보가 없으면 db초기화 후 현재 버전정보 insert
			//	db.delete("userinfo", null, null);
			//	db.delete("device", null, null);
			//	db.delete("appinfo", null, null);
//				db_manager.onUpgrade(db, 0, 2);
				Cursor cursor = db.rawQuery("select version from version  ",null,null);
				cursor.moveToFirst();
				if(cursor.getCount()>0){
					String sql = "update version  set version='"+Util.getVersion(this)+"'";
					db.execSQL(sql);
				}else {
					String sql = "insert into version ( version) values ('" + Util.getVersion(this) + "')";
					db.execSQL(sql);
				}

			}
		} catch(Exception e) {	    		
		}
		Cursor tw_cursor = db.query("userinfo", new String[] {"userid","password","siteid"}, null, null, null, null, null);
		Cursor device_cursor = db.query("device", new String[] {"device_token"}, null, null, null, null, null);

		try {
			Log.d("passone","db count="+tw_cursor.getCount());
			if (tw_cursor.getCount()!= 0) {
				tw_cursor.moveToFirst();
				for(int i=0; i<tw_cursor.getCount();i++)
				{
					id=tw_cursor.getString(0);
					passwd=tw_cursor.getString(1);
					CUser.siteid=tw_cursor.getString(2);
					tw_cursor.moveToNext();
				}
				if (device_cursor.getCount()!= 0) {
					device_cursor.moveToFirst();
					for(int i=0; i<device_cursor.getCount();i++)
					{
						device_token=device_cursor.getString(0);
						CUser.device_token=device_token;
						tw_cursor.moveToNext();
					}
				}

			}
		} catch(Exception e) {
		}
		device_cursor.close();
		tw_cursor.close();
		appinfo_cursor.close();


		if(db!=null)
			db.close();
	}   
	public void onResponseReceived(int api, Object result) {

		Util.debug("Logout result    :   "+result);	
		switch(api) {

		case Api.SIGN_IN :

			if (result != null) {

				Sign signIn = (Sign)result;	    		

				// 로그아웃 성공
				if (signIn.result.trim().equals("OK")) {  			
					StaticVars.uid = signIn.mno;	    			
					goMain();	    		



				} else if (signIn.result.trim().equals("FAIL")) {    			
					Util.ToastMessage(self, "로그인 정보가 변경되어 로그인에 실패하였습니다.");
					goLogin();

				}  

			} 
			else {
				// 수신, 파싱 오류
				Util.PopupMessage(this, getResources().getString(R.string.api_http_alert));
			}			
			break; 


		}	
	}
}