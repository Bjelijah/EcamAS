package com.howell.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.android.howell.webcam.R;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {
	public static String getCharacterAndNumber() {
		String rel="";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());
		rel = formatter.format(curDate);
		return rel;
	}

	public static String getFileName() {
		// mu
		//String fileNameRandom = getCharacterAndNumber(8);
		String fileNameRandom = getCharacterAndNumber();
		return fileNameRandom;
	}
	
	public static void deleteImage(File file){
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			}
		} 
	}

	private static void installApk(Context context, File file){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}


	private static File getFileFromServer(String httpUrl,ProgressDialog pd) throws IOException {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))return null;
		URL url = new URL(httpUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		pd.setMax(conn.getContentLength());
		InputStream is = conn.getInputStream();
		File file = new File(Environment.getExternalStorageDirectory(),"ecamera.apk");
		FileOutputStream fos = new FileOutputStream(file);
		BufferedInputStream bis = new BufferedInputStream(is);
		byte [] buffer = new byte[1024];
		int len=0,total=0;
		while ((len=bis.read(buffer))!=-1){
			fos.write(buffer,0,len);
			total+=len;
			pd.setProgress(total);
		}
		fos.close();
		bis.close();
		is.close();
		return file;
	}


	public static void downLoadApk(final Context context, final String url){
		final ProgressDialog pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage(context.getString(R.string.download_dialog_message));
		pd.show();


		new AsyncTask<Void,Void,Boolean>(){
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					File f = getFileFromServer(url,pd);
					FileUtils.installApk(context,f);
					pd.dismiss();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}

			@Override
			protected void onPostExecute(Boolean aBoolean) {
				super.onPostExecute(aBoolean);
				if (!aBoolean){
					Toast.makeText(context,context.getString(R.string.download_dialog_fail),Toast.LENGTH_LONG).show();
				}
			}
		}.execute();




	}

	
}
