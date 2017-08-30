package com.howell.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.Handler;
import android.os.IBinder;

import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;


import com.android.howell.webcam.R;
import com.howell.action.LoginAction;
import com.howell.activity.LogoActivity;
import com.howell.bean.Custom;
import com.howell.pushlibrary.AbsWorkService;
import com.howell.pushlibrary.DaemonEnv;
import com.howell.utils.ServerConfigSp;

import com.howell.utils.ThreadUtil;
import com.howell.utils.UserConfigSp;
import com.howellnet.bean.http.Fault;
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

    private NotificationManager mNotificationManager;
    private Notification mNotification;

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
        initNotifcation();
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
        Log.i("547","link ip="+ip);
        try {
            mgr.registMessage(this).initURL(ip);
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }
    private void unLink(){
        Log.i("547","we unLink");
        mgr.deInit();
    }

    private void sendLink(){
        ThreadUtil.cachedThreadStart(new Runnable() {
            @Override
            public void run() {
                Log.i("547","sendlink");
                try {
                    String session = null;
                    if (LoginAction.getInstance().getmInfo().getLr()==null||
                            LoginAction.getInstance().getmInfo().getLr().getLoginSession()==null){
                        //重新登入
                    }else{
                        session = LoginAction.getInstance().getmInfo().getLr().getLoginSession();
                    }

                    mgr.alarmLink(getCseq(), session,
                            LoginAction.getInstance().getmInfo().getImei());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    private void sendHeart() {
        ThreadUtil.cachedThreadStart(new Runnable() {
            @Override
            public void run() {
                try {

                    mgr.alarmAlive(getCseq(),0,0,0,false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startHeart(long delaySec){
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
    }

    private void initNotifcation(){
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void showNotification(String name){
        Log.i("547","showNotification   name="+name);

        Notification.Builder nb = new Notification.Builder(this);
        nb.setTicker("报警");
        nb.setContentTitle(name + "入侵警报");
        nb.setSmallIcon(R.mipmap.logo);
        nb.setWhen(System.currentTimeMillis());
        nb.setAutoCancel(true);
        nb.setDefaults(Notification.DEFAULT_SOUND);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,LogoActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        nb.setContentIntent(pendingIntent);
        mNotificationManager.notify(0,nb.build());

    }



    @Override
    public void onWebSocketOpen() {
        Log.i("547","on websocket open");
        mWsIsOpen = true;
        sendLink();
    }

    @Override
    public void onWebSocketClose() {

    }



    @Override
    public void onGetMessage(WSRes res) {
        Log.i("547","on get message res="+res.toString());
        switch (res.getType()){
            case ALARM_LINK:
                sendHeart();
                break;
            case ALARM_ALIVE:
                WSRes.AlarmAliveRes aRes = (WSRes.AlarmAliveRes) res.getResultObject();
                startHeart(aRes.getHeartbeatinterval());
                break;
            case ALARM_EVENT:
//                WSRes.AlarmEvent event = (WSRes.AlarmEvent) res.getResultObject();
//                Log.i("547","ALARM_EVENT="+res.toString());
                break;
            case ALARM_NOTICE:
                break;
            case PUSH_MESSAGE:
                WSRes.PushMessage ps = (WSRes.PushMessage) res.getResultObject();
                String content = new String(Base64.decode(ps.getContent(),0));
                Log.i("547","content="+content);
                //直接notficiation
                showNotification(content);

                break;

            default:
                break;
        }
    }

    @Override
    public void onError(int error) {
        Log.e("547","on error="+error);
    }
}