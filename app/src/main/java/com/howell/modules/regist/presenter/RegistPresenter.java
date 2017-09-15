package com.howell.modules.regist.presenter;

import android.util.Log;

import com.howell.modules.BasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.regist.IRegistContract;
import com.howell.modules.regist.bean.Type;
import com.howellsdk.api.ApiManager;
import com.howellsdk.net.soap.bean.CreateAccountReq;
import com.howellsdk.net.soap.bean.Result;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/9/15.
 */

public class RegistPresenter extends BasePresenter implements IRegistContract.IPresenter {
    IRegistContract.IVew mView;
    @Override
    public void bindView(ImpBaseView view) {
        mView = (IRegistContract.IVew) view;
    }

    @Override
    public void unbindView() {
        dispose();
        mView = null;
    }


    @Override
    public void register(String name, String password, String email) {
        ApiManager.getInstance()
                .getSoapService()
                .createAccount(new CreateAccountReq(name,password,email))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Result, String>() {
                    @Override
                    public String apply(@NonNull Result result) throws Exception {
                        return result.getResult();
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        Type type;
                        if (s.equalsIgnoreCase("OK")){                  type = Type.OK;
                        }else if(s.equalsIgnoreCase("AccountExist")){   type = Type.ERROR_REG_ACCOUNT;
                        }else if(s.equalsIgnoreCase("EmailExist")){     type = Type.ERROR_REG_EMAIL;
                        }else if(s.equalsIgnoreCase("AccountFormat")){  type = Type.ERROR_REG_FORMAT;
                        }else {                                         type = Type.ERROR_REG_OTHER;}
                        mView.onRegistResult(type);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mView.onError();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","regist finish");
                    }
                });
    }
}
