package net.passone.hrd;

import java.util.ArrayList;
import java.util.List;

import net.passone.hrd.adapter.AdapterItemManager;
import net.passone.hrd.adapter.Api;
import net.passone.hrd.adapter.DBmanager;
import net.passone.hrd.adapter.OnResponseListener;
import net.passone.hrd.common.CUser;
import net.passone.hrd.common.Constants;
import net.passone.hrd.common.Environments;
import net.passone.hrd.common.IntentModelActivity;
import net.passone.hrd.common.StaticVars;
import net.passone.hrd.common.Util;
import net.passone.hrd.container.Sign;
import net.passone.hrd.container.Site;
import net.passone.hrd.container.SiteItem;
import net.passone.hrd.container.Version;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieSyncManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LoginActivity extends IntentModelActivity implements OnClickListener, OnResponseListener{
TextView tv_company;
ImageButton btn_search,btn_login;
CheckBox chk_autologin;
EditText edit_id,edit_password;
LoginActivity self=null;
Context context;
DBmanager db_manager;	
SQLiteDatabase db;
OnResponseListener callback;   
String userid="",passwd="",siteid="",device_token="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		  setContentView(R.layout.login);
			CookieSyncManager.createInstance(this);

		  self=this;
		  context=this;
		  callback=this;
		  initializeDebuggable();
		  loadEnvironments();
		  loadDatabase();
		  initialize();
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
		Cursor appinfo_cursor = db.query("version", new String[] {"version"}, "version="+Util.getVersion(this), null, null, null, null);

		try {
			if (appinfo_cursor.getCount()!= 0) {
				appinfo_cursor.moveToFirst();
				Cursor tw_cursor = db.query("userinfo", new String[] {"userid","password","siteid"}, null, null, null, null, null);
				Cursor device_cursor = db.query("device", new String[] {"device_token"}, null, null, null, null, null);

				try {
					Log.d("passone","db count="+tw_cursor.getCount());
					if (tw_cursor.getCount()!= 0) {
						tw_cursor.moveToFirst();
						for(int i=0; i<tw_cursor.getCount();i++)
						{
							userid=tw_cursor.getString(0);
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
								device_cursor.moveToNext();
							}
						} 

					}
				} catch(Exception e) {	    		
				}
				device_cursor.close();
				tw_cursor.close(); 
				edit_id.setText(userid);
				edit_password.setText("passwd");
			}
			else
			{ //현재 버전정보가 없으면 db초기화 후 현재 버전정보 insert
				db.delete("userinfo", null, null);
//				db.delete("device", null, null);
//				db.delete("appinfo", null, null);
//				db_manager.onUpgrade(db, 0, 1);
				String sql = "insert into version ( version) values ('"+Util.getVersion(this)+"')";
				db.execSQL(sql);

			}
		} catch(Exception e) {
		}
		appinfo_cursor.close();


		if(db!=null)
			db.close();
	}   
	 public void initialize() {
			_api.version(context, callback);

		  	tv_company=(TextView)findViewById(R.id.tv_company);
			edit_id = (EditText)findViewById(R.id.edit_id);
			edit_password = (EditText)findViewById(R.id.edit_password);
			edit_id.setOnClickListener(self);
			edit_password.setOnClickListener(self);
			CUser.siteid="";
			/////////////////////////
//			edit_id.setText("test1234");
//			edit_password.setText("1234");
//			tv_company.setText("CAMPUS 21");
//			siteid="35";
			///////////////////////////////

			edit_id.setOnEditorActionListener(new OnEditorActionListener(){

				 public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					  // TODO Auto-generated method stub

					  if ((actionId == EditorInfo.IME_ACTION_DONE) ||
					   (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
						  InputMethodManager imm = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );
						  imm.toggleSoftInput( 0, 0 );
					  }
					  return false;
				 }
			});

			edit_password.setOnEditorActionListener(new OnEditorActionListener(){

				 public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					  // TODO Auto-generated method stub
					  
					  if ((actionId == EditorInfo.IME_ACTION_DONE) ||
					   (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
						  InputMethodManager imm = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );
						  imm.toggleSoftInput( 0, 0 );
					  }  
					  return false;
				 }
			});

			chk_autologin=(CheckBox)findViewById(R.id.chk_autologin);
			chk_autologin.setChecked(Environments.SAVE_ACCOUNT_INFO);
			chk_autologin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Environments.SAVE_ACCOUNT_INFO = isChecked;
					Environments.save(self);
				
				}
			});
			btn_login = (ImageButton)findViewById(R.id.btn_login);
			btn_login.setOnClickListener(this);
			 tv_company.setOnClickListener(this);
		 ((ImageButton)findViewById(R.id.btn_findid)).setOnClickListener(this);
		 ((ImageButton)findViewById(R.id.btn_findpwd)).setOnClickListener(this);

		 if(Environments.SAVE_ACCOUNT_INFO && (!userid.equals("")))
			{
				edit_id.setText(userid.trim());
				userid = userid.trim();

				//Log.d("passone","user_id="+userid+"&passwd="+passwd+"&siteid="+CUser.siteid+"&device_token="+CUser.device_token);
				_api.signIn(userid, passwd, CUser.siteid,/*Util.getDeviceID(context),*/"",CUser.device_token, context, callback);
				

			}
			
	}
	public void onResponseReceived(int api, Object result) {
		// TODO Auto-generated method stub
		Util.debug("Login result    :   "+result);	
		switch(api) {

		// 로그인
		case Api.SIGN_IN :

			if (result != null) {

				Sign signIn = (Sign)result;
				Log.d("passone","sigin result="+signIn.result.trim());

				// 로그인 성공
				if (signIn.result.trim().equals("OK")) {  			
					db_manager = new DBmanager(this,"UserInfo.db");
					addEvent();	
					startActivity(new Intent(self,MainActivity.class));
					finish();
					// 로그인 실패
				} else if (signIn.result.trim().equals("FAIL")) {    			
					Util.ToastMessage(this, signIn.message);

				}  

			} else {
				// 수신, 파싱 오류
				Util.PopupMessage(this, getResources().getString(R.string.api_http_alert));
			}			
			break; 
			
		case Api.ID_SEARCH :

			if (result != null) {

				AdapterItemManager.AddSite((List<Site>) result);
				setGroupCate();
			} else {
				// 수신, 파싱 오류
				Util.PopupMessage(this, getResources().getString(R.string.api_http_alert));
			}
			break; 
		case Api.VERSION :
			if (result != null) {
				final Version ver = (Version)result;	    		
				if(ver.servercheck>0)
				{
					String serverStr="";
					if(ver.serverstr.length()>0)
					{
						serverStr=ver.serverstr;
					}
					else 
						serverStr="서버 점검중입니다.";
					Util.alert(self, "New Version", serverStr, "확인", null, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {					
							finish();
						}
					}, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {					
						}
					});
				}
				else {

					if (ver.version.trim().length()>0) {  			
						String str_current=Util.getVersion(self);
						if(Double.parseDouble(ver.version.trim())>Double.parseDouble(str_current))
						{
							String alert_str="현재버전: "+str_current+"\n최신버전: "+ver.version.trim()+"\n\n- What's New -\n"+ver.newcontent;

							Util.alert(self, "New Version", alert_str, "업데이트하기", "취소", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {					
									if(!ver.link.equals(""))
									{
										startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ver.link.replace("\\/", "/"))));		
									}
								}
							}, new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {					
								}
							});

						}


					} else {    			
						Util.ToastMessage(this, "버전 정보 로딩 실패");

					}  
				}
			} else {
				// 수신, 파싱 오류
				Util.PopupMessage(this, getResources().getString(R.string.api_http_alert));
			}			
			break; 
			
		}		
	}
	/**
	 * 로그인 OK 후 유저정보 저장<br>
	 * @author boram han
	 */
	private void addEvent() {

		db = db_manager.getWritableDatabase();

		String sql = "delete from userinfo";
		db.execSQL(sql);
// 로그인 초기 정보 저장
		sql = "insert into userinfo ( mno,userid, password,siteid) values ('"+CUser.mno+"', '"+userid+"', '"+passwd+"', '"+CUser.siteid+"')";
		db.execSQL(sql);

		db.close();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		userid=edit_id.getText().toString();
		userid = userid.trim();
		passwd=edit_password.getText().toString();
		switch(v.getId())
		{
		case R.id.btn_login:
			if(userid.length()>0 && passwd.length()>0 && CUser.siteid.length()>0)
				_api.signIn(userid, passwd, CUser.siteid,Util.getDeviceID(self), CUser.device_token, context, callback);
			break;
		case R.id.tv_company:
			Log.d("passone","length: "+userid.length());
			if(userid.length()>0)
			{
				userid = userid.trim();
				_api.idSearch(userid, context, callback);
			}
			else
			{
				Util.ToastMessage(self, "아이디를 입력해 주세요");
			}
			break;
			case R.id.btn_findid:
				startActivity(new Intent(this,WebActivity.class).putExtra("url",Constants.findidUrl));
				break;
			case R.id.btn_findpwd:
				startActivity(new Intent(this,WebActivity.class).putExtra("url",Constants.findpwUrl));
				break;
		}
		super.onClick(v);
	}
	private void setGroupCate() {
		// TODO Auto-generated method stub

		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("그룹  카테고리를 선택하세요");
		List<String> site_items = new ArrayList<String>();
		if(StaticVars.siteItems.size()>0){
			for(int i=0;i<StaticVars.siteItems.size();i++)
			{
				SiteItem site_item=StaticVars.siteItems.get(i);
				site_items.add(site_item.getsiteName());

			}
			ArrayAdapter<String> aa_site =new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, site_items);

			builder.setSingleChoiceItems(aa_site, -1, new DialogInterface.OnClickListener() {


				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					//					Util.ToastMessage(self, StaticVars.groupItems.get(which).getGrpid());
					CUser.siteid=String.valueOf(StaticVars.siteItems.get(which).getsiteId());
					tv_company.setText(StaticVars.siteItems.get(which).getsiteName());
				}
			});
			builder.setPositiveButton("확인", null);
			AlertDialog alert = builder.create();
			alert.show();
		}
		else
			CUser.siteid="";
	}
	/**
	 * ����� ������ ����<br>
	 * Manifaest�� debuggable ���¸� ���<br>
	 */
	public void initializeDebuggable() {
		Constants.debuggable = 
				(0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE));
	}
}
