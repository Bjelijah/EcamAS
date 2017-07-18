package com.howell.bean;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.howell.action.AudioAction;
import com.howell.action.LoginAction;
import com.howell.activity.BasePlayActivity;
import com.howell.entityclass.VODRecord;
import com.howell.jni.JniUtil;
import com.howell.utils.IConst;
import com.howell.utils.JsonUtil;
import com.howell.utils.PhoneConfig;
import com.howell.utils.SDCardUtils;
import com.howell.utils.ServerConfigSp;
import com.howell.utils.ThreadUtil;
import com.howell.utils.TurnJsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/12/16.
 */

public class H265Mgr implements ICam,IConst {

    Context mContext;
    CameraItemBean mBean;
    Handler mHandler;

    private String mTurnServiceIP = null;
    private int mTurnServicePort = -1;
    private boolean mIsTurnCrypto = true;
    private String mSessionID = null;
    private int mUnexpectNoFrame = 0;
    boolean mIsTransDeinit = false;
    private long dialogId = 0;
    private int mIsSub = 1;

    private Timer timer = null;
    private MyTimerTask myTimerTask = null;

    private static final int F_TIME = 1;
    ArrayList<VODRecord> mList = null;
    ICam.IStream mStreamCB = null;
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
    public void registStreamLenCallback(IStream cb) {
        mStreamCB = cb;
    }

    @Override
    public void unregistStreamLenCallback() {
        mStreamCB = null;
    }

    @Override
    public void setStreamBSub(boolean isSub) {
        mIsSub = isSub?1:0;
    }

    @Override
    public void setPlayBack(boolean isPlayback) {
        this.mIsSub = isPlayback?1:0;
    }

    @Override
    public void setPlayBackTime(String startTime, String endTime) {

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
    public boolean loginCam() {
        try {
            initServerInfo();
        }catch (NullPointerException e){
            return false;
        }

        if (mTurnServiceIP==null||mTurnServicePort==0) return false;


        Log.i("123", "doinback");
        JniUtil.netInit();
        transInit(mTurnServiceIP,mTurnServicePort);
        JniUtil.transSetUseSSL(mIsTurnCrypto);
        mIsTransDeinit = false;
        JniUtil.transSetCallBackObj(H265Mgr.this, 0);
        JniUtil.transSetCallbackMethodName("onConnect", 0);
        JniUtil.transSetCallbackMethodName("onDisConnect", 1);
        JniUtil.transSetCallbackMethodName("onRecordFileList", 2);
        JniUtil.transSetCallbackMethodName("onDisconnectUnexpect", 3);
        JniUtil.transSetCallbackMethodName("onSubscribe",4);
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

        if(!transConnect(type, imei, LoginAction.getInstance().getmInfo().getAccount(), LoginAction.getInstance().getmInfo().getPassword())){
            return false;
        }

        Log.i("123", "H265Mgr transConnect ok");

        AudioAction.getInstance().initAudio();
        AudioAction.getInstance().playAudio();


        return true;
    }

    @Override
    public boolean logoutCam() {
        JniUtil.transDisconnect();
        transDeInit();
        AudioAction.getInstance().stopAudio();
        AudioAction.getInstance().deInitAudio();
        return true;
    }

    @Override
    public boolean playViewCam() {
        Log.i("123", "h265  play view cam");
        if (mSessionID==null)return true;
        Log.e("123","mSessionID="+mSessionID);
        Subscribe s = new Subscribe(mSessionID, (int)getDialogId(),mBean.getDeviceId(), "live",mIsSub);
        s.setStartTime(null);
        s.setEndTime(null);
        String jsonStr = JsonUtil.subScribeJson(s);
        Log.i("123", "jsonStr="+jsonStr);
        transSubscribe(jsonStr, jsonStr.length());
        return true;
    }

    @Override
    public boolean stopViewCam() {
        JniUtil.stopView();
        JniUtil.transUnsubscribe();
        stopTimerTask();
        return true;
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

    private boolean transConnect(int type,String id,String name,String pwd){
        return JniUtil.transConnect(type, id, name, pwd);
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


    public void onRecordFileList(String jsonStr){
        try {
            mList = JsonUtil.parseRecordFileList(new JSONObject(jsonStr));
//            handler.sendEmptyMessage(MSG_RECORD_LIST_GET);//FIXME
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean reLink(){
        //stop
        Log.d("123", "relink........");
//        mHandler.sendEmptyMessage(PlayerActivity.SHOWPROGRESSBAR);//fixme
        ThreadUtil.cachedThreadStart(new Runnable() {
            @Override
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
                playViewCam();
            }
        });
        return true;
    }

    @Override
    public boolean playBackReplay(long begOffset,long curProgress) {
        return false;
    }

    @Override
    public boolean playBackPause(boolean bPause, long begOffset, long curProgress) {
        return false;
    }


    @Override
    public boolean catchPic(String path) {
        JniUtil.catchPic(path);
        return true;
    }

    @Override
    public boolean soundSetData(byte[] buf, int len) {
        return true;
    }

    @Override
    public boolean ptzSetInfo(String account, String loginSession, String devID, int channelNo) {
        return false;
    }

    @Override
    public boolean zoomTeleStart() {
        return false;
    }

    @Override
    public boolean zoomTeleStop() {
        return false;
    }

    @Override
    public boolean zoomWideStart() {
        return false;
    }

    @Override
    public boolean zoomWideStop() {
        return false;
    }

    @Override
    public boolean ptzMoveStart(String direction) {
        return false;
    }

    @Override
    public boolean ptzMoveStop() {
        return false;
    }


    @Override
    public boolean hasVideoList() {
        return false;
    }

    @Override
    public void setVideoListTime(String startTime, String endTime) {

    }

    @Override
    public int getVideoListPageCount(int nowPage, int pageSize) {
        return 0;
    }

    @Override
    public ArrayList<VODRecord> getVideoList() {
        return mList;
    }

    @Override
    public boolean playPause(boolean b) {
        return JniUtil.pause(b);
    }

    @Override
    public boolean isPlayBackCtrlAllow() {
        return false;
    }


    private void onConnect(String sessionId){
        Log.i("123", "session id = "+sessionId);
        mSessionID = sessionId;
        mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_LOGIN_CAM_OK);
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

    private void onSubscribe(String jsonStr){
        Log.i("123","onASubscribe   jsonStr="+jsonStr);

        TurnSubScribeAckBean bean = TurnJsonUtil.getTurnSubscribeAckAllFromJsonStr(jsonStr);
        CodecBean codec = new CodecBean();
        codec.setAudioCodec(bean.getAudioCodec())
                .setVideoCodec(bean.getVideoCodec())
                .setAudioChannels(bean.getAudioChannels())
                .setAudioBitwidth(bean.getAudioBitwidth())
                .setAudioSamples(bean.getAudioSamples());

        if(JniUtil.readyPlay(codec,mIsSub)){
            JniUtil.playView();
            startTimerTask();
        }else{
            Log.e("123", "ready play live error");
        }
    }



    private void initServerInfo() throws NullPointerException {
        mTurnServiceIP = ServerConfigSp.loadServerIP(mContext);
        mIsTurnCrypto = ServerConfigSp.loadServerIsCrypto(mContext);
//        mTurnServicePort = mIsTurnCrypto?8862:8812;//fixme
        mTurnServicePort = mIsTurnCrypto?DEFAULT_TURN_SERVER_PORT_SSL:DEFAULT_TURN_SERVER_PORT_NOSSL;


        if (mTurnServiceIP==null){
            throw new NullPointerException();
        }

    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            int streamLen = JniUtil.transGetStreamLenSomeTime();
            Log.i("123","from my time task   "+ streamLen+"    speed="+streamLen*8/1024/F_TIME+"kbit");
            int speed = streamLen*8/1024/F_TIME;
            if (mStreamCB!=null){
                mStreamCB.showStreamSpeed(speed);
            }

            if (streamLen == 0) {
                mUnexpectNoFrame++;
            }else{
                mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_PLAY_UNWAIT);
                mUnexpectNoFrame = 0;
            }

            if (mUnexpectNoFrame==3  ){
                mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_PLAY_WAIT);
            }

            if (mUnexpectNoFrame == 10) {// 10s / 1000ms
//                mHandler.sendEmptyMessage(PlayerActivity.MSG_DISCONNECT_UNEXPECT);//FIXME
                // TODO reLink
                mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_RELINK_START);
            }
        }
    }
}
