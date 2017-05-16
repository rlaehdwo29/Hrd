package net.passone.hrd.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * <b>����</b><br><br>
 *
 */
public class Preference {

	String preferenceFileName;
	SharedPreferences prefs;

	/**
	 * Constructor<br>
	 * @param context<br>
	 * @param preference filename<br>
	 */
	public Preference(Context ctx, String preferenceFileName) {
		this.preferenceFileName = preferenceFileName;
		this.prefs = ctx.getSharedPreferences(this.preferenceFileName, Context.MODE_PRIVATE);
	}

	/*
	 * Write preference
	 */

	public void write(String key, String value) {		
		SharedPreferences.Editor editor = this.prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void write(String key, boolean value) {		
		SharedPreferences.Editor editor = this.prefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void write(String key, int value) {		
		SharedPreferences.Editor editor = this.prefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public void write(String key, long value) {		
		SharedPreferences.Editor editor = this.prefs.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public void write(String key, float value) {		
		SharedPreferences.Editor editor = this.prefs.edit();
		editor.putFloat(key, value);
		editor.commit();
	}	

	/*
	 * Read preference
	 */

	public String read(String key, String defValue) {
		return prefs.getString(key, defValue);
	}

	public boolean read(String key, boolean defValue) {
		return prefs.getBoolean(key, defValue);
	}

	public int read(String key, int defValue) {
		return prefs.getInt(key, defValue);
	}

	public long read(String key, long defValue) {
		return prefs.getLong(key, defValue);
	}

	public float read(String key, float defValue) {
		return prefs.getFloat(key, defValue);
	}
}

