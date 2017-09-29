package com.howell.modules.param;

import android.content.Context;

import com.howell.bean.CameraItemBean;
import com.howell.modules.ImpBasePresenter;
import com.howell.modules.ImpBaseView;
import com.howellsdk.net.soap.bean.AuxiliaryRes;
import com.howellsdk.net.soap.bean.CodingParamRes;
import com.howellsdk.net.soap.bean.DevVerRes;
import com.howellsdk.net.soap.bean.DeviceStatusRes;
import com.howellsdk.net.soap.bean.VMDParamRes;
import com.howellsdk.net.soap.bean.VideoParamRes;

/**
 * Created by Administrator on 2017/9/27.
 */

public interface IParamContract {
    interface IVew extends ImpBaseView{
        void onCodeRes(CodingParamRes res);
        void onVMDRes(VMDParamRes res);
        void onAuxiliaryRes(AuxiliaryRes res);
        void onAndroidPushRes(DeviceStatusRes res);
        void onVideoParamRes(VideoParamRes res);
        void onVersionRes(DevVerRes res);
        void onSetCodeRes(boolean isOk);
        void onSetVideoRes(boolean isOk);
        void onSetAuxiliaryRes(boolean isOk);
        void onSetVmdRes(boolean isOk);
        void onSetPushRes(boolean isOk,boolean isPush);
        void onSetNewNameRes(boolean isOk);
        void onError();
    }
    interface IPresenter extends ImpBasePresenter{
        IPresenter init(Context context, CameraItemBean bean);
        void getCodingParam();
        void getVMDParam();
        void getAuxiliaryParam();
        void getVideoParam();
        void getVersionParam();
        void getPushParam();


        void setEncodeParam(int bitrate,String streamType,String frameSize);
        void setTurn180(boolean isTurn);
        void setLampOnOff(boolean bLamp);
        void setVMDOnOff(boolean bVmd);
        void setPush(boolean bPush);
        void setNewCameraName(String name);
    }
}
