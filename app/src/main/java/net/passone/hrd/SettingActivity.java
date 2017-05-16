package net.passone.hrd;

import net.passone.hrd.adapter.DBmanager;
import net.passone.hrd.adapter.HttpHelper;
import net.passone.hrd.adapter.OnResponseListener;
import net.passone.hrd.adapter.WaitDialog;
import net.passone.hrd.common.CUser;
import net.passone.hrd.common.Constants;
import net.passone.hrd.common.Environments;
import net.passone.hrd.common.IntentModelActivity;
import net.passone.hrd.common.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.cookie.Cookie;
import org.w3c.dom.Text;

import java.io.File;
import java.util.regex.Pattern;

public class SettingActivity extends IntentModelActivity implements OnResponseListener,OnClickListener{
	SettingActivity self=null;

	Cursor cursor;
	String uid,url;
	WebView mWebView;
	ImageButton btn_home,btn_logout;
	CheckBox chk_3g,chk_alarm;
    private final Handler handler = new Handler();
    AlertDialog alert;
	protected static WaitDialog dialog;
	private ProgressDialog progressDialog;
	Button btn_update,btn_login_info;
	RelativeLayout layout_newver;
	String update_url="",str_current="";
	CookieManager cookieManager;
	HttpHelper hh=new HttpHelper();
	TextView tv_id,tv_current,tv_new;
	DBmanager db_manager;	
	SQLiteDatabase db;
	Context context;
	OnResponseListener callback;   
	int progress_cnt=0;
	RelativeLayout titlebar;
	TextView tv_storage;
	Button btn_filedelete;
	long lSize=0;
	File file;
	public void onCreate(Bundle savedInstanceState) {
		self=this;
		context = this;							// 컨텍스트
		callback = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		((TextView)findViewById(R.id.titleText)).setText("설정");
		titlebar=(RelativeLayout)findViewById(R.id.titleBar);
		titlebar.setVisibility(View.VISIBLE);
		layout_newver=(RelativeLayout)findViewById(R.id.layout_newver);
		btn_login_info=(Button)findViewById(R.id.btn_login_info);
		btn_login_info.setOnClickListener(self);
		btn_update=(Button)findViewById(R.id.btn_update);
		btn_update.setOnClickListener(self);
		tv_storage = (TextView)findViewById(R.id.tv_storage);
		btn_filedelete = (Button)findViewById(R.id.btn_filedelete);
		btn_filedelete.setOnClickListener(this);

		btn_logout=(ImageButton)findViewById(R.id.rightImageButton);
		setTitleButton(btn_logout,R.drawable.btn_close);
        CookieSyncManager.createInstance(this);
        cookieManager=CookieManager.getInstance();
        if(CUser.mno.equals(""))
        {
          cookieManager.removeAllCookie();
    		hh.clearCookie();	
        }
        //버전정보 api 호출
//		_api.version("android", context, callback);

        tv_id=(TextView)findViewById(R.id.tv_id);
        tv_current=(TextView)findViewById(R.id.tv_current);
        tv_new=(TextView)findViewById(R.id.tv_new);
        str_current=Util.getVersion(self);
        tv_current.setText(str_current);
        loadDatabase();
		 chk_3g=(CheckBox)findViewById(R.id.chk_3g);
		 chk_3g.setChecked(Environments.ALLOW_3G);
		 chk_3g.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Environments.ALLOW_3G = isChecked;
					Environments.save(self);
				
				}
			});
			file = new File(Util.getDownloadPath());
			lSize = Util.dirSize(file);
			if(lSize > 0){
				lSize = lSize/1048576;
			}
			tv_storage.setText(lSize+"MB");

			//Log.d("passone","size="+lSize);

		 chk_alarm=(CheckBox)findViewById(R.id.chk_alarm);

		 chk_alarm.setChecked(Environments.ALLOW_ALRAM);
		 chk_alarm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Environments.ALLOW_ALRAM = isChecked;
					Environments.save(self);

				}
			});
	
    }
	@Override
	public void onClick(View view) {
		switch(view.getId())
		{
		case R.id.btn_login_info:
			logout();
			break;
		case R.id.rightImageButton:
			onBackPressed();
			break;
		case R.id.btn_filedelete:
			if (file.exists() && lSize > 0) {
				Util.alertYesNo(context, R.string.confirm_delete, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {


						long result = 0;
						File[] fileList = file.listFiles();
						for (int i = 0; i < fileList.length; i++) {
							// Recursive call if it's a directory

							File tmpfile = new File(fileList[i].getPath());
							if (tmpfile.isFile() && tmpfile.exists()) {
								tmpfile.delete();
								db_manager = new DBmanager(self, "UserInfo.db");
								db = db_manager.getReadableDatabase();
								String sql = "SELECT * FROM download  WHERE filename=?";
								Cursor cursor = db.rawQuery(sql, new String[]{tmpfile.getName()});

								try {
									if (cursor.getCount() != 0) {
										String dsql = "Delete FROM download  WHERE filename='" + tmpfile.getName() + "'";
										db.execSQL(dsql);
									}
								} catch (Exception e) {
								}
								cursor.close();


								if (db != null)
									db.close();
							}

						}//end for
						file = new File(Util.getDownloadPath());
						lSize = Util.dirSize(file);
						if(lSize > 0){
							lSize = lSize/1048576;
						}
						tv_storage.setText(lSize+"MB");
					}//end if

				}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
			}else{
				Util.alertOk(context, R.string.no_delete);
			}

			break;

		
		}
	
		
	}
	public void logout()
	{
		db = db_manager.getWritableDatabase();
		String szNoticeCookieKey,szNoticeCookieValue="";

		CookieSyncManager.createInstance(this);
        cookieManager=CookieManager.getInstance();

    	cookieManager.removeAllCookie();
		hh.clearCookie();
		CUser.mno="";
		CUser.siteid="";
		String sql = "delete from userinfo";
		db.execSQL(sql);
		Environments.SAVE_ACCOUNT_INFO=false;
		Environments.save(self);
		db.close();
		finish();
		startActivity(new Intent(self,LoginActivity.class));
	}

	    public void onStart() { 

	        super.onStart(); 

	        CookieSyncManager.createInstance(this);

	    }   
		
		@Override

	    public void onPause(){

	    	super.onPause();

	    	CookieSyncManager.getInstance().stopSync();

	    }
		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			if(alert!=null)
				alert.dismiss();
			super.onResume();

				// TODO Auto-generated method stub
				
				super.onResume();
		}
	
	    
		
		public void loadDatabase() {

			db_manager = new DBmanager(this,"UserInfo.db");
			db=db_manager.getReadableDatabase();
			Cursor userinfo_cursor = db.query("userinfo", new String[] {"userid","siteid"}, null, null, null, null, null);
			try {
				Log.d("passone","db count="+userinfo_cursor.getCount());
				if (userinfo_cursor.getCount()!= 0) {
					userinfo_cursor.moveToFirst();
					
						CUser.siteid=userinfo_cursor.getString(1).toString();
						tv_id.setText(userinfo_cursor.getString(0).toString());
						userinfo_cursor.moveToNext();
					
				 
					
				}
			} catch(Exception e) {	    		
			}
		}
		public void onResponseReceived(int api, Object result) {

			Util.debug("Login result    :   "+result);	
//			switch(api) {
//			//버전 정보
//			case Api.VERSION :
//
//				if (result != null) {
//
//					Version ver = (Version)result;	    		
//
//					if (ver.version.trim().length()>0) {  			
//						tv_new.setText(ver.version);
//						if(ver.link.trim().length()>0)
//						{
//							update_url=ver.link.replace("\\/", "/");
//						}
//						if(!str_current.equals(ver.version.trim()))
//						{
//							layout_newver.setVisibility(View.VISIBLE);
//
//						}
//						else
//						{
//							layout_newver.setVisibility(View.GONE);
//
//						}
//						
//					} else {    			
//						Util.ToastMessage(getParent(), "버전 정보 로딩 실패");
//						
//					}  
//
//				} else {
//					// 수신, 파싱 오류
//					Util.PopupMessage(getParent(), getResources().getString(R.string.api_http_alert));
//				}			
//				break; 
//			}		
		}
		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
			finish();
			startActivity(new Intent(self,MainActivity.class));

		}
}
