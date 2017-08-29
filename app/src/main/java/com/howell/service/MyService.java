package com.howell.service;


import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import android.support.annotation.Nullable;
import android.util.Log;


import com.howell.action.LoginAction;
import com.howell.pushlibrary.AbsWorkService;
import com.howell.pushlibrary.DaemonEnv;
import com.howell.utils.ServerConfigSp;

import com.howell.utils.ThreadUtil;
import com.howellnet.bean.websocket.WSRes;
import com.howellnet.protocol.autobahn.WebSocketException;
import com.howellnet.protocol.websocket.WebSocketManager;

import org.json.JSONException;

import java.util.concurrent.TimeUnit;


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
//        myFun();
        link();
    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        isWorking = false;
        Log.e("547",TAG+":stop work");
        //TODO do work
        unLink();

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
//        isWorking = false;

        Log.e("547",TAG+":onServiceKilled  reborn in "+DaemonEnv.DEFAULT_WAKE_UP_INTERVAL+" ms");
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
//        isWorking = false;
        Log.e("547","my service on destroy reborn in "+DaemonEnv.DEFAULT_WAKE_UP_INTERVAL+" ms");
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
                Log.i("123","shouldWakeUp="+DaemonEnv.mShouldWakeUp);
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
        stopHeart();
        mgr.deInit();
    }

    private void startHeart(long delaySec){
        if (isAlive)return;
        ThreadUtil.scheduledSingleThreadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    mgr.alarmAlive(getCseq(),0,0,0,false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },delaySec,delaySec, TimeUnit.SECONDS);
        isAlive = true;
    }

    private void stopHeart(){
        ThreadUtil.scheduledSingleThreadShutDown();
        isAlive = false;
    }


    @Override
    public void onWebSocketOpen() {
        mWsIsOpen = true;
        try {
            mgr.alarmLink(getCseq(), LoginAction.getInstance().getmInfo().getLr().getLoginSession()
                    ,LoginAction.getInstance().getmInfo().getImei());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebSocketClose() {

    }



    @Override
    public void onGetMessage(WSRes res) {
        switch (res.getType()){
            case ALARM_LINK:
                //发送第一个心跳
                try {
                    mgr.alarmAlive(getCseq(),0,0,0,false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;
            case ALARM_ALIVE:
                //打开心跳
                Log.i("123","get alive="+res.toString());
                WSRes.AlarmAliveRes aRes = (WSRes.AlarmAliveRes) res.getResultObject();
                startHeart(aRes.getHeartbeatinterval());
                break;
            case ALARM_EVENT:
                //推送过来
                Log.i("123","event come="+res.toString());
                break;
            case ALARM_NOTICE:
                break;
            default:
                break;


        }

    }

    @Override
    public void onError(int error) {
        Log.e("123","on error  ="+error);
    }
}
