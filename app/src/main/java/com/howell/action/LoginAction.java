package com.howell.action;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.howell.bean.Custom;
import com.howell.bean.UserLoginDBBean;
import com.howell.db.UserLoginDao;
import com.howell.protocol.AccountRequest;
import com.howell.protocol.AccountResponse;
import com.howell.protocol.LoginRequest;
import com.howell.protocol.LoginResponse;
import com.howell.protocol.SoapManager;
import com.howell.utils.DecodeUtils;
import com.howell.utils.ServerConfigSp;
import com.howell.utils.UserConfigSp;

import java.util.List;

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
    private Context mContext;
    public LoginAction setContext(Context c){
        this.mContext = c;
        return this;
    }
    private UserInfo mInfo = new UserInfo();
    public UserInfo getmInfo() {
        return mInfo;
    }
    private boolean mIsGuest = true;

    public boolean ismIsGuest() {
        return mIsGuest;
    }

    public LoginAction setmIsGuest(boolean mIsGuest) {
        this.mIsGuest = mIsGuest;
        return this;
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

    public void Login(final String account, final String password, final Custom custom){
        if (custom==null){
            if(mCallback!=null)mCallback.onLoginError(ERROR_LOGIN_OTHER);
            return;
        }
        mInfo.setAccount(account).setPassword(password);
        setmIsGuest(account.equals("100868"));

        Log.i("123","account="+account+"  password="+password+"  custom="+custom.isCustom()+
        "  ip="+custom.getCustomIP()+"  port="+custom.getCustomPort()+"   ssl="+custom.isSSL());


        SoapManager.initUrl(mContext,custom.isCustom(),custom.getCustomIP(),custom.getCustomPort(),custom.isSSL());



        new AsyncTask<Void,Void,Boolean>(){
            int mError;
            LoginResponse mLoginRes = null;
            AccountResponse mAccountResponse = null;
            private boolean checkLoginRes(LoginResponse res){
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
            private boolean checkAccountRes(AccountResponse res){
                if (res==null)return false;
                String str = res.getResult().toString();
                if (str.equalsIgnoreCase("OK")){
                    return true;
                }else{
                    return false;
                }
            }
            private boolean login(String account,String password){
                String encodedPassword = DecodeUtils.getEncodedPassword(password);
                LoginRequest loginReq = new LoginRequest(account, "Common",encodedPassword, "1.0.0.1");
                String imei = ((TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                mInfo.setImei(imei);
                loginReq.setmIMEI(imei);
                try {
                    mLoginRes = mSoapManager.getUserLoginRes(loginReq);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
            private boolean account(String account,String session){
                AccountRequest r = new AccountRequest(account,session);
                try {
                    mAccountResponse = mSoapManager.getAccountRes(r);
                } catch (Exception e) {
                    return false;
                }
                return true;
            }
            @Override
            protected Boolean doInBackground(Void... voids) {
                LoginRequest loginReq = null;
                if (!login(account,password)){
                    mError = ERROR_LINK_ERROR;
                    return false;
                }
                mInfo.setCustom(custom);

                if (!checkLoginRes(mLoginRes)){
                    return false;
                }
                mInfo.setLr(mLoginRes);

                if(!account(account,mLoginRes.getLoginSession())){
                    return false;
                }
                if(checkAccountRes(mAccountResponse)){
                    mInfo.setAr(mAccountResponse);
                    saveLogin2DB();
                }
                return true;
            }
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (mCallback==null)return;
                if (aBoolean){
                    saveLogin2Sp();
                    mCallback.onLoginSuccess();
                }else{
                    mCallback.onLoginError(mError);
                }
            }
        }.execute();
    }

    private void saveLogin2DB(){
        UserLoginDao dao = new UserLoginDao(mContext, "user.db", 1);
        if(dao.findByNameAndIP(mInfo.getAccount(),mInfo.getCustom().getCustomIP())){
            List<UserLoginDBBean> list = dao.queryByNameAndIP(mInfo.getAccount(),mInfo.getCustom().getCustomIP());
            for (UserLoginDBBean b : list){
                if (b.getUserNum()==0){
                    dao.close();
                    return;
                }
            }
            dao.insert(new UserLoginDBBean(0,mInfo.getAccount(),mInfo.getPassword(),mInfo.getAr().getEmail(), mInfo.getCustom()));
        }else{
            dao.insert(new UserLoginDBBean(0,mInfo.getAccount(),mInfo.getPassword(),mInfo.getAr().getEmail(), mInfo.getCustom()));
        }
        dao.close();
    }

    private void saveLogin2Sp(){
        UserConfigSp.saveUserInfo(mContext,mInfo.getAccount(),mInfo.getPassword(),mInfo.getCustom().isCustom());
        if (mInfo.getCustom().isCustom()) {
            ServerConfigSp.saveServerInfo(mContext, mInfo.getCustom().getCustomIP(), mInfo.getCustom().getCustomPort(),
                    mInfo.getCustom().isSSL());
        }else{
            ServerConfigSp.saveServerInfo(mContext,"www.haoweis.com",8800,false);
        }
    }


    public  interface IloginRes{
        void onLoginSuccess();
        void onLoginError(int e);
    }

    /**
     * login info
     */
    public class UserInfo{
        private String account;
        private String password;
        private LoginResponse lr;
        private AccountResponse ar;
        private Custom custom;
        private String imei;

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public Custom getCustom() {
            return custom;
        }

        public void setCustom(Custom custom) {
            this.custom = custom;
        }

        public LoginResponse getLr() {
            return lr;
        }
        public UserInfo setLr(LoginResponse lr) {
            this.lr = lr;
            return this;
        }
        public String getAccount() {
            return account;
        }
        public UserInfo setAccount(String account) {
            this.account = account;
            return this;
        }
        public String getPassword() {
            return password;
        }
        public UserInfo setPassword(String password) {
            this.password = password;
            return this;
        }

        public AccountResponse getAr() {
            return ar;
        }

        public void setAr(AccountResponse ar) {
            this.ar = ar;
        }
    }




}
