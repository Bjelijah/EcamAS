package com.howell.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by howell on 2016/11/29.
 */

public class ServerConfigSp {
    private static final String SP_NAME = "server_set";
    public static void saveServerInfo(Context context, String ip, int port,boolean isSSL){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("server_ip",ip);
        editor.putInt("server_port",port);
        editor.putBoolean("server_ssl",isSSL);
        editor.commit();
    }

    public static String loadServerIP(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getString("server_ip",null);
    }

    public static int loadServerPort(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getInt("server_port",0);
    }

    public static boolean loadServerSSL(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getBoolean("server_ssl",false);
    }


    public static void saveCommunicationInfo(Context context,boolean isTurn,boolean isCrypto){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("server_turn",isTurn);
        editor.putBoolean("server_crypto",isCrypto);
        editor.commit();
        Log.e("123","saveCommunicationInfo  isTurn= "+isTurn+" iscrypto="+isCrypto);
    }
    public static void saveServerIsTurn(Context context,boolean isTurn){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("server_turn",isTurn);
        editor.commit();
    }

    public static void saveServerIsCrypto(Context context,boolean isCrypto){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("server_crypto",isCrypto);
        editor.commit();
    }

    public static boolean loadServerIsTurn(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getBoolean("server_turn",false);
    }
    public static boolean loadServerIsCrypto(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getBoolean("server_crypto",false);
    }

    public static void saveCenterInfo(Context context,String ip,int port){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("center_ip",ip);
        editor.putInt("center_port",port);
        editor.commit();
    }

    public static String loadCenterIP(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getString("center_ip",null);
    }

    public static int loadCenterPort(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getInt("center_port",0);
    }

}
