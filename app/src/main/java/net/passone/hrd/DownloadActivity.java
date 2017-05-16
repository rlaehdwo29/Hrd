package net.passone.hrd;

import static android.provider.BaseColumns._ID;

import java.io.File;
import java.util.ArrayList;

import net.passone.hrd.adapter.DBmanager;
import net.passone.hrd.adapter.DownListAdapter;
import net.passone.hrd.adapter.HttpHelper;
import net.passone.hrd.adapter.OnResponseListener;
import net.passone.hrd.common.CUser;
import net.passone.hrd.common.IntentModelActivity;
import net.passone.hrd.common.Util;
import net.passone.hrd.container.LectureItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DownloadActivity extends IntentModelActivity implements OnResponseListener{
	Cursor cursor;
	String classkey="",title="",part="",filename="";
	AlertDialog alert;
	DownloadActivity self=null;
	CookieManager cookieManager;
	HttpHelper hh=new HttpHelper();
	ListView list;
	DownListAdapter dadapter;
	ArrayList<LectureItem> lec_list,down_list;
	OnResponseListener callback;
	Context context;
	boolean mode_edit=false;
	boolean mode_all=false;
	boolean mode_complete=false;
	LinearLayout layout_edit;
	ImageButton btn_all,btn_del;
	int chk_cnt=0,down_cnt=0,chk_total=0;
	long totalsize=0;
	Dialog dialog;
	TextView tv_total;
	TextView tv_subject;
	TextView tv_progress,tv_result;
	ProgressBar down_progress,total_progress;
	private static final int CONNECTIVITY_MSG = 0;
	private static final int PHONE_STATE_MSG = 1;
	private static final int DIALOG_PROGRESS = 0;
	DBmanager db_manager;
	SQLiteDatabase db;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("passone","여기 맞어?");
		self=this;
		context=this;
		callback=this;
		setContentView(R.layout.download);
		((TextView)findViewById(R.id.titleText)).setText("다운로드");
		setTitleButton(((ImageButton) findViewById(R.id.leftImageButton)), R.drawable.btn_back);
		setTitleButton(((ImageButton)findViewById(R.id.rightImageButton)),R.drawable.btn_file);

		layout_edit=(LinearLayout)findViewById(R.id.layout_edit);
		list=(ListView)findViewById(R.id.list);
		if(!mode_edit){
			layout_edit.setVisibility(View.GONE);
		}
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				if(lec_list.size()>0) {
					CheckBox checkbox = (CheckBox) view.getTag(R.id.chk_item);
					if (mode_edit) {
						checkbox.setChecked(!checkbox.isChecked());
						lec_list.get(position).setSelected(checkbox.isChecked());
						countChk(checkbox.isChecked());
						//					 dadapter.chkItem(position);
					} else {
						Intent i = new Intent(self, MyClassPopActivity.class);
						Log.v("intenturl", lec_list.get(position).getHtmlurl());
						Log.v("intenturl", lec_list.get(position).getTitle());
						Log.v("intenturl", lec_list.get(position).getClassCount());
						Log.v("intenturl", lec_list.get(position).getPart());
						Log.v("intenturl", lec_list.get(position).getPage());
						Log.v("intenturl", lec_list.get(position).getContentsKey());
						Log.v("intenturl", lec_list.get(position).getStudykey());

						i.putExtra("url", lec_list.get(position).getHtmlurl());
						i.putExtra("title", lec_list.get(position).getTitle());
						i.putExtra("classcount", lec_list.get(position).getClassCount());
						i.putExtra("part", lec_list.get(position).getPart());
						i.putExtra("page", lec_list.get(position).getPage());
						i.putExtra("contentskey", lec_list.get(position).getContentsKey());
						i.putExtra("studykey", lec_list.get(position).getStudykey());
						startActivity(i);
					}
				}
			}

		});
		lec_list=new ArrayList<LectureItem>();
		down_list=new ArrayList<LectureItem>();
		btn_all=(ImageButton)findViewById(R.id.btn_all);
		btn_all.setOnClickListener(self);
		btn_del=(ImageButton)findViewById(R.id.btn_del);
		btn_del.setOnClickListener(self);
		((ImageButton)findViewById(R.id.btn_deselect)).setOnClickListener(this);
		loadDatabase();
	}
	private void loadDatabase() {
		// TODO Auto-generated method stub
		db_manager = new DBmanager(self,"UserInfo.db");
		db=db_manager.getReadableDatabase();
		String sql = "SELECT * FROM download ";
		Cursor cursor=db.rawQuery(sql,null);

		try {
			if (cursor.getCount()!= 0 && cursor.moveToFirst()) {
				//				classkey VARCHAR, part VARCHAR, page VARCHAR, subject TEXT, filename TEXT, htmlurl TEXT, size VARCHAR
				do {
					//	classcount VARCHAR, studykey VARCHAR, classkey VARCHAR, finish VARCHAR,contentskey VARCHAR,part VARCHAR, duration VARCHAR, markertime VARCHAR, page VARCHAR, userid VARCHAR);");

					String classkey=cursor.getString(cursor.getColumnIndex("classkey"));
					String subject=cursor.getString(cursor.getColumnIndex("subject"));
					String part=cursor.getString(cursor.getColumnIndex("part"));
					String page=cursor.getString(cursor.getColumnIndex("page"));
					String filename=cursor.getString(cursor.getColumnIndex("filename"));
					String htmlurl=cursor.getString(cursor.getColumnIndex("htmlurl"));
					String size=cursor.getString(cursor.getColumnIndex("size"));
					String title=cursor.getString(cursor.getColumnIndex("title"));
					String chasi=cursor.getString(cursor.getColumnIndex("chasi"));
					String contentskey=cursor.getString(cursor.getColumnIndex("contentskey"));
					String classcount=cursor.getString(cursor.getColumnIndex("classcount"));
					String studykey=cursor.getString(cursor.getColumnIndex("studykey"));
					Log.d("passone","filename="+filename+"&classkey="+classkey+"&part="+part+"&page="+page);
					if(studykey.equals(CUser.mno) && filename.length()>0) //본인이 다운받은 것만 노출
					{
						LectureItem item=new LectureItem();
						item.setDownLecture(part, page, subject, "", "", htmlurl, size, classkey,filename,chasi,title,contentskey,classcount,studykey);
						lec_list.add(item);
					}
				}while(cursor.moveToNext());
			}
		} catch(Exception e) {
		}
		cursor.close();
		if(db!=null)
			db.close();
		initialize();
	}
	@Override
	public void onClick(View view) {
		int id=view.getId();
		switch(id)
		{
		case R.id.leftImageButton:
			finish();
			break;
		case R.id.rightImageButton:
				mode_edit = !mode_edit;
				dadapter.setMode(mode_edit);
				if(mode_edit)
				{
					setTitleButton(((ImageButton)findViewById(R.id.rightImageButton)),R.drawable.btn_finish);
					layout_edit.setVisibility(View.VISIBLE);
				}
				else
				{

					setTitleButton(((ImageButton)findViewById(R.id.rightImageButton)),R.drawable.btn_file);
					layout_edit.setVisibility(View.GONE);
				}
				dadapter.notifyDataSetChanged();

				break;
		case R.id.btn_all:
				mode_all=true;
				chk_cnt=0;

				for(LectureItem item:lec_list)
				{
					item.setSelected(true);
					countChk(true);
				}
				dadapter.notifyDataSetChanged();


			break;
			case R.id.btn_deselect:
				chk_cnt=0;
				for(LectureItem item:lec_list)
				{
					item.setSelected(false);
					countChk(false);

				}
				dadapter.notifyDataSetChanged();
				btn_all.setImageResource(R.drawable.btn_all);
				break;
		case R.id.btn_del:
			Util.alert(this, "다운로드 삭제","삭제하시겠습니까?", "확인", "취소", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					makeChkList(false);

				}
			}, null);
			break;
		}

	}
	//다운로드 or 삭제할 리스트 선택
	public void countChk(boolean ischk)
	{


		if(ischk)
		{
			chk_cnt++;
		}
		else
		{
			if(chk_cnt>0)
				chk_cnt--;
		}
		if(chk_cnt==0)
		{
			btn_del.setEnabled(false);
			btn_del.setClickable(false);

		}
		else
		{
			btn_del.setImageResource(R.drawable.btn_del);
			btn_del.setEnabled(true);
			btn_del.setClickable(true);

			if(lec_list.size()==chk_cnt)
			{
				mode_all=true;

			}
			else
			{
				mode_all=false;
				btn_all.setImageResource(R.drawable.btn_all);
			}

		}

	}
	public void initialize() {
		// TODO Auto-generated method stub
		dadapter=new DownListAdapter(self,lec_list);
	//	dadapter.setMode(mode_edit);
		list.setItemsCanFocus(false);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		list.setAdapter(dadapter);
		if(lec_list.size()==0)
		{
			btn_all.setClickable(false);
			View v = LayoutInflater.from(this).inflate(R.layout.empty_cell, null);
			if(list.getFooterViewsCount()==0)
				list.addFooterView(v);
		}
	}


	public void onStart() {

		super.onStart();

		CookieSyncManager.createInstance(this);

	}

	@Override

	public void onPause(){

		super.onPause();

		CookieSyncManager.getInstance().stopSync();

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(alert!=null)
			alert.dismiss();
		super.onResume();

		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed()
	{
		finish();
		return;
	}
	@Override
	public void onConfigurationChanged (Configuration newConfig)
	{

		super.onConfigurationChanged(newConfig);

	}
	@Override
	public void onResponseReceived(int api, Object result) {
		// TODO Auto-generated method stub

	}
	public void makeChkList(boolean isdown)
	{
		int size=lec_list.size();
		for(int i = size-1; i >= 0; i--)
		{
			if(lec_list.get(i).isSelected())
			{
				delDownload(lec_list.get(i).getFilename());
				lec_list.remove(i);

			}


		}
		dadapter.clearChoices();
		clearDown();
		Util.ToastMessage(this, "삭제되었습니다.");
		dadapter.notifyDataSetChanged();
	}

	public void delDownload(String filename)
	{
		db_manager = new DBmanager(self,"UserInfo.db");
		db=db_manager.getWritableDatabase();
		String sql = "SELECT * FROM download  WHERE filename=?";
		Cursor cursor=db.rawQuery(sql, new String[]{filename});

		try {
			if (cursor.getCount()!= 0) {
				String dsql = "Delete FROM download  WHERE filename='"+filename+"'";
				db.execSQL(dsql);
				//				db.rawQuery(dsql, new String[]{filename});

			}
		} catch(Exception e) {
		}
		cursor.close();


		if(db!=null)
			db.close();

		File file = new File(Util.getDownloadPath(filename));

		if(file.isFile() && file.exists())
		{
			file.delete();
		}


	}

	public void clearDown()
	{
		down_cnt=0;
		chk_cnt=0;
	}
}
