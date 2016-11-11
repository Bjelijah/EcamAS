package com.howell.action;

import android.os.AsyncTask;

import com.howell.protocol.LoginRequest;
import com.howell.protocol.LoginResponse;
import com.howell.protocol.SoapManager;
import com.howell.utils.DecodeUtils;

/**
 * Created by howell on 2016/11/9.
 */

public class LoginAction {
    public static final int ERROR_LOGIN_ACCOUNT = 0xe3;
    public static final int ERROR_LOGIN_PWD = 0xe1;
    public static final int ERROR_LOGIN_OTHER = 0xe2;
    public static final int ERROR_LINK_ERROR = 0xe0;
    private static LoginAction mInstance = null;
    public static LoginAction getInstance(){
        if (mInstance == null){
            mInstance = new LoginAction();
        }
        return mInstance;
    }
    private SoapManager mSoapManager = SoapManager.getInstance();
    private IloginRes mCallback;
    public LoginAction regLoginResCallback(IloginRes i){
        mCallback = i;
        return this;
    }
    public void unRegLoginResCallback(){
        mCallback = null;
    }


    private LoginAction(){}

    public void Login(final String account,final String password){
        new AsyncTask<Void,Void,Boolean>(){
            int mError;

            private boolean checkRes(LoginResponse res){
                if (res==null)return false;
                String str = res.getResult().toString();
                if (str.equalsIgnoreCase("OK")){
                    return true;
                }else if(str.equalsIgnoreCase("AccountNotExist")){
                    mError = ERROR_LOGIN_ACCOUNT;
                    return false;
                }else if(str.equalsIgnoreCase("Authencation")){
                    mError = ERROR_LOGIN_PWD;
                    return false;
                }else{
                    mError = ERROR_LOGIN_OTHER;
                    return false;
                }
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                LoginRequest loginReq = null;
                LoginResponse loginRes = null;
                try{
                    String encodedPassword = DecodeUtils.getEncodedPassword(password);
                    loginReq = new LoginRequest(account, "Common",encodedPassword, "1.0.0.1");
                    loginRes = mSoapManager.getUserLoginRes(loginReq);
                }catch (Exception e){
                    mError = ERROR_LINK_ERROR;
                    return false;
                }
                return checkRes(loginRes);
            }
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (mCallback==null)return;
                if (aBoolean){
                    mCallback.onLoginSuccess();
                }else{
                    mCallback.onLoginError(mError);
                }
            }
        }.execute();
    }


    public  interface IloginRes{
        void onLoginSuccess();
        void onLoginError(int e);
    }



}
