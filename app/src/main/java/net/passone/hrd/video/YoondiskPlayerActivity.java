
package net.passone.hrd.video;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.yoondisk.lib.libvlc.EventHandler;
import com.yoondisk.lib.libvlc.IVideoPlayer;
import com.yoondisk.lib.libvlc.LibVLC;
import com.yoondisk.lib.libvlc.LibVlcException;
import com.yoondisk.lib.player.AudioServiceController;
import com.yoondisk.lib.player.Util;
import com.yoondisk.lib.player.WeakHandler;
import com.yoondisk.lib.player.gui.video.Etc;


import net.passone.hrd.R;
import net.passone.hrd.adapter.Adapter;
import net.passone.hrd.adapter.Api;
import net.passone.hrd.adapter.DBmanager;
import net.passone.hrd.adapter.OnResponseListener;
import net.passone.hrd.common.Adrm;
import net.passone.hrd.common.CUser;
import net.passone.hrd.common.StaticVars;
import net.passone.hrd.container.ApiResult;
import net.passone.hrd.container.PlayStatus;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class YoondiskPlayerActivity extends Activity implements IVideoPlayer, OnResponseListener {
    public final static String TAG = "YoondiskPlayerActivity";
    private final static String PLAY_FROM_VIDEOGRID = "com.yoondisk.lib.player.gui.video.PLAY_FROM_VIDEOGRID";

    private SurfaceView mSurface;
    private SurfaceHolder mSurfaceHolder;
    private FrameLayout mSurfaceFrame;
    private LibVLC VideoLib;
    private String mLocation;
    boolean firstStart=true;

    private String web_id =null;
    DBmanager db_manager;
    SQLiteDatabase db;
    //핸드러 상태 변수 정의
    boolean bhandler = false;
    //cur_position ms단위 저장 변수
    long cur_milpos = 0;

    private boolean network;
    private String bandwidth;
    private String obandwidth;
    private int download= 0;

    private String jurl =null;

    private Timer	timer = new Timer();
    private  long id_time=0;

    private static AlertDialog.Builder errbox;
    private static float speedx = (float)1.00;
    boolean isPrice=true;
    int lec_seq=0;
    String uid="",playtype="",chasi="",classkey="",part="",page="",classcount="",contentskey="",studykey="",is_finish="",p_markerTime="",is_control="";
    int content_seq=0;
    int total_possible_time=0;
    int log_seq=0;
    int progress_time=0;
    int cur_posi=0;
    int send_cnt=0;
    int lasttime=0;
    String prevpart="",prevpage="",nextpart="",nextpage="",sampleyn="";
    String title = "";
    int pause_posi=0,last_posi=0;
    boolean isFinish=false;

    //   boolean blog_check=false;
    //  int total_play_time = 0;
    // int play_cnt = 0;
    @Override
    public void onResponseReceived(int api, Object result) {
//        try {
            net.passone.hrd.common.Util.debug("Login result    :   " + result);
            switch(api) {
                case Api.STATUS:
                    PlayStatus apiResult=(PlayStatus)result;
                    if(result!=null) {
                        String message="";
                        if (apiResult.result.equals("OK")) {
                            lastPlayTime = System.currentTimeMillis();
                        }else {
                            message=getString(R.string.err_video);
                        }


                        if(message.length()>0)
                        {
                            net.passone.hrd.common.Util.alert(this, getString(R.string.app_name), getString(R.string.err_video), "확인", null, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            }, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                        } else{
                            if(isFinish)
                                onBackPressed();
                        }
                    }
                    else {
                        VideoLib.pause();
                        net.passone.hrd.common.Util.alert(this, getString(R.string.app_name), getString(R.string.err_video), "확인", null, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                    }
                    break;
                case Api.PLAYMOVE:
                    PlayStatus move=(PlayStatus)result;
                    if(move!=null)                {
                        String message="";
                        if (move.result.equals("OK")) {
                            prevpage=move.prevpage;
                            prevpart=move.prevpart;
                            nextpage=move.nextpage;
                            nextpart=move.nextpart;
                            mLocation=move.moviename.replace("\\/","/");
                            net.passone.hrd.common.Util.debug("playmove");
                            //   mLocation="http://c06.yoondisk.co.kr/5205/cybercampus/227019/01/01_02.mp4";
                            CUser.callbackUrl=move.htmlname.replace("\\/","/");
                            if(sampleyn.equals("N"))
                            {
                                chasi=move.ncnt + " / " + move.tcnt;
                                title=move.pagename;

                            }
                            classkey=move.classkey;
                            part = move.part;
                            page = move.page;
                            classcount = move.classcount;
                            studykey = move.studykey;
                            is_finish=move.finish;
                            p_markerTime=move.markertime;
                            cur_posi = Integer.parseInt(p_markerTime);
                            cur_milpos=cur_posi*1000;
                            last_posi=cur_posi*1000;
                            is_control=move.controlpbar;
                            if(is_control.equals("N"))
                                mSeekbar.setEnabled(false);
                            else
                                mSeekbar.setEnabled(true);
                            String filename=classkey+"_"+part+"_"+page+".mp4";
                            if(mLocation.length()==0)
                            {

                                finish();
                            }
                            File dfile = new File(net.passone.hrd.common.Util.getDownloadPath(filename));

                            String sql = "SELECT * FROM download  WHERE filename=?";
                            Cursor cursor=db.rawQuery(sql, new String[]{filename});
                            int nDownloadCnt = cursor.getCount();
                            cursor.close();
                            if(dfile.isFile() && dfile.exists() && nDownloadCnt>0)
                            {
                                mLocation="file://"+ net.passone.hrd.common.Util.getDownloadPath(filename);
                                ((ImageView)findViewById(R.id.iv_state)).setImageResource(R.drawable.icon_download);
                            }
                            else
                            {
                                ((ImageView)findViewById(R.id.iv_state)).setImageResource(R.drawable.icon_streaming);

                            }
                            Log.d("passone","start_time=&cur_posi="+cur_posi);
                            // 상단 타이틀 표시
                            mTitle.setText(title+" "+chasi);
                            VideoLib.stop();

                            // 플레이어 embed 재생시작!! start_player() 함수를 참조하세요.
                            eventHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    start_player();
                                }
                            }, 1);
                            if(send_timer == null) {
                                send_timer = new Timer();
                            }
                            net.passone.hrd.common.Util.debug("test oncreate send");

                            if(send_timer!=null)
                            {
                                send_timer.schedule(new TimerTask() {
                                    Handler sHandler = new Handler();
                                    final Runnable showToastMessage = new Runnable() {
                                        public void run() {
                                            Toast.makeText(context, "재생중", Toast.LENGTH_SHORT).show();
                                        }
                                    };
                                    @Override
                                    public void run() {

                                        String state = "";
//                                        sHandler.post(showToastMessage);

                                        sendPlayStatus((int) VideoLib.getTime());
                                    }
                                }, 0, STATUS_PERIOD);
                            }
                            lastPlayTime = System.currentTimeMillis();
//        sendPlayStatus((int) VideoLib.getTime());

                            // 버퍼링 로딩바 숨김.
                            mloading.setVisibility(View.INVISIBLE);
                            // 화면에 5분마드 회원 아이디 표시 및  hdmi연결체크 타이머 등록.
                            if(timer == null) {
                                timer = new Timer();
                            }
                            if(timer!=null) {
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (jurl!= null) {
                                                id_time++;
                                                if (id_time==300) {
                                                    id_time=0;
                                                    if (VideoLib.isPlaying()==true){
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                // 화면에 5분마다 랜덤하게 아이디 표시.
                                                                set_webid();
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        } catch (Exception  e) {


                                        }
                                        // hdmi 연결 감지
                                        if (dog.is_hdmi()!=0){
                                            err_msg="HDMI에 연결된 기기를 해제후 다시 사용하세요.";
                                            VideoLib.stop();
                                            YoondiskPlayerActivity.this.runOnUiThread(new Runnable() {
                                                public void run()
                                                {
                                                    String msg=err_msg;
                                                    AlertDialog.Builder ad=new AlertDialog.Builder(YoondiskPlayerActivity.this);
                                                    ad.setMessage(msg);
                                                    ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            finish();
                                                        }
                                                    });
                                                    ad.show();
                                                }
                                            });

                                        }

                                    }
                                }, 1000, 2000);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    // only for gingerbread and newer versions
//                                new DrmTask().execute();

                                }
                            }
                        }
                        else {
                            net.passone.hrd.common.Util.alert(this, getString(R.string.app_name), "재생할 수 없습니다.", "확인", null, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            }, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                        }


                    }
                    else {
                        VideoLib.pause();
                        net.passone.hrd.common.Util.alert(this, getString(R.string.app_name), getString(R.string.err_video), "확인", null, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                    }
                    break;
/*
            case Api.LOGSAVE:
                LogInfo loginfo= (LogInfo)result;
                if(result!=null) {
                    send_cnt=0;
                    if (loginfo.error.equals("True")) {
//                        net.passone.hrdandroid.common.Util.analyticsSend(this, "동영상", "진도율 전송 완료", "ID: " + CUser.userid);

                        lastPlayTime = System.currentTimeMillis();

                        db_manager = new DBmanager(context, "UserInfo.db");
                        db = db_manager.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.clear();
                        cv.put("viewdate", new Date().getTime());
                        int update = db.update("lecture_detail", cv, "lec_basket_seq=? and lecture_seq=? and content_seq=?", new String[]{String.valueOf(lec_basket_seq), String.valueOf(lec_seq), String.valueOf(content_seq)});
                        net.passone.hrdandroid.common.Util.debug("update:" + update);

                    } else {
                        VideoLib.pause();
                        net.passone.hrdandroid.common.Util.alert(this, getString(R.string.app_name), loginfo.message, "확인", null, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                    }
                }
                else
                {
                    if(send_cnt<3)
                    {
                        sendPlayStatus((int) VideoLib.getTime());
                        send_cnt++;

                    }
                    else
                    {
                        VideoLib.pause();

                        net.passone.hrdandroid.common.Util.PopupMessage(this, getResources().getString(R.string.api_http_alert));

                        finish();
                    }
                }
                break;
                */
            }
//        }catch(Exception e){
//            net.passone.hrd.common.Util.debug("onResponseReceived:"+e.toString());
//
//        }
    }

    private enum RepeatState {NONE, START, END, ING};


    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;
    private int mCurrentSize = SURFACE_BEST_FIT;

    /** Overlay */
    private View mOverlayHeader;
    private View mOverlayProgress;
    private static final int OVERLAY_TIMEOUT = 4000;
    private static final int OVERLAY_INFINITE = 3600000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private static final int SURFACE_SIZE = 3;
    private static final int FADE_OUT_INFO = 4;
    private boolean mDragging;
    private boolean mShowing;
    private int mUiVisibility = -1;
    private SeekBar mSeekbar;
    private TextView mTitle;
    private TextView webidR;
    private TextView mSysTime;
    private TextView mBattery;
    private TextView mTime;
    private TextView mCache;
    private TextView mLength;
    private TextView mInfo;
    private ProgressBar mloading;
    private ImageButton mPlayPause;
    private ImageButton btn_player_close;

    private boolean mode_lock=false;
    private boolean mEnableBrightnessGesture;
    private boolean mDisplayRemainingTime = false;
    private ImageButton mAudioTrack;
    private ImageButton mSubtitle;
    private ImageButton mSize;
    private boolean mIsLocked = false;
    public Etc dog = new Etc();
    private String err_msg="";


    private boolean mSwitchingView;
    private boolean mEndReached;

    private int savedIndexPosition = -1;

    // size of the video
    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;

    //Volume
    private AudioManager mAudioManager;
    private int mAudioMax;

    //Touch Events
    private static final int TOUCH_NONE = 0;
    private static final int TOUCH_VOLUME = 1;
    private static final int TOUCH_BRIGHTNESS = 2;
    private static final int TOUCH_SEEK = 3;
    private int mTouchAction;
    private int mSurfaceYDisplayRange;
    private float mTouchY, mTouchX, mVol;
    private RepeatState repeat_state = RepeatState.NONE;
    private long repeat_startTime = 0;
    private long repeat_endTime = 0;
    private BroadcastReceiver netReceiver;
    Context context;
    OnResponseListener callback;
    private long lastPlayTime = 0; //본시간 계산하기 위함
    private static final int STATUS_PERIOD = 20 * 60* 1000; //기본: 20분
    Timer send_timer;
    // Brightnessncre
    private boolean mIsFirstBrightnessGesture = true;
    Adapter _api;
    PowerManager pm;
    PowerManager.WakeLock wl;
    int jumprate=0;
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    /**
     * Player UI 구성  -- onCreate
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
//        net.passone.hrdandroid.common.Util.analyticsSend(this, "동영상", "플레이어 진입", "ID: " + CUser.userid);
        setContentView(R.layout.yoondisk_player);

        // 현재 Instance 저장.
        Util.setLibVlcInstance(this);
        _api=new Adapter();
        context=this;
        callback=this;
        db_manager = new DBmanager(this,"UserInfo.db");
        db=db_manager.getReadableDatabase();
        pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mLocation = null;
        //cur_posi = 0; //20160712 추가
        String lines="[Streaming] ";
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            mLocation = extras.getString("url");
            title=extras.getString("title");
            net.passone.hrd.common.Util.debug("title:"+title);
            classkey=extras.getString("classkey");
            part = extras.getString("part");
            page = extras.getString("page");
            classcount = extras.getString("classcount");
            contentskey = extras.getString("contentskey");
            studykey = extras.getString("studykey");
            sampleyn = extras.getString("sampleyn");
            if(sampleyn!=null && sampleyn.length()>0)
            {
                if(sampleyn.equals("Y"))
                {
                    ((ImageButton)findViewById(R.id.btn_next)).setVisibility(View.GONE);
                    ((ImageButton)findViewById(R.id.btn_prev)).setVisibility(View.GONE);
                    isPrice=false;
                }
                else
                {
                    chasi=extras.getString("current") + " / " + extras.getString("total");

                    ((ImageButton)findViewById(R.id.btn_next)).setVisibility(View.VISIBLE);
                    ((ImageButton)findViewById(R.id.btn_prev)).setVisibility(View.VISIBLE);
                    isPrice=true;

                }
            }

            if(extras.getString("finish")!=null)
                is_finish=extras.getString("finish");
            Log.d("passone","isfinish="+is_finish);
            if(extras.getString("markerTime")!=null)
            {
                p_markerTime=extras.getString("markerTime");
                if(p_markerTime.length()>0) {
                    Log.d("passone","real cur_pos=start"+p_markerTime);
                    cur_posi = Integer.parseInt(p_markerTime);
                    //lovesing 추가
                    cur_milpos = cur_posi*1000;
                    //lovesing 추가 끝

                    Log.d("passone","real cur_pos=start"+cur_posi);
                }

            }

            if(extras.getString("control")!=null)
                is_control=extras.getString("control");

        }
        Log.d("passone","real cur_pos="+cur_posi);


        /**
         * 인터넷 연결 상태 및 BroadcastReceiver 등록.
         */
        network = isDataConnected();
        obandwidth=bandwidth = isHighBandwidth();
        netReceiver=new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                network = isDataConnected();
                if (network==false){
                    String msg="인터넷 연결이 종료되었습니다.";
                    Toast.makeText(YoondiskPlayerActivity.this, msg, Toast.LENGTH_LONG).show();
                    VideoLib.stop();
                    finish();
                }
                bandwidth = isHighBandwidth();

                if (!bandwidth.equals(obandwidth)){
                    String msg="인터넷  환경이 변경되었습니다.";
                    Toast.makeText(YoondiskPlayerActivity.this, msg, Toast.LENGTH_LONG).show();
                    if (download==0){
                        VideoLib.stop();
                        msg="인터넷 환경이 변경되었습니다. 계속하시겠습니까?";
                        net.passone.hrd.common.Util.alert(context, getString(R.string.app_name), msg, "확인", "취소", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                finish();

                            }
                        });

                    }
                }
                obandwidth=bandwidth;
            };
        };
        registerReceiver(netReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));


        /**
         * xml layout 추가 및 버튼 구성 이벤트  등록.
         */

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        if(Util.isICSOrLater())
            getWindow().getDecorView().findViewById(android.R.id.content).setOnSystemUiVisibilityChangeListener(
                    new OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if (visibility == mUiVisibility)
                                return;
                            setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
                            if (visibility == View.SYSTEM_UI_FLAG_VISIBLE && !mShowing) {
                                showOverlay();
                            }
                            mUiVisibility = visibility;
                        }
                    }
            );

        /** initialize Views an their Events */
        mOverlayHeader = findViewById(R.id.player_overlay_header);
        mOverlayProgress = findViewById(R.id.progress_overlay);

        /* header */
        mTitle = (TextView) findViewById(R.id.player_overlay_title);
        webidR =  (TextView) findViewById(R.id.web_id);


        mSysTime = (TextView) findViewById(R.id.player_overlay_systime);
        mBattery = (TextView) findViewById(R.id.player_overlay_battery);

        // Position and remaining time
        mTime = (TextView) findViewById(R.id.player_overlay_time);
        mCache = (TextView) findViewById(R.id.player_loading_buf);
        mTime.setOnClickListener(mRemainingTimeListener);
        mLength = (TextView) findViewById(R.id.player_overlay_length);
        mLength.setOnClickListener(mRemainingTimeListener);

        // the info textView is not on the overlay
        mInfo = (TextView) findViewById(R.id.player_overlay_info);
        mloading=(ProgressBar) findViewById(R.id.player_loading);

        mEnableBrightnessGesture = true;

        // 버튼 이벤트 처리 등록.
        mPlayPause = (ImageButton) findViewById(R.id.player_overlay_play);
        mPlayPause.setOnClickListener(mPlayPauseListener);

        btn_player_close = (ImageButton) findViewById(R.id.btn_player_close);
        btn_player_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        mSize = (ImageButton) findViewById(R.id.player_overlay_size);
        mSize.setOnClickListener(mSizeListener);

        // Surface 등록 및 화질 지정.
        mSurface = (SurfaceView) findViewById(R.id.player_surface);
        mSurfaceHolder = mSurface.getHolder();
        mSurfaceFrame = (FrameLayout) findViewById(R.id.player_surface_frame);
        mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
        mSurfaceHolder.addCallback(mSurfaceCallback);

        mSeekbar = (SeekBar) findViewById(R.id.player_overlay_seekbar);
        if(is_control.equals("Y")) {

            mSeekbar.setOnSeekBarChangeListener(mSeekListener);
        }
        // AudioMnanger 및  최대 볼륨 크기 구함.
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mSwitchingView = false;
        mEndReached = false;

        // 스마트폰 밧데리 변화  Receiver 등록.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mReceiver, filter);

        //라이브러리 initialisation 체크.
        try {
            VideoLib = Util.getLibVlcInstance();
        } catch (LibVlcException e) {
            Log.d(TAG, "Lib initialisation failed");
            return;
        }

        // 플레이어 재생 상태 이벤트 등록.
        // 상태 이벤트는 VideoPlayerEventHandler 참조.
        EventHandler em = EventHandler.getInstance();
        em.addHandler(eventHandler);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // 에러 alertdialog 등록.
        errbox = new AlertDialog.Builder(this);
        ((ImageButton)findViewById(R.id.btn_next)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                timer=null;
                send_timer=null;
                sendPlayStatus((int) VideoLib.getTime());
                _api.play_move(classkey,classcount,studykey,nextpart,nextpage,context,callback);

            }

        });
        ((ImageButton)findViewById(R.id.btn_prev)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                timer=null;
                send_timer=null;
                sendPlayStatus((int) VideoLib.getTime());

                _api.play_move(classkey,classcount,studykey,prevpart,prevpage,context,callback);

            }

        });
        Log.d("passone","start_time=start&cur_posi="+cur_posi);
        _api.play_move(classkey,classcount,studykey,part,page,null,callback);

    }

    /**
     * 인터넷 연결 상태 체크.
     */
    private boolean isDataConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 인터넷 연결 Bandwidth 상태.
     */
    private String isHighBandwidth() {
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wm = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo winfo = wm.getConnectionInfo();
                String ssid = winfo.getSSID();
                return ssid;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return "^3g";
            }
        } catch (Exception e) {

        }
        return "";
    }

    /**
     *  플레이어 Activity onStart 함수 구현.
     */
    @Override
    protected void onStart() {
        super.onStart();
        showOverlay();
        mSwitchingView = false;
    }

    /**
     *  플레이어 Activity onPause 함수 구현.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Adrm.isPlay(false);
        net.passone.hrd.common.Util.debug("pause_posi" + pause_posi);
        try{
            if(wl!=null)
                wl.release(); // wake mode 끝
        }catch(Exception e){}
        if(mSwitchingView) {
            Log.d(TAG, "mLocation = \"" + mLocation + "\"");
            AudioServiceController.getInstance().showWithoutParse(savedIndexPosition);
            AudioServiceController.getInstance().unbindAudioService(this);
            return;
        }

        long time = VideoLib.getTime();
        long length = VideoLib.getLength();
        //remove saved position if in the last 5 seconds
        if (length - time < 5000)
            time = 0;
        else
            time -= 5000; // go back 5 seconds, to compensate loading time
        if(VideoLib.isPlaying())
            pause();
        mSurface.setKeepScreenOn(false);
        AudioServiceController.getInstance().unbindAudioService(this);
        if (send_timer != null) {
            send_timer.cancel();
            send_timer=null;
        }
    }

    /**
     *  플레이어 Activity onStop 함수 구현.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     *  플레이어 Activity onDestroy 함수 구현.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unregisterReceiver(netReceiver);
        if (VideoLib != null) {
            VideoLib.stop();
        }
        EventHandler em = EventHandler.getInstance();
        em.removeHandler(eventHandler);
        try {
            timer.cancel();
        } catch (Exception e) {

        }
        if (send_timer != null) {
            send_timer.cancel();
            send_timer=null;
        }
        mAudioManager = null;
        if(db!=null)
            db.close();
    }

    /**
     *  스마트폰 밧데리 실시간 상태 체크  onReceive
     */
    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int batteryLevel = intent.getIntExtra("level", 0);
            mBattery.setText(String.format("%d%%", batteryLevel));
        }
    };

    /**
     *  플레이어 Activity onResume 함수 구현.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(wl==null)
        {
            wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "MyActivity");
            wl.acquire(); // wake mode 시작

        }
        AudioServiceController.getInstance().bindAudioService(this);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (VideoLib != null && VideoLib.isPlaying()) {
//                    KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//                    if (km.inKeyguardRestrictedInputMode())
//                        VideoLib.pause();
//                }
//            }
//        }, 500);



        showOverlay();
        if (VideoLib != null) {
            net.passone.hrd.common.Util.debug("onresume");

        }

        Log.d("passone",String.valueOf(cur_posi));
        Log.d("cur_posi",String.valueOf(cur_posi));
    }


    /**
     *  플레이어 Activity start 함수 구현.
     */
    public static void start(Context context, String location) {
        start(context, location, null, -1, false, false);
    }

    public static void start(Context context, String location, Boolean fromStart) {
        start(context, location, null, -1, false, fromStart);
    }

    public static void start(Context context, String location, String title, Boolean dontParse) {
        start(context, location, title, -1, dontParse, false);
    }

    public static void start(Context context, String location, String title, int position, Boolean dontParse) {
        start(context, location, title, position, dontParse, false);
    }

    public static void start(Context context, String location, String title, int position, Boolean dontParse, Boolean fromStart) {

        Intent intent = new Intent(context, YoondiskPlayerActivity.class);
        intent.setAction(YoondiskPlayerActivity.PLAY_FROM_VIDEOGRID);
        intent.putExtra("itemLocation", location);
        intent.putExtra("itemTitle", title);
        intent.putExtra("dontParse", dontParse);
        intent.putExtra("fromStart", fromStart);
        intent.putExtra("itemPosition", position);

        if (dontParse)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        else {
            AudioServiceController asc = AudioServiceController.getInstance();
            asc.stop();
        }

        context.startActivity(intent);

    }

    /**
     *  스마트폰 밧데리 실시간 상태 체크 BroadcastReceiver
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_CHANGED)) {
                int batteryLevel = intent.getIntExtra("level", 0);
                if (batteryLevel >= 50)
                    mBattery.setTextColor(Color.GREEN);
                else if (batteryLevel >= 30)
                    mBattery.setTextColor(Color.YELLOW);
                else
                    mBattery.setTextColor(Color.RED);
                mBattery.setText(String.format("%d%%", batteryLevel));
            }

        }
    };

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        showOverlay();
        return true;
    }

    /**
     *  화면 크기가 변경되었을때 Surface 사이즈 변경.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
        super.onConfigurationChanged(newConfig);
    }

    /**
     *  비디오 출력을 위한 Surface 사이즈 지정.
     */
    @Override
    public void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {
        if (width * height == 0)
            return;

        mVideoHeight = height;
        mVideoWidth = width;
        mVideoVisibleHeight = visible_height;
        mVideoVisibleWidth  = visible_width;
        mSarNum = sar_num;
        mSarDen = sar_den;
        Message msg = mHandler.obtainMessage(SURFACE_SIZE);
        mHandler.sendMessage(msg);
    }


    /**
     * 화면 중앙 상태표시 view 표시 / text=>표시할 문자열 ,  duration=> 해당시간 만큼 표시.
     */
    private void showInfo(String text, int duration) {
        mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(text);
        mHandler.removeMessages(FADE_OUT_INFO);
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
    }

    /**
     * 화면 중앙 상태표시 view 표시 / text=>표시할 문자열
     */
    private void showInfo(String text) {
        mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(text);
        mHandler.removeMessages(FADE_OUT_INFO);
    }

    /**
     * 화면 중앙 상태표시를  delay후  FADE_OUT_INFO처리함.
     */
    private void hideInfo(int delay) {
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, delay);
    }

    /**
     * 화면 중앙 상태표시
     */
    private void hideInfo() {
        hideInfo(0);
    }

    /**
     * 상태바 및 화면 정보  fadeOut
     */
    private void fadeOutInfo() {
        if (mInfo.getVisibility() == View.VISIBLE)
            mInfo.startAnimation(AnimationUtils.loadAnimation(
                    YoondiskPlayerActivity.this, android.R.anim.fade_out));
        mInfo.setVisibility(View.INVISIBLE);
    }

    /**
     *  플레이어 재생 상태 이벤트  / 진도율 관련은 여기서 기능을 구현하셔야 합니다.
     */

    private final Handler eventHandler = new VideoPlayerEventHandler(this);
    private static class VideoPlayerEventHandler extends WeakHandler<YoondiskPlayerActivity> {
        public VideoPlayerEventHandler(YoondiskPlayerActivity owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            YoondiskPlayerActivity activity = getOwner();
            //Log.d("passone","playtime=1cur_pos="+activity.cur_posi+"start_time="+activity.cur_milpos+"&isfinish="+activity.is_finish+"&length="+activity.VideoLib.getLength());
            if(!activity.bhandler && activity.cur_milpos>0 ){
                //Log.d("passone","playtime=2cur_pos="+activity.cur_posi+"start_time="+activity.cur_milpos+"&isfinish="+activity.is_finish);

                if(activity.VideoLib.getTime() < activity.cur_milpos) {
                    //Log.d("passone","playtime=4cur_pos="+activity.cur_posi+"start_time="+activity.cur_milpos+"&isfinish="+activity.is_finish);
                    activity.VideoLib.setTime(activity.cur_milpos);
                    activity.bhandler= true;
                }
            }


            if(activity == null) return;
            DecimalFormat dFormat = new DecimalFormat("###");

            // 버퍼링 상태 표시
            float cache2=activity.VideoLib.getCache();

            if (cache2>0){
                if (cache2<0){cache2=(float) 0.01;}
                if ( cache2>0 && cache2<1){
                    cache2=(float) (cache2+0.01);
                    if (cache2>1){cache2=1;}
                    activity.mCache.setVisibility(View.VISIBLE);
                    activity.mCache.setText(dFormat.format(cache2*100)+"%");
                }
            }
            switch (msg.getData().getInt("event")) {
                case EventHandler.MediaPlayerBuffering:
                    //lovesing 추가
                    int nBuffer = (int)cache2*100;
                    if(nBuffer==100){
                        //   Log.d("passone","playtime=startok"+activity.cur_posi+"&length="+activity.VideoLib.getLength());
                        if(activity.cur_milpos > activity.VideoLib.getLength()){
                            //   Log.d("passone","playtime=pass="+activity.cur_posi+"&length="+activity.VideoLib.getLength());
                            activity.cur_milpos = activity.cur_posi;
                            activity.cur_posi = activity.cur_posi/1000;
                            activity.VideoLib.setTime(activity.cur_milpos);
                        }
                    }
                    //lovesing 추가 끝

                    //  Log.d("passone","playtime="+nBuffer+"&length="+activity.VideoLib.getLength());
                    if (activity.mloading.getVisibility()!=View.VISIBLE){
                        activity.mloading.setVisibility(View.VISIBLE);
                        activity.mCache.setVisibility(View.VISIBLE);
                    }
                    break;
                case EventHandler.MediaPlayerTimeChanged:
                    activity.mCache.setVisibility(View.GONE);
                    activity.mloading.setVisibility(View.GONE);
                    break;
                case EventHandler.MediaPlayerPlaying:
                    activity.mloading.setVisibility(View.GONE);
                    activity.mCache.setVisibility(View.GONE);
                    activity.setOverlayProgress();
                    activity.showOverlay();
                    break;
                case EventHandler.MediaPlayerPaused:

                    break;
                case EventHandler.MediaPlayerStopped:

                    break;
                case EventHandler.MediaPlayerEndReached:
                    getOwner().endReached();

                    break;
                case EventHandler.MediaPlayerVout:
                    activity.handleVout(msg);
                    break;
                case EventHandler.MediaPlayerPositionChanged:
                    activity.mloading.setVisibility(View.GONE);
                    activity.mCache.setVisibility(View.GONE);
                    break;
                case EventHandler.MediaPlayerEncounteredError:
                    errbox.setTitle("알림");
                    errbox.setMessage("파일이 존재하지 않거나\n재생할수 없는 형식의 파일입니다.");
                    errbox.setIcon(R.mipmap.ic_launcher).setPositiveButton("확인", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick( DialogInterface dialog, int which )
                        {
                            getOwner().endReached();

                        }
                    });
                    errbox.show();
                    break;

            }
            activity.updateOverlayPausePlay();
        }
    };

    /**
     * 화면 재생 컨트롤 및 상단 뷰 show /hidden
     */
    private final Handler mHandler = new VideoPlayerHandler(this);

    private static class VideoPlayerHandler extends WeakHandler<YoondiskPlayerActivity> {
        public VideoPlayerHandler(YoondiskPlayerActivity owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            YoondiskPlayerActivity activity = getOwner();
            if(activity == null)
                return;

            switch (msg.what) {
                case FADE_OUT:
                    activity.hideOverlay(false);
                    break;
                case SHOW_PROGRESS:
                    int pos = activity.setOverlayProgress();
                    if (activity.canShowProgress()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
                case SURFACE_SIZE:
                    activity.changeSurfaceSize();
                    break;
                case FADE_OUT_INFO:
                    activity.fadeOutInfo();
                    break;
            }
        }
    };

    /**
     * 플레이러 하단 컨트롤러 show 상테.
     */
    private boolean canShowProgress() {
        return !mDragging && mShowing && VideoLib.isPlaying();
    }

    /**
     * 재생이 끝난경우 호출됨.
     */
    private void endReached() {
        mEndReached = true;
        //lovesing 추가
        is_finish = "Y";
        //lovesing 추가 끝
        net.passone.hrd.common.Util.debug("test endreach");
        mSurface.setKeepScreenOn(false);

        try{
            if(wl!=null)
                wl.release(); // wake mode 끝
        }catch(Exception e){}
        sendPlayStatus((int) VideoLib.getTime());
//        finish();
    }


    /**
     * 플레이어 video 입력신호 검출시.(최초 재생진입후 재새 바로이전에)
     */
    private void handleVout(Message msg) {
        if (msg.getData().getInt("data") == 0 && !mEndReached) {
            mSwitchingView = true;
            finish();
        }
    }

    /**
     * 플레이어 비디오 Surface 사이즈 변경 .
     */
    private void changeSurfaceSize() {
        // get screen size
        int dw = getWindow().getDecorView().getWidth();
        int dh = getWindow().getDecorView().getHeight();

        // getWindow().getDecorView() doesn't always take orientation into account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (dw > dh && isPortrait || dw < dh && !isPortrait) {
            int d = dw;
            dw = dh;
            dh = d;
        }

        // sanity check
        if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0) {
            Log.e(TAG, "Invalid surface size");
            return;
        }

        // compute the aspect ratio
        double ar, vw;
        double density = (double)mSarNum / (double)mSarDen;
        if (density == 1.0) {
            /* No indication about the density, assuming 1:1 */
            vw = mVideoVisibleWidth;
            ar = (double)mVideoVisibleWidth / (double)mVideoVisibleHeight;
        } else {
            /* Use the specified aspect ratio */
            vw = mVideoVisibleWidth * density;
            ar = vw / mVideoVisibleHeight;
        }

        // compute the display aspect ratio
        double dar = (double) dw / (double) dh;

        switch (mCurrentSize) {
            case SURFACE_BEST_FIT:
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_FIT_HORIZONTAL:
                dh = (int) (dw / ar);
                break;
            case SURFACE_FIT_VERTICAL:
                dw = (int) (dh * ar);
                break;
            case SURFACE_FILL:
                break;
            case SURFACE_16_9:
                ar = 16.0 / 9.0;
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_4_3:
                ar = 4.0 / 3.0;
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_ORIGINAL:
                dh = mVideoVisibleHeight;
                dw = (int) vw;
                break;
        }

        // force surface buffer size
        mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        LayoutParams lp = mSurface.getLayoutParams();
        lp.width  = dw * mVideoWidth / mVideoVisibleWidth;
        lp.height = dh * mVideoHeight / mVideoVisibleHeight;
        mSurface.setLayoutParams(lp);

        // set frame size (crop if necessary)
        lp = mSurfaceFrame.getLayoutParams();
        lp.width = dw;
        lp.height = dh;
        mSurfaceFrame.setLayoutParams(lp);

        mSurface.invalidate();
    }


    /**
     * 플레이어 트래킹 터치 이벤트.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsLocked) {
            // locked, only handle show/hide & ignore all actions
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!mShowing) {
                    showOverlay();
                } else {
                    hideOverlay(true);
                }
            }
            return false;
        }

        DisplayMetrics screen = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(screen);

        if (mSurfaceYDisplayRange == 0)
            mSurfaceYDisplayRange = Math.min(screen.widthPixels, screen.heightPixels);

        float y_changed = event.getRawY() - mTouchY;
        float x_changed = event.getRawX() - mTouchX;

        // coef is the gradient's move to determine a neutral zone
        float coef = Math.abs (y_changed / x_changed);
        float xgesturesize = ((x_changed / screen.xdpi) * 2.54f);

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                // Audio
                mTouchY = event.getRawY();
                mVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mTouchAction = TOUCH_NONE;
                // Seek
                mTouchX = event.getRawX();
                break;

            case MotionEvent.ACTION_MOVE:
                // No volume/brightness action if coef < 2
                if (coef > 2) {
                    // Volume (Up or Down - Right side)
                    if (!mEnableBrightnessGesture || mTouchX > (screen.widthPixels / 2)){
                        doVolumeTouch(y_changed);
                    }
                    // Brightness (Up or Down - Left side)
                    if (mEnableBrightnessGesture && mTouchX < (screen.widthPixels / 2)){
                        doBrightnessTouch(y_changed);
                    }
                    // Extend the overlay for a little while, so that it doesn't
                    // disappear on the user if more adjustment is needed. This
                    // is because on devices with soft navigation (e.g. Galaxy
                    // Nexus), gestures can't be made without activating the UI.
                    if(Util.hasNavBar())
                        showOverlay();
                }
                // Seek (Right or Left move)
                doSeekTouch(coef, xgesturesize, false);
                break;

            case MotionEvent.ACTION_UP:
                // Audio or Brightness

                if ( mTouchAction == TOUCH_NONE) {
                    if (!mShowing) {
                        net.passone.hrd.common.Util.debug("no mShowing");
//                        mShowing=true;

                        showOverlay();

                    } else {
//                        mShowing=false;
                        net.passone.hrd.common.Util.debug("mShowing");
                        hideOverlay(true);
                    }
                }
                // Seek
                doSeekTouch(coef, xgesturesize, true);
                break;
        }
        return mTouchAction != TOUCH_NONE;
    }

    /**
     * 플레이어 좌측 부터 우측 트래킹 재생시간 이동 이벤트.
     */
    private void doSeekTouch(float coef, float gesturesize, boolean seek) {
        if (coef > 0.5 || Math.abs(gesturesize) < 1)
            return;

        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_SEEK)
            return;
        mTouchAction = TOUCH_SEEK;

        if (!mShowing) showOverlay();

        long length = VideoLib.getLength();
        long time = VideoLib.getTime();

        int jump = (int) (Math.signum(gesturesize) * ((600000 * Math.pow((gesturesize / 8), 4)) + 3000));

        if ((jump > 0) && ((time + jump) > length))
            jump = (int) (length - time);
        if ((jump < 0) && ((time + jump) < 0))
            jump = (int) -time;
        if(is_control.equals("Y")) {
            if (seek && length > 0)
                VideoLib.setTime(time + jump);

            if (length > 0)
                showInfo(String.format("%s%s (%s)",
                        jump >= 0 ? "+" : "",
                        Util.millisToString(jump),
                        Util.millisToString(time + jump)), 1000);
            else
                showInfo("Unseekable stream", 1000);
        }
    }

    /**
     * 플레이어 우측 상하 트래킹 볼륨조절 이벤트.
     */
    private void doVolumeTouch(float y_changed) {
        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_VOLUME)
            return;
        int delta = -(int) ((y_changed / mSurfaceYDisplayRange) * mAudioMax);
        int vol = (int) Math.min(Math.max(mVol + delta, 0), mAudioMax);
        if (delta != 0) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
            mTouchAction = TOUCH_VOLUME;
            showInfo("볼륨" + '\u00A0' + Integer.toString(vol),1000);
        }
    }

    /**
     * 플레이어 화면  초기  밝기 최대 밝게.
     */
    private void initBrightnessTouch() {
        float brightnesstemp = 0.01f;
        // Initialize the layoutParams screen brightness
        if(CUser.bright==0)
        {
            try {
                brightnesstemp = android.provider.Settings.System.getInt(getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS) / 255.0f;
            } catch (SettingNotFoundException e) {
                e.printStackTrace();
            }
        }
        else
            brightnesstemp=CUser.bright;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightnesstemp;
        getWindow().setAttributes(lp);
        mIsFirstBrightnessGesture = false;
    }

    /**
     * 플레이어 화면 밝기 조정 왼쪽 영역 트래킹 이벤트
     */
    private void doBrightnessTouch(float y_changed) {
        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_BRIGHTNESS)
            return;
        if (mIsFirstBrightnessGesture) initBrightnessTouch();
        mTouchAction = TOUCH_BRIGHTNESS;

        float delta = - y_changed / mSurfaceYDisplayRange * 0.07f;

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        CUser.bright=Math.min(Math.max(lp.screenBrightness + delta, 0.01f), 1);
        lp.screenBrightness =  CUser.bright;

        getWindow().setAttributes(lp);

        ContentValues cv = new ContentValues();
        cv.put("bright", CUser.bright);
        int update=db.update("version",cv,null,null);
        if(update==0)
            db.insert("version","",cv);

        showInfo("밝기" + '\u00A0' + Math.round(lp.screenBrightness*15),1000);
    }

    /**
     * 플레이어 재생 포지션바 Listener
     */
    private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        /**
         * 플레이어 재생 포지션바 StartTracking
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mDragging = true;
            showOverlay(OVERLAY_INFINITE);
        }
        /**
         * 플레이어 재생 포지션바 StopTrackingTouch
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mDragging = false;
            showOverlay();
            hideInfo();
        }

        /**
         * 플레이어 재생 포지션바 이동 정지시 이벤트 처리.
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                VideoLib.setTime(progress);
                setOverlayProgress();
                last_posi=progress;
                mTime.setText(Util.millisToString(progress));
                showInfo(Util.millisToString(progress));
            }

        }
    };


    /**
     * 플레이어 재생 버튼 또는 일시정지 버튼 클릭시 처리 이벤트.
     */
    private final OnClickListener mPlayPauseListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (VideoLib.isPlaying())
                pause();
            else
            {
                net.passone.hrd.common.Util.debug("seekbar:"+mSeekbar.getProgress()+"/max:"+mSeekbar.getMax()+"/lastposi:"+last_posi);
                if(mEndReached)
                {
                    if(last_posi==mSeekbar.getMax())
                    {
                        cur_posi=0;
                    }
                    else
                        cur_posi=last_posi/1000;

                    start_player();
                }
                else
                    play();
            }
            showOverlay();
        }
    };

    /**
     * 플레이어 10초 뒤로 이동 << 버튼 클릭 이벤트.
     */
    private final OnClickListener mBackwardListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            seek(-jumprate);
        }
    };

    /**
     * 플레이어 10초 앞으로 이동 >> 버튼 클릭 이벤트.
     */
    private final OnClickListener mForwardListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            seek(jumprate);
        }
    };

    /**
     * 플레이어 재생이동 함수
     */
    public void seek(int delta) {
        // unseekable stream
        if(VideoLib.getLength() <= 0) return;

        long position = VideoLib.getTime() + delta;
        if (position < 0) position = 0;
        VideoLib.setTime(position);
        showOverlay();
    }


    /**
     * 플레이어 하단 우측 화면크기 버튼 클릭시 화면 사이즈 조절 루틴.
     */
    private final OnClickListener mSizeListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mCurrentSize < SURFACE_ORIGINAL) {
                mCurrentSize++;
            } else {
                mCurrentSize = 0;
            }
            changeSurfaceSize();
            switch (mCurrentSize) {
                case SURFACE_BEST_FIT:
                    showInfo("화면맞춤", 1000);
                    break;
                case SURFACE_FIT_HORIZONTAL:
                    showInfo("가로맞춤", 1000);
                    break;
                case SURFACE_FIT_VERTICAL:
                    showInfo("세로맞춤", 1000);
                    break;
                case SURFACE_FILL:
                    showInfo("화면채움", 1000);
                    break;
                case SURFACE_16_9:
                    showInfo("16:9", 1000);
                    break;
                case SURFACE_4_3:
                    showInfo("4:3", 1000);
                    break;
                case SURFACE_ORIGINAL:
                    showInfo("원본크기", 1000);
                    break;
            }
            showOverlay();
        }
    };

    private final OnClickListener mRemainingTimeListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mDisplayRemainingTime = !mDisplayRemainingTime;
            showOverlay();
        }
    };


    /**
     * 플레이어 비디오 화면 surface 이벤트에 따른 연결.
     */
    private final Callback mSurfaceCallback = new Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            VideoLib.attachSurface(holder.getSurface(), YoondiskPlayerActivity.this, width, height);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            VideoLib.detachSurface();
        }
    };
    public void setKillPackage(){

        ActivityManager manager =  (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> activityes = ((ActivityManager)manager).getRunningAppProcesses();

        for (int iCnt = 0; iCnt < activityes.size(); iCnt++){

            System.out.println("APP: "+iCnt +" "+ activityes.get(iCnt).processName);

            if (activityes.get(iCnt).processName.equals("com.rsupport.mvagent")){
                android.os.Process.sendSignal(activityes.get(iCnt).pid, android.os.Process.SIGNAL_KILL);
                android.os.Process.killProcess(activityes.get(iCnt).pid);
                //manager.killBackgroundProcesses("com.android.email");

                //manager.restartPackage("com.android.email");

                System.out.println("Inside if");
            }

        }

    }

    /**
     * 플레이어 콘트롤 영역 오버레이 OVERLAY_TIMEOUT으로 표시.
     */
    private void showOverlay() {
        showOverlay(OVERLAY_TIMEOUT);
    }
    private void lockOverlay() {
        showOverlay(1);
    }
    /**
     * 플레이어 콘트롤 영역 오버레이 표시.
     */
    private void showOverlay(int timeout) {
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        if (!mShowing) {
            mShowing = true;
            if (!mIsLocked) {
                mOverlayHeader.setVisibility(View.VISIBLE);
                mOverlayProgress.setVisibility(View.VISIBLE);
                dimStatusBar(false);
            }
            mOverlayProgress.setVisibility(View.VISIBLE);
        }
        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
        updateOverlayPausePlay();
    }


    /**
     * 플레이어 콘트롤 영역 오버레이 슴김.
     */
    private void hideOverlay(boolean fromUser) {
        if (mShowing) {

            mHandler.removeMessages(SHOW_PROGRESS);
            Log.i(TAG, "remove View!");
            if (!fromUser && !mIsLocked) {
                mOverlayHeader.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                mOverlayProgress.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

            }
            mOverlayHeader.setVisibility(View.INVISIBLE);
            mOverlayProgress.setVisibility(View.INVISIBLE);
            mShowing = false;
            dimStatusBar(true);
        }
    }


    /**
     * Android 네비게이션 바 및 아이콘 숨김.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void dimStatusBar(boolean dim) {
        if (!Util.isHoneycombOrLater() || !Util.hasNavBar())
            return;
        int layout = 0;
        if (!Util.hasCombBar() && Util.isJellyBeanOrLater())
            layout = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        mSurface.setSystemUiVisibility(
                (dim ? (Util.hasCombBar()
                        ? View.SYSTEM_UI_FLAG_LOW_PROFILE
                        : View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                        : View.SYSTEM_UI_FLAG_VISIBLE) | layout);
    }

    /**
     * 플레이어 재생에 따른 재생버튼 , 일시정지 버튼 변경.
     */
    private void updateOverlayPausePlay() {
        if (VideoLib == null) {
            return;
        }

        mPlayPause.setImageResource(VideoLib.isPlaying()
                ? R.drawable.yoondisk_player_pause : R.drawable.yoondisk_player_play);
    }

    /**
     * 플레이어 슬리이드 바 콘트롤  및  현재재생시간 및  전체 재생시간 표시.
     */
    private int setOverlayProgress() {
        if (VideoLib == null) {
            return 0;
        }

        int time = (int) VideoLib.getTime();
        int length = (int) VideoLib.getLength();
        if(firstStart && length>0)
        {
            // 초기재생시간 초 입력 (이어듣기 구현)
            int start_time=0;
            start_time=cur_posi;

            if (start_time>=0){
                start_time = start_time*1000;
                Log.d("passone", "VideoLib.get()"+length+"/"+start_time);
                if(start_time>60000) //60초 이후면 무조건 -5초 땡겨 시작.
                {
                    time=start_time-5000;
                    cur_posi=cur_posi-5;
                    cur_milpos=time;
                    start_time=time;
                }
//                if(length-start_time<=5000) {
//                time=0;
//                cur_posi=0;
//                cur_milpos=0;
//                start_time=0;
//            }

                VideoLib.setTime(start_time);
            }

            firstStart=false;
        }
        mSeekbar.setMax(length);
        mSeekbar.setProgress(time);
        mSeekbar.setFocusable(true);
        pause_posi=time;
        mSysTime.setText(DateFormat.getTimeFormat(this).format(new Date(System.currentTimeMillis())));
        if (time >= 0) mTime.setText(Util.millisToString(time));
        if (length >= 0) mLength.setText(mDisplayRemainingTime && length > 0
                ? "- " + Util.millisToString(length - time)
                : Util.millisToString(length));
        if(repeat_state.equals(RepeatState.ING))
        {
            if(time>=repeat_endTime)
                VideoLib.setTime(repeat_startTime);
        }

        return time;
    }



    /**
     * 플레이어 재생.
     */
    private void play() {
        VideoLib.play();
        net.passone.hrd.common.Util.debug("pause:"+last_posi);
        if(wl==null)
        {
            wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "MyActivity");
            wl.acquire(); // wake mode 시작

        }
        if(last_posi>0)
            VideoLib.setTime(last_posi);

        mSurface.setKeepScreenOn(true);
        lastPlayTime = System.currentTimeMillis();
        net.passone.hrd.common.Util.debug("test play method");
        if(send_timer == null) {
            send_timer = new Timer();
        }
        if(send_timer!=null )
        {
            send_timer.schedule(new TimerTask() {
                Handler sHandler = new Handler();
                final Runnable showToastMessage = new Runnable() {
                    public void run() {
                        Toast.makeText(context, "재생중", Toast.LENGTH_SHORT).show();
                    }
                };
                @Override
                public void run() {
                    String state="";
//                    sHandler.post(showToastMessage);
                    sendPlayStatus((int) VideoLib.getTime());
                }
            }, 0, STATUS_PERIOD);
        }
    }

    /**
     * 플레이어 재생 중 일시정지.
     */
    private void pause() {
        VideoLib.pause();
        try{
            if(wl!=null)
                wl.release(); // wake mode 끝
        }catch(Exception e){}
        sendPlayStatus(pause_posi);
        last_posi=pause_posi;
        mSurface.setKeepScreenOn(false);
        if (send_timer != null) {
            send_timer.cancel();
            send_timer=null;
        }
    }

    /**
     * 안드로이드 스마트폰 기기정보 문자열
     */
    private String DeviceInfo(){
        String agents="";
        try{
            TelephonyManager mng = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String num ="";
            String isp ="";
            try{
                num=mng.getLine1Number();
                isp=mng.getNetworkOperatorName();
            } catch (Exception e) {}
            agents="Android;"+isp+";"+num+";";
            agents=agents+Build.BOARD+","+Build.BRAND+"-"+Build.MODEL+"("+Build.VERSION.RELEASE+Build.VERSION.CODENAME+"),"+Build.CPU_ABI+","+Build.DEVICE+ ","+Build.DISPLAY;
            agents=agents+","+Build.HOST+","+Build.ID;
            agents=agents+Build.TAGS+","+Build.TYPE+","+Build.USER+")";
        } catch (Exception e) {}
        return agents;
    }


    /**
     * 화면  상좌 , 상우 , 하좌 , 하우 , 가운데 회원 아이디 표시 .
     */
    public void set_webid(){
        try {
            webidR.setText(web_id);
            webidR.setVisibility(View.VISIBLE);
            webidR.setText(web_id);
            double rand=Math.random();
            int num= 1 + (int)(rand*5);
            switch (num) {
                case 1:
                    webidR.setGravity(Gravity.TOP | Gravity.LEFT); // 1
                    break;
                case 2:
                    webidR.setGravity(Gravity.TOP | Gravity.RIGHT); // 2
                    break;
                case 3:
                    webidR.setGravity(Gravity.BOTTOM | Gravity.LEFT); // 3
                    break;
                case 4:
                    webidR.setGravity(Gravity.BOTTOM | Gravity.RIGHT); // 4
                    break;
                case 5:
                    webidR.setGravity(Gravity.CENTER); //5
                    break;
            }
            Handler handler=new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    webidR.setVisibility(View.GONE);
                }
            };

            handler.sendEmptyMessageDelayed(0, 3000); // 3초 후에 종료시킴
        } catch (Exception e) {

        }
    }


    /**
     * 화면에 5분마드 회원 아이디 표시 및  hdmi연결체크 타이머 .
     */
    TimerTask myTask = new TimerTask() {
        @Override
        public void run() {
            try {
                if (jurl!= null) {
                    id_time++;
                    if (id_time==300) {
                        id_time=0;
                        if (VideoLib.isPlaying()==true){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 화면에 5분마다 랜덤하게 아이디 표시.
                                    set_webid();
                                }
                            });
                        }
                    }
                }
            } catch (Exception  e) {


            }
            // hdmi 연결 감지
            if (dog.is_hdmi()!=0){
                err_msg="HDMI에 연결된 기기를 해제후 다시 사용하세요.";
                VideoLib.stop();
                YoondiskPlayerActivity.this.runOnUiThread(new Runnable() {
                    public void run()
                    {
                        String msg=err_msg;
                        AlertDialog.Builder ad=new AlertDialog.Builder(YoondiskPlayerActivity.this);
                        ad.setMessage(msg);
                        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }
                        });
                        ad.show();
                    }
                });

            }

        }
    };


    /**
     * 문자열 urlencode 특수 문자 대처용.
     */
    public static String urlencode(String original)
    {
        try
        {
            return URLEncoder.encode(original, "utf-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
        }
        catch(UnsupportedEncodingException e)
        {
        }
        return null;
    }

    /**
     * DRM 플레이어 초기화 및 URL,재생파일 시작.
     */
    private void start_player() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        mEndReached=false;
        mSurface.setKeepScreenOn(true);
        VideoLib.readMedia(mLocation);
//        VideoLib.readMedia(mLocation,0);
        if(CUser.bright>0)
        {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = CUser.bright;
            getWindow().setAttributes(lp);

        }
        VideoLib.play();

        // 초기재생시간 초 입력 (이어듣기 구현)
        int start_time=0;
        //진도시간이 오버 타임되면 0으로 가라


        start_time = cur_posi;

        Log.d("passone","start_time="+start_time+"&settime="+VideoLib.getTime());

        if (start_time>0){
            Log.d("passone","start_time="+start_time+"&cur_posi="+cur_posi);
            start_time = start_time*1000;
            VideoLib.setTime(start_time);
        }
        Log.d("passone","start_time="+start_time+"&settime="+VideoLib.getTime()+"&getLength="+(int)VideoLib.getLength());

        // 버퍼링 상태 초기화
        mCache.setText("0%");
        if (mloading.getVisibility()!=View.VISIBLE){
            mloading.setVisibility(View.VISIBLE);
            mCache.setVisibility(View.VISIBLE);

        }


    }


    /**
     * 현재 플레이어 화면 회전 상태
     */
    @SuppressWarnings("deprecation")
    private int getScreenRotation(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO /* Android 2.2 has getRotation */) {
            try {
                Method m = display.getClass().getDeclaredMethod("getRotation");
                return (Integer) m.invoke(display);
            } catch (Exception e) {
                return Surface.ROTATION_0;
            }
        } else {
            return display.getOrientation();
        }
    }

    /**
     * 안드로이드 진버브레드 이상 버젼 비디오 화면 크기 조절.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private int getScreenOrientation(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int rot = getScreenRotation();
        @SuppressWarnings("deprecation")
        boolean defaultWide = display.getWidth() > display.getHeight();
        if(rot == Surface.ROTATION_90 || rot == Surface.ROTATION_270)
            defaultWide = !defaultWide;
        if(defaultWide) {
            switch (rot) {
                case Surface.ROTATION_0:
                    return ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                // case Surface.ROTATION_90:
                //return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                case Surface.ROTATION_180:
                    // SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
                    // Level 9+
                    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                            : ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                //case Surface.ROTATION_270:
                // SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
                // Level 9+
                //   return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                //           : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                default:
                    return 0;
            }
        } else {
            switch (rot) {
                case Surface.ROTATION_0:
                    return ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                case Surface.ROTATION_180:
                    // SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
                    // Level 9+
                    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                            : ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                case Surface.ROTATION_270:
                    // SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
                    // Level 9+
                    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                            : ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                default:
                    return 0;
            }
        }
    }



    /**
     * 플레이어 화면, 배속 감소버튼 클릭 이벤트
     */
    private final OnClickListener onSpeedDn = new OnClickListener() {
        @Override
        public void onClick(View v) {
            speedx=speedx-(float)0.10;
            if (speedx<(float)0.9){speedx=(float)0.9;}
            speedx=(float) Math.round(speedx * 100) / 100 ;
            showInfo("배속 : "+Float.toString(speedx), 1000);
            VideoLib.setRate(speedx);

            showOverlay();
        }
    };



    public void sendPlayStatus(final int currentPosition) {
        net.passone.hrd.common.Util.debug("lec_seq"+lec_seq);
        if(isPrice)
        {


            final Bundle extras = getIntent().getExtras();
            String message="";
            net.passone.hrd.common.Util.debug("now:"+System.currentTimeMillis()+", last:"+lastPlayTime);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    String log_check = "Y";
                    // TODO Auto-generated method stub
                    int currentTime=currentPosition/1000;

                    int elapsedTime = (lastPlayTime == 0)? 0 : (int) ((System.currentTimeMillis() - lastPlayTime) / 1000);
                    net.passone.hrd.common.Util.debug(content_seq + " elapsedTime:" + elapsedTime + " " + log_seq + "/" + currentTime);
                    Log.d("playtime", "playtime=" + elapsedTime + "&System.currentTimeMillis()=" + System.currentTimeMillis() + "&lastPlayTime=" + lastPlayTime+"&is_finish="+is_finish);
                    //_api.LogSave(String.valueOf(lec_seq), String.valueOf(content_seq), String.valueOf(lec_basket_seq), vod_widType, String.valueOf(total_possible_time), String.valueOf(log_seq), String.valueOf(elapsedTime), String.valueOf(currentPosition), log_check,context,callback);
                    _api.play_state(classkey,classcount,studykey,part,page,is_finish,currentTime,elapsedTime,context,callback);
                }
            });
        }
        else //무료특강
        {

        }
    }


    @Override
    public void onBackPressed() {
        if(VideoLib!=null)
        {
            isFinish=true;

//            if(VideoLib.isPlaying())
//                pause();
            finish();
        }
    }
    private  class DrmTask extends AsyncTask<Void, Void, String> {

        protected void onPostExecute(String result) {
            // DRM 메세지가 있을경우 플레이어를 종료하고 메세지를 뿌린다.
            // 메세지 종류
            // ======================================================================
            // 안드로이드 스마트폰 또는 테블릿에서 사용하세요.(루팅폰 지원안함)
            // 녹음,녹화 가능한 앱이 실행중입니다.해당앱을 종료후 다시 사용하세요.
            // miracast(미러스크린)이 실행중입니다.해당앱을 종료후 다시 사용하세요.
            // HDMI에 연결된 기기를 해제후 다시 사용하세요.
            // ======================================================================
            if (result.length() > 0) {
                Toast.makeText(YoondiskPlayerActivity.this, result, Toast.LENGTH_LONG).show();
                try {
                    VideoLib.stop();
                    finish();
                } catch (Exception e) {

                }
            }
            Log.e("Yoon Lib Ver : ", "===========0=================" + result);
        }

        @Override
        protected String doInBackground(Void... params) {
            Adrm.isPlay(true);

            String drm_msg = Adrm.drm_cking(YoondiskPlayerActivity.this);
            Log.e("Yoon Lib Ver : ", "===========1=================" + drm_msg);
            return drm_msg;

        }
    }
}
