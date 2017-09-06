package com.howell.action;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.howell.activity.DeviceSettingActivity;
import com.howell.bean.CameraItemBean;
import com.howell.entityclass.VMDGrid;
import com.howell.protocol.CodingParamReq;
import com.howell.protocol.CodingParamRes;
import com.howell.protocol.GetAuxiliaryReq;
import com.howell.protocol.GetAuxiliaryRes;
import com.howell.protocol.GetDevVerReq;
import com.howell.protocol.GetDevVerRes;
import com.howell.protocol.GetVideoParamReq;
import com.howell.protocol.GetVideoParamRes;
import com.howell.protocol.QueryDeviceReq;
import com.howell.protocol.QueryDeviceRes;
import com.howell.protocol.SetAuxiliaryReq;
import com.howell.protocol.SetAuxiliaryRes;
import com.howell.protocol.SetVideoParamReq;
import com.howell.protocol.SetVideoParamRes;
import com.howell.protocol.SoapManager;
import com.howell.protocol.SubscribeAndroidPushReq;
import com.howell.protocol.SubscribeAndroidPushRes;
import com.howell.protocol.UpdateChannelNameReq;
import com.howell.protocol.UpdateChannelNameRes;
import com.howell.protocol.VMDParamReq;
import com.howell.protocol.VMDParamRes;
import com.howell.utils.DeviceVersionUtils;

/**
 * Created by Administrator on 2016/12/30.
 */

public class SettingAction {
    private static SettingAction mInstance=null;
    public static SettingAction getInstance(){
        if (mInstance==null){
            mInstance = new SettingAction();
        }
        return mInstance;
    }
    private SoapManager mSoapManager = SoapManager.getInstance();
    private static String[] VMD_DEFAULT_GRIDS = {
            "00000000000",
            "00000000000",
            "00011111000",
            "00011111000",
            "00011111000",
            "00011111000",
            "00011111000",
            "00000000000",
            "00000000000",
    };
    private static String[] VMD_ZERO_GRIDS = {
            "00000000000",
            "00000000000",
            "00000000000",
            "00000000000",
            "00000000000",
            "00000000000",
            "00000000000",
            "00000000000",
            "00000000000",
    };
    CodingParamRes mResParam;
    VMDParamRes mResVMD;
    GetAuxiliaryRes mResAux;
    GetVideoParamRes mResVideo;
    GetDevVerRes mResDev;
    QueryDeviceRes mQueryDeviceRes;



    private SettingAction(){}

    private Handler mHandler;
    private CameraItemBean mBean;
    public SettingAction setHandler(Handler h){
        this.mHandler = h;
        return this;
    }
    public SettingAction setBean(CameraItemBean b){
        this.mBean = b;
        return this;
    }


    public void loadSetting(){
        new AsyncTask<Void,Void,Boolean>(){
            CodingParamRes resParam;
            VMDParamRes resVMD;
            GetAuxiliaryRes resAux;
            GetVideoParamRes resVideo;
            GetDevVerRes resDev;
            QueryDeviceRes queryDeviceRes;
            @Override
            protected Boolean doInBackground(Void... params) {
                String account = LoginAction.getInstance().getmInfo().getAccount();
                String session =  LoginAction.getInstance().getmInfo().getLr().getLoginSession();
                String devId = mBean.getDeviceId();
                int ch = mBean.getChannelNo();

                CodingParamReq reqParam = new CodingParamReq(account, session, devId,ch,"Sub");
                VMDParamReq reqVMD = new VMDParamReq(account,session,devId,ch);
                GetAuxiliaryReq reqAux = new GetAuxiliaryReq(account,session,devId,"SignalLamp");
                GetVideoParamReq reqVideo = new GetVideoParamReq(account,session,devId,ch);
                GetDevVerReq reqDev = new GetDevVerReq(account,session,devId);

                try {
                    resParam = mSoapManager.getCodingParamRes(reqParam);
                    resVMD = mSoapManager.getVMDParam(reqVMD);
                    resAux = mSoapManager.getGetAuxiliaryRes(reqAux);
                    resVideo = mSoapManager.getGetVideoParamRes(reqVideo);
                    resDev = mSoapManager.getGetDevVerRes(reqDev);
                }catch (Exception e){
                    return false;
                }
                if (!resParam.getResult().equalsIgnoreCase("OK")||
                        !resVMD.getResult().equalsIgnoreCase("OK")||
                        !resAux.getResult().equalsIgnoreCase("OK")||
                        !resVideo.getResult().equalsIgnoreCase("OK")||
                        !resDev.getResult().equalsIgnoreCase("OK")){
                    return false;
                }
                if (resVMD.getEnabled()){
                    QueryDeviceReq reqQueryDevice = new QueryDeviceReq(account,session,devId);
                    queryDeviceRes = mSoapManager.getQueryDeviceRes(reqQueryDevice);
                }

                mResParam = resParam;
                mResVMD = resVMD;
                mResAux = resAux;
                mResVideo = resVideo;
                mResDev = resDev;


                return true;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                if (mHandler==null)return;
                mHandler.sendEmptyMessage(DeviceSettingActivity.MSG_SETTING_WAIT_DISSHOW);
                Message msg = new Message();
                if (aVoid){
                    String frameSize = resParam.getFrameSize();
                    int bitrate = Integer.parseInt(resParam.getBitRate());
                    boolean bVmd = resVMD.getEnabled();
                    boolean bPush = false;
                    if (bVmd){
                        bPush = queryDeviceRes.getAndroidPushSubscribedFlag()==0?false:true;
                    }
                    boolean bLamp = resAux.getAuxiliaryState().equals("Inactive")?false:true;
                    boolean bRotation = resVideo.getRotationDegree()==0?false:true;
                    boolean bNeedUpdata = false;
                    try {
                        bNeedUpdata = DeviceVersionUtils.needToUpdate(resDev.getCurDevVer(),resDev.getNewDevVer());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("frameSize",frameSize);
                    bundle.putInt("bitrate",bitrate);
                    bundle.putBoolean("bVmd",bVmd);
                    bundle.putBoolean("bPush",bPush);
                    bundle.putBoolean("bLamp",bLamp);
                    bundle.putBoolean("bRotation",bRotation);
                    bundle.putBoolean("bNeedUpdata",bNeedUpdata);
                    bundle.putString("curVer",resDev.getCurDevVer());
                    bundle.putString("newVer",resDev.getNewDevVer());

                    Log.i("123","bundle "+bundle.toString());
                    msg.what = DeviceSettingActivity.MSG_SETTING_GAIN_MSG;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);

                }else {
                    msg.what = DeviceSettingActivity.MSG_SETTING_GAIN_ERROR;
                    mHandler.sendMessage(msg);
                }


            }
        }.execute();



    }

    public void setRenameCameraName(String newName){

    }


    public void saveSetting(Bundle bundle){
        final boolean bSaveEncode = bundle.getBoolean("bSaveEncode");
        final boolean bSaveTurn = bundle.getBoolean("bSaveTurn");
        final boolean bSaveLamp = bundle.getBoolean("bSaveLamp");
        final boolean bSaveVmd = bundle.getBoolean("bSaveVmd");
        final boolean bSavePush = bundle.getBoolean("bSavePush");

        final int resoIndex = bundle.getInt("resoIndex");
        final int qualityIndex = bundle.getInt("qualityIndex");
        final boolean bTurn = bundle.getBoolean("bTurn");
        final boolean bLamp = bundle.getBoolean("bLamp");
        final boolean bVmd = bundle.getBoolean("bVmd");
        final boolean bPush = bundle.getBoolean("bPush");
        final boolean brename = bundle.getBoolean("bRename");
        final String renameStr = bundle.getString("newName");
        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                try{
                    if (bSaveEncode)saveEncodeParam(resoIndex,qualityIndex);
                    if (bSaveTurn)saveTurnParam(bTurn);
                    if (bSaveLamp)saveLampParam(bLamp);
                    if (bSaveVmd)saveVmdParam(bVmd);
                    if (bSavePush)savePushParam(bPush);
                    if (brename) saveNewCameraName(renameStr);
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (mHandler==null)return;

                if (aBoolean){
                    mHandler.sendEmptyMessage(DeviceSettingActivity.MSG_SETTING_SAVE_OK);
                }else{
                    mHandler.sendEmptyMessage(DeviceSettingActivity.MSG_SETTING_SAVE_ERROR);
                }



            }
        }.execute();




    }

    private void saveEncodeParam(int resoIndex,int qualityIndex) throws Exception{
        int bitrate = DeviceSettingActivity.reso_bitrate_map_[resoIndex][qualityIndex];
        Log.i("123","bitrate="+bitrate);
        String streamType=resoIndex==0?"Sub":"Main";
        mResParam.setStreamType(streamType);
        mResParam.setFrameSize(DeviceSettingActivity.mFrameSizeValues[resoIndex]);
        mResParam.setBitRate(String.valueOf(bitrate));


        mSoapManager.setCodingParam(mResParam);
    }

    private void saveTurnParam(boolean isTurn) throws Exception{
        String account = LoginAction.getInstance().getmInfo().getAccount();
        String session =  LoginAction.getInstance().getmInfo().getLr().getLoginSession();
        String devId = mBean.getDeviceId();
        int ch = mBean.getChannelNo();
        SetVideoParamReq req = new SetVideoParamReq(account,session,devId,ch,isTurn?180:0);
        SetVideoParamRes res = mSoapManager.getSetVideoParamRes(req);
        if (!res.getResult().equalsIgnoreCase("OK"))throw new IllegalStateException("save turn param res="+res.getResult());
    }

    private void saveLampParam(boolean bLamp) throws Exception{
        String account = LoginAction.getInstance().getmInfo().getAccount();
        String session =  LoginAction.getInstance().getmInfo().getLr().getLoginSession();
        String devId = mBean.getDeviceId();
        int ch = mBean.getChannelNo();
        SetAuxiliaryReq req = new SetAuxiliaryReq(account,session,devId,"SignalLamp",bLamp?"Active":"Inactive");
        SetAuxiliaryRes res = mSoapManager.getSetAuxiliaryRes(req);
        if (!res.getResult().equalsIgnoreCase("OK"))throw new IllegalStateException("save lamp res="+res.getResult());
    }

    private void saveVmdParam(boolean bVmd) throws Exception{
        mResVMD.setEnabled(bVmd);
        mResVMD.setSensitivity(40);
        mResVMD.setGrids(new VMDGrid(bVmd?VMD_DEFAULT_GRIDS:VMD_ZERO_GRIDS));
        mSoapManager.setVMDParam(mResVMD);
    }

    private void savePushParam(boolean bPush) throws Exception{
        String account = LoginAction.getInstance().getmInfo().getAccount();
        String session =  LoginAction.getInstance().getmInfo().getLr().getLoginSession();
        String devId = mBean.getDeviceId();
        int ch = mBean.getChannelNo();
        SubscribeAndroidPushReq req = new SubscribeAndroidPushReq(account,session,bPush?0x01:0x00,devId,ch);
        SubscribeAndroidPushRes res = mSoapManager.getSubscribeAndroidPushRes(req);
        Log.i("123","android push res="+res.toString());
        if (!res.getResult().equalsIgnoreCase("OK"))throw new IllegalStateException("save push param res="+res.getResult());
    }

    private void saveNewCameraName(String name){
        String account = LoginAction.getInstance().getmInfo().getAccount();
        String session = LoginAction.getInstance().getmInfo().getLr().getLoginSession();
        String devId = mBean.getDeviceId();
        int ch = mBean.getChannelNo();
        UpdateChannelNameReq req = new UpdateChannelNameReq(account,session,devId,ch,name);
        UpdateChannelNameRes  res = mSoapManager.getUpdateChannelNameRes(req);
        if (res.getResult().equalsIgnoreCase("OK"))throw new IllegalStateException("save new name res="+res.getResult());
    }

}
