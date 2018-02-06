package com.howell.utils;

import android.os.Bundle;
import android.util.Log;



import org.json.JSONException;
import org.json.JSONObject;





public class JsonUtil {



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
