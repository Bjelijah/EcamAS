package com.howell.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by howell on 2016/11/29.
 */

public class ServerConfigSp implements IConst{
    private static final String SP_NAME = "server_set";

    public static void saveServerURL(Context context,String ip,int port,int serverMode,boolean isSSL){
        String url = "";
        if (serverMode==0){//soap
            url=(isSSL?"https":"http")+"://"+ip+":"+port+"/HomeService/HomeMCUService.svc?wsdl";
        }else if(serverMode == 1){//http
            url=(isSSL?"https":"http")+"://"+ip+":"+port;
        }
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("server_ip",ip);
        editor.putInt("server_port",port);
        editor.putString("server_url",url);
        editor.putInt("server_mode",serverMode);
        editor.putBoolean("server_ssl",isSSL);
        editor.commit();

    }

    public static String loadServerURL(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        String url= sp.getString("server_url","http://www.haoweis.com:8800/HomeService/HomeMCUService.svc?wsdl");//default: haoweis
        return url;
    }

    public static int loadServerMode(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getInt("server_mode",0);//default: soap  0 soap  1 http
    }

    public static void saveServerMode(Context context,int mode){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("server_mode",mode);
        editor.commit();
    }


    @Deprecated
    public static void saveServerInfo(Context context, String ip, int port,boolean isSSL){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("server_ip",ip);
        editor.putInt("server_port",port);

        editor.putBoolean("server_ssl",isSSL);
        editor.commit();
    }

    public static void saveTurnServerInfo(Context context,String ip,int port){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("turn_ip",ip);
        editor.putInt("turn_port",port);
        editor.commit();
    }

    public static String loadTurnIP(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getString("turn_ip",DEFAULT_TURN_SERVER_IP);
    }

    public static int loadTurnPort(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getInt("turn_port",DEFAULT_TURN_SERVER_PORT_NOSSL);
    }

    public static String loadServerIP(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getString("server_ip",DEFAULT_IP);
    }

    public static int loadServerPort(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getInt("server_port",DEFAULT_PORT);
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
        return sp.getString("center_ip",DEFAULT_CENTER_IP);
    }

    public static int loadCenterPort(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getInt("center_port",DEFAULT_CENTER_PORT);
    }

    public static void savePushOnOff(Context context,boolean isOnOff){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("push_on_off",isOnOff);
        editor.commit();
    }
    public static boolean loadPushOnOff(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getBoolean("push_on_off",false);
    }


}
