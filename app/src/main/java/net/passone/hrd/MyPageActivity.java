package net.passone.hrd;


import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import net.passone.hrd.adapter.AdapterItemManager;
import net.passone.hrd.adapter.Api;
import net.passone.hrd.adapter.DBmanager;
import net.passone.hrd.adapter.OnResponseListener;
import net.passone.hrd.adapter.PageListAdapter;
import net.passone.hrd.common.DownLoad;
import net.passone.hrd.common.IntentModelActivity;
import net.passone.hrd.common.StaticVars;
import net.passone.hrd.common.Util;
import net.passone.hrd.container.LectureInfo;
import net.passone.hrd.container.LectureItem;
import net.passone.hrd.video.YoondiskPlayerActivity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyPageActivity extends IntentModelActivity implements OnClickListener,OnResponseListener,DownLoad.Communicator {
	ImageButton btn_login;
	Context context;
	OnResponseListener callback;
	Handler handler;
	ListView list_lecture;
	ArrayList<LectureItem> lectureList=new ArrayList<LectureItem>();
	int cindex=0,progress,totaltime,days;
	Dialog dialog;
	boolean mode_complete=false;

	PageListAdapter lAdapter;
	String course,beginDate,lastLectureId,orderid;
	LinearLayout layout_downtools;
	boolean isDown=false,mode_all=false;
	int chk_cnt=0,down_cnt=0,chk_total=0,complete_cnt=0,ing_cnt=0;;
	long totalsize=0;
	ImageButton btn_all, btn_down, btn_del,btn_deselect;
	ArrayList<LectureItem> down_list=new ArrayList<LectureItem>();
	int recent_position=0;
	String classkey="",title="",part="",filename="",studykey="",classcount="",partnum="",partname="",tmp_file="",filepath="";

	private int down_no = 0;
	public String   err_msg = "";
	public String   down_msg = "";
	private int down_load_end=0;
	public String down_arg = "";
	DBmanager db_manager;
	SQLiteDatabase db;
	DownLoad drmdownload;
	ImageButton btn_dcancel;
	TextView tv_total;
	TextView tv_subject;
	TextView tv_progress,tv_result;
	ProgressBar down_progress,total_progress;
	LectureItem selectItem;
	boolean isCancel = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.lecture);
		Intent i=getIntent();
		classkey=i.getStringExtra("classkey");
		title=i.getStringExtra("title");
		part=i.getStringExtra("part");
		studykey=i.getStringExtra("studykey");
		classcount=i.getStringExtra("classcount");
		partnum=i.getStringExtra("partnum");
		partname=i.getStringExtra("partname");


		((TextView)findViewById(R.id.titleText)).setText("강의목록");
		((Button)findViewById(R.id.sub_rightImageButton)).setVisibility(View.VISIBLE);
		((Button)findViewById(R.id.sub_rightImageButton)).setOnClickListener(this);
		((Button)findViewById(R.id.sub_rightImageButton)).setText("다운로드");

		((ImageView)findViewById(R.id.leftImageButton)).setVisibility(View.VISIBLE);
		((ImageView)findViewById(R.id.leftImageButton)).setImageResource(R.drawable.btn_back);
		((ImageView)findViewById(R.id.leftImageButton)).setOnClickListener(this);

		context=this;
		callback = this;


		btn_all=(ImageButton)findViewById(R.id.btn_all);
		btn_all.setOnClickListener(this);
		btn_deselect=(ImageButton)findViewById(R.id.btn_deselect);
		btn_deselect.setOnClickListener(this);
		btn_del=(ImageButton)findViewById(R.id.btn_del);
		btn_down=(ImageButton)findViewById(R.id.btn_lecdown);
		btn_down.setVisibility(View.VISIBLE);
		btn_del.setOnClickListener(this);
		btn_down.setOnClickListener(this);
		layout_downtools=(LinearLayout)findViewById(R.id.layout_edit);
		lAdapter=new PageListAdapter(this,StaticVars.lectureItems,classkey,false);
		((TextView)findViewById(R.id.tv_chasi)).setText(partnum);
		((TextView)findViewById(R.id.tv_chasititle)).setText(partname);

		list_lecture=(ListView)findViewById(R.id.list);
		list_lecture.setItemsCanFocus(true);
		list_lecture.setFocusable(false);
		list_lecture.setFocusableInTouchMode(false);
		list_lecture.setClickable(false);
		list_lecture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LectureItem item = StaticVars.lectureItems.get(position);
				CheckBox checkbox = (CheckBox) view.getTag(R.id.chk_item);
				Util.debug("click:" + position);
				selectItem = item;
				if (layout_downtools.getVisibility() == View.VISIBLE) {
					checkbox.setChecked(!checkbox.isChecked());
					item.setSelected(checkbox.isChecked());
					countChk(checkbox.isChecked());
					//					 dadapter.chkItem(position);
				} else {
					Intent i=new Intent(self,MyClassPopActivity.class);
					i.putExtra("url",item.getHtmlurl());
					i.putExtra("title",  title);
					i.putExtra("classcount", classcount);
					i.putExtra("part", item.getPart());
					i.putExtra("page", item.getPage());
					i.putExtra("contentskey", item.getContentsKey());
					i.putExtra("studykey", studykey);
					startActivity(i);

				}

			}
		});


		super.onCreate(savedInstanceState);
	}
	@Override
	public void onResponseReceived(int api, Object result) {
		Util.debug("Login result    :   " + result);
		switch(api) {

			case Api.LECTURE :


				if (result != null) {

					AdapterItemManager.AddLecture((List<LectureInfo>) result);
					lectureList=StaticVars.lectureItems;
					chk_total=0;


					for(LectureItem lec : StaticVars.lectureItems)
					{
						Log.d("passone","download=onReceived="+lec.getFilename());
						db_manager = new DBmanager(self,"UserInfo.db");
						db=db_manager.getReadableDatabase();
						filename=classkey+"_"+lec.getPart()+"_"+lec.getPage()+".mp4";
						String sql = "SELECT * FROM download  WHERE filename=?";
//						Cursor cursor=db.rawQuery(sql, new String[]{filename});
						Cursor cursor=db.query("download",null,"filename=?",new String[]{filename},null,null,null);
						Log.d("passone","cnt="+cursor.getCount()+"&filename="+filename);
						try {
							if (cursor.getCount()> 0) {
								lec.setIsDown(1);
							}else{
								lec.setIsDown(0);
							}


						} catch(Exception e) {
						}
						Log.d("passone","download=3"+lec.getFilename());
						cursor.close();
						if(lec.getMovieurl().length()>0)
						{
							chk_total++;
						}
					}
					if(chk_total==0)
						btn_all.setClickable(false);
					lAdapter=new PageListAdapter(self,lectureList,classkey,false);
					list_lecture.setItemsCanFocus(false);
					list_lecture.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					list_lecture.setAdapter(lAdapter);
					setDownMode();

				} else {
					// 수신, 파싱 오류
					Util.PopupMessage(this, getResources().getString(R.string.api_http_alert));
				}
				break;



		}
	}

	@Override
	protected void onResume() {

		_api.getLecture(classkey, part, studykey, classcount, context, callback);
		super.onResume();
	}


	public Date getDate(String key) {
		if (key == null) return null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return format.parse(key);
		} catch (ParseException e) {
			return null;
		}

	}
	public static String formatDate(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.sub_rightImageButton:
				isDown=!isDown;
				setDownMode();
				/*
				if(isDown)
				{
					complete_cnt=0;
					ing_cnt=0;
					if (StaticVars.lectureItems.size() > 0) {
						for (LectureItem lecItem : StaticVars.lectureItems) {
							String chkfilename=classkey+"_"+lecItem.getPart()+"_"+lecItem.getPage()+".mp4";
							String chkfilepath= Util.getDownloadPath(chkfilename);
							File dfile=new File(chkfilepath);
							if(dfile.isFile() && dfile.exists())
							{
								if(lecItem.getIsDown()>0)
									complete_cnt++;
								else
									ing_cnt++;

							}

						}
					}
					if(chk_total==complete_cnt)
					{
						btn_del.setEnabled(true);
						btn_down.setEnabled(false);
					}
					else
					{
						if(complete_cnt>0 || ing_cnt>0)
						{
							btn_del.setEnabled(true);
							btn_down.setEnabled(true);
						}
						else {
							btn_del.setEnabled(false);
							btn_down.setEnabled(true);
						}
					}
					findViewById(R.id.layout_edit).setVisibility(View.VISIBLE);
					((Button)findViewById(R.id.sub_rightImageButton)).setText("완료");
					lAdapter.setMode(true);

				}
				else {
					findViewById(R.id.layout_edit).setVisibility(View.GONE);
					((Button)findViewById(R.id.sub_rightImageButton)).setText("다운로드");

					lAdapter.setMode(false);

				}
				*/
				break;
			case R.id.btn_all:
				chk_cnt=0;
				Util.debug("all click" + mode_all);
				for(LectureItem item:StaticVars.lectureItems)
				{
					item.setSelected(true);
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						lAdapter.setList(StaticVars.lectureItems);
						lAdapter.notifyDataSetChanged();

					}
				});

				break;
			case R.id.btn_deselect:

				for(LectureItem item:StaticVars.lectureItems)
				{
					item.setSelected(false);

				}
				down_list.clear();
				down_cnt=0;
				chk_cnt=0;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						lAdapter.setList(StaticVars.lectureItems);
						lAdapter.notifyDataSetChanged();

					}
				});
				break;
			case R.id.btn_del:
				Util.alert(this, "다운로드 삭제", "삭제하시겠습니까?", "확인", "취소", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						makeChkList(false);

					}
				}, null);
				break;
			case R.id.btn_lecdown: {
				Log.d("passone", "lecdown");
				if (Util.isWifiConnected(context)) {
					Log.d("passone", "lecdown");
					makeChkList(true);
				} else {
					Util.alert(context, "3G/LTE 다운로드", "다운로드 하시겠습니까?", "확인", "취소", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							makeChkList(true);

						}
					}, null);
				}
			}
				break;


			case R.id.leftImageButton:
				finish();
				break;
		}
		super.onClick(v);
	}
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

//			btn_down.setEnabled(false);
//			btn_del.setEnabled(false);
		}
		else
		{

//			btn_down.setEnabled(true);
//			btn_del.setEnabled(true);

		}

	}
	public void makeChkList(boolean isdown)
	{
		down_list.clear();

		int size=StaticVars.lectureItems.size();
		for(LectureItem item:lectureList)
		{

			if(item.isSelected() && item.getMovieurl().contains("mp4"))
			{
				filename = classkey+"_"+item.getPart()+"_"+item.getPage()+".mp4";
				File file = new File(Util.getDownloadPath(filepath+filename));
				db_manager = new DBmanager(self,"UserInfo.db");
				db=db_manager.getReadableDatabase();
				String sql = "SELECT * FROM download  WHERE filename=?";
				Cursor cursor=db.rawQuery(sql, new String[]{filename});
				int nDownCursorCnt = cursor.getCount();
				Log.d("passone","down_list=nDownCursorCnt"+nDownCursorCnt);
				cursor.close();
				if((file.exists() && file.isFile() &&  nDownCursorCnt==0) || (!file.isFile() && !file.exists() && nDownCursorCnt==0)){
					Log.d("passone","down_list="+down_list.size());
					down_list.add(item);
				}
			}


		}

		if(isdown) //다운로드버튼을 클릭하면
		{

			if(down_list.size()>0)
			{
				down_cnt=0;
				sendDownload(down_cnt);
			}
		}
		else //삭제
		{

					for(LectureItem item:lectureList)
					{
						if(item.isSelected())
						{
							File file = new File(Util.getDownloadPath(filepath=classkey+"_"+item.getPart()+"_"+item.getPage()+".mp4"));
							if(file.isFile() && file.exists()) {
								delDownload(classkey+"_"+item.getPart()+"_"+item.getPage()+".mp4");
							}

						}
					}


				Util.ToastMessage(context, "삭제되었습니다.");
				clearDown();
		}

	}
	public void sendDownload(int position)
	{
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		isCancel = false;
		mode_complete = false;
		drmdownload = null;

		LectureItem item=down_list.get(position);


		filename=classkey+"_"+item.getPart()+"_"+item.getPage()+".mp4";
		down_arg=item.getMovieurl().replace("\\/","/");
//		down_arg="http://heesomobile.wjpass.hd.stream.scs.skcdn.co.kr/ssamplussp/15/03/11ks-ir/11ks-ir-1504001.mp4?SM4558_38087044";
		db_manager = new DBmanager(self,"UserInfo.db");
		db=db_manager.getReadableDatabase();
		String sql = "SELECT * FROM download  WHERE filename=?";
		Cursor cursor=db.rawQuery(sql, new String[]{filename});
		int nCursorCnt = 0;
		try {
			nCursorCnt = cursor.getCount();
			if (cursor.getCount()> 0) {

				item.setIsDown(1);
			}
		} catch(Exception e) {
		}
		cursor.close();

		if(cursor.getCount()==0)
		{
			isCancel=false;
			if(dialog==null) {
				showDownload();
			}
			if(down_progress!=null)
			{
				total_progress.setProgress(down_cnt+1);
				tv_total.setText("전체 ( "+(down_cnt + 1)+"/"+down_list.size()+" )");
				tv_subject.setText(item.getSubject());
			}
			//다운로드 기능 선언부.
			drmdownload=  new DownLoad();
			drmdownload.setCommunicator(this);
	                 /*파일 다운로드 시작 url , 로컬저장경로 . */
			String filePath=Util.getDownloadPath(filename);
			Util.debug("down filepath:"+filePath);

			if(down_arg.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
				String[] down_arg_arr = down_arg.split("/");
				down_arg = "";
				for (int i = 0; i < down_arg_arr.length; i++) {
					if ((down_arg_arr.length - 1) == i) {
						try {
							down_arg_arr[i] = URLEncoder.encode(down_arg_arr[i], "UTF-8").replaceAll("\\+", "%20");

						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					down_arg += "/" + down_arg_arr[i];
				}
			}
			Log.d("passone",down_arg);
			drmdownload.drm_download_start(down_arg, filePath);

		}
		else
		{
			if(down_cnt<(down_list.size()-1))
			{
				Log.d("passone", "down_listsize=" + down_list.size() + "/" + chk_total);
				if(down_list.size()<chk_total)
					Util.ToastMessage(this,"이미 다운로드 받은 강의입니다.");
				if(!isCancel) {
					down_cnt++;
					sendDownload(down_cnt);
				}
			}
			else
			{
				Util.debug("already down clear"+down_cnt+","+down_list.size());
				clearDown();

			}
		}


	}
	public void delDownload(String filename)
	{

		File file = new File(Util.getDownloadPath(filename));
		if(file.isFile() && file.exists()) {
			file.delete();
		}
		db_manager = new DBmanager(self,"UserInfo.db");
		db=db_manager.getReadableDatabase();
		String sql = "SELECT * FROM download  WHERE filename=?";
		Cursor cursor=db.rawQuery(sql, new String[]{filename});

		try {
			if (cursor.getCount()> 0) {
				String dsql = "update  download set filename=''  WHERE filename='"+filename+"'";
				db.execSQL(dsql);
			}
		} catch(Exception e) {
		}
		cursor.close();


		if(db!=null)
			db.close();

		//파일 삭제 20160317 신경선 과장님과 협의됨
		boolean deleted = file.delete();
		for(LectureItem item : StaticVars.lectureItems)
		{
			String tmpfilename=classkey+"_"+item.getPart()+"_"+item.getPage()+".mp4";
			Util.debug("del file:"+tmpfilename+","+filename);

			if(tmpfilename.equals(filename))
			{
				item.setIsDown(0);
			}

		}


	}
	private void showDownload() {
		isCancel = false;
		mode_complete = false;
		// TODO Auto-generated method stub
		dialog = new Dialog(MyPageActivity.this,android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.down_dialog);
		dialog.setCancelable(false);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//there are a lot of settings, for dialog, check them all out!
		tv_total = (TextView) dialog.findViewById(R.id.tv_total);
		tv_subject = (TextView) dialog.findViewById(R.id.tv_downtitle);
		tv_progress = (TextView) dialog.findViewById(R.id.tv_progress);
		down_progress=(ProgressBar)dialog.findViewById(R.id.down_progress);
		total_progress =(ProgressBar)dialog.findViewById(R.id.total_progress);
		total_progress.setMax(down_list.size());

		total_progress.setProgress(down_cnt);
		tv_total.setText("전체 ( "+down_cnt+"/"+down_list.size()+" )");
		tv_subject.setText(filename);
		btn_dcancel = (ImageButton) dialog.findViewById(R.id.btn_dcancel);
		btn_dcancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub


				if (drmdownload!=null  )
				{
					if(!mode_complete)
					{
						isCancel = true;
						dialog.dismiss();
						drmdownload.drm_download_cancel();
						clearDown();
						mode_complete = false;
						dialog = null;
						drmdownload = null;
					}

				}


			}

		});

		dialog.show();
		btn_dcancel.setVisibility(View.VISIBLE);
	}
	public void clearDown()
	{

		for (LectureItem item : StaticVars.lectureItems)
		{
			item.setSelected(false);
			countChk(false);

		}
		if (!isCancel) {
			lAdapter.setMode(false);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					findViewById(R.id.layout_edit).setVisibility(View.GONE);
					((Button)findViewById(R.id.sub_rightImageButton)).setText("다운로드");

					lAdapter.setMode(false);
				}
			});
		}
		lAdapter.notifyDataSetChanged();
		down_list.clear();
		down_cnt=0;
		chk_cnt=0;
		if(dialog!=null && dialog.isShowing())
		{


			dialog.dismiss();
			dialog=null;

		}
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setDownMode();


	}

	public void setDownMode() {
		if (isDown) {
			complete_cnt = 0;
			ing_cnt = 0;
			if (StaticVars.lectureItems.size() > 0) {
				for (LectureItem lecItem : StaticVars.lectureItems) {
					String chkfilename = classkey + "_" + lecItem.getPart() + "_" + lecItem.getPage() + ".mp4";
					String chkfilepath = Util.getDownloadPath(chkfilename);
					File dfile = new File(chkfilepath);
					if (dfile.isFile() && dfile.exists()) {
						if (lecItem.getIsDown() > 0)
							complete_cnt++;
						else
							ing_cnt++;

					}

				}
			}
			if (chk_total == complete_cnt) {
				btn_del.setEnabled(true);
				btn_down.setEnabled(false);
			} else {
				if (complete_cnt > 0 || ing_cnt > 0) {
					btn_del.setEnabled(true);
					btn_down.setEnabled(true);
				} else {
					btn_del.setEnabled(false);
					btn_down.setEnabled(true);
				}
			}
			findViewById(R.id.layout_edit).setVisibility(View.VISIBLE);
			((Button) findViewById(R.id.sub_rightImageButton)).setText("완료");
			lAdapter.setMode(true);

		} else {
			findViewById(R.id.layout_edit).setVisibility(View.GONE);
			((Button) findViewById(R.id.sub_rightImageButton)).setText("다운로드");

			lAdapter.setMode(false);
		}
	}

	@Override
	public void drm_download_ing(final float total, final float fileLength) {
		Util.debug("total:" + total + ",fileLength:" + fileLength);
		isCancel=false;
		mode_complete = false;


		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (down_progress != null) {
					down_progress.setMax(Math.round(fileLength));
					btn_dcancel.setEnabled(true);
					totalsize=(long)total;
					down_progress.setProgress(Math.round(total));
					if (tv_progress != null)
						tv_progress.setText("( " + Util.toDisplayMB((long) total, (long) fileLength) + " )");

					//					tv_progress.setText("( "+(int)bytesWritten+"MB/"+(int)bytesTotal+"MB )");
					//Log.i(TAG, String.format("onProgressUpdate() : %d / %d", bytesWritten, bytesTotal));
				}
			}
		});

	}

	@Override
	public void drm_download_cancel(int err) {
		mode_complete=false;
		mode_complete=false;
		isCancel=true;
		/*ContentValues cv=new ContentValues();
		cv.clear();
		cv.put("filename", "");
		if(!db.isOpen()) {
									db_manager = new DBmanager(self,"UserInfo.db");
			db=db_manager.getReadableDatabase();
			db.delete("download", "filename='"+filepath+"'", null);
			db.close();

			// delete file...
		}
		runOnUiThread(new Notifier(err));
		drmdownload=null;*/
		/*for(LectureItem item : StaticVars.lectureItems){
			if(item.getFilename()==filepath) {
				item.setIsDown(0);

			}
		}*/






		runOnUiThread(new Notifier(err));
		drmdownload=null;
	}
	@Override
	public void drm_download_end(String downfile) {
		//if(dialog!=null) {
		//	((ImageButton) dialog.findViewById(R.id.btn_dcancel)).setVisibility(View.GONE);
		//}
		if(down_list.size()>0) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mode_complete = true;
			isCancel = false;

			lAdapter.notifyDataSetChanged();
			db_manager = new DBmanager(self, "UserInfo.db");
			db = db_manager.getReadableDatabase();
			ContentValues cv = new ContentValues();
			cv.clear();


			LectureItem item = down_list.get(down_cnt);

			filename = classkey + "_" + item.getPart() + "_" + item.getPage() + ".mp4";

			cv.put("classkey", classkey);
			cv.put("part", item.getPart());
			cv.put("page", item.getPage());
			cv.put("subject", item.getSubject()); //강의명
			cv.put("title", partname); //차시명
			cv.put("chasi", partnum);
			cv.put("filename", filename);
			cv.put("htmlurl", item.getHtmlurl());
			if (totalsize > 0)
				cv.put("size", String.valueOf(totalsize / Util.MegaBytes));
			cv.put("classcount", classcount);
			cv.put("studykey", studykey);
			cv.put("contentskey", item.getContentsKey());
			int update = db.update("download", cv, "part=? and page=? and classkey=?", new String[]{item.getPart(), item.getPage(), item.getClasskey()});
			if (update == 0)
				db.insert("download", "", cv);
			if (db.isOpen()) {
				db.close();
			}
			for (LectureItem lecitem : StaticVars.lectureItems) {
				//Log.d("passone","download="+lecitem.getFilename());
				if (lecitem.getPage() == item.getPage()) {

					lecitem.setIsDown(1);
					lecitem.setFilePath(filename);
				}
			}


			Util.debug("down cnt:" + down_cnt + "/down list:" + down_list.size());
			if (down_cnt < (down_list.size() - 1)) {
				down_cnt++;
				btn_dcancel.setEnabled(false);


				sendDownload(down_cnt);


			} else {
				btn_dcancel.setEnabled(true);
				isDown = !isDown;
				clearDown();


			}
		}

		//drmdownload=null;
	}

	class Notifier implements Runnable {

		private int error;

		public Notifier(int error) {
			this.error = error;
		}

		public void run() {
			switch (error) {
				case 1000:

					Util.ToastMessage(context, "남은 여유 공간이 요청한 파일크기 보다 작습니다.");

					break;
				case 1100:

					Util.ToastMessage(context, "이미 다운로드 받은 파일이거나 인터넷이 불안정하여 다운로드가 취소되었습니다.");
					break;
				case 1200:


					break;
				default:
					Util.ToastMessage(context, "http error code:" + error);
					break;
			}
			clearDown();
			// err < 1000 보다 작을경우 에러 숫자는 http status code 을 의미함.
			// 참조 : http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html

			// err : 1000 = 남은 여유 공간이 요청한 파일크기 보다 작습니다.
			// err : 1100 = 파일 다운로드중 인터넷 환경이 변경되었거나 인터넷이 불안정하여 다운로드가 취소되었습니다.
			// err : 1200 = 다운로드 취소 요청으로 다운로드가 취소되었습니다.

		}
	};


	@Override
	protected void onDestroy() {
		if(db!=null)
			db.close();
		super.onDestroy();
	}
}