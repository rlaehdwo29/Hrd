package net.passone.hrd.common;


import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

public class DownLoad extends Fragment {


	Communicator comm;
	int err=0;
	int fbreak_=0;


	public void setCommunicator(Communicator c){
		comm=c;
	}

	/* Http DownLoad AsyncTask */
	private class DownloadFilesTask extends AsyncTask<String, Float, Long> {
		String fsave="";
	    protected Long doInBackground(String... urls) {

	        long mal=0;
			String urlinfo= null;
			urlinfo = urls[0];
			Log.d("passone","xxx="+urlinfo);
	        String savepath=urls[1];
			String ext = Environment.getExternalStorageState();
//	        if (ext.equals(Environment.MEDIA_MOUNTED)) {
//	        	savepath = Environment.getExternalStorageDirectory().getAbsolutePath() + savepath;
//	        }else{
//	        	 comm.drm_download_cancel(1100);
//	        	 return mal;
//	        }

	        fbreak_=0;


	        try {

	            InputStream input = null;
	            HttpURLConnection connection = null;
	            RandomAccessFile outputx =null;
	            long fileSize=0;

	            int DEFAULT_TIMEOUT = 15000;
	            File dir=null;
	            long total = 0;
	            long  fileLength =0;
	            float old_per=0;


	            try {


	            	Uri fileUri = Uri.parse(urlinfo);
	            	String xv=urlinfo;
					Log.d("passone",xv);


	            	URL url = new URL(xv);
	                connection = (HttpURLConnection) url.openConnection();


	               // ����� �������� ���� ��� ����� ����

	                File file = new File(savepath);
	                 if (file.exists() == false) {
	                	file.createNewFile();
	                }

	                outputx = new RandomAccessFile(file.getAbsolutePath(), "rw");

	                fileSize = outputx.length();
	                outputx.seek(fileSize);
	                connection.setRequestMethod("GET");
	                connection.setRequestProperty("User-Agent", "Yoondisk_and");
	                connection.setRequestProperty("Range", "bytes=" + String.valueOf(fileSize) + '-');

	                connection.setDoOutput(true);
	                connection.setConnectTimeout(DEFAULT_TIMEOUT);
	                connection.setReadTimeout(DEFAULT_TIMEOUT);
	                connection.connect();
					Util.debug("test");

	                // expect HTTP 200 OK, so we don't mistakenly save error report
	                // instead of the file


	                // ��������!!
	                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK && connection.getResponseCode() != HttpURLConnection.HTTP_PARTIAL){
	                	err=connection.getResponseCode();

						comm.drm_download_cancel(err);
	                	return mal;
		            }

	                fileLength = connection.getContentLength()+fileSize;


	                File path = Environment.getDataDirectory();
	                StatFs stat = new StatFs(path.getPath());
	                long blockSize = stat.getBlockSize();
	                long availableBlocks = stat.getAvailableBlocks();
	                if (fileLength>availableBlocks * blockSize){
	                	comm.drm_download_cancel(1000);
	                	return mal;
	                }


	                // download the file
	                input = connection.getInputStream();

	                byte data[] = new byte[4096*100];
	                total =   fileSize;

	                int count1;
	                if (total>=fileLength){
	                	comm.drm_download_end(savepath);
	                	return mal;
	                }else{
	                	 Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	                	 while ((count1 = input.read(data)) != -1) {
		                    total += count1;
		                    outputx.write(data, 0, count1);
		                    float percent=(float)total/(float)fileLength;
		                    float put_per=percent*100;
		                    if ((int)old_per!=(int)put_per){

		                    	comm.drm_download_ing(total,fileLength);
			                }
		                    old_per=put_per;
		                    if (fbreak_==1){
		                    		break;
		                    }
		                 }
	                }
	            } catch (Exception e) {
	            	 Log.d("Yoon", "drm_download_err : "+ e.toString());
	            	 comm.drm_download_cancel(1100);
	            } finally {
	                try {
	                	 if (total>=fileLength && fileLength>0 ){
	                		 mal=1;
	                		 fsave=savepath;
	                	 }else{
		                	 if (fbreak_==1){
		                		 comm.drm_download_cancel(1200);
		                	 }else{
		                		 comm.drm_download_cancel(1100);
		                	 }
	                	 }


	                    if (outputx != null)
	                        outputx.close();


	                }
	                catch (IOException ignored) { }

	                 connection=null;
	                 input=null;
	            }
	        } finally {
	           // wl.release();
	        }

	        return mal;


	    }

	    // This is called when doInBackground() is finished
	    protected void onPostExecute(Long result) {
	      if (result==1){
	    	  comm.drm_download_end(fsave);
	      }
	    }


	}

	/*
	 ���� �ٿ�ε� ���� url , ���������� .
	 */
	public void drm_download_start(String urlinfo,String savepath){
		new DownloadFilesTask().execute(urlinfo,savepath);
  	}

	/*
	 ���� �ٿ�ε� �� �����.
	 */
	public void drm_download_cancel(){
		 fbreak_=1;
	}

	public interface Communicator{
			/*
			 ���� �ٿ�ε����϶� percent:�ٿ�ε������ �Բ�  ȣ����.
			*/
			public void drm_download_ing(float total, float fileLength);


			/*
			 ���� �ٿ�ε尡 ���н� �����ڵ�� �Բ� ȣ����.
			 */
			public void drm_download_cancel(int err);
			// err < 1000 ���� ������� ���� ���ڴ� http status code �� �ǹ���.
			// ���� : http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html

			// err : 1000 = ���� ���� ���� ��û�� ����ũ�� ���� �۽��ϴ�.
			// err : 1100 = ���� �ٿ�ε��� ���ͳ� ȯ���� ����Ǿ�ų� ���ͳ��� �Ҿ����Ͽ� �ٿ�ε尡 ��ҵǾ���ϴ�.
			// err : 1200 = �ٿ�ε� ��� ��û���� �ٿ�ε尡 ��ҵǾ���ϴ�.

			/*
			 ���� �ٿ�ε尡 �Ϸ�Ǹ�, downfile:������+�����̸����� ȣ����.
			 */
			public void drm_download_end(String downfile);


	}

}
