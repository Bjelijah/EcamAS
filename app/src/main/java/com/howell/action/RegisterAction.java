package com.howell.action;

import android.os.AsyncTask;

import com.howell.protocol.CreateAccountReq;
import com.howell.protocol.CreateAccountRes;
import com.howell.protocol.SoapManager;
import com.howell.utils.DecodeUtils;

/**
 * Created by howell on 2016/11/10.
 */

public class RegisterAction {
    public static final int ERROR_LINK_ERROR = 0xe0;
    public static final int ERROR_REG_ACCOUNT = 0xe1;
    public static final int ERROR_REG_EMAIL = 0xe2;
    public static final int ERROR_REG_FORMAT = 0xe3;
    public static final int ERROR_REG_OTHER = 0xe4;
    private static RegisterAction mInstance = null;
    public static RegisterAction getInstance(){
        if (mInstance == null){
            mInstance = new RegisterAction();
        }
        return mInstance;
    }
    private SoapManager mSoapManager = SoapManager.getInstance();
    private RegisterAction(){}

    private IRegisterRes mCallback;
    public RegisterAction registerCallback(IRegisterRes i){
        mCallback = i;
        return this;
    }

    public RegisterAction unRegisterCallback(){
        mCallback = null;
        return this;
    }

    public void register(final String userName,final String password,final String email){
        new AsyncTask<Void,Void,Boolean>(){
            int mError;

            private boolean checkRes(CreateAccountRes res){
                if (res==null)return false;
                if (res.getResult().equalsIgnoreCase("OK")){
                    return true;
                }else if(res.getResult().equalsIgnoreCase("AccountExist")){
                    mError = ERROR_REG_ACCOUNT;
                    return false;
                }else if(res.getResult().equalsIgnoreCase("EmailExist")){
                    mError = ERROR_REG_EMAIL;
                    return false;
                }else if(res.getResult().equalsIgnoreCase("AccountFormat")){
                    mError = ERROR_REG_FORMAT;
                    return false;
                }else {
                    mError = ERROR_REG_OTHER;
                    return false;
                }
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                String encodedPassword = DecodeUtils.getEncodedPassword(password);
                CreateAccountReq req = new CreateAccountReq(userName,encodedPassword,email);
                CreateAccountRes res = null;
                try{
                    res = mSoapManager.getCreateAccountRes(req);
                }catch (Exception e){
                    mError = ERROR_LINK_ERROR;
                    return false;
                }
                return checkRes(res);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (mCallback==null)return;
                if (aBoolean){
                    mCallback.registerSuccess();
                }else{
                    mCallback.registerError(mError);
                }

            }
        }.execute();
    }


    public interface IRegisterRes{
        void registerSuccess();
        void registerError(int e);
    }
}
