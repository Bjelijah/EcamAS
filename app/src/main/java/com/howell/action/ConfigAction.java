package com.howell.action;

import android.content.Context;
import android.util.Log;

import com.howell.utils.PhoneConfig;
import com.howell.utils.ServerConfigSp;
import com.howell.utils.UserConfigSp;

/**
 * Created by Administrator on 2017/9/14.
 */

public class ConfigAction {
    private static ConfigAction mInstance = null;
    String mURL;
    String mIp;//用于websocket
    int mPort;
    int mMode;
    boolean mIsSSL;
    String mName;
    String mPassword;
    boolean mIsFirst;
    String mImei;
    String mEmail;

    boolean mIsTurn;
    boolean mIsCrypto;


    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    private ConfigAction (Context c){
        //load
        load(c);
    }
    public static ConfigAction getInstance(Context context){
        if (mInstance==null){
            synchronized (ConfigAction.class){
                if (mInstance == null){
                    mInstance = new ConfigAction(context);
                }
            }
        }
        return mInstance;
    }

    private void load(Context c){
        //server
        mURL = ServerConfigSp.loadServerURL(c);
        mIp = ServerConfigSp.loadServerIP(c);
        mPort = ServerConfigSp.loadServerPort(c);
        mMode = ServerConfigSp.loadServerMode(c);
        mIsSSL = ServerConfigSp.loadServerSSL(c);
        //user
        mName = UserConfigSp.loadUserName(c);
        mPassword = UserConfigSp.loadUserPwd(c);
        mIsFirst = UserConfigSp.loadUserFirstLogin(c);
        mImei = PhoneConfig.getIMEI(c);
        Log.i("123","mName="+mName+" isfirst="+mIsFirst);
        mIsTurn = ServerConfigSp.loadServerIsTurn(c);
        mIsCrypto = ServerConfigSp.loadServerIsCrypto(c);




    }

    public boolean isTurn() {
        return mIsTurn;
    }

    public void setIsTurn(boolean mIsTurn) {
        this.mIsTurn = mIsTurn;
    }

    public boolean isCrypto() {
        return mIsCrypto;
    }

    public void setIsCrypto(boolean mIsCrypto) {
        this.mIsCrypto = mIsCrypto;
    }

    public void refresh(Context context){
        load(context);
    }

    public String getURL() {
        return mURL;
    }

    public String getIp() {
        return mIp;
    }

    public int getMode() {
        return mMode;
    }

    public boolean isSSL() {
        return mIsSSL;
    }

    public String getName() {
        return mName;
    }

    public String getPassword() {
        return mPassword;
    }

    public boolean isFirst() {
        return mIsFirst;
    }

    public int getPort() {
        return mPort;
    }

    public String getImei() {
        return mImei;
    }
}
