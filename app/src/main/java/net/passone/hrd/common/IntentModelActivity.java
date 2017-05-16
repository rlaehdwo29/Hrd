package net.passone.hrd.common;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import net.passone.hrd.LoginActivity;
import net.passone.hrd.R;
import net.passone.hrd.adapter.Adapter;
import net.passone.hrd.adapter.DBmanager;
import net.passone.hrd.adapter.HttpHelper;
import net.passone.hrd.adapter.OnResponseListener;
import net.passone.hrd.container.Sign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

public class IntentModelActivity extends SherlockActivity implements OnClickListener, OnResponseListener {
	protected static final int HOME = 15;
	public boolean isMain=false;
	protected IntentModelActivity self = this;
	protected Adapter _api= new Adapter();
	DBmanager db_manager;
	SQLiteDatabase db;
	public CookieManager cookieManager;
	HttpHelper hh=new HttpHelper();

	protected boolean finish_alert=false;
	//	public Model getIntentModel() {
	//		Bundle extras = getIntent().getExtras();
	//		return new Model(this, (Map) extras.getSerializable("model"));
	//	}





	public void setProgress(int resId, int max, int progress) {
		((ProgressBar) findViewById(resId)).setMax(max);
		((ProgressBar) findViewById(resId)).setProgress(progress);
	}

	public void setText(int resId, String text) {
		((TextView) findViewById(resId)).setText(text);
	}

	public String getDeviceID() {
		String uid = "";
		uid = "35" + //we make this look like a valid IMEI
				Build.BOARD.length()%10+ Build.BRAND.length()%10 +
				Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
				Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
				Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
				Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
				Build.TAGS.length()%10 + Build.TYPE.length()%10 +
				Build.USER.length()%10 ; //13 digits

		return uid;

	}

	public void setImageFromURL(int resId, String url) {
		try {
			URLConnection conn = new URL(url).openConnection();
			((ImageView) findViewById(resId)).setImageDrawable(Drawable.createFromStream(conn.getInputStream(), "src"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setImageResource(int iconResId, int drawableResId) {
		((ImageView) findViewById(iconResId)).setImageResource(drawableResId);
	}

//	@Override
//	public void onBackPressed() {
//				TabBar tabBar = (TabBar) findViewById(R.id.tabBar);
//
//				if (TabRegistry.globalTabBar && tabBar != null && this.belongsTo(tabBar.tabInfos)) {
//					Intent intent = new Intent(this, MainActivity.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					startActivity(intent);
//					overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//				}
//				else if(isMain) {
//					if(finish_alert)
//					{
//						finish();
//		//				CUser.uid="";
//		//				CUser.userid="";
//		//				CUser.target="";
//					}
//					else
//					{
//						Util.ToastMessage(this, "?�로버튼???�번 ???�르�?종료?�니??");
//						finish_alert=!finish_alert;
//					}
//				}
//				else
//					finish();
//	}

	public static void closeActivity(final Context context) {


		((Activity)context).finish();
		System.exit(0);
	}


	public void setTitleButton(ImageButton btn, int resource)
	{
		btn.setImageResource(resource);
		btn.setVisibility(View.VISIBLE);
		btn.setOnClickListener(this);
	}

	@Override
	public void onConfigurationChanged (Configuration newConfig)
	{

		super.onConfigurationChanged(newConfig);

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public void logout()
	{
		db_manager = new DBmanager(this,"UserInfo.db");
		db = db_manager.getWritableDatabase();
		String sql = "delete from userinfo";
		db.execSQL(sql);
		Environments.SAVE_ACCOUNT_INFO=false;
		Environments.save(this);
		db.close();
		finish();
		startActivity(new Intent(this, LoginActivity.class));
	}
	public void loadUserInfo() {
		DBmanager db_manager= new DBmanager(this,"UserInfo.db");
		SQLiteDatabase db=db_manager.getReadableDatabase();
		Cursor tw_cursor = db.query("userinfo", new String[] {"mno","userid","siteid","password"}, null, null, null, null, null);
		//	values.put("uid", StaticVars.uid);
		String userid="",siteid="";
		try {
			Log.d("passone","db count="+tw_cursor.getCount());
			if (tw_cursor.getCount()!= 0) {
				tw_cursor.moveToFirst();
				for(int i=0; i<tw_cursor.getCount();i++)
				{
					CUser.mno=tw_cursor.getString(0);
					userid=tw_cursor.getString(1);
					CUser.userid=userid;
					CUser.passwd=tw_cursor.getString(3);
					CUser.siteid=tw_cursor.getString(2);
					tw_cursor.moveToNext();
				}
				CUser.userid=userid;
				DefaultHttpClient client = HttpHelper.client;
				BasicClientCookie cookie1 = new BasicClientCookie("mno", CUser.mno);
				cookie1.setDomain(Constants.cookieUrl);
				cookie1.setPath("/");
				client.getCookieStore().addCookie(cookie1);

				BasicClientCookie cookie2 = new BasicClientCookie("userid", userid);
				cookie2.setDomain(Constants.cookieUrl);
				cookie2.setPath("/");
				client.getCookieStore().addCookie(cookie2);
				BasicClientCookie cookie3 = new BasicClientCookie("groupcode", CUser.siteid);
				cookie3.setDomain(Constants.cookieUrl);
				cookie3.setPath("/");
				client.getCookieStore().addCookie(cookie3);

			}
		} catch(Exception e) {
		}

		tw_cursor.close();
		if(db!=null)
			db.close();
	}
	public void onResponseReceived(int api, Object result) {
		// TODO Auto-generated method stub
		if (result != null) {

			Sign signIn = (Sign)result;

			if (signIn.result.trim().equals("OK")) {
//				self.onRestart();
			} else if (signIn.result.trim().equals("FAIL")) {

				Util.ToastMessage(this, signIn.message);

			}

		} else {
			Util.PopupMessage(this, getResources().getString(R.string.api_http_alert));
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
