package com.howell.modules.Login.presenter;

import android.support.annotation.Nullable;
import android.util.Log;

import com.howell.action.ConfigAction;
import com.howell.bean.Custom;
import com.howell.bean.UserLoginDBBean;
import com.howell.db.UserLoginDao;
import com.howell.modules.Login.bean.Type;
import com.howell.utils.IConst;
import com.howell.utils.UserConfigSp;
import com.howellsdk.api.ApiManager;
import com.howellsdk.net.soap.bean.AccountRes;
import com.howellsdk.net.soap.bean.AndroidTokenReq;
import com.howellsdk.net.soap.bean.LoginRequest;
import com.howellsdk.net.soap.bean.LoginResponse;
import com.howellsdk.net.soap.bean.LogoutRequest;
import com.howellsdk.net.soap.bean.Request;
import com.howellsdk.net.soap.bean.Result;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;



/**
 * Created by Administrator on 2017/9/14.
 */

public class LoginSoapPresenter extends LoginBasePresenter implements IConst {

    String email;
    Custom mCustom;
    @Override
    public void login(@Nullable String name,@Nullable String pwd,@Nullable Custom custom) {
        Log.i("123","login ");
        if (name==null||pwd==null){
            if (mIsFirst) {Log.e("123","first return");mView.onLoginResult(Type.FIRST_LOGIN);return;}
            if (mName==null || mPwd==null) { Log.e("123","mName = null error");mView.onError();return;}
            if (mName.equals(GUEST_NAME)){ Log.e("123","guest do not login auto  name="+mName);mView.onError();return;}
            name = mName;
            pwd = mPwd;
        }else{
            mName = name;
            mPwd = pwd;
        }
        this.mCustom = custom;
        if (custom==null){
            //getFromDB
            this.mCustom = loadCustomFromDB(name,pwd);

        }
        if (this.mCustom==null){
            Log.e("123","custom = null");
            mView.onError();
            return;
        }
        Log.i("123","custom="+this.mCustom.toString());

        LoginRequest req = new LoginRequest(mName,mPwd);
        ApiManager.getInstance()
                .initSoapClient(mContext,mIsSSL)
                .getSoapService(this.mCustom.getURL())
                .userLogin(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<LoginResponse, String>() {

                    @Override
                    public String apply(@NonNull LoginResponse loginResponse) throws Exception {
                        ApiManager.SoapHelp.setsSession(loginResponse.getLoginSession());
                        return loginResponse.getResult();
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        Log.i("123","login result s="+s);
                        Type type;
                        if (s.equalsIgnoreCase("ok")){
                            type = Type.OK;
                            //get account res
                            getAccountInfo();
                            androidToken();
                        }else if(s.equalsIgnoreCase("AccountNotExist")){
                            type = Type.ACCOUNT_NOT_EXIST;
                        }else if(s.equalsIgnoreCase("Authencation")){
                            type = Type.AUTHENCATION;
                        }else{
                            type = Type.ERROR;
                        }
                        mView.onLoginResult(type);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onError();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","login finish");
                    }
                });

    }


    @Override
    public void logout() {
        LogoutRequest req = new LogoutRequest(mName,ApiManager.SoapHelp.getsSession());
        ApiManager.getInstance()
                .getSoapService(mUrl)
                .userLogout(req)
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
                       mView.onLogoutResult(s.equalsIgnoreCase("ok")?Type.OK:Type.ERROR);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","on logout finish");
                    }
                });

    }


    @Override
    protected void saveLoginInformation() {
        //save to db;
        save2DB(mName,mPwd,email,mCustom);
        //save to sp;
        save2SP();
    }




    private void getAccountInfo(){
        ApiManager.getInstance()
                .getSoapService()
                .getAccount(new Request(mName,ApiManager.SoapHelp.getsSession(),null,0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AccountRes>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull AccountRes accountRes) {
                        //save info
                        email = accountRes.getEmail();
                        saveLoginInformation();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","get account info finish");
                    }
                });



    }

    private void androidToken(){
//        ApiManager.getInstance()
//                .getSoapService()
//                .queryAndroidToken(new AndroidTokenReq(mName,ApiManager.SoapHelp.getsSession(),))
    }


    private Custom loadCustomFromDB(String userName,String password){
        Custom c = null;
        UserLoginDao dao = new UserLoginDao(mContext, "user.db", 1);
        if(dao.findByName(userName)){
            List<UserLoginDBBean> list = dao.queryByName(userName);
            for(UserLoginDBBean b:list){
                if (b.getUserPassword().equals(password)){
                    c = b.getC();
                    break;
                }
            }
        }
        return c;
    }

    private void save2DB(String account, String password, String email, Custom c){
        UserLoginDao dao = new UserLoginDao(mContext, "user.db", 1);
        if(dao.findByNameAndIP(account,c.getCustomIP())){
            List<UserLoginDBBean> list = dao.queryByNameAndIP(account,c.getCustomIP());
            for (UserLoginDBBean b : list){
                if (b.getUserNum()==0){
                    dao.close();
                    return;
                }
            }
            dao.insert(new UserLoginDBBean(0,account,password,email, c));
        }else{
            dao.insert(new UserLoginDBBean(0,account,password,email, c));
        }
        dao.close();
    }

    private void save2SP(){
        UserConfigSp.saveUserInfo(mContext,mName,mPwd,mCustom.isCustom());
        Log.i("123","save sp  name="+mName+"  pwd="+mPwd+"  custom="+mCustom);
        ConfigAction.getInstance(mContext).refresh(mContext);
    }
}
