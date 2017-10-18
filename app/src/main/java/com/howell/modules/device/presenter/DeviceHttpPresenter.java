package com.howell.modules.device.presenter;

import android.content.Context;
import android.util.Log;

import com.howell.action.ConfigAction;
import com.howell.bean.APDeviceDBBean;
import com.howell.bean.CameraItemBean;
import com.howell.bean.PlayType;
import com.howell.db.ApDeviceDao;
import com.howellsdk.api.ApiManager;
import com.howellsdk.net.http.bean.VideoInputChannelPermission;
import com.howellsdk.net.http.bean.VideoInputChannelPermissionList;
import com.howellsdk.net.http.utils.Util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/10/17.
 */

public class DeviceHttpPresenter extends DeviceBasePresenter {
    @Override
    public void queryDevices() {
        try {
            queryBusinessVideoInput();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }

    private void queryBusinessVideoInput() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ApiManager.getInstance()
                .getHWHttpService(mURL)
                .queryBusinessVideoInputChannel(ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.BUSINESS_VIDEO_INPUT),null,null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<VideoInputChannelPermissionList, ArrayList<VideoInputChannelPermission>>() {

                    @Override
                    public ArrayList<VideoInputChannelPermission> apply(@NonNull VideoInputChannelPermissionList videoInputChannelPermissionList) throws Exception {
                        return videoInputChannelPermissionList.getVideoInputChannelPermissiones();
                    }
                })
                .flatMap(new Function<ArrayList<VideoInputChannelPermission>, ObservableSource<VideoInputChannelPermission>>() {
                    @Override
                    public ObservableSource<VideoInputChannelPermission> apply(@NonNull ArrayList<VideoInputChannelPermission> videoInputChannelPermissions) throws Exception {
                        return Observable.fromIterable(videoInputChannelPermissions);
                    }
                })
                .map(new Function<VideoInputChannelPermission, CameraItemBean>() {
                    @Override
                    public CameraItemBean apply(@NonNull VideoInputChannelPermission v) throws Exception {
                        String ip = ConfigAction.getInstance(mContext).getIp();
                        if (!com.howell.utils.Util.isIP(ip)){
                            ip = com.howell.utils.Util.parseIP(ip);
                        }

                        return new CameraItemBean()
                                .setType(PlayType.TURN)
                                .setCameraName(v.getName())
                                .setCameraDescription("")
                                .setIndensity(0)
                                .setDeviceId(Util.transformItemId2DeviceId(v.getId()))
                                .setChannelNo(Util.transfromItemId2DeviceChannel(v.getId()))
                                .setOnline(true)
                                .setPtz(true)
                                .setStore(true)
                                .setUpnpIP(ip)
                                .setUpnpPort(ConfigAction.getInstance(mContext).isSSL()?8862:8812)
                                .setMethodType(0)
                                .setDeVer("")
                                .setAndroidPush(false)
                                .setPicturePath("/sdcard/eCamera/cache/"+v.getId()+".jpg");
                    }
                })
                .toList()
                .map(new Function<List<CameraItemBean>, List<CameraItemBean>>() {
                    private List<APDeviceDBBean> getAPCameraList(Context context, String userName){
                        ApDeviceDao dao = new ApDeviceDao(context,"user.db",1);
                        List<APDeviceDBBean> beanList =  dao.queryByName(userName);
                        Log.i("123","!!!!! ap beanList="+beanList.toString()+"  username="+userName);
                        dao.close();
                        return beanList;
                    }

                    @Override
                    public List<CameraItemBean> apply(@NonNull List<CameraItemBean> cameraItemBeen) throws Exception {
                        List<APDeviceDBBean> apList = getAPCameraList(mContext,mAccount);
                        for (APDeviceDBBean apBean : apList){
                            CameraItemBean camBean = new CameraItemBean()
                                    .setType(PlayType.HW5198)
                                    .setCameraName(apBean.getDeviceName())
                                    .setCameraDescription("AP:"+apBean.getDeviceIP())
                                    .setOnline(true)
                                    .setIndensity(0)
                                    .setStore(true)
                                    .setPtz(true)
                                    .setUpnpIP(apBean.getDeviceIP())
                                    .setUpnpPort(apBean.getDevicePort())
                                    .setDeviceId(apBean.getDeviceIP())
                                    .setPicturePath("/sdcard/eCamera/cache/"+apBean.getDeviceIP()+".jpg");
                            cameraItemBeen.add(0,camBean);//// FIXME: 2017/9/18  插在最前面
                        }
                        return cameraItemBeen;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<CameraItemBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onSuccess(@NonNull List<CameraItemBean> cameraItemBeen) {
                        mView.onQueryResult(cameraItemBeen);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

}
