package com.howell.action;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.howell.activity.DeviceSettingActivity;
import com.howell.bean.CameraItemBean;
import com.howell.protocol.CodingParamReq;
import com.howell.protocol.CodingParamRes;
import com.howell.protocol.GetAuxiliaryReq;
import com.howell.protocol.GetAuxiliaryRes;
import com.howell.protocol.GetDevVerReq;
import com.howell.protocol.GetDevVerRes;
import com.howell.protocol.GetVideoParamReq;
import com.howell.protocol.GetVideoParamRes;
import com.howell.protocol.SoapManager;
import com.howell.protocol.VMDParamReq;
import com.howell.protocol.VMDParamRes;

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


                return true;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                if (mHandler==null)return;
                Message msg = new Message();
                if (aVoid){
                    String frameSize = resParam.getFrameSize();
                    int bitrate = Integer.parseInt(resParam.getBitRate());





                }else {
                    msg.what = DeviceSettingActivity.MSG_SETTING_WAIT_DISSHOW;
                    mHandler.sendMessage(msg);
                    msg.what = DeviceSettingActivity.MSG_SETTING_GAIN_ERROR;
                    mHandler.sendMessage(msg);
                }


            }
        }.execute();



    }


}
