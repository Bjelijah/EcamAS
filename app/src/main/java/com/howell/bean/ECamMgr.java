package com.howell.bean;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import android.provider.MediaStore;
import android.util.Log;

import com.howell.action.AudioAction;
import com.howell.action.LoginAction;
import com.howell.activity.BasePlayActivity;
import com.howell.entityclass.Crypto;
import com.howell.entityclass.StreamReqContext;
import com.howell.entityclass.StreamReqIceOpt;
import com.howell.entityclass.VODRecord;
import com.howell.jni.JniUtil;
import com.howell.protocol.GetDevVerReq;
import com.howell.protocol.GetDevVerRes;
import com.howell.protocol.GetNATServerRes;
import com.howell.protocol.InviteRequest;
import com.howell.protocol.InviteResponse;
import com.howell.protocol.NullifyDeviceReq;
import com.howell.protocol.NullifyDeviceRes;
import com.howell.protocol.SoapManager;
import com.howell.protocol.VodSearchRes;
import com.howell.utils.DeviceVersionUtils;
import com.howell.utils.IConst;

import org.kobjects.base64.Base64;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by howell on 2016/12/6.
 */

public class ECamMgr implements ICam,IConst {
    Context mContext;
    CameraItemBean mCamBean;
    Handler mHandler;
    SoapManager mSoapManager = SoapManager.getInstance();
    int mIsSub = 1;
    int mIsPlayBack = 0;
    private String mStreamType = "Sub";
    private Random random;
    long mPlayBackStartTime = 0,mPlayBackEndTime = 0;
    int mPlayBackRe = 0;//是否是移动滑杆条

    private int auType = 0;
    private static final int F_TIME = 2;//刷新率  s
    ICam.IStream mStreamCB = null;
    private MyTimerTask myTimerTask = null;
    private Timer timer = null;
    private String lastRefreshStartTime,lastRefreshEndTime;
    //playback  video list

    VodSearchRes mVodSearchRes = null;

    @Override
    public void init(Context context, CameraItemBean bean) {
        this.mCamBean = bean;
        this.mContext = context;
        random = new Random();
    }

    @Override
    public void deInit() {

    }

    @Override
    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void registStreamLenCallback(IStream cb) {
        this.mStreamCB = cb;
    }

    @Override
    public void unregistStreamLenCallback() {
        this.mStreamCB = null;
    }



    @Override
    public void setStreamBSub(boolean isSub) {
        this.mIsSub = isSub?1:0;
        this.mStreamType = isSub?"Sub":"Main";
    }

    @Override
    public void setPlayBack(boolean isPlayback) {
        this.mIsPlayBack = isPlayback?1:0;
        if (!isPlayback){
            mPlayBackStartTime = 0;
            mPlayBackEndTime = 0;
        }
    }

    @Override
    public void setPlayBackTime(String startTime, String endTime) {
        SimpleDateFormat bar = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss");
        bar.setTimeZone(TimeZone.getTimeZone("UTC"));
        long start = 0;
        long end = 0;
        try {
            start = bar.parse(startTime).getTime()/1000;
            end = bar.parse(endTime).getTime()/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.mPlayBackStartTime = start;
        this.mPlayBackEndTime = end;
    }

    @Override
    public boolean bind() {
        return true;
    }

    @Override
    public boolean unBind() {
        NullifyDeviceRes res = null;
        Log.i("123","unBind eCam deviceId="+mCamBean.getDeviceId());
        try {
            NullifyDeviceReq req = new NullifyDeviceReq(LoginAction.getInstance().getmInfo().getAccount()
                    ,LoginAction.getInstance().getmInfo().getLr().getLoginSession(),mCamBean.getDeviceId(),mCamBean.getDeviceId());
            res = SoapManager.getInstance().getNullifyDeviceRes(req);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("removeDevice:"+res.getResult());
        Log.e("123","removeDevice:"+res.getResult());
        return true;
    }

    @Override
    public boolean loginCam() {
        Log.e("123","login cam~~~~~~~~~~");
        return ecamloginCam();
    }

    @Override
    public boolean logoutCam() {
       return ecamlogoutCam();
    }

    @Override
    public boolean playViewCam() {
        Log.e("123","play view cam ~~~~~~~~~~~");
        return ecamPlayViewCam();
    }



    @Override
    public boolean stopViewCam() {
        return ecamStopViewCam();
    }

    @Override
    public boolean reLink() {
        if(!ecamStopViewCam()){
            Log.e("123","stop view cam error");
            return false;
        }
        if(!ecamlogoutCam()){
            Log.e("123","logout cam error");
            return false;
        }
        if(!ecamloginCam()){
            Log.e("123","login cam error");
            return false;
        }
        if(!ecamPlayViewCam()){
            Log.e("123","play view cam error");
            return false;
        }
        return true;
    }

    @Override
    public boolean catchPic(String path) {
        JniUtil.catchPic(path);
        return true;
    }

    @Override
    public boolean soundSetData(byte[] buf, int len) {
        return JniUtil.ecamSendAudioData(buf,len)==0?false:true;
    }



    @Override
    public boolean hasVideoList() {
        return mCamBean.isStore();
    }

    @Override
    public void setVideoListTime(String startTime, String endTime) {
        lastRefreshStartTime = startTime;
        lastRefreshEndTime = endTime;
    }


    @Override
    public int getVideoListPageCount(int nowPage,int pageSize) {
        String account = LoginAction.getInstance().getmInfo().getAccount();
        String loginSession = LoginAction.getInstance().getmInfo().getLr().getLoginSession();
        String devID = mCamBean.getDeviceId();
        int channelNo = mCamBean.getChannelNo();
        Log.i("123","getVideoListPageCount");

        if (checkVerIsNew()){
            mVodSearchRes = mSoapManager.getVodSearchReq(account, loginSession, devID,
                    channelNo, mStreamType , nowPage,lastRefreshStartTime,lastRefreshEndTime,pageSize);
            Log.i("123","pageConut = "+mVodSearchRes.getPageCount());
            return mVodSearchRes.getPageCount();
        }else{

        }
        return 0;
    }

    @Override
    public ArrayList<VODRecord> getVideoList() {
        return mVodSearchRes==null?null:mVodSearchRes.getRecord();
    }

    private boolean checkVerIsNew(){
        String account = LoginAction.getInstance().getmInfo().getAccount();
        String session = LoginAction.getInstance().getmInfo().getLr().getLoginSession();
        String devId = mCamBean.getDeviceId();
        GetDevVerReq req = new GetDevVerReq(account,session,devId);
        GetDevVerRes res = SoapManager.getInstance().getGetDevVerRes(req);
        Log.e("123", "CurDevVer:"+res.getCurDevVer());
        return DeviceVersionUtils.isNewVersionDevice(res.getCurDevVer());

    }


    private boolean ecamloginCam(){ //login and ready encode
        JniUtil.netInit();
        JniUtil.ecamInit(LoginAction.getInstance().getmInfo().getAccount());
        JniUtil.ecamSetCallbackObj(ECamMgr.this,0);
        JniUtil.ecamSetContextObj(getStreamReqContext());
        boolean ret = false;
        try {
            ret = invite();//sdp
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("123","invite error  try");
            return false;
        }
        if (ret){
            auType = JniUtil.ecamGetAudioType();
        }else {
            Log.e("123","invite error");
            return false;
        }

        if(!JniUtil.readyPlay(1,auType,mIsPlayBack)){
            Log.e("123","readplay live error");
            return false;
        }//解码器 初始化

        AudioAction.getInstance().initAudio();
        Log.e("123","init audio");
        AudioAction.getInstance().playAudio();
        Log.e("123","play audio");
        return true;
    }

    private boolean ecamlogoutCam(){
        JniUtil.releasePlay();//释放解码器
        AudioAction.getInstance().deInitAudio();
        JniUtil.ecamDeinit();
        JniUtil.netDeinit();
        return true;
    }

    private boolean ecamPlayViewCam(){//stream and play
        int ret = 0;
        if((ret = JniUtil.ecamStart())!=0){//申请流
            Log.e("123","ecam start error   申请流 ret ="+ret);
            return false;
        }
        JniUtil.playView();
        startTimerTask();
        return true;
    }

    private boolean ecamStopViewCam(){
        int ret = -3;
        ret = JniUtil.ecamStop();
        if (ret!=0) {
            Log.e("123", "ecam stop error ret=" + ret);
        }
        AudioAction.getInstance().stopAudio();
        JniUtil.stopView();
        stopTimerTask();
        return true;
    }


    private StreamReqContext fillStreamReqContext(int isPlayBack,long beg,long end,int re_invite,int methodType,int stream){
        Log.e("123","isPlayBack="+isPlayBack+" beg="+beg+" end="+end+" re="+re_invite+" methodtype="+methodType+" stream="+stream);
        String UpnpIP = mCamBean.getUpnpIP();
        int UpnpPort = mCamBean.getUpnpPort();
        Log.i("123","ip="+UpnpIP+"  port="+UpnpPort);

        StreamReqContext streamReqContext = null;
        GetNATServerRes res = mSoapManager.getLocalGetNATServerRes();
        if(res == null){
            Log.e("InviteUtils", "res == null");
        }else{
            Log.e("InviteUtils", res.toString());
        }
        try{
            StreamReqIceOpt opt = new StreamReqIceOpt(1, res.getSTUNServerAddress(), res.getSTUNServerPort(),
                    res.getTURNServerAddress(), res.getTURNServerPort(),
                    0, res.getTURNServerUserName(), res.getTURNServerPassword());
            Crypto crypto = new Crypto(1);
            if(methodType == 0){
                streamReqContext = new StreamReqContext(isPlayBack,
                        beg, end, re_invite, 1 << 1 | 1 << 2 ,UpnpIP , UpnpPort, opt,crypto,0,stream);
                Log.e("streamReqContext", "java stream:"+stream);
                Log.e("streamReqContext", "UpnpIP:"+UpnpIP+"UpnpPort:"+UpnpPort);
            }else if(methodType == 2){
//	        	streamReqContext = new StreamReqContext(isPlayBack,
//		                beg, end, re_invite, 1 << 2 ,UpnpIP , UpnpPort, opt);
                streamReqContext = new StreamReqContext(isPlayBack,
                        beg, end, re_invite, 1 << 2 ,UpnpIP , UpnpPort, opt,crypto,0,stream);
                Log.e("streamReqContext", "java stream:"+stream);
                Log.e("streamReqContext", "UpnpIP:"+UpnpIP+"UpnpPort:"+UpnpPort);
            }

        }catch (Exception e) {
            // TODO: handle exception
            Log.e("", "fillStreamReqContext fail");

        }
        System.out.println("fillStreamReqContext2222222222222");
        return streamReqContext;
    }


    private StreamReqContext getStreamReqContext(){

        return fillStreamReqContext(mIsPlayBack,mPlayBackStartTime,mPlayBackEndTime,mPlayBackRe,mCamBean.getMethodType(),mIsSub);
    }

    private boolean invite() throws Exception{
        String dilogID = String.valueOf(random.nextInt());
        String localSDP = JniUtil.ecamPrepareSDP();
        String SDPMessage = Base64.encode(localSDP.getBytes());
        InviteResponse inviteRes = mSoapManager.getIviteRes(new InviteRequest(LoginAction.getInstance().getmInfo().getAccount(),
                LoginAction.getInstance().getmInfo().getLr().getLoginSession(),
                mCamBean.getDeviceId(),
                mCamBean.getChannelNo(),
                mStreamType,
                dilogID,
                SDPMessage));
        if (!inviteRes.getResult().equalsIgnoreCase("OK")){
            return false;
        }

        String remoteSPD = new String(Base64.decode(inviteRes.getSDPMessage()));
        JniUtil.ecamHandleRemoteSDP(dilogID,remoteSPD);
        return true;
    }

    private void startTimerTask(){
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask,0,F_TIME*1000);
    }

    private void stopTimerTask(){
        if (timer!=null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (myTimerTask!=null){
            myTimerTask.cancel();
            myTimerTask = null;
        }
    }


    class MyTimerTask extends TimerTask{

        int mUnexpectNoFrame = 0;
        boolean doOnce = false;
        @Override
        public void run() {
            int streamLen = JniUtil.ecamGetStreamLenSomeTime();
            int speed = streamLen*8/1024/F_TIME;
            if (mStreamCB!=null){
                mStreamCB.showStreamSpeed(speed);
            }
            if (streamLen==0){
                mUnexpectNoFrame++;
            }else {
                mUnexpectNoFrame = 0;
                if (!doOnce) {
                    doOnce = true;
                    mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_PLAY_UNWAIT);
                }
            }

            if (mUnexpectNoFrame==3){
                mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_PLAY_WAIT);
                doOnce = false;
            }
            if (mUnexpectNoFrame == 10){
                //TODO relink
            }
        }
    }

}
