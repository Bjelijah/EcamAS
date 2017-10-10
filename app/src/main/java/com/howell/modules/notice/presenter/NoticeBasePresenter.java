package com.howell.modules.notice.presenter;

import android.content.Context;
import android.util.Log;

import com.howell.action.ConfigAction;
import com.howell.modules.BasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.notice.INoticeContract;
import com.howell.rxbus.RxBus;
import com.howell.rxbus.RxConstants;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/9/18.
 */

public abstract class NoticeBasePresenter extends BasePresenter implements INoticeContract.IPresenter {
    Context mContext;
    INoticeContract.IVew mView;
    String mURL;
    String mAccount;
    @Override
    public void bindView(ImpBaseView view) {
        mView = (INoticeContract.IVew) view;
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
        mAccount = cf.getName();
    }


    protected void initEvent(){
//        if (mDisposable!=null)mDisposable.dispose();
        Log.e("123","notice base present regist event 222222222222");
        RxBus.getDefault().toObservableWithCode(RxConstants.RX_CONFIG_CODE,String.class)
                .subscribeWith(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.i("123","onSubscribe  device base");
//                        mDisposable = d;
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        Log.i("123"," ~~~~~ notice base presenter get RX_CONFIG_CODE");
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
    }



}
