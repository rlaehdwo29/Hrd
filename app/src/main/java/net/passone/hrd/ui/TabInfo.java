package net.passone.hrd.ui;

public class TabInfo {
	public final Class clazz;
	public final String title;
	public final int iconResId;
	public final int iconSelectedResId;

	public TabInfo(String title, int iconResId, int iconSelectedResId, Class clazz) {
		this.title = title;
		this.iconResId = iconResId;
		this.iconSelectedResId = iconSelectedResId;
		this.clazz = clazz;
	}

	public TabInfo(String title, int iconResId, Class clazz) {
		this(title, iconResId, 0, clazz);
	}
}
