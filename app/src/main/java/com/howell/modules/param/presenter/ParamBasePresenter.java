package com.howell.modules.param.presenter;

import android.content.Context;
import android.util.Log;

import com.howell.action.ConfigAction;
import com.howell.bean.CameraItemBean;
import com.howell.modules.BasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.param.IParamContract;
import com.howellsdk.api.ApiManager;
import com.howellsdk.net.soap.bean.CodingParamReq;
import com.howellsdk.net.soap.bean.CodingParamRes;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/9/27.
 */

public abstract class ParamBasePresenter extends BasePresenter implements IParamContract.IPresenter {
    IParamContract.IVew mView;
    CameraItemBean mBean;
    String mAccount;
    Context mContext;
    @Override
    public void bindView(ImpBaseView view) {
        mView = (IParamContract.IVew) view;

    }

    @Override
    public void unbindView() {
        dispose();
        mView = null;
    }


    @Override
    public IParamContract.IPresenter init(Context context, CameraItemBean bean) {
        this.mBean = bean;
        mAccount = ConfigAction.getInstance(context).getName();
        mContext = context;
        return this;
    }

}
