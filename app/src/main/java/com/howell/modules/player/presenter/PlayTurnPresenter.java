package com.howell.modules.player.presenter;

import android.content.Context;
import android.util.Log;

import com.howell.action.ConfigAction;
import com.howell.bean.CameraItemBean;
import com.howell.modules.player.IPlayContract;
import com.howell.modules.player.bean.PTZ;
import com.howell.modules.player.bean.VODRecord;
import com.howell.utils.FileUtils;
import com.howell.utils.Util;
import com.howellsdk.api.ApiManager;
import com.howellsdk.api.HWPlayApi;
import com.howellsdk.player.turn.bean.PTZ_CMD;
import com.howellsdk.player.turn.bean.TurnGetRecordedFileAckBean;
import com.howellsdk.player.turn.bean.TurnSubScribeAckBean;
import com.howellsdk.utils.RxUtil;
import com.howellsdk.utils.ThreadUtil;

import java.io.File;
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
    private long firstTimeStamp = 0;

    @Override
    public void init(final Context context, final CameraItemBean bean) {
        super.init(context, bean);
        Log.i("123","~~~~playTurnPresenter init   bean device id="+bean.getDeviceId()+"  channel="+bean.getChannelNo());
        Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                final boolean ret =         ApiManager.getInstance()
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
                                        if (mView==null){
                                            Log.e("123","onDisconnectUnexpect flag="+flag+"  but mView=null we return") ;
                                            return;
                                        }
                                        switch (flag){
                                            case 3:
                                                Log.i("123","mView onError");
                                                mView.onError(0);
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onRecordFileList(TurnGetRecordedFileAckBean fileList) {
                                        mCurPage++;
                                        if (fileList.getRecordFileCount()>0) {
                                            ArrayList<TurnGetRecordedFileAckBean.RecordedFile> lists= fileList.getRecordedFiles();
//                                            for(TurnGetRecordedFileAckBean.RecordedFile rf:lists){
//                                                Log.i("123","rf="+rf);
//                                            }



                                            List<VODRecord> vods = new ArrayList<VODRecord>();
                                            for (int i=0;i<lists.size();i++){
                                                boolean hasTitle = false;



                                                String beg = Util.ISODateString2Date(lists.get(i).getBeginTime());
                                                String end = Util.ISODateString2Date(lists.get(i).getEndTime());

                                                if (!mLastVODTime.equals(beg.substring(0,10))){
                                                    mLastVODTime = beg.substring(0,10);
                                                    hasTitle = true;
                                                }
//                                                Log.i("123","begTIme="+lists.get(i).getBeginTime()+"  beg="+beg+"  title="+hasTitle);
                                                vods.add(new VODRecord(
                                                        beg,
                                                        end,
                                                        lists.get(i).getBeginTime(),
                                                        lists.get(i).getEndTime(),
                                                        0,
                                                        "",
                                                        hasTitle));
                                            }
//                                            Log.i("123","mView="+mView);
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
        final String isoBeg = com.howellsdk.utils.Util.DateString2ISODateString(beg);
        final String isoEnd = com.howellsdk.utils.Util.DateString2ISODateString(end);
        firstTimeStamp = 0;
        Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                ApiManager.getInstance()
                        .getTurnService()
                        .playback(isSub,isoBeg,isoEnd);
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
                        Log.i("123","beg="+beg+" end="+end);//ms
                        long begTime = com.howellsdk.utils.Util.ISODateString2ISODate(isoBeg).getTime()/1000;
                        long endTime = com.howellsdk.utils.Util.ISODateString2ISODate(isoEnd).getTime()/1000;
                        mView.onPlaybackStartEndTime(begTime,endTime);//need s
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
        final String isoBeg = com.howellsdk.utils.Util.DateString2ISODateString(beg);
        final String isoEnd = com.howellsdk.utils.Util.DateString2ISODateString(end);
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<Object>() {
            @Override
            public void doTask() {
                ApiManager.getInstance()
                        .getTurnService()
                        .reLink(isSub,isoBeg,isoEnd);
            }
        });

    }

    @Override
    public void relink(final boolean isSub) {
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<Object>() {
            @Override
            public void doTask() {
                ApiManager.getInstance().getTurnService()
                        .reLink(isSub,null,null);
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
        final String isoBeg = com.howellsdk.utils.Util.DateString2ISODateString(beg);
        final String isoEnd = com.howellsdk.utils.Util.DateString2ISODateString(end);
        Log.i("123","getVodRecord  beg="+beg+"  end="+end);
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<Object>() {
            @Override
            public void doTask() {
                Log.i("123","getVodRecord");
                ApiManager.getInstance().getTurnService()
                        .getRecordedFiles(isoBeg,isoEnd,mCurPage,mPageSize);
            }
        });

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
                ApiManager.getInstance().getTurnService().catchPic(getT());
            }
        });
    }

    @Override
    public void catchPic(String path) {
        File destDir = new File(path);
        if (!destDir.exists()){
            destDir.mkdirs();
        }
        String nameDirPath = path+"/"+mBean.getDeviceId()+".jpg";
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<String>(nameDirPath) {
            @Override
            public void doTask() {
                ApiManager.getInstance().getTurnService().catchPic(getT());
            }
        });
    }

    @Override
    public void holdServer() {
        ApiManager.PlayHelp.keepApi(ApiManager.getInstance().getTurnService());
    }

    @Override
    public void resumeServer() {
        ApiManager.getInstance().setTurnService(ApiManager.PlayHelp.getAPi());
    }

    @Override
    protected void startTimeTask() {
        super.startTimeTask();
        ThreadUtil.scheduledSingleThreadStart(new Runnable() {
            @Override
            public void run() {
                boolean bWait = true;
                int streamLen = ApiManager.getInstance().getTurnService().getStreamLen();
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
                long timestamp = ApiManager.getInstance().getTurnService().getTimestamp();
                long firstTimestamp = ApiManager.getInstance().getTurnService().getFirstTimestamp();

//                Log.i("123","timestamp:"+timestamp+"  FIRST="+firstTimestamp);
                if (firstTimeStamp==0 && firstTimestamp!=0){
                    firstTimeStamp = firstTimestamp;
                }
//                Log.i("123","timestamp:"+timestamp+"  FIRST="+firstTimestamp+"   mF="+firstTimeStamp);
                if(firstTimestamp!=0) {//第一针还没来
                    mView.onTime(speed, timestamp, firstTimeStamp, bWait);
                }
            }
        },0,F_TIME, TimeUnit.SECONDS);
    }

    @Override
    protected void stopTimeTask() {
        super.stopTimeTask();
        ThreadUtil.scheduledSingleThreadShutDown();
    }


}
