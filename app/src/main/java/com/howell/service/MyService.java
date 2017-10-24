package com.howell.service;


import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.howell.action.ConfigAction;
import com.howell.action.LoginAction;
import com.howell.modules.player.IPlayContract;
import com.howell.modules.push.IPushContract;
import com.howell.modules.push.presenter.PushPresenter;
import com.howell.pushlibrary.AbsWorkService;
import com.howell.pushlibrary.DaemonEnv;
import com.howell.utils.PhoneConfig;
import com.howell.utils.ServerConfigSp;
import com.howell.utils.ThreadUtil;


import org.json.JSONException;

import java.util.concurrent.TimeUnit;


/**
 * Created by Administrator on 2017/6/8.
 */

public class MyService extends AbsWorkService implements IPushContract.IVew{


    IPushContract.IPresenter mPresenter;



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
        isWorking = false;
        Log.e("547",TAG+":onServiceKilled  reborn in "+DaemonEnv.DEFAULT_WAKE_UP_INTERVAL+" ms");
        unLink();
        unbindPresenter();

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
        Log.e("547","ondestroy  my service on destroy reborn in "+DaemonEnv.DEFAULT_WAKE_UP_INTERVAL+" ms");
        unLink();
        unbindPresenter();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("547", TAG + ":on start command");
        DaemonEnv.mShouldWakeUp = true;
        sShouldStopService = false;
        bindPresenter();
        return super.onStartCommand(intent, flags, startId);
    }

    private void link(){
        String url =  "ws://" + ConfigAction.getInstance(this).getIp() + ":8803/howell/ver10/ADC";
        Log.i("123","server link url="+url);
        mPresenter.init(this,url, PhoneConfig.getIMEI(this)).connect();
    }

    private void unLink(){
        Log.i("547","unLink");
        if (mPresenter!=null) {
            mPresenter.disconnect();
        }
    }


    @Override
    public void bindPresenter() {
        if (mPresenter==null){
            mPresenter = new PushPresenter();
        }
        mPresenter.bindView(this);
    }

    @Override
    public void unbindPresenter() {
        Log.i("547","unbind presenter");
        if (mPresenter!=null){
            mPresenter.unbindView();
            mPresenter = null;
        }
    }

    @Override
    public void onWebSocketOpen() {
        isWorking = true;
    }

    @Override
    public void onWebSocketClose() {
        isWorking = false;
    }
}
