package com.howell.utils;

import android.os.Bundle;
import android.util.Log;


import com.howell.modules.player.bean.VODRecord;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class JsonUtil {






	public static ArrayList<VODRecord> parseRecordFileList(JSONObject obj) throws JSONException{
		ArrayList<VODRecord> list = new ArrayList<VODRecord>();
		int code = obj.getInt("code");
		String deviceId = obj.getString("device_id");
		int channel = obj.getInt("channel");
		int recordedfileCount = obj.getInt("recordedfile_count");
		JSONArray array = obj.getJSONArray("recordedfile");

		for (int i = 0; i < array.length(); i++) {
			JSONObject bar =  (JSONObject) array.get(i);
			String startTime = bar.getString("begin");
			String endTime = bar.getString("end");
			Log.i("123",i+ ": "+"startTime: "+startTime+" endTime:"+endTime);
			VODRecord vod = new VODRecord();
			vod.setTimeZoneStartTime(startTime);
			vod.setTimeZoneEndTime(endTime);
			list.add(vod);
		}
		
		return list;
	}
	
	public static Bundle getDeviceIdANDKey(String jsonStr) throws JSONException {
		Bundle bundle = new Bundle();
		JSONObject obj = new JSONObject(jsonStr);
		String id=null,key=null,serial=null;
		try{id = obj.getString("id");}catch (JSONException e){}
		try{key = obj.getString("key");}catch (JSONException e){}
		try{serial = obj.getString("serial");}catch (JSONException e){}
		bundle.putString("id",id);
		bundle.putString("key",key);
		bundle.putString("serial",serial);
		return bundle;
	}

}
