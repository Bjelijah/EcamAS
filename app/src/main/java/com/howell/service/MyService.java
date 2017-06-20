package com.howell.service;


import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import android.support.annotation.Nullable;
import android.util.Log;


import com.howell.pushlibrary.AbsWorkService;
import com.howell.pushlibrary.DaemonEnv;
import com.howell.utils.ServerConfigSp;

import com.howellnet.bean.websocket.WSRes;
import com.howellnet.protocol.autobahn.WebSocketException;
import com.howellnet.protocol.websocket.WebSocketManager;

import org.json.JSONException;


/**
 * Created by Administrator on 2017/6/8.
 */

public class MyService extends AbsWorkService implements WebSocketManager.IMessage {
    WebSocketManager mgr = new WebSocketManager();
    boolean mWsIsOpen = false;
    int mCseq = 0;
    Handler mHandler = new Handler();

    Runnable heartRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mgr.alarmAlive(getCseq(),0,0,0,false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mHandler.postDelayed(this,60*1000);
        }
    };

    private static boolean isAlive = false;
    public static boolean sShouldStopService=false;
    public static boolean isWorking = false;
    public static String TAG = MyService.class.getName();
    public static void stopService(){
        Log.i("547","myservice stop service");
        DaemonEnv.mShouldWakeUp = false;
        sShouldStopService = true;
        cancelJobAlarmSub();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("547",TAG+":onBind1");
        return null;
    }

    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return sShouldStopService;
    }

    @Override
    public Boolean shouldStopService() {
        return sShouldStopService;
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {
        isWorking = true;
        Log.e("547",TAG+":start work");
        myFun();
    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        isWorking = false;
        Log.e("547",TAG+":stop work");
        //TODO do work
        link();

    }

    @Override
    public void stopWork() {
        isWorking = false;
        Log.e("547",TAG+":stop work");
        //TODO stop work
        unLink();
    }

    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        return isWorking;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent, Void alwaysNull) {
        Log.e("547",TAG+":onBind2");
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
        Log.e("547",TAG+":onServiceKilled");
    }

    @Override
    protected int onStart(Intent intent, int flags, int startId) {
        Log.i("123","MyService   onStart");
        return super.onStart(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e("547","on start");
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("547","my service on destroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("547", TAG + ":on start command");
        DaemonEnv.mShouldWakeUp = true;
        sShouldStopService = false;
        return super.onStartCommand(intent, flags, startId);
    }

    private long num = 0;
    private void myFun(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                while(DaemonEnv.mShouldWakeUp) {
                    try {
                        sleep(2000);
                        num++;
                        Log.e("547", "i am alive!!  num="+num);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    private int getCseq(){
        return mCseq++;
    }

    private void link(){
        String ip = ServerConfigSp.loadServerIP(this);
        try {
            mgr.registMessage(this).initURL(ip);
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }
    private void unLink(){
        mgr.deInit();
    }



    @Override
    public void onWebSocketOpen() {
        mWsIsOpen = true;
//        mgr.alarmLink(getCseq(),);
    }

    @Override
    public void onWebSocketClose() {

    }



    @Override
    public void onGetMessage(WSRes res) {

    }

    @Override
    public void onError(int error) {

    }
}
