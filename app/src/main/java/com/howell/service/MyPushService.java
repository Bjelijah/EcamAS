package com.howell.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.howell.activity.LogoActivity;
import com.howell.ecam.R;
import com.howell.utils.PhoneConfig;

import atuobahn.WebSocketConnection;
import atuobahn.WebSocketConnectionHandler;
import atuobahn.WebSocketException;

/**
 * Created by Administrator on 2016/12/23.
 */

public class MyPushService extends Service {
    int mNoticationID = 1;
    int num = 0;
    NotificationManager notificationManager;
    private BroadcastReceiver receiver;
    public static final String NOTIFY_ACTION = "com.howell.service.notification";
    private WebSocketConnection mConnection = new WebSocketConnection();


    private String mWS,mAccount,mPhoneID;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("123","MyPushService onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("123","MyPushService onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        Log.e("123","MyPushService onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("123","MyPushService onStartCommand");
        startForeground(100, new Notification());
        initNotification();
        initConn();
        //TODO : wsuri, session that last login



        mWS = "";
        mAccount = getSharedPreferences("set",Context.MODE_PRIVATE).getString("account", "");
        mPhoneID = PhoneConfig.getPhoneDeveceID(this);


        this.start(mWS,mAccount,mPhoneID);


        sendNotification("title","msg");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("123","MyPushService onDestroy");
        //TODO send broadcast to weak up push_service
        Intent intent = new Intent();
        intent.setAction("com.howell.service.pushService");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);

        super.onDestroy();
    }



    private void initNotification(){

        if (notificationManager==null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        this.receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("123","on receive");
                //TODO open app
                Intent appIntent = new Intent(MyPushService.this, LogoActivity.class);
                context.startActivity(appIntent);
            }
        };

        IntentFilter filter = new IntentFilter(NOTIFY_ACTION);
        registerReceiver(this.receiver,filter);
    }

    private void initConn(){
        if (mConnection==null){
            mConnection = new WebSocketConnection();
        }
    }



    private void sendNotification(String title,String msg){
        Log.i("123","send notficiation");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ecam_logo)
                .setContentTitle(title)
                .setContentText(msg)
                .setNumber((int) (Math.random() * 1000))
                .setTicker("you have a message")
                .setDefaults(Notification.DEFAULT_SOUND
                        | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setAutoCancel(true)
                .setWhen(0);

        Intent intent = new Intent(NOTIFY_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyPushService.this,
                1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        Notification build = builder.build();
        notificationManager.notify(num, build);
        num++;
        mNoticationID++;

    }


    private void start(final String uri,final String account,final String phoneID){
        try {
            mConnection.connect(uri,new WebSocketConnectionHandler(){
                @Override
                public void onOpen() {
                    super.onOpen();
                    //login ok
                    //TODO client send phone info to server

                }

                @Override
                public void onTextMessage(String payload) {
                    super.onTextMessage(payload);


                }

                @Override
                public void onClose(int code, String reason) {
                    super.onClose(code, reason);
                    Log.e("123","webSocket close code="+code);

                    if (code == 2 || code == 3){
                        start(mWS,mAccount,mPhoneID);
                    }

                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }







}
