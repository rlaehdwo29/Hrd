package net.passone.hrd.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import net.passone.hrd.MyPageActivity;
import net.passone.hrd.R;
import net.passone.hrd.common.Util;
import net.passone.hrd.container.LectureItem;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PageListAdapter extends BaseAdapter {
	Context context;
	ArrayList<LectureItem> list;
	private LayoutInflater mInflater;
	int gubun=0;
	DBmanager db_manager;	
	SQLiteDatabase db;
	boolean mode_down=false;
	String classkey="";
	private boolean[] isCheckedConfrim;
	boolean isCdn;
	public Hashtable<Integer, View> hashConvertView = new Hashtable<Integer, View>();
	public PageListAdapter(Context context,ArrayList<LectureItem> list,String classkey,boolean isCdn) {
		this.context=context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list=list;
		this.isCheckedConfrim = new boolean[list.size()];
		this.classkey=classkey;
		this.isCdn=isCdn;
	}
	public void setList(ArrayList<LectureItem> list)
	{
		this.list=list;
	}
	public void setMode(boolean mode)
	{
		mode_down=mode;
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
					R.layout.page_chkitem, parent, false);
			//			convertView = mInflater.inflate(R.layout.wronglist_cell, null);

			holder = new ViewHolder();
			holder.chk_item = (CheckBox) convertView.findViewById(R.id.chk_item);
			holder.tv_page = (TextView) convertView.findViewById(R.id.tv_page);
			holder.tv_subject = (TextView) convertView.findViewById(R.id.tv_subject);
			holder.iv_finish = (ImageView) convertView.findViewById(R.id.iv_finish);
			holder.iv_down = (ImageView) convertView.findViewById(R.id.iv_down);
			if(isCdn)
				holder.iv_down.setVisibility(View.GONE);
			else {
				holder.iv_down.setVisibility(View.VISIBLE);
			}
			convertView.setTag(holder);
			hashConvertView.put(position, convertView);
			convertView.setTag(R.id.chk_item, holder.chk_item);

		} else {
			convertView = (View) hashConvertView.get(position);

			holder = (ViewHolder) convertView.getTag();
		}
		holder.chk_item.setId(position);
		holder.chk_item.setChecked(item.isSelected());
		holder.chk_item.setFocusable(false);
		if(mode_down)
		{
			if(item.getMovieurl().contains("mp4")) {
				holder.chk_item.setVisibility(View.VISIBLE);
				holder.tv_page.setVisibility(View.GONE);
				if (item.getMovieurl().length() == 0) {
					holder.tv_page.setVisibility(View.VISIBLE);
					holder.chk_item.setVisibility(View.GONE);


				}
			}

		}
		else
		{
			holder.tv_page.setVisibility(View.VISIBLE);
			holder.chk_item.setVisibility(View.GONE);

		}

		holder.tv_subject.setText(item.getSubject());
		holder.tv_page.setText(item.getPage());
		//Log.d("passone","isfinish=readflag"+item.getReadFlag());
		if(item.getReadFlag().equals("Y"))
		{
			holder.iv_finish.setImageResource(R.drawable.btn_completion_on);
		}
		else
			holder.iv_finish.setImageResource(R.drawable.ico_notclass);
		String filename=classkey+"_"+item.getPart()+"_"+item.getPage()+".mp4";
		File dfile = new File(Util.getDownloadPath(filename));
		Log.d("passone","download="+dfile.getName()+"|"+dfile.exists()+"|");
		if(dfile.isFile() && dfile.exists())
		{
			if(item.getIsDown()>0)
				holder.iv_down.setImageResource(R.drawable.ico_down_on);
			else
				holder.iv_down.setImageResource(R.drawable.ico_downstop);

		}
		else
			holder.iv_down.setImageResource(R.drawable.ico_down_off);

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
			if(holder.chk_item.getVisibility()==View.VISIBLE)
			{
				if (holder.chk_item.isChecked()){
					lstIds.add(index);
				}
			}
			index++;
		}

		Long[] arrLong = (Long[]) lstIds.toArray(new Long[0]);
		return arrLong;
	}
	public void chkItem(int position) {
		if (hashConvertView == null || hashConvertView.size() == 0)
			return ;
		Integer key;
		View view;
		List<Long> lstIds = new ArrayList<Long>();
		Enumeration<Integer> e = hashConvertView.keys();
		long index = 0;
		while(e.hasMoreElements()) {
			key = (Integer)e.nextElement();
			view = (View)hashConvertView.get(key);
			ViewHolder holder = (ViewHolder) view.getTag();
			if(key==position)
			{
				holder.chk_item.setChecked(!holder.chk_item.isChecked());
				if(context.getClass() == (MyPageActivity.class) )
				{
					((MyPageActivity)context).countChk(holder.chk_item.isChecked());
				}
			}
			index++;
		}

		Long[] arrLong = (Long[]) lstIds.toArray(new Long[0]);
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
			if(holder.chk_item.getVisibility()==View.VISIBLE)
			{
				if (holder.chk_item.isChecked()){
					holder.chk_item.setChecked(false);
					if(context.getClass() == (MyPageActivity.class) )
					{
						((MyPageActivity)context).countChk(holder.chk_item.isChecked());
					}
				}
			}
			index++;
		}		
	}
	static class ViewHolder {
		CheckBox chk_item;
		TextView tv_subject;
		TextView tv_page;
		ImageView iv_finish;
		ImageView iv_down;
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
			if(holder.chk_item.isEnabled())
			{
				if(holder.chk_item.getVisibility()==View.VISIBLE)
					holder.chk_item.setChecked(true);
				else
					holder.chk_item.setChecked(false);
			}
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
			if(holder.chk_item.getVisibility()==View.VISIBLE)
			{
				holder.chk_item.setChecked(ischk);
				if(context.getClass() == (MyPageActivity.class) )
				{
					((MyPageActivity)context).countChk(holder.chk_item.isChecked());
				}
			}
			index++;

		}		
	}
}
