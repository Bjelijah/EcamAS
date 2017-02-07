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

    public String getIp() {
        return ip;
    }

    public CenterAction setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public int getPort() {
        return port;
    }

    public CenterAction setPort(int port) {
        this.port = port;
        return this;
    }
}
