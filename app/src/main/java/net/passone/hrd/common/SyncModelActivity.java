package net.passone.hrd.common;

import net.passone.hrd.R;
import android.widget.ListView;


public abstract class SyncModelActivity extends IntentModelActivity {

	protected ReloadableAdapter adapter;

	protected ReloadableAdapter getAdapter() {
		if (adapter == null) {
			adapter = prepareAdapter();
			adapter.loadLocal();
		}
		return adapter;
	}

	protected abstract ReloadableAdapter prepareAdapter();
	
	protected ListView getListView() {
		return (ListView) findViewById(R.id.list);
	}
	
	protected void initializeListView() {
		ListView listView = getListView();
		listView.setAdapter(getAdapter());
		listView.setOnItemClickListener(getAdapter().getListener());
		
	}

//	protected void setListEmptyView(int textResId) {
//	//	EmptyCellAdapter adapter = new EmptyCellAdapter(getAdapter(), Styles.CELL_BG, 80, Styles.CELL_TITLE_COLOR, textResId);
////		View emptyView = adapter.getView(0, null, null);
////		((ViewGroup) findViewById(R.id.listFooter)).addView(emptyView);
////		getListView().setEmptyView(emptyView);
//
//			View view = View.inflate(this, R.layout.empty_cell, (ViewGroup)findViewById(R.id.listFooter));
//			findViewById(R.id.tv_empty).setPadding(30, 105, 30, 105);
//			setText(R.id.tv_empty, getText(R.string.empty_lecture).toString());
//
//	}
	
}
