package com.howell.bean;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.howell.action.AudioAction;
import com.howell.action.LoginAction;
import com.howell.activity.PlayerActivity;
import com.howell.jni.JniUtil;
import com.howell.utils.JsonUtil;
import com.howell.utils.PhoneConfig;
import com.howell.utils.SDCardUtils;
import com.howell.utils.ServerConfigSp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/12/16.
 */

public class H265Mgr implements ICam {

    Context mContext;
    CameraItemBean mBean;
    Handler mHandler;

    private String mTurnServiceIP = null;
    private int mTurnServicePort = -1;
    private String mSessionID = null;
    private int mUnexpectNoFrame = 0;
    boolean mIsTransDeinit = false;
    private long dialogId = 0;
    private int m_bSub = 1;

    private Timer timer = null;
    private MyTimerTask myTimerTask = null;

    private static final int F_TIME = 1;


    @Override
    public void init(Context context, CameraItemBean bean) {
        this.mBean = bean;
        this.mContext = context;
    }

    @Override
    public void deInit() {
        mHandler = null;
//        mHandler.removeMessages(PlayerActivity.MSG_DISCONNECT_UNEXPECT);
    }

    @Override
    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void setStreamBSub(int isSub) {
        this.m_bSub = isSub;
    }

    @Override
    public boolean bind() {
        return false;
    }

    @Override
    public boolean unBind() {
        return false;
    }

    @Override
    public void loginCam() {
        mTurnServiceIP = ServerConfigSp.loadServerIP(mContext);
        mTurnServicePort = ServerConfigSp.loadServerPort(mContext);
        if (mTurnServiceIP==null||mTurnServicePort==0) return;

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                Log.i("123", "doinback");
                JniUtil.netInit();
                transInit(mTurnServiceIP,mTurnServicePort);
                mIsTransDeinit = false;
                JniUtil.transSetCallBackObj(H265Mgr.this, 0);
                JniUtil.transSetCallbackMethodName("onConnect", 0);
                JniUtil.transSetCallbackMethodName("onDisConnect", 1);
                JniUtil.transSetCallbackMethodName("onRecordFileList", 2);
                JniUtil.transSetCallbackMethodName("onDisconnectUnexpect", 3);
                InputStream ca = getClass().getResourceAsStream("/assets/ca.crt");
                InputStream client = getClass().getResourceAsStream("/assets/client.crt");
                InputStream key = getClass().getResourceAsStream("/assets/client.key");
                String castr = new String(SDCardUtils.saveCreateCertificate(ca, "ca.crt",mContext));
                String clstr = new String(SDCardUtils.saveCreateCertificate(client, "client.crt",mContext));
                String keystr = new String(SDCardUtils.saveCreateCertificate(key, "client.key",mContext));

                Log.i("123", "castr="+castr);
                JniUtil.transSetCrtPaht(castr, clstr, keystr);

                try {
                    ca.close();
                    client.close();
                    key.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int type = 101;
                String id = PhoneConfig.getPhoneUid(mContext);//FIXME  android id
                String imei = PhoneConfig.getPhoneDeveceID(mContext);  //imei

                transConnect(type, imei, LoginAction.getInstance().getmInfo().getAccount(), LoginAction.getInstance().getmInfo().getPassword());

                Log.i("PlayManager", "transConnect ok");

                AudioAction.getInstance().initAudio();
                AudioAction.getInstance().playAudio();

                return null;
            }

        }.execute();
        Log.i("123", "login task exe over");
    }

    @Override
    public void logoutCam() {
        JniUtil.transDisconnect();
        transDeInit();
        AudioAction.getInstance().stopAudio();
        AudioAction.getInstance().deInitAudio();
    }

    @Override
    public void playViewCam(int is_sub) {
        if(JniUtil.readyPlayLive(2,0)){
            m_bSub = is_sub;
            Log.i("123", "play view cam");
            Subscribe s = new Subscribe(mSessionID, (int)getDialogId(),mBean.getDeviceId(), "live",is_sub);
            s.setStartTime(null);
            s.setEndTime(null);
            String jsonStr = JsonUtil.subScribeJson(s);
            Log.i("123", "jsonStr="+jsonStr);
            transSubscribe(jsonStr, jsonStr.length());
            JniUtil.playView();
            startTimerTask();
        }else{
            Log.e("123", "ready play live error");
        }
    }

    @Override
    public void stopViewCam() {
        JniUtil.stopView();
        JniUtil.transUnsubscribe();
        stopTimerTask();
    }

    private void transInit(String ip,int port){
        JniUtil.transInit(ip, port);
    }

    private void transDeInit(){
        if (!mIsTransDeinit) {
            JniUtil.transDeinit();
            mIsTransDeinit = true;
        }
    }

    private void transConnect(int type,String id,String name,String pwd){
        JniUtil.transConnect(type, id, name, pwd);
    }

    private void transSubscribe(String jsonStr,int jsonLen){
        JniUtil.transSubscribe(jsonStr, jsonLen);
    }

    private long getDialogId(){
        this.dialogId++;
        return dialogId;
    }


    private void startTimerTask(){
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 0,F_TIME*1000);
    }

    private void stopTimerTask(){
        if (timer!=null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (myTimerTask!=null) {
            myTimerTask.cancel();
            myTimerTask = null;
        }
    }

    @Override
    public void reLink(){
        //stop
        Log.d("123", "relink........");
        mHandler.sendEmptyMessage(PlayerActivity.SHOWPROGRESSBAR);
        new Thread(){
            public void run() {
                stopViewCam();
//                logoutCam();
//                transDeInit();

                try {
                    Thread.sleep(1000);//
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                loginCam();
                playViewCam(m_bSub);
            };
        }.start();
    }

    @Override
    public void catchPic(String path) {
        JniUtil.catchPic(path);
    }

    private void onConnect(String sessionId){
        Log.i("123", "session id = "+sessionId);
        mSessionID = sessionId;
//        mHandler.sendEmptyMessage(MSG_LOGIN_CAM_OK);
    }

    private void onDisConnect(){
        Log.i("123", "onDisConnect ");
//        mHandler.sendEmptyMessage(MSG_DISCONNECT);
    }

    private void onDisconnectUnexpect(){
        Log.i("123", "on disConnectUnexpect  we need reLink");
        stopTimerTask();
//        PlayerActivity.ShowStreamSpeed(0);
//        mHandler.sendEmptyMessageDelayed(PlayerActivity.MSG_DISCONNECT_UNEXPECT, 5000);
    }





    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            int streamLen = JniUtil.transGetStreamLenSomeTime();
            Log.i("123","from my time task   "+ streamLen+"    speed="+streamLen*8/1024/F_TIME+"kbit");

//            PlayerActivity.ShowStreamSpeed(streamLen*8/1024/F_TIME);//FIXME
            if (streamLen == 0) {
                mUnexpectNoFrame++;
            }else{
                mHandler.sendEmptyMessage(PlayerActivity.HIDEPROGRESSBAR);
                mUnexpectNoFrame = 0;
            }

            if (mUnexpectNoFrame == 10) {// 10s / 1000ms
//                mHandler.sendEmptyMessage(PlayerActivity.MSG_DISCONNECT_UNEXPECT);//FIXME
            }
        }
    }





}
