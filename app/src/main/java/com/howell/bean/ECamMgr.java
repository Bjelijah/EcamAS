package com.howell.bean;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import android.util.Log;

import com.howell.action.AudioAction;
import com.howell.action.LoginAction;
import com.howell.activity.BasePlayActivity;
import com.howell.entityclass.Crypto;
import com.howell.entityclass.StreamReqContext;
import com.howell.entityclass.StreamReqIceOpt;
import com.howell.jni.JniUtil;
import com.howell.protocol.GetNATServerRes;
import com.howell.protocol.InviteRequest;
import com.howell.protocol.InviteResponse;
import com.howell.protocol.NullifyDeviceReq;
import com.howell.protocol.NullifyDeviceRes;
import com.howell.protocol.SoapManager;
import com.howell.utils.IConst;

import org.kobjects.base64.Base64;

import java.util.Random;

import static com.zys.brokenview.Utils.random;

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
    public void setStreamBSub(boolean isSub) {
        this.mIsSub = isSub?1:0;
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
    public void setPlayBackTime(long startTime, long endTime) {
        this.mPlayBackStartTime = startTime;
        this.mPlayBackEndTime = endTime;
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
    public void loginCam() {

        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                JniUtil.netInit();
                JniUtil.ecamInit(LoginAction.getInstance().getmInfo().getAccount());
                JniUtil.ecamSetCallbackObj(ECamMgr.this,0);
                JniUtil.ecamSetContextObj(getStreamReqContext());
                boolean ret = false;
                try {
                    ret = invite();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                if (ret){
                    auType = JniUtil.ecamGetAudioType();
                }

                AudioAction.getInstance().initAudio();
                AudioAction.getInstance().playAudio();
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (mHandler==null)return;
                if (aBoolean){
                    mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_LOGIN_CAM_OK);
                }else{
                    mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_LOGIN_CAM_ERROR);
                }
            }
        }.execute();



    }

    @Override
    public void logoutCam() {




    }

    @Override
    public void playViewCam() {

        if (JniUtil.readyPlayLive(1,auType)){
            JniUtil.ecamStart();
            JniUtil.playView();
            mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_PLAY_CAM_OK);
        }else{
            //TODO playview error;
            mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_PLAY_CAM_ERROR);
        }
    }



    @Override
    public void stopViewCam() {
        JniUtil.stopView();
        JniUtil.ecamStop();

    }

    @Override
    public void reLink() {

    }

    @Override
    public void catchPic(String path) {
        JniUtil.catchPic(path);
    }


    private StreamReqContext fillStreamReqContext(int isPlayBack,long beg,long end,int re_invite,int methodType,int stream){

        String UpnpIP = mCamBean.getUpnpIP();
        int UpnpPort = mCamBean.getUpnpPort();


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



}
