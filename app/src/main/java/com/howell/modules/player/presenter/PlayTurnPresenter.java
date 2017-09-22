package com.howell.modules.player.presenter;

import android.content.Context;
import android.util.Log;

import com.howell.action.ConfigAction;
import com.howell.bean.CameraItemBean;
import com.howell.modules.player.IPlayContract;
import com.howell.modules.player.bean.PTZ;
import com.howell.modules.player.bean.VODRecord;
import com.howellsdk.api.ApiManager;
import com.howellsdk.api.HWPlayApi;
import com.howellsdk.player.turn.bean.PTZ_CMD;
import com.howellsdk.player.turn.bean.TurnGetRecordedFileAckBean;
import com.howellsdk.player.turn.bean.TurnSubScribeAckBean;
import com.howellsdk.utils.RxUtil;
import com.howellsdk.utils.ThreadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/9/20.
 */

public class PlayTurnPresenter extends PlayBasePresenter {
    private int mCurPage;
    private final int mPageSize = 20;


    @Override
    public void init(final Context context, final CameraItemBean bean) {
        super.init(context, bean);
        Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                boolean ret =         ApiManager.getInstance()
                        .getTurnService(
                                context,
                                bean.getUpnpIP(),
                                bean.getUpnpPort(),
                                bean.getDeviceId(),
                                bean.getChannelNo(),
                                true,
                                mAccount,
                                ConfigAction.getInstance(context).getPassword(),
                                ConfigAction.getInstance(context).isSSL(),
                                ConfigAction.getInstance(context).getImei(),
                                new HWPlayApi.ITurnCB() {
                                    @Override
                                    public void onConnect(String sessionId) {
                                        Log.i("123","turn onConnect sessionID="+sessionId);
                                    }

                                    @Override
                                    public void onDisconnect() {
                                        Log.i("123","turn ondisconnect");
                                    }

                                    @Override
                                    public void onDisconnectUnexpect(int flag) {
                                        Log.i("123","on disconnect unexpect flag="+flag);
                                    }

                                    @Override
                                    public void onRecordFileList(TurnGetRecordedFileAckBean fileList) {
                                        mCurPage++;
                                        if (fileList.getRecordFileCount()>0) {
                                            ArrayList<TurnGetRecordedFileAckBean.RecordedFile> lists= fileList.getRecordedFiles();
                                            List<VODRecord> vods = new ArrayList<VODRecord>();
                                            for (int i=0;i<lists.size();i++){
                                                boolean hasTitle = false;
                                                if (!mLastVODTime.equals(lists.get(i).getBeginTime().substring(0,10))){
                                                    mLastVODTime = lists.get(i).getBeginTime().substring(0,10);
                                                    hasTitle = true;
                                                }
                                                vods.add(new VODRecord(
                                                        lists.get(i).getBeginTime(),
                                                        lists.get(i).getEndTime(),
                                                        0,
                                                        "",
                                                        hasTitle));
                                            }
                                            mView.onRecord(vods);
                                        }
                                    }

                                    @Override
                                    public void onSubscribe(TurnSubScribeAckBean res) {
                                        Log.i("123","turn on subscribe res="+res.toString());
                                    }

                                    @Override
                                    public void onUnsubscribe() {
                                        Log.i("123","turn on unsubscribe");
                                    }
                                })
                        .bindCam().connect();
                e.onNext(ret);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        mView.onConnect(aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mView.onError(0);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","turn connect ok");
                    }
                });

    }

    @Override
    public void deInit() {
        super.deInit();
        ApiManager.getInstance().getTurnService().disconnect();
        ApiManager.getInstance().getTurnService().unBindCam();
    }

    @Override
    public void play(final boolean isSub) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                ApiManager.getInstance()
                        .getTurnService()
                        .play(isSub);
                e.onNext(true);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        startTimeTask();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onError(0);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void playback(final boolean isSub, final String beg, final String end) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                ApiManager.getInstance()
                        .getTurnService()
                        .playback(isSub,beg,end);
                e.onNext(true);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        startTimeTask();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onError(0);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void stop() {
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<Object>() {
            @Override
            public void doTask() {
                ApiManager.getInstance()
                        .getTurnService()
                        .stop();
                stopTimeTask();
            }
        });

    }

    @Override
    public boolean pause() {
        return ApiManager.getInstance()
                .getTurnService()
                .playPause();
    }

    @Override
    public void playMoveTo(final boolean isSub, final String beg, final String end) {
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<Object>() {
            @Override
            public void doTask() {
                ApiManager.getInstance()
                        .getTurnService()
                        .reLink(isSub,beg,end);
            }
        });

    }

    @Override
    public void ptzCtrl(PTZ cmd) {
        PTZ_CMD turnCMD;
        switch (cmd){
            case PTZ_UP:
                turnCMD = PTZ_CMD.ptz_up;
                break;
            case PTZ_DOWN:
                turnCMD = PTZ_CMD.ptz_down;
                break;
            case PTZ_LEFT:
                turnCMD = PTZ_CMD.ptz_left;
                break;
            case PTZ_RIGHT:
                turnCMD = PTZ_CMD.ptz_right;
                break;
            case PTZ_STOP:
                turnCMD = PTZ_CMD.ptz_stop;
                break;
            case PTZ_ZOOM_WIDE:
                turnCMD = PTZ_CMD.ptz_zoomWide;
                break;
            case PTZ_ZOOM_TELE:
                turnCMD = PTZ_CMD.ptz_zoomTele;
                break;
            case PTZ_ZOOM_STOP:
                turnCMD = PTZ_CMD.ptz_zoomStop;
                break;
            default:
                turnCMD = PTZ_CMD.ptz_null;
                break;
        }
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<PTZ_CMD>(turnCMD) {
            @Override
            public void doTask() {
                ApiManager.getInstance().getTurnService()
                        .ptzControl(getT(),15,null);
            }
        });

    }

    @Override
    public IPlayContract.IPresent vodReset() {
        mCurPage = 1;
        return this;
    }

    @Override
    public void getVODRecord(boolean isSub, final String beg, final String end) {
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<Object>() {
            @Override
            public void doTask() {
                ApiManager.getInstance().getTurnService()
                        .getRecordedFiles(beg,end,mCurPage,mPageSize);
            }
        });

    }

    @Override
    protected void startTimeTask() {
        super.startTimeTask();
        ThreadUtil.scheduledSingleThreadStart(new Runnable() {
            @Override
            public void run() {
                int streamLen = ApiManager.getInstance().getTurnService().getStreamLen();
                int speed = streamLen*8/1024/F_TIME;
                long timestamp = ApiManager.getInstance().getTurnService().getTimestamp();
                long firstTimestamp = ApiManager.getInstance().getTurnService().getFirstTimestamp();
                mView.onTime(speed,timestamp,firstTimestamp);
            }
        },0,F_TIME, TimeUnit.SECONDS);
    }

    @Override
    protected void stopTimeTask() {
        super.stopTimeTask();
        ThreadUtil.scheduledThreadShutDown();
    }
}
