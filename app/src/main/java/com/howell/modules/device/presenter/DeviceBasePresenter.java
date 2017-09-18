package com.howell.modules.device.presenter;

import android.content.Context;

import com.howell.action.ConfigAction;
import com.howell.modules.BasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.device.IDeviceContract;
import com.howell.utils.ServerConfigSp;
import com.howell.utils.UserConfigSp;

/**
 * Created by Administrator on 2017/9/15.
 */

public abstract class DeviceBasePresenter extends BasePresenter implements IDeviceContract.IPresenter {
    IDeviceContract.IVew mView;
    Context mContext;
    String mURL;
    boolean mIsTurn;
    @Override
    public void bindView(ImpBaseView view) {
       mView = (IDeviceContract.IVew) view;
    }

    @Override
    public void unbindView() {
        dispose();
        mView = null;
    }

    @Override
    public void init(Context context) {
        mContext = context;
        ConfigAction cf = ConfigAction.getInstance(context);
        mURL = cf.getURL();
        mIsTurn = ServerConfigSp.loadServerIsTurn(context);
    }
}
