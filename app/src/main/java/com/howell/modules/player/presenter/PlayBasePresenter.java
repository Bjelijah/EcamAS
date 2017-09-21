package com.howell.modules.player.presenter;

import android.content.Context;

import com.howell.action.ConfigAction;
import com.howell.bean.CameraItemBean;
import com.howell.modules.BasePresenter;
import com.howell.modules.ImpBasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.notice.INoticeContract;
import com.howell.modules.player.IPlayContract;
import com.howellsdk.utils.ThreadUtil;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/9/20.
 */

public abstract class PlayBasePresenter extends BasePresenter implements IPlayContract.IPresent{
    IPlayContract.IVew mView;
    Context mContext;
    String mAccount;
    CameraItemBean mBean;
    protected static final int F_TIME = 1;//刷新率  s
    @Override
    public void bindView(ImpBaseView view) {
        mView = (IPlayContract.IVew) view;
    }

    @Override
    public void unbindView() {
        dispose();
        mView = null;
    }

    @Override
    public void init(Context context, CameraItemBean bean) {
        mContext = context;
        mAccount = ConfigAction.getInstance(context).getName();
        mBean = bean;
    }

    @Override
    public void deInit() {

    }

    protected void startTimeTask(){

    }

    protected void stopTimeTask(){

    }
}
