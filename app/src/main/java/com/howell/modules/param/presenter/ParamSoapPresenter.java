package com.howell.modules.param.presenter;

import android.util.Log;

import com.howell.activity.DeviceSettingActivity;
import com.howell.modules.player.presenter.PlayBasePresenter;
import com.howellsdk.api.ApiManager;
import com.howellsdk.net.soap.bean.AuxiliaryRes;
import com.howellsdk.net.soap.bean.CodingParamReq;
import com.howellsdk.net.soap.bean.CodingParamRes;
import com.howellsdk.net.soap.bean.DevVerReq;
import com.howellsdk.net.soap.bean.DevVerRes;
import com.howellsdk.net.soap.bean.DeviceStatusReq;
import com.howellsdk.net.soap.bean.DeviceStatusRes;
import com.howellsdk.net.soap.bean.GetAuxiliaryReq;
import com.howellsdk.net.soap.bean.Request;
import com.howellsdk.net.soap.bean.Result;
import com.howellsdk.net.soap.bean.SetAuxiliaryReq;
import com.howellsdk.net.soap.bean.SetCodingParamReq;
import com.howellsdk.net.soap.bean.SubscribeAndroidPushReq;
import com.howellsdk.net.soap.bean.UpdateChannelNameReq;
import com.howellsdk.net.soap.bean.VMDParamReq;
import com.howellsdk.net.soap.bean.VMDParamRes;
import com.howellsdk.net.soap.bean.VideoParamReq;
import com.howellsdk.net.soap.bean.VideoParamRes;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/9/27.
 */

public class ParamSoapPresenter extends ParamBasePresenter {

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

    @Override
    public void getCodingParam() {
        ApiManager.getInstance().getSoapService()
                .getCodingParam(new CodingParamReq(
                        mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        mBean.getDeviceId(),
                        mBean.getChannelNo(),
                        "Sub"))//// FIXME: 2017/9/29  just set sub
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CodingParamRes>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull CodingParamRes codingParamRes) {
                        mView.onCodeRes(codingParamRes);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onError();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","get code finish");
                    }
                });
    }

    @Override
    public void getVMDParam() {
        ApiManager.getInstance().getSoapService()
                .getVMDParam(new Request(
                        mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        mBean.getDeviceId(),
                        mBean.getChannelNo()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VMDParamRes>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull VMDParamRes vmdParamRes) {
                        mView.onVMDRes(vmdParamRes);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onError();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","get vmd finish");
                    }
                });
    }

    @Override
    public void getAuxiliaryParam() {
        ApiManager.getInstance().getSoapService()
                .getAuxiliary(new GetAuxiliaryReq(
                        mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        mBean.getDeviceId(),
                        "SignalLamp"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AuxiliaryRes>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull AuxiliaryRes auxiliaryRes) {
                        mView.onAuxiliaryRes(auxiliaryRes);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onError();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","get aux finish");
                    }
                });
    }

    @Override
    public void getVideoParam() {
        ApiManager.getInstance().getSoapService()
                .getVideoParam(new Request(
                        mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        mBean.getDeviceId(),
                        mBean.getChannelNo()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VideoParamRes>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull VideoParamRes videoParamRes) {
                        mView.onVideoParamRes(videoParamRes);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onError();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","get video param finish");
                    }
                });

    }

    @Override
    public void getVersionParam() {
        ApiManager.getInstance().getSoapService()
                .queryDevVer(new DevVerReq(
                        mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        mBean.getDeviceId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DevVerRes>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull DevVerRes devVerRes) {
                        mView.onVersionRes(devVerRes);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onError();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","get dev ver finish");
                    }
                });
    }

    @Override
    public void getPushParam() {
        ApiManager.getInstance().getSoapService()
                .queryDeviceStatus(new DeviceStatusReq(
                        mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        mBean.getDeviceId()
                ))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DeviceStatusRes>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull DeviceStatusRes deviceStatusRes) {
                        mView.onAndroidPushRes(deviceStatusRes);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onError();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","get android push finish");
                    }
                });

    }

    @Override
    public void setEncodeParam(int bitrate,String streamType,String frameSize) {
        ApiManager.getInstance().getSoapService()
                .setCodingParam(new SetCodingParamReq(
                        mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        mBean.getDeviceId(),
                        mBean.getChannelNo(),
                        streamType,
                        frameSize,
                        null,
                        null,
                        bitrate,
                        null,
                        null
                ))
                .map(new Function<Result, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Result result) throws Exception {
                        return result.getResult().equalsIgnoreCase("ok");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        mView.onSetCodeRes(aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onSetCodeRes(false);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","set code ok");
                    }
                });
    }

    @Override
    public void setTurn180(boolean isTurn) {
        ApiManager.getInstance().getSoapService()
                .setVideoParam(new VideoParamReq(
                        mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        mBean.getDeviceId(),
                        mBean.getChannelNo(),
                        null,
                        isTurn?180:0,
                        null,null,null,null,null
                ))
                .map(new Function<Result, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Result result) throws Exception {
                        return result.getResult().equalsIgnoreCase("ok");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        mView.onSetVideoRes(aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onSetVideoRes(false);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","set video finish");
                    }
                });
    }

    @Override
    public void setLampOnOff(boolean bLamp) {
        ApiManager.getInstance().getSoapService()
                .setAuxiliary(new SetAuxiliaryReq(
                        mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        mBean.getDeviceId(),
                        "SignalLamp",
                        bLamp?"Active":"Inactive"
                ))
                .map(new Function<Result, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Result result) throws Exception {
                        return result.getResult().equalsIgnoreCase("ok");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        mView.onSetAuxiliaryRes(aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onSetAuxiliaryRes(false);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","set aux finish");
                    }
                });
    }

    @Override
    public void setVMDOnOff(boolean bVmd) {
        ApiManager.getInstance().getSoapService()
                .setVMDParam(new VMDParamReq(
                        mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        mBean.getDeviceId(),
                        mBean.getChannelNo(),
                        bVmd,
                        40,
                        null,null,
                        new VMDParamReq.VMDGrid(bVmd?VMD_DEFAULT_GRIDS:VMD_ZERO_GRIDS),
                        null
                ))
                .map(new Function<Result, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Result result) throws Exception {
                        Log.i("123","result = "+result.toString());
                        return result.getResult().equalsIgnoreCase("ok");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        mView.onSetVmdRes(aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onSetVmdRes(false);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","set vmd finish");
                    }
                });
    }

    @Override
    public void setPush(final boolean bPush) {
        ApiManager.getInstance().getSoapService()
                .subscribeAndroidPush(new SubscribeAndroidPushReq(
                        mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        bPush?0x01:0x00,
                        mBean.getDeviceId(),
                        mBean.getChannelNo()))
                .map(new Function<Result, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Result result) throws Exception {
                        return result.getResult().equalsIgnoreCase("ok");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        mView.onSetPushRes(aBoolean,bPush);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onSetPushRes(false,bPush);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","set push finish");
                    }
                });

    }

    @Override
    public void setNewCameraName(String name) {
        ApiManager.getInstance().getSoapService()
                .updateChannelName(new UpdateChannelNameReq(
                        mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        mBean.getDeviceId(),
                        mBean.getChannelNo(),
                        name))
                .map(new Function<Result, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Result result) throws Exception {
                        return result.getResult().equalsIgnoreCase("ok");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        mView.onSetNewNameRes(aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onSetNewNameRes(false);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","set name finish");
                    }
                });
    }


}
