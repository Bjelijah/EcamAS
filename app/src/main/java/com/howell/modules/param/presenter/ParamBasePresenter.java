package com.howell.modules.param.presenter;

import com.howell.bean.CameraItemBean;
import com.howell.modules.BasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.param.IParamContract;

/**
 * Created by Administrator on 2017/9/27.
 */

public abstract class ParamBasePresenter extends BasePresenter implements IParamContract.IPresenter {
    IParamContract.IVew mView;
    CameraItemBean mBean;
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
    public IParamContract.IPresenter init(CameraItemBean bean) {
        this.mBean = bean;
        return this;
    }

}
