package com.howell.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by howell on 2016/11/29.
 */

public class UserConfigSp {

    private static final String SP_NAME = "user_set";
    public static void saveUserInfo(Context context,String name,String pwd){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user_name",name);
        editor.putString("user_pwd",pwd);
        editor.commit();


        SharedPreferences sp2 = context.getSharedPreferences("set",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sp2.edit();
        editor2.putString("account",name);
        editor2.putString("password",pwd);
        editor2.commit();

    }
    public static String loadUserName(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
       return sp.getString("user_name",null);
    }

    public static String loadUserPwd(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getString("user_pwd",null);
    }


}
