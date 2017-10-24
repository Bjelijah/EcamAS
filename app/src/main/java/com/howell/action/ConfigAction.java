package com.howell.action;

import android.content.Context;
import android.util.Log;

import com.howell.rxbus.RxBus;
import com.howell.rxbus.RxConstants;
import com.howell.utils.IConst;
import com.howell.utils.PhoneConfig;
import com.howell.utils.ServerConfigSp;
import com.howell.utils.UserConfigSp;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/9/14.
 */

public class ConfigAction implements IConst{
    private static ConfigAction mInstance = null;
    Context mContext;
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

    private ConfigAction (final Context c){
        mContext = c;
        //注册rxbus
        RxBus.getDefault().toObservableWithCode(RxConstants.RX_CONFIG_CODE,String.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i("123","~~~~~~~~~~~~~~ConfigAction get config rxbus");
                        load(c);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
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

        boolean isCustom = UserConfigSp.loadUserIsCustom(c);
        //server
        mURL = isCustom?ServerConfigSp.loadServerURL(c):DEFAULT_URL;
        mIp = isCustom?ServerConfigSp.loadServerIP(c):DEFAULT_SERVER_IP;
        mIsSSL = isCustom?ServerConfigSp.loadServerSSL(c):false;
        mPort = isCustom?ServerConfigSp.loadServerPort(c):DEFAULT_SERVER_PORT_NOSSL;
        mMode = isCustom?ServerConfigSp.loadServerMode(c):0;
        mIsTurn = isCustom?ServerConfigSp.loadServerIsTurn(c):false;
        mIsCrypto = isCustom?ServerConfigSp.loadServerIsCrypto(c):false;
        //user
        mName = UserConfigSp.loadUserName(c);
        mPassword = UserConfigSp.loadUserPwd(c);
        mIsFirst = UserConfigSp.loadUserFirstLogin(c);
        mImei = PhoneConfig.getIMEI(c);
        Log.i("123","ConfigAction="+toString());

    }

    public void saveCustom(boolean isCustom){
        UserConfigSp.saveUserIsCustom(mContext,isCustom);
        load(mContext);
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

    public void setMode(int mode){
        mMode = mode;
    }


    @Override
    public String toString() {
        return "ConfigAction{" +
                "mURL='" + mURL + '\'' +
                ", mIp='" + mIp + '\'' +
                ", mPort=" + mPort +
                ", mMode=" + mMode +
                ", mIsSSL=" + mIsSSL +
                ", mName='" + mName + '\'' +
                ", mPassword='" + mPassword + '\'' +
                ", mIsFirst=" + mIsFirst +
                ", mImei='" + mImei + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mIsTurn=" + mIsTurn +
                ", mIsCrypto=" + mIsCrypto +
                '}';
    }
}
