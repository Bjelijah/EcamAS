package com.howell.modules.pdc.presenter;

import android.util.Log;

import com.howellsdk.api.ApiManager;
import com.howellsdk.net.http.bean.PDCServiceVersion;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/11/23.
 */

public class PDCPresenter extends PDCBasePresenter {

    @Override
    public void test() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        addDisposable(ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcServiceVersion(ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_VERSION,null))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PDCServiceVersion>() {
                    @Override
                    public void accept(PDCServiceVersion pdcServiceVersion) throws Exception {
                        Log.i("123","version="+pdcServiceVersion.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }));

    }
}
