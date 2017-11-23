package com.howell.modules.pdc.presenter;

import android.content.Context;

import com.howell.action.ConfigAction;
import com.howell.modules.BasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.pdc.IPDCContract;

/**
 * Created by Administrator on 2017/11/23.
 */

public abstract class PDCBasePresenter extends BasePresenter implements IPDCContract.IPresent {

    IPDCContract.IVew mView;
    Context mContext;
    String mUrl;
    @Override
    public void bindView(ImpBaseView view) {
        mView = (IPDCContract.IVew) view;
    }

    @Override
    public void unbindView() {
        dispose();
        mView = null;
    }

    @Override
    public void init(Context context) {
        mContext = context;
        ConfigAction cf = ConfigAction.getInstance(mContext);
        mUrl = cf.getURL();
    }
}
