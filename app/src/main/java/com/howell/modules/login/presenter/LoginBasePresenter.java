package com.howell.modules.login.presenter;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.howell.action.ConfigAction;
import com.howell.bean.Custom;
import com.howell.modules.BasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.login.ILoginContract;

/**
 * Created by Administrator on 2017/8/15.
 * do something soap
 */
public abstract class LoginBasePresenter extends BasePresenter implements ILoginContract.IPresenter{
    protected ILoginContract.IView mView;
    protected Context mContext;
    protected String mName;
    protected String mPwd;
    protected String mUrl;
    protected boolean mIsSSL;
    protected boolean mIsFirst;
    @Override
    public void bindView(ImpBaseView view) {
        mView = (ILoginContract.IView) view;
    }

    @Override
    public void unbindView() {
        dispose();
        mView = null;
    }


    @Override
    public void init(Context context) {
        mContext = context;
        ConfigAction config = ConfigAction.getInstance(context);
        mUrl = config.getURL();
        mName = config.getName();
        mPwd = config.getPassword();
        mIsSSL = config.isSSL();
        mIsFirst = config.isFirst();

        Log.i("123",toString());
    }




    protected void saveLoginInformation(){}

    @Override
    public String toString() {
        return "LoginBasePresenter{" +
                "mName='" + mName + '\'' +
                ", mPwd='" + mPwd + '\'' +
                ", mUrl='" + mUrl + '\'' +
                ", mIsSSL=" + mIsSSL +
                ", mIsFirst=" + mIsFirst +
                '}';
    }
}
