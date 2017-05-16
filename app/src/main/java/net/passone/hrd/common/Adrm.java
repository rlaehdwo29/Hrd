package net.passone.hrd.common;

//
//Source code recreated from a .class file by IntelliJ IDEA
//(powered by Fernflower decompiler)
//


import android.app.Activity;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.media.AudioRecord;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Display;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class Adrm extends Activity {
 private Handler mHandler;
 private static AudioRecord audio = null;
 static boolean isPlay = true;
 static int baseSampleRate = '걄';
 static int channel = 16;
 static int format = 2;
 private static final int WFD_DISABLED = -1;
 private static final int WFD_SOURCE = 0;
 private static final int PRIMARY_SINK = 1;
 private static final int SECONDARY_SINK = 2;
 private static final int SOURCE_OR_PRIMARY_SINK = 3;
 private static final int DEVICE_TYPE = 3;

 public Adrm() {
 }

 public static void MatrixTime(int delayTime) {
     long saveTime = System.currentTimeMillis();

     for(long currTime = 0L; currTime - saveTime < (long)delayTime; currTime = System.currentTimeMillis()) {
         ;
     }

 }

 public static void drm_end() {
 }

 private static int getWFDDeviceInfoFromString(String devStr) {
     if(devStr == null) {
         return -1;
     } else {
         boolean wfd = false;
         String[] var5;
         int var4 = (var5 = devStr.split("\n")).length;

         String line;
         int var3;
         String[] tokens;
         int toks;
         int i;
         String tok;
         for(var3 = 0; var3 < var4; ++var3) {
             line = var5[var3];
             if(line.matches(".*WFD enabled:.*")) {
                 tokens = line.split(":");
                 toks = tokens.length;

                 for(i = 0; i < toks - 1; ++i) {
                     if(tokens[i].contains("WFD enabled")) {
                         tok = tokens[i + 1].replaceAll("\\s", "");
                         if(tok.startsWith("true")) {
                             wfd = true;
                             break;
                         }
                     }
                 }
             }
         }

         if(!wfd) {
             return -1;
         } else {
             var4 = (var5 = devStr.split("\n")).length;

             for(var3 = 0; var3 < var4; ++var3) {
                 line = var5[var3];
                 if(line.matches(".*WFD DeviceInfo:.*")) {
                     tokens = line.split(":");
                     toks = tokens.length;

                     for(i = 0; i < toks - 1; ++i) {
                         if(tokens[i].contains("WFD DeviceInfo")) {
                             tok = tokens[i + 1].replaceAll("\\s", "");
                             int deviceInfo = Integer.parseInt(tok);
                             Log.d("DRM", "line[" + line + "] DeviceInfo[" + deviceInfo + "] masked[" + (deviceInfo & 3) + "]");
                             return deviceInfo;
                         }
                     }
                 }
             }

             return -1;
         }
     }
 }

 private static boolean isWifiDisplaySource(WifiP2pDevice dev) {
     if(dev == null) {
         return false;
     } else {
         int deviceInfo = getWFDDeviceInfoFromString(dev.toString());
         if(deviceInfo == -1) {
             return false;
         } else {
             int deviceType = deviceInfo & 3;
             return deviceType == 0 || deviceType == 3;
         }
     }
 }

 private static boolean isWifiDisplaySink(WifiP2pDevice dev) {
     if(dev == null) {
         return false;
     } else {
         int deviceInfo = getWFDDeviceInfoFromString(dev.toString());
         if(deviceInfo == -1) {
             return false;
         } else {
             int deviceType = deviceInfo & 3;
             return deviceType == 1 || deviceType == 3;
         }
     }
 }
public static void isPlay(boolean isplay)
{
	isPlay=isplay;
}
 public static String drm_cking(Context context) {
     if(audio != null) {
         audio.stop();
         audio.release();
         audio = null;
     }

     int buffSize = AudioRecord.getMinBufferSize(baseSampleRate, channel, format);
     audio = new AudioRecord(1, baseSampleRate, channel, format, buffSize);
     audio.startRecording();
     short[] buffer = new short[buffSize];
     int audioStatus = audio.read(buffer, 0, buffSize);
     String drm_msg = "";
     boolean drm_error = false;
     Log.e("Yoon", "Drm Task Start .....");

     while(isPlay) {
         MatrixTime(1000);
         if(!drm_error) {
             boolean displayManager = false;

             try {
                 Runtime.getRuntime().exec("su");
                 displayManager = true;
             } catch (Exception var14) {
                 ;
             }

             if(Build.BRAND.equalsIgnoreCase("generic")) {
                 displayManager = true;
             }

             if(displayManager && !drm_error) {
                 drm_error = true;
                 drm_msg = "안드로이드 스마트폰 또는 테블릿에서 사용하세요.(루팅폰 지원안함)";
             }
         }

         int i;
         if(!drm_error) {
             String var16 = "/sys/class/switch/hdmi/state";
             char[] peers = new char[1024];
             byte displays = 0;
             i = displays;

             try {
                 FileReader file = new FileReader(var16);
                 int len = file.read(peers, 0, 1024);
                 i = Integer.valueOf((new String(peers, 0, len)).trim()).intValue();
             } catch (FileNotFoundException var12) {
                 ;
             } catch (Exception var13) {
                 ;
             }

             if(i != 0) {
                 drm_error = true;
                 drm_msg = "HDMI에 연결된 기기를 해제후 다시 사용하세요.";
             }
         }

         if(!drm_error && audioStatus == -3) {
             drm_error = true;
             drm_msg = "녹음,녹화 가능한 앱이 실행중입니다.해당앱을 종료후 다시 사용하세요.";
         }

         if(!drm_error) {
             Log.e("Yoon", "WifiP2pDevice Drm Task ing .....");
             ArrayList var18 = new ArrayList();
             int var15 = var18.size();
             String[] var20 = new String[var15];

             for(i = 0; i < var15; ++i) {
                 if(isWifiDisplaySink((WifiP2pDevice)var18.get(i))) {
                     drm_error = true;
                     drm_msg = "miracast(미러스크린)이 실행중입니다.해당앱을 종료후 다시 사용하세요.";
                 }

                 if(isWifiDisplaySource((WifiP2pDevice)var18.get(i))) {
                     drm_error = true;
                     drm_msg = "miracast(미러스크린)이 실행중입니다.해당앱을 종료후 다시 사용하세요.";
                 }

                 Log.e("Yoon", "Drm deviceName  ....." + ((WifiP2pDevice)var18.get(i)).deviceName);
                 var20[i] = ((WifiP2pDevice)var18.get(i)).deviceName;
             }
         }

         if(!drm_error) {
             Log.e("Yoon", "WifiP2pDevice Drm Task ing .....");
             DisplayManager var17 = (DisplayManager)context.getSystemService("display");
             Display[] var19 = var17.getDisplays();
             if(var19.length > 1) {
            	 String displayName=var19[1].getName();
            	 if(!displayName.contains("digital pen off-screen"))
            	 {
            		 drm_error = true;
                     drm_msg = "가상스크린 " + var19[1].getName() + "가 실행중입니다.해당앱을 종료후 다시 사용하세요.";
            	 }
                 
             }
         }

         Log.e("Yoon", "Drm Task ing .....");
         if(drm_error) {
             break;
         }
     }

     Log.e("Yoon", "Drm Task End .....");
     return drm_msg;
 }
}
