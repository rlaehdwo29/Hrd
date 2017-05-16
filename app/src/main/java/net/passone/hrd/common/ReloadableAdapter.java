package net.passone.hrd.common;

import android.content.Context;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

public abstract class ReloadableAdapter extends BaseAdapter {
	protected final Context context;
	protected ReloadableAdapter parent;

	public ReloadableAdapter(Context context) {
		this(context, false);
	}
	
	public ReloadableAdapter(Context context, boolean loadLocal) {
		this.context = context;
		if (loadLocal) loadLocal();
	}

	public abstract OnItemClickListener getListener();	
	public abstract void loadLocal();
	
	
	
	public abstract void syncWithServer();

	
	public int getCount() {
		return 1;
	}


	
	public long getItemId(int position) {
		return 0;
	}

	public void setParent(ReloadableAdapter parent) {
		this.parent = parent;
	}
	
	
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if (parent != null) parent.notifyDataSetChanged();
	}


	public boolean hideWhenEmpty() {
		return false;
	}
}
