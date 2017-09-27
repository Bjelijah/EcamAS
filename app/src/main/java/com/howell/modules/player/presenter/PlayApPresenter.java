package com.howell.modules.player.presenter;

import android.content.Context;

import com.howell.bean.CameraItemBean;
import com.howell.modules.player.IPlayContract;
import com.howell.modules.player.bean.PTZ;
import com.howell.modules.player.bean.VODRecord;
import com.howell.utils.FileUtils;
import com.howellsdk.api.ApiManager;
import com.howellsdk.api.HWPlayApi;
import com.howellsdk.player.ap.bean.ReplayFile;
import com.howellsdk.player.turn.bean.PTZ_CMD;
import com.howellsdk.utils.RxUtil;
import com.howellsdk.utils.ThreadUtil;

import java.io.File;
import java.util.ArrayList;
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
 * Created by Administrator on 2017/9/21.
 */

public class PlayApPresenter extends PlayBasePresenter {
    int mCurPage = 0;
    private String mLastVODTime = "";
    private final int mPageSize = 20;
    @Override
    public void init(Context context, final CameraItemBean bean) {
        super.init(context, bean);

        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                boolean ret =  ApiManager.getInstance()
                        .getAPcamService(
                                bean.getUpnpIP(),
                                bean.getChannelNo(),
                                bean.getMethodType(),
                                new HWPlayApi.IAPCamCB() {
                                    @Override
                                    public void onRecordFileList(final ArrayList<ReplayFile> files) {

                                        Observable.create(new ObservableOnSubscribe<ArrayList<VODRecord>>() {

                                            @Override
                                            public void subscribe(@NonNull ObservableEmitter<ArrayList<VODRecord>> e) throws Exception {
                                                ArrayList<VODRecord> vods = new ArrayList<VODRecord>();
                                                for(ReplayFile f:files){
                                                    StringBuffer begSb = new StringBuffer()
                                                            .append(f.getBegYear()).append("-")
                                                            .append(f.getBegMonth()).append("-")
                                                            .append(f.getBegDay()).append(" ")
                                                            .append(f.getBegHour()).append(":")
                                                            .append(f.getBegMin()).append(":")
                                                            .append(f.getBegSec());
                                                    StringBuffer endSb = new StringBuffer()
                                                            .append(f.getEndYear()).append("-")
                                                            .append(f.getEndMonth()).append("-")
                                                            .append(f.getEndDay()).append(" ")
                                                            .append(f.getEndHour()).append(":")
                                                            .append(f.getEndMin()).append(":")
                                                            .append(f.getEndSec());
                                                    String beg = begSb.toString();
                                                    String end = endSb.toString();

                                                    vods.add(new VODRecord(
                                                            beg,
                                                            end,
                                                            0,
                                                            "",
                                                            mLastVODTime.equals(f.getBegDay()+"")?false:true));
                                                    mLastVODTime = f.getBegDay()+"";
                                                    e.onNext(vods);
                                                }
                                            }
                                        })
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Observer<ArrayList<VODRecord>>() {
                                                    @Override
                                                    public void onSubscribe(@NonNull Disposable d) {
                                                        addDisposable(d);
                                                    }

                                                    @Override
                                                    public void onNext(@NonNull ArrayList<VODRecord> vodRecords) {
                                                        mView.onRecord(vodRecords);
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
                                }).bindCam().connect();
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
                        e.printStackTrace();
                        mView.onError(0);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void deInit() {
        super.deInit();
        ApiManager.getInstance().getAPcamService().disconnect();
        ApiManager.getInstance().getAPcamService().unBindCam();
    }

    @Override
    public void play(final boolean isSub) {
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<Object>() {
            @Override
            public void doTask() {
                ApiManager.getInstance().getAPcamService()
                        .play(isSub);
            }
        });

    }

    @Override
    public void playback(final boolean isSub, final String beg, final String end) {
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<Object>() {
            @Override
            public void doTask() {
                ApiManager.getInstance().getAPcamService()
                        .playback(isSub,beg,end);
            }
        });
    }

    @Override
    public void stop() {
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<Object>() {
            @Override
            public void doTask() {
                ApiManager.getInstance().getAPcamService()
                        .stop();
            }
        });
    }

    @Override
    public boolean pause() {
        return ApiManager.getInstance().getAPcamService().playPause();
    }

    @Override
    public void playMoveTo(final boolean isSub, final String beg, final String end) {
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<Object>() {
            @Override
            public void doTask() {
                ApiManager.getInstance().getAPcamService()
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
                ApiManager.getInstance().getAPcamService()
                        .ptzControl(getT(),15,null);
            }
        });
    }

    @Override
    public IPlayContract.IPresent vodReset() {
        mCurPage = 0;
        return this;
    }

    @Override
    public void getVODRecord(final boolean isSub, final String beg, final String end) {
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<Object>() {
            @Override
            public void doTask() {
                ApiManager.getInstance().getAPcamService().getRecordedFiles(beg,end,mCurPage,mPageSize);
            }
        });

    }

    @Override
    protected void startTimeTask() {
        super.startTimeTask();
        ThreadUtil.scheduledSingleThreadStart(new Runnable() {
            @Override
            public void run() {
                boolean bWait = true;
                int streamLen = ApiManager.getInstance().getAPcamService().getStreamLen();
                if (streamLen!=0){
                    bWait = false;
                    mWaiteNum = 0;
                }else{
                    mWaiteNum++;
                    if (mWaiteNum==3){
                        bWait = true;
                    }
                }
                int speed = streamLen*8/1024/F_TIME;
                long timestamp = ApiManager.getInstance().getAPcamService().getTimestamp();
                long firstTimestamp = ApiManager.getInstance().getAPcamService().getFirstTimestamp();
                mView.onTime(speed,timestamp,firstTimestamp,bWait);
            }
        },0,F_TIME, TimeUnit.SECONDS);
    }

    @Override
    protected void stopTimeTask() {
        super.stopTimeTask();
        ThreadUtil.scheduledThreadShutDown();
    }

    @Override
    public void catchPic() {
        File destDir = new File("/sdcard/eCamera");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        final String nameDirPath = "/sdcard/eCamera/"+ FileUtils.getFileName()+".jpg";
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<String>(nameDirPath) {
            @Override
            public void doTask() {
                ApiManager.getInstance().getAPcamService().catchPic(getT());
            }
        });
    }

    @Override
    public void catchPic(final String path) {
        File destDir = new File(path);
        if (!destDir.exists()){
            destDir.mkdirs();
        }
        String nameDirPath = path+"/"+mBean.getDeviceId()+".jpg";
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<String>(nameDirPath) {
            @Override
            public void doTask() {
                ApiManager.getInstance().getAPcamService().catchPic(getT());
            }
        });
    }
}
