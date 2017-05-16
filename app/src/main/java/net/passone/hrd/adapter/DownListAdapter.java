package net.passone.hrd.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import net.passone.hrd.DownloadActivity;
import net.passone.hrd.R;
import net.passone.hrd.common.CUser;
import net.passone.hrd.common.Environments;
import net.passone.hrd.common.Util;
import net.passone.hrd.container.LectureItem;
import net.passone.hrd.video.YoondiskPlayerActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DownListAdapter extends BaseAdapter {
	Context context;
	ArrayList<LectureItem> list;
	private LayoutInflater mInflater;
	int gubun=0;
	DBmanager db_manager;	
	SQLiteDatabase db;
	boolean mode_edit=false;
	private boolean[] isCheckedConfrim;
	public Hashtable<Integer, View> hashConvertView = new Hashtable<Integer, View>();
	public DownListAdapter(Context context,ArrayList<LectureItem> list) {
		this.context=context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list=list;
		this.isCheckedConfrim = new boolean[list.size()];
	}
	public void setList(ArrayList<LectureItem> list)
	{
		this.list=list;
	}
	public void setMode(boolean mode)
	{
		mode_edit=mode;
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}


	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}


	public long getItemId(int positon) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder=new ViewHolder();
		final LectureItem item=list.get(position);
		if (hashConvertView.containsKey(position) == false) {
			convertView = (LinearLayout) LayoutInflater.from(context).inflate(
					R.layout.down_chkitem, parent, false);
			//			convertView = mInflater.inflate(R.layout.wronglist_cell, null);

			holder = new ViewHolder();
			holder.chk_item = (CheckBox) convertView.findViewById(R.id.chk_item);
			holder.tv_subject = (TextView) convertView.findViewById(R.id.tv_subject);
			holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tv_chasi = (TextView) convertView.findViewById(R.id.tv_chasi);
			convertView.setTag(holder);
			convertView.setTag(R.id.chk_item, holder.chk_item);
			hashConvertView.put(position, convertView);
		} else {
			convertView = (View) hashConvertView.get(position);

			holder = (ViewHolder) convertView.getTag();
		}

		holder.chk_item.setId(position);
		holder.chk_item.setChecked(item.isSelected());
		holder.chk_item.setFocusable(false);
		if(mode_edit)
		{
			holder.chk_item.setVisibility(View.VISIBLE);
		
		}
		else
			holder.chk_item.setVisibility(View.GONE);
		holder.tv_subject.setText(item.getPage()+" "+item.getSubject());
		holder.tv_size.setText(item.getSize()+"MB");
		holder.tv_title.setText(item.getTitle());
		if(item.getChasi().equals("00"))
		holder.tv_chasi.setText("오리엔테이션");
		else
			holder.tv_chasi.setText(item.getChasi());

		String filename=item.getClasskey()+"_"+item.getPart()+"_"+item.getPage()+".mp4";

		return convertView;
	}

	public Long[] getCheckItemIds() {
		if (hashConvertView == null || hashConvertView.size() == 0)
			return null;
		Integer key;
		View view;
		List<Long> lstIds = new ArrayList<Long>();
		Enumeration<Integer> e = hashConvertView.keys();
		long index = 0;
		while(e.hasMoreElements()) {
			key = (Integer)e.nextElement();
			view = (View)hashConvertView.get(key);
			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder.chk_item.isChecked()){
				lstIds.add(index);
			}

			index++;
		}

		Long[] arrLong = (Long[]) lstIds.toArray(new Long[0]);
		return arrLong;
	}

	public void clearChoices()
	{
		if (hashConvertView == null || hashConvertView.size() == 0)
			return;

		Integer key;
		View view;
		List<Long> lstIds = new ArrayList<Long>();
		Enumeration<Integer> e = hashConvertView.keys();
		long index = 0;
		while(e.hasMoreElements()) {
			key = (Integer)e.nextElement();
			view = (View)hashConvertView.get(key);
			ViewHolder holder = (ViewHolder) view.getTag();

			if (holder.chk_item.isChecked()){
				holder.chk_item.setChecked(false);
				if(context.getClass() == (DownloadActivity.class) )
				{
					((DownloadActivity)context).countChk(holder.chk_item.isChecked());
				}
			}

			index++;
		}		
	}
	static class ViewHolder {
		CheckBox chk_item;
		TextView tv_subject;
		TextView tv_size;
		TextView tv_title;
		TextView tv_chasi;

	}
	public void setAllChecked() {
		if (hashConvertView == null || hashConvertView.size() == 0)
			return;

		Integer key;
		View view;
		List<Long> lstIds = new ArrayList<Long>();
		Enumeration<Integer> e = hashConvertView.keys();
		long index = 0;
		while(e.hasMoreElements()) {
			key = (Integer)e.nextElement();
			view = (View)hashConvertView.get(key);
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.chk_item.setChecked(true);
			
			index++;
		}
	}
	public void setAllChecked(boolean ischk) {
		if (hashConvertView == null || hashConvertView.size() == 0)
			return;

		Integer key;
		View view;
		List<Long> lstIds = new ArrayList<Long>();
		Enumeration<Integer> e = hashConvertView.keys();
		long index = 0;
		while(e.hasMoreElements()) {
			key = (Integer)e.nextElement();
			view = (View)hashConvertView.get(key);
			ViewHolder holder = (ViewHolder) view.getTag();

				holder.chk_item.setChecked(ischk);
				if(context.getClass() == (DownloadActivity.class) )
				{
					((DownloadActivity)context).countChk(holder.chk_item.isChecked());
				}
		
			index++;

		}		
	}
}
