package com.howell.modules.player.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.howell.action.ConfigAction;
import com.howell.activity.BasePlayActivity;
import com.howell.activity.PlayViewActivity;
import com.howell.bean.CameraItemBean;
import com.howell.modules.BasePresenter;
import com.howell.modules.ImpBasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.notice.INoticeContract;
import com.howell.modules.player.IPlayContract;
import com.howell.utils.FileUtils;
import com.howell.utils.UserConfigSp;
import com.howellsdk.api.ApiManager;
import com.howellsdk.utils.RxUtil;
import com.howellsdk.utils.ThreadUtil;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by Administrator on 2017/9/20.
 */

public abstract class PlayBasePresenter extends BasePresenter implements IPlayContract.IPresent{
    IPlayContract.IVew mView;
    Context mContext;
    String mAccount;
    CameraItemBean mBean;
    String mLastVODTime = "";
    int mWaiteNum = 0;
    protected static final int F_TIME = 1;//刷新率  s
    @Override
    public void bindView(ImpBaseView view) {
        Log.i("123","play base presenter bindview");
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
        Log.e("123","init  mbean="+mBean);
    }

    @Override
    public void deInit() {
        Log.e("123","deInit  mbean set null");
        mBean = null;
        mContext = null;
    }

    protected void startTimeTask(){

    }

    protected void stopTimeTask(){

    }

    @Override
    public void setSoundMute(boolean setMute) {
        UserConfigSp.saveSoundState(mContext,!setMute);
    }

    @Override
    public void talkFun(boolean bTalking) {

    }

    public IPlayContract.IVew getView() {
        return mView;
    }

    public String getmAccount() {
        return mAccount;
    }

    public CameraItemBean getmBean() {
        return mBean;
    }

    @Override
    public void relink(boolean isSub) {

    }

    @Override
    public void playMoveTo(boolean isSub, long beg, long end) {

    }
}
