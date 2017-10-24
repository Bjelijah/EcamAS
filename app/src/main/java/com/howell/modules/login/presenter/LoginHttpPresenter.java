package com.howell.modules.login.presenter;

import android.support.annotation.Nullable;
import android.util.Log;

import com.howell.action.ConfigAction;
import com.howell.bean.Custom;
import com.howell.modules.login.bean.Type;
import com.howell.utils.UserConfigSp;
import com.howellsdk.api.ApiManager;
import com.howellsdk.net.http.bean.ClientCredential;
import com.howellsdk.net.http.bean.Fault;
import com.howellsdk.net.http.bean.TeardownCredential;
import com.howellsdk.net.http.bean.UserNonce;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/10/17.
 */

public class LoginHttpPresenter extends LoginBasePresenter{

    private void login2Server(final String userName, final String userPassword, final ClientCredential req){
        ApiManager.getInstance().getHWHttpService()
                .doUserAuthenticate(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Fault>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Fault fault) {
                        Log.i("123","fault="+fault);
                        ApiManager.HttpHelp.setCookie(req.getUserName(),req.getDomain(),fault.getId(),req.getVerifySession());
                        //mView.onLoginSuccess(req.getUserName(),"");
                        if (fault.getFaultCode().equalsIgnoreCase("0")){
                            mName = userName;
                            mPwd  = userPassword;
                            saveLoginInformation();
                            mView.onLoginSuccess(req.getUserName(),req.getUserName());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("123","http login2server error");
                        e.printStackTrace();
                        mView.onError(Type.ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    public void login(@Nullable final String name, @Nullable final String pwd, @Nullable Custom custom) {
        if (name==null || pwd==null){
            if (mIsFirst){mView.onError(Type.FIRST_LOGIN);return;}
            if (mName==null||mPwd==null){mView.onError(Type.ERROR);return;}

        }else{
            mName = name;
            mPwd = pwd;
        }

        ApiManager.getInstance().initHttpClient(mContext,mIsSSL)
                .getHWHttpService(mUrl)
                .getUserNonce(mName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserNonce>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull UserNonce userNonce) {
                        Log.i("123","userNonce="+userNonce);
                        try {
                            login2Server(mName,mPwd,new ClientCredential(
                                    mName,
                                    mPwd,
                                    userNonce.getDomain(),
                                    userNonce.getNonce()
                            ));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            mView.onError(Type.ERROR);
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                            mView.onError(Type.ERROR);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("123","http login nonce error");
                        e.printStackTrace();
                        mView.onError(Type.ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void logout() {
        ApiManager.getInstance().getHWHttpService()
                .doUserTeardown(new TeardownCredential(mName, ApiManager.HttpHelp.getSession(),null))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Fault>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Fault fault) {
                        Log.i("123","fault="+fault.toString());
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

    @Override
    public void changeUser(String userName, String email) {

    }

    private void save2SP(){
        UserConfigSp.saveUserInfo(mContext,mName,mPwd,true);
        Log.i("123","save sp  name="+mName+"  pwd="+mPwd+"  custom="+true);
        ConfigAction.getInstance(mContext).refresh(mContext);
    }

    @Override
    protected void saveLoginInformation() {
        super.saveLoginInformation();
        save2SP();
    }
}
