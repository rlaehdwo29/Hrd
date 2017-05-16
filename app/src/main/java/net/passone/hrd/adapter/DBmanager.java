package net.passone.hrd.adapter;

import static android.provider.BaseColumns._ID;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
//import android.util.Log;

public class DBmanager extends SQLiteOpenHelper{
	//private static final String DATABASE_NAME = "History.db";
	private static final int DTATBASE_VERSION =4;
	private String databasename ;

	public DBmanager(Context ctx, String dbname){

		super(ctx, dbname, null, DTATBASE_VERSION);
		databasename = dbname;
		// Log.d("EVENTSDATA", "EventsData ");
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		 Log.d("EVENTSDATA", "SQLiteDatabase start");
		if(databasename.equals(databasename))
		{
			db.execSQL("CREATE TABLE IF NOT EXISTS " + "userinfo" + 
					" ("+_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+"mno"+" TEXT NOT NULL,"+"password"+" TEXT NOT NULL,"+"userid"+" TEXT,"+"siteid"+" TEXT);"
					);
			db.execSQL("CREATE TABLE IF NOT EXISTS " + "device" + 
					" ("+_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+"device_token"+" TEXT);"
					);
			db.execSQL("CREATE TABLE IF NOT EXISTS " + "version" +
					" ("+_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+"version"+" TEXT,"+"isCheck TEXT, bright REAR);"
					);
//			db.execSQL("CREATE TABLE IF NOT EXISTS " + "version" +
//					" ("+_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+"version"+" TEXT,"+"isCheck TEXT);"
//			);

			db.execSQL("CREATE TABLE IF NOT EXISTS " + "appinfo" + 
					" ("+_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+"first_chk"+" TEXT);"
					);
			db.execSQL("CREATE TABLE IF NOT EXISTS download ("+_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, classkey VARCHAR, classcount VARCHAR, studykey VARCHAR, part VARCHAR, page VARCHAR, contentskey VARCHAR, subject TEXT, filename TEXT, htmlurl TEXT, size VARCHAR, chasi VARCHAR, title TEXT);");
			db.execSQL("CREATE TABLE IF NOT EXISTS lecture_progress ("+_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, classcount VARCHAR, studykey VARCHAR, classkey VARCHAR, finish VARCHAR,contentskey VARCHAR,part VARCHAR, duration VARCHAR, markertime VARCHAR, page VARCHAR, userid VARCHAR);");
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		 Log.d("EVENTSDATA", "SQLiteDatabase onUpgrade "+oldVersion+" to "+newVersion);
		if(databasename.equals(databasename))
		{
			if(newVersion==1)
			{
			db.execSQL("DROP TABLE IF EXISTS " + "userinfo");
			db.execSQL("DROP TABLE IF EXISTS " + "device");
			db.execSQL("DROP TABLE IF EXISTS " + "version");
			db.execSQL("DROP TABLE IF EXISTS " + "appinfo");
			db.execSQL("DROP TABLE IF EXISTS " + "download");
			db.execSQL("DROP TABLE IF EXISTS " + "lecture_progress");

			onCreate(db);

			}
			else if(newVersion>=2)
			{
				db.execSQL("CREATE TABLE IF NOT EXISTS download ("+_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, classkey VARCHAR, classcount VARCHAR, studykey VARCHAR, part VARCHAR, page VARCHAR, contentskey VARCHAR, subject TEXT, filename TEXT, htmlurl TEXT, size VARCHAR, chasi VARCHAR, title TEXT);");
				db.execSQL("CREATE TABLE IF NOT EXISTS lecture_progress ("+_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, classcount VARCHAR, studykey VARCHAR, classkey VARCHAR, finish VARCHAR,contentskey VARCHAR,part VARCHAR, duration VARCHAR, markertime VARCHAR, page VARCHAR, userid VARCHAR);");

			}
			if(oldVersion<4)
			{
				boolean bla = existsColumnInTable(db,"version","bright");
				Log.d("EVENTSDATA", "SQLiteDatabase onUpgrade"+newVersion);

				if(!bla)
				{

					db.execSQL("ALTER TABLE version " +
							"ADD bright REAR");

				}
			}
		}

	}
	private boolean existsColumnInTable(SQLiteDatabase inDatabase, String inTable, String columnToCheck) {
		try{
			//query 1 row
			Cursor mCursor  = inDatabase.rawQuery( "SELECT * FROM " + inTable + " LIMIT 0", null );

			//getColumnIndex gives us the index (0 to ...) of the column - otherwise we get a -1
			if(mCursor.getColumnIndex(columnToCheck) != -1)
				return true;
			else
				return false;

		}catch (Exception Exp){
			//something went wrong. Missing the database? The table?
			Log.d("... - existsColumnInTable","When checking whether a column exists in the table, an error occurred: " + Exp.getMessage());
			return false;
		}
	}
}