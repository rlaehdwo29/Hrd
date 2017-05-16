package net.passone.hrd.common;

import java.util.Iterator;
import java.util.List;

import net.passone.hrd.IntroActivity;
import net.passone.hrd.R;
import net.passone.hrd.adapter.DBmanager;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
/**
 * <b>?�시 리시�?</b><br>
 * @author boram han
 */
public class C2dm_BroadcastReceiver extends BroadcastReceiver {


	static String registration_id = null;
	static String c2dm_msg = "";
	DBmanager db_manager;	
	SQLiteDatabase db;	
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
    		
    		handleRegistration(context, intent);
    		
    	} else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
    		
    		c2dm_msg = intent.getExtras().getString("msg");
    		
    		System.out.println("c2dm_msg======>"+c2dm_msg);
    		Toast toast = Toast.makeText(context, c2dm_msg, Toast.LENGTH_LONG );
 			toast.setGravity( Gravity.TOP | Gravity.CENTER, 0, 150 );
 			toast.show();
 			showNotification(context);
 			
    	}
    }
    
    private void showNotification(Context context) {
		// TODO Auto-generated method stub
    	NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		    PendingIntent pendingIntent = PendingIntent.getActivity(
		    context, 0,new Intent(context, IntroActivity.class), 0);
		    String ticker = c2dm_msg;
		    String title = "HomeTong-교사";
		    String text = c2dm_msg;
		    
		    //?�면 켜짐 ?�무 체크
		    KeyguardManager km = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
		    // Create Notification Object
//		    if(GetClassName(context) && !km.inKeyguardRestrictedInputMode()){}
//		    else if(km.inKeyguardRestrictedInputMode()){
		      
		     PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		     PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK , "push");
		     wl.acquire();
		     Notification notification = new Notification(R.drawable.ic_launcher,ticker, System.currentTimeMillis());
		     notification.defaults |= (Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
		     notification.ledARGB=0xff00ff00;
		     notification.ledOnMS=300;
		     notification.ledOffMS=1000;
		     notification.flags = notification.flags |  Notification.FLAG_ONGOING_EVENT |Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS  ;
		     
		     notification.setLatestEventInfo(context,title, text, pendingIntent);
		     nm.notify(1234, notification);
//		     try {
//				nm.wait(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		     wl.release();
//		    }
	}

	private Boolean GetClassName(Context context) {
		// TODO Auto-generated method stub
		ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List info;
        info = activityManager.getRunningTasks(7);
         for (Iterator iterator = info.iterator(); iterator.hasNext();)  {
             RunningTaskInfo runningTaskInfo = (RunningTaskInfo) iterator.next();
             if(runningTaskInfo.topActivity.getClassName().equals("net.passone.hrd")) {
                 return true;
             }
             else return false;
         }
		return false;
	}

	private void handleRegistration(Context context, Intent intent) {
    	
    	registration_id = intent.getStringExtra("registration_id");
    	
    	System.out.println("registration_id====>"+registration_id);
    	if (intent.getStringExtra("error") != null) {
    	
    		Log.v("C2DM_REGISTRATION",">>>>>" + "Registration failed, should try again later." + "<<<<<");
    		
    	} else if (intent.getStringExtra("unregistered") != null) {
    		CUser.device_token="";
    		Log.v("C2DM_REGISTRATION",">>>>>" + "unregistration done, new messages from the authorized sender will be rejected" + "<<<<<");
    		db_manager = new DBmanager(context,"UserInfo.db");
    		db = db_manager.getWritableDatabase();
    		String sql = "delete from device";
    		db.execSQL(sql);
    		db.close();
    	} else if (registration_id != null) {
       // 	Log.d("passone","registration_id length="+registration_id.length());
    		CUser.device_token=registration_id;
    		System.out.println("registration_id complete!!");
    		db_manager = new DBmanager(context,"UserInfo.db");
    		db = db_manager.getWritableDatabase();
    		String sql = "delete from device";
    		db.execSQL(sql);
    		sql = "insert into device (device_token) values('"+registration_id+"')";
    		db.execSQL(sql);
    		db.close();
    		
    		
    		}
    		
    }
    
    
}