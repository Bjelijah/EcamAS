package com.howell.modules.device.presenter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.howell.action.ConfigAction;
import com.howell.bean.APDeviceDBBean;
import com.howell.bean.CameraItemBean;
import com.howell.bean.PlayType;
import com.howell.db.ApDeviceDao;
import com.howell.modules.BasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.device.IDeviceContract;
import com.howell.rxbus.RxBus;
import com.howell.rxbus.RxConstants;
import com.howell.utils.ServerConfigSp;
import com.howell.utils.UserConfigSp;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/9/15.
 */

public abstract class DeviceBasePresenter extends BasePresenter implements IDeviceContract.IPresenter {
    IDeviceContract.IVew mView;
    Context mContext;
    String mURL;
    boolean mIsTurn;
    String mAccount;

    @Override
    public void bindView(ImpBaseView view) {
       mView = (IDeviceContract.IVew) view;
       initEvent();
    }

    @Override
    public void unbindView() {
        dispose();
        mView = null;
    }

    @Override
    public void init(Context context) {
        mContext = context;
        initConfig();
    }

    protected void initConfig(){
        ConfigAction cf = ConfigAction.getInstance(mContext);
        mURL = cf.getURL();
        mIsTurn = ServerConfigSp.loadServerIsTurn(mContext);
        mAccount = UserConfigSp.loadUserName(mContext);
    }

    protected void initEvent(){

        RxBus.getDefault().toObservableWithCode(RxConstants.RX_CONFIG_CODE,String.class)
                .subscribeWith(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        initConfig();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        RxBus.getDefault()
                .toObservableWithCode(RxConstants.RX_PLAY_TYPE_CODE,Bundle.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<Bundle>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Bundle bundle) {
                        Boolean isTurn ,isCrypto;
                        try{isTurn= bundle.getBoolean("isTurn");}catch (Exception e){isTurn = null;}
                        try{ isCrypto = bundle.getBoolean("isCrypto");}catch (Exception e){isCrypto = null;}
                        mIsTurn = isTurn;
                        mView.onUpdateCamBean(isTurn,isCrypto);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","on complete");
                    }
                });


    }




    @Override
    public void addDevice(final CameraItemBean bean) {
        if (bean.getType()!= PlayType.HW5198)return;

        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {

                ApDeviceDao dao = new ApDeviceDao(mContext,"user.db",1);
                APDeviceDBBean apBean = new APDeviceDBBean(mAccount,bean.getCameraName(),bean.getUpnpIP(),bean.getUpnpPort());
                if (dao.findByName(mAccount,bean.getCameraName())){
                    dao.updataByName(apBean,mAccount,bean.getCameraName());
                }else{
                    dao.insert(apBean);
                }
                List<APDeviceDBBean> aps = dao.queryByName(mAccount);
                Log.i("123","aps="+aps.toString());


                dao.close();
                e.onNext(true);
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
                        mView.onAddResult(aBoolean,PlayType.HW5198);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mView.onAddResult(false,PlayType.HW5198);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","add device finish");
                    }
                });



    }

    @Override
    public void removeDevice(final CameraItemBean bean, final int pos) {
        if (bean.getType()!=PlayType.HW5198)return;
        Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                ApDeviceDao dao = new ApDeviceDao(mContext,"user.db",1);
                dao.deleteByName(mAccount,bean.getCameraName());
                dao.close();
                e.onNext(true);
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
                        mView.onRemoveResult(false,pos);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","remove finish");
                    }
                });


    }

    @Override
    public void getDevicesMatchCode() {

    }

    @Override
    public void getDeviceMatchResult(String matchCode,String name) {

    }
}
