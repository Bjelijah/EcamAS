package com.howell.modules.push.presenter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.android.howell.webcam.R;
import com.howell.activity.LogoActivity;
import com.howell.modules.BasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.push.IPushContract;
import com.howell.utils.NetWorkUtils;
import com.howell.utils.PhoneConfig;
import com.howellsdk.api.ApiManager;
import com.howellsdk.api.HWWebSocketApi;
import com.howellsdk.net.websocket.bean.WSRes;
import com.howellsdk.utils.ThreadUtil;

import org.json.JSONException;

import java.util.concurrent.TimeUnit;



/**
 * Created by Administrator on 2017/9/29.
 */

public class PushPresenter extends BasePresenter implements IPushContract.IPresenter {
    IPushContract.IVew mView;
    String mURL;
    String mIMEI;
    int mCseq = 0;
    boolean mIsOpen = false;
    private int heartNo = 0;
    private static boolean isAliveHeart = false;
    Context mContext;
    private NotificationManager mNotificationManager;
    private int notificationId=0;
    private boolean mIsServiceWork = false;
    @Override
    public void bindView(ImpBaseView view) {
        mView = (IPushContract.IVew) view;
    }

    @Override
    public void unbindView() {
        dispose();
        mView = null;
        unLink();
    }

    @Override
    public IPushContract.IPresenter init(Context c, String url, String imei) {
        mContext = c;
        mURL = url;
        mIMEI = imei;
        mIsOpen = false;
        notificationId = 0;
        mIsServiceWork = false;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        ApiManager.getInstance().getWebSocketService(url, new HWWebSocketApi.IWebSocketCB() {
            @Override
            public void onWebSocketOpen() {
                mIsOpen = true;
                sendLink();
            }

            @Override
            public void onWebSocketClose() {
                mIsOpen = false;
                Log.i("547","on web socket close");
                stopHeart();
                if (!NetWorkUtils.isNetworkConnected(mContext)){Log.e("547","on socket close  network not link  we not link");return;}
                if (!mIsServiceWork){Log.e("547","on socket close  server stop  we not link");return;}
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                link();
            }

            @Override
            public void onGetMessage(WSRes res) {
                switch (res.getType()){
                    case ALARM_LINK:
                        WSRes.AlarmLinkRes alarmLinkRes = (WSRes.AlarmLinkRes) res.getResultObject();
                        if (alarmLinkRes.getResult()==0){
                            sendHeart();
                        }else{
                            unLink();
                        }
                        break;
                    case ALARM_ALIVE:
                        heartNo = 0;
                        WSRes.AlarmAliveRes aRes = (WSRes.AlarmAliveRes) res.getResultObject();
                        startHeart(aRes.getHeartbeatinterval());
                        break;
                    case ALARM_EVENT:
                        break;
                    case ALARM_NOTICE:
                        break;
                    case PUSH_MESSAGE:
                        WSRes.PushMessage ps = (WSRes.PushMessage) res.getResultObject();
                        sendPushAfk(ps.getCseq());
                        String content = new String(Base64.decode(ps.getContent(),0));
                        Log.i("547","content = "+content);
                        //show
                        //直接notficiation
                        showNotification(content);
                        break;
                    default:
                        break;
                }


            }

            @Override
            public void onError(int error) {
                Log.e("547","on error ="+error);
            }
        });
        return this;
    }

    @Override
    public void connect() {
        mIsServiceWork = true;
        ThreadUtil.cachedThreadStart(new Runnable() {
            @Override
            public void run() {
                ApiManager.getInstance().getWebSocketService()
                        .connect();
            }
        });

    }



    @Override
    public void disconnect() {
        mIsServiceWork = false;
        ThreadUtil.cachedThreadStart(new Runnable() {
            @Override
            public void run() {
                ApiManager.getInstance().getWebSocketService()
                        .disconnect();
            }
        });

    }

    private void link(){
        connect();
    }


    private void unLink(){
        ApiManager.getInstance().getWebSocketService()
                .disconnect();
        //stop heart;
        stopHeart();
    }


    private int getCseq(){
        return mCseq++;
    }

    private void sendLink(){
        ThreadUtil.cachedThreadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("547","send link");
                    ApiManager.getInstance().getWebSocketService()
                            .alarmLink(getCseq(),null,mIMEI);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void sendHeart(){
        ThreadUtil.cachedThreadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("547","send heart");
                    ApiManager.getInstance().getWebSocketService()
                            .alarmAlive(getCseq(),0,null,null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendPushAfk(final int cseq){
        ThreadUtil.cachedThreadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("547","send push afk");
                    ApiManager.getInstance().getWebSocketService()
                            .ADCEventRes(cseq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startHeart(long delaySec){
        Log.i("547","isAliveHeart="+isAliveHeart);
        if (!isAliveHeart) {

            ThreadUtil.scheduledSingleThreadStart(new Runnable() {
                @Override
                public void run() {
                        Log.e("547","start alarmAlive  in  scheduledSingleThreadStart  runnable ");
                        sendHeart();
                        heartNo ++;
                        if (heartNo>=3) {
                            Log.e("547", "no heart we unlink websocket");
                            unLink();
                            heartNo = 0;
                        }
                }
            },delaySec,delaySec, TimeUnit.SECONDS);
            isAliveHeart = true;
        }
    }

    private void stopHeart(){
        Log.e("547","stop heart");
        ThreadUtil.scheduledSingleThreadShutDown();
        isAliveHeart = false;
    }

    private int getNotificationId(){
        notificationId++;
        if(notificationId>10){
            notificationId = 0;
        }
        return notificationId;
    }

    private void showNotification(String content){
        Notification.Builder nb = new Notification.Builder(mContext);
        nb.setTicker("报警");
        try {
            String str[] = content.split(",");
            String title = str[0];
            String text = str[1];
            nb.setContentTitle(title);
            nb.setContentText(text);
        }catch (Exception e){
            nb.setContentTitle(content);
        }
        nb.setSmallIcon(R.mipmap.logo);
        nb.setWhen(System.currentTimeMillis());
        nb.setAutoCancel(true);
        nb.setDefaults(Notification.DEFAULT_SOUND);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,0,new Intent(mContext,LogoActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        nb.setContentIntent(pendingIntent);
        mNotificationManager.notify(getNotificationId(),nb.build());
    }



}
