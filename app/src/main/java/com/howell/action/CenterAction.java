package com.howell.action;

/**
 * Created by Administrator on 2017/2/6.
 */

public class CenterAction {
    private static CenterAction mInstance = null;
    public static CenterAction getInstance(){
        if (mInstance==null){
            mInstance = new CenterAction();
        }
        return mInstance;
    }

    private CenterAction(){}

    String ip;
    int port;
    boolean mIsUpdata = false;
    public String getIp() {
        return ip;
    }

    public boolean ismIsUpdata() {
        return mIsUpdata;
    }

    public void setmIsUpdata(boolean mIsUpdata) {
        this.mIsUpdata = mIsUpdata;
    }

    public CenterAction setIp(String ip) {
        this.ip = ip;
        mIsUpdata = true;
        return this;
    }

    public int getPort() {
        return port;
    }

    public CenterAction setPort(int port) {
        this.port = port;
        mIsUpdata = true;
        return this;
    }
}
