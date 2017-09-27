package com.howell.modules.device.presenter;

import android.content.Context;
import android.util.Log;

import com.howell.bean.APDeviceDBBean;
import com.howell.bean.CameraItemBean;
import com.howell.bean.PlayType;
import com.howell.db.ApDeviceDao;
import com.howell.utils.PhoneConfig;
import com.howell.utils.ServerConfigSp;
import com.howell.utils.UserConfigSp;
import com.howellsdk.api.ApiManager;
import com.howellsdk.net.http.bean.DeviceStatus;
import com.howellsdk.net.soap.bean.AddDeviceReq;
import com.howellsdk.net.soap.bean.DeviceStatusReq;
import com.howellsdk.net.soap.bean.DeviceStatusRes;
import com.howellsdk.net.soap.bean.LoginRequest;
import com.howellsdk.net.soap.bean.LoginResponse;
import com.howellsdk.net.soap.bean.NullifyDeviceReq;
import com.howellsdk.net.soap.bean.Result;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/9/15.
 */

public class DeviceSoapPresenter extends DeviceBasePresenter {

    //超时重新登入
    private void login(){
        //get information from sp last saved
        mURL = ServerConfigSp.loadServerURL(mContext);
        boolean isSSL = ServerConfigSp.loadServerSSL(mContext);
        String account = UserConfigSp.loadUserName(mContext);
        String password = UserConfigSp.loadUserPwd(mContext);
        String imei = PhoneConfig.getIMEI(mContext);
        ApiManager.getInstance()
                .initSoapClient(mContext,isSSL)
                .getSoapService(mURL)
                .userLogin(new LoginRequest(account,password,imei))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull LoginResponse loginResponse) {
                        ApiManager.SoapHelp.setsSession(loginResponse.getLoginSession());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.e("123","relogin finish");
                    }
                });
    }


    @Override
    public void queryDevices() {

        ApiManager.getInstance()
                .getSoapService(mURL)
                .queryDeviceStatus(new DeviceStatusReq(mAccount,ApiManager.SoapHelp.getsSession()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<DeviceStatusRes, ArrayList<DeviceStatusRes.Node>>() {
                    private ArrayList<DeviceStatusRes.Node> sort(ArrayList<DeviceStatusRes.Node> list){
                        for(int i = 0 ; i <  list.size() ; i++){
                            if(list.get(i).getOnLine()){
                                list.add(0,list.remove(i));
                            }
                        }
                        return list;
                    }
                    @Override
                    public ArrayList<DeviceStatusRes.Node> apply(@NonNull DeviceStatusRes deviceStatusRes) throws Exception {
                        if (!deviceStatusRes.getResult().equalsIgnoreCase("ok")){
                            login();
                            return null;
                        }

                        return sort(deviceStatusRes.getNodes());
                    }
                })
                .flatMap(new Function<ArrayList<DeviceStatusRes.Node>, ObservableSource<DeviceStatusRes.Node>>() {
                    @Override
                    public ObservableSource<DeviceStatusRes.Node> apply(@NonNull ArrayList<DeviceStatusRes.Node> nodes) throws Exception {
                        return Observable.fromIterable(nodes);
                    }
                })
                .map(new Function<DeviceStatusRes.Node, CameraItemBean>() {
                    @Override
                    public CameraItemBean apply(@NonNull DeviceStatusRes.Node node) throws Exception {
                        return new CameraItemBean()
                                .setType(mIsTurn?PlayType.TURN:PlayType.ECAM)
                                .setCameraName(node.getName())
                                .setCameraDescription(null)
                                .setIndensity(node.getNetwork().getIntensity())
                                .setDeviceId(node.getDevID())
                                .setOnline(node.getOnLine())
                                .setPtz(node.getPtzFlag())
                                .setStore(node.geteStoreFlag())
                                .setUpnpIP(node.getUpnpIP())
                                .setUpnpPort(node.getUpnpPort())
                                .setMethodType(0)
                                .setDeVer(node.getDevVer())
                                .setPicturePath("/sdcard/eCamera/cache/"+node.getDevID()+".jpg");
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
                    public void onSuccess(@NonNull List<CameraItemBean> cameraItemBean) {
                        mView.onQueryResult(cameraItemBean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void addDevice(CameraItemBean bean) {
        super.addDevice(bean);
        if (bean.getType()!=PlayType.ECAM)return;
        //add ecam
        ApiManager.getInstance().getSoapService()
                .addDevice(new AddDeviceReq(mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        bean.getDeviceId(),
                        bean.getDevKey(),
                        bean.getCameraName(),
                        false))
                .map(new Function<Result, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Result result) throws Exception {
                        if (result.getResult().equalsIgnoreCase("SessionExpired")){
                            login();
                            return false;
                        }
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
                        mView.onAddResult(aBoolean,PlayType.ECAM);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onAddResult(false,PlayType.ECAM);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","add ecam finish");
                    }
                });
    }

    @Override
    public void removeDevice(CameraItemBean bean, final int pos) {
        super.removeDevice(bean,pos);
        if (bean.getType()!=PlayType.ECAM)return;
        ApiManager.getInstance().getSoapService()
                .nullifyDevice(new NullifyDeviceReq(
                        mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        bean.getDeviceId(),
                        bean.getDeviceId()))
                .map(new Function<Result, Boolean>() {

                    @Override
                    public Boolean apply(@NonNull Result result) throws Exception {
                        if (result.getResult().equalsIgnoreCase("SessionExpired")){
                            login();
                            return false;
                        }
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
                        mView.onRemoveResult(aBoolean,pos);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onRemoveResult(false,pos);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","remove finish");
                    }
                });

    }




}
