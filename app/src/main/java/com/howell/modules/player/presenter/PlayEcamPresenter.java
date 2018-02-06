package com.howell.modules.player.presenter;

import android.content.Context;
import android.util.Log;

import com.howell.bean.CameraItemBean;

import com.howell.modules.player.IPlayContract;
import com.howell.modules.player.bean.PTZ;
import com.howell.modules.player.bean.VODRecord;


import com.howell.utils.FileUtils;

import com.howellsdk.api.ApiManager;
import com.howellsdk.api.HWPlayApi;
import com.howellsdk.audio.AudioAction;
import com.howellsdk.net.soap.bean.InviteReq;
import com.howellsdk.net.soap.bean.InviteRes;
import com.howellsdk.net.soap.bean.NATServerReq;
import com.howellsdk.net.soap.bean.NATServerRes;
import com.howellsdk.net.soap.bean.PtzControlReq;
import com.howellsdk.net.soap.bean.Result;
import com.howellsdk.net.soap.bean.SetAuxiliaryReq;
import com.howellsdk.net.soap.bean.VodSearchReq;
import com.howellsdk.net.soap.bean.VodSearchRes;
import com.howellsdk.utils.RxUtil;
import com.howellsdk.utils.ThreadUtil;
import com.howellsdk.utils.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Administrator on 2017/9/20.
 */

public class PlayEcamPresenter extends PlayBasePresenter {

    private int mCurPage = 1;
    private final int mPageSize = 20;
    private int mTotalPage;


    private PublishSubject mMission = null;
    private void login(){

    }

    private void stopMission(){
        Log.i("123","stop mission");
        if (mMission!=null){
            mMission.onNext(false);
            mMission=null;
        }
    }

    private void startMission(){
        if (mMission!=null){
            return;
        }
        Log.i("123","start mission");
        mMission = PublishSubject.create();
        mMission.mergeWith(Observable.interval(F_TIME,TimeUnit.SECONDS))
                .takeWhile(new Predicate<Boolean>() {

                    @Override
                    public boolean test(@NonNull Boolean o) throws Exception {
                        return o;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Object o) {
                        Log.i("123","mission on next");
                        int streamLen = ApiManager.getInstance().getEcamService().getStreamLen();
                        int speed = streamLen*8/1024/F_TIME;
//                        mView.onTime(speed);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","mission complete");
                    }
                });
    }




    @Override
    public void init(Context context, final CameraItemBean bean) {
        super.init(context,bean);
        Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                boolean ret = ApiManager.getInstance().getEcamService(mAccount, bean.getUpnpIP(), bean.getUpnpPort(), bean.getMethodType(), new HWPlayApi.IEcamCB() {
                    String remoteSPD;
                    NATServerRes nat;
                    @Override
                    public String getBase64RemoteSDP(boolean isSub, String dilogID, String sdpMessage) {
                        Log.i("123","thisAccount="+mAccount);
                        ApiManager.getInstance().getSoapService()
                                .invite(new InviteReq(
                                        mAccount,
                                        ApiManager.SoapHelp.getsSession(),
                                        bean.getDeviceId(),
                                        bean.getChannelNo(),
                                        isSub?"Sub":"Main",
                                        dilogID,
                                        sdpMessage
                                ))
                                .map(new Function<InviteRes, String>() {
                                    @Override
                                    public String apply(@NonNull InviteRes inviteRes) throws Exception {
                                        return inviteRes.getSdpMessage();
                                    }
                                })
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        remoteSPD = s;
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        throwable.printStackTrace();
                                        remoteSPD = null;
                                    }
                                });
                        return remoteSPD;
                    }

                    @Override
                    public NATServerRes getNATServer() {
                        ApiManager.getInstance().getSoapService()
                                .getNATServer(new NATServerReq(mAccount,ApiManager.SoapHelp.getsSession()))
                                .subscribe(new Consumer<NATServerRes>() {
                                    @Override
                                    public void accept(NATServerRes natServerRes) throws Exception {
                                        nat = natServerRes;
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        throwable.printStackTrace();
                                        nat = null;
                                    }
                                });
                        return nat;
                    }

                    @Override
                    public void onError(int flag) {
                        Log.e("123","cb on error flag="+flag);
                    }

                    @Override
                    public void onPlayBackBegEndTime(final long beg, final long end) {
                        mView.onPlaybackStartEndTime(beg,end);
                        Log.e("123","~~~~~~~~~~~~~~~onPlayBackBegEndTime  ok");
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
                        Log.i("123","onConnect view="+mView.getClass().getName());
                        mView.onConnect(aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mView.onError(0);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","ecam connect finish");
                    }
                });




    }

    @Override
    public void deInit() {
        super.deInit();
        ApiManager.getInstance().getEcamService().disconnect();
        ApiManager.getInstance().getEcamService().unBindCam();
    }

    @Override
    public void play(final boolean isSub) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                ApiManager.getInstance().getEcamService()
                        .play(isSub);
                e.onNext(true);
            }
        })      .subscribeOn(Schedulers.io())
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
                        mView.onError(1);
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
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                ApiManager.getInstance().getEcamService()
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
        ApiManager.getInstance()
                .getEcamService()
                .stop();
        stopTimeTask();


    }

    @Override
    public boolean pause() {
        return ApiManager.getInstance()
                .getEcamService()
                .playPause();
    }

    @Override
    public void playMoveTo(final boolean isSub, final String beg, final String end) {
        final long _beg = com.howellsdk.utils.Util.DateString2Date(beg).getTime();
        final long _end = com.howellsdk.utils.Util.DateString2Date(end).getTime();
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<Object>() {
            @Override
            public void doTask() {
                ApiManager.getInstance()
                        .getEcamService()
                        .playbackReLink(isSub,_beg,_end);
            }
        });
    }

    @Override
    public void playMoveTo(boolean isSub, long beg, long end) {
        super.playMoveTo(isSub, beg, end);
        ApiManager.getInstance().getEcamService().playbackReLink(isSub,beg,end);
    }

    @Override
    public void relink(final boolean isSub) {
        RxUtil.doInIOTthread(new RxUtil.RxSimpleTask<Object>() {
            @Override
            public void doTask() {
                ApiManager.getInstance().getEcamService()
                        .reLink(isSub,null,null);
            }
        });
    }

    @Override
    public void ptzCtrl(PTZ cmd) {
        ApiManager.getInstance()
                .getSoapService()
                .ptzControl(new PtzControlReq(
                        mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        mBean.getDeviceId(),
                        mBean.getChannelNo(),
                        cmd.getVal()
                ))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Result>() {
                    @Override
                    public void accept(Result result) throws Exception {
                        Log.i("123","ptz res="+result.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public IPlayContract.IPresent vodReset() {
        mCurPage = 1;
        mTotalPage = 1;
        mLastVODTime = "";
        return this;
    }



    @Override
    public void getVODRecord(boolean isSub,String beg, String end) {
        if (mCurPage>mTotalPage){
            Log.i("123","may be last page");
//            mView.onError(0);
//            return;
        }
//        final boolean isNew = DeviceVersionUtils.isNewVersionDevice(mBean.getDeVer());
        final String isoBeg = Util.DateString2ISODateString(beg);
        final String isoEnd = Util.DateString2ISODateString(end);
        Log.i("123","~~~~~~~~~~~mCurPage="+mCurPage+"   tot="+mTotalPage);
        ApiManager.getInstance().getSoapService()
                .vodSearch(new VodSearchReq(
                        mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        mBean.getDeviceId(),
                        mBean.getChannelNo(),
                        isSub?"Sub":"Main",
                        isoBeg,
                        isoEnd,
                        null,
                        mCurPage,
                        mPageSize))
                .map(new Function<VodSearchRes, ArrayList<VodSearchRes.Record>>() {

                    @Override
                    public ArrayList<VodSearchRes.Record> apply(@NonNull VodSearchRes vodSearchRes) throws Exception {
                        if (vodSearchRes.getResult().equalsIgnoreCase("SessionExpired")){
                            login();
                            mView.onError(1);
                            return null;
                        }
                        if (!vodSearchRes.getResult().equalsIgnoreCase("ok")){
                            //error
                            mView.onError(1);
                            return null;
                        }

                        if (mCurPage == 1) {
                            mTotalPage = vodSearchRes.getPageCount();
                        }
                        mCurPage++;

                        return  vodSearchRes.getRecord();
                    }
                })
                .concatMap(new Function<ArrayList<VodSearchRes.Record>, ObservableSource<VodSearchRes.Record>>() {
                    @Override
                    public ObservableSource<VodSearchRes.Record> apply(@NonNull ArrayList<VodSearchRes.Record> records) throws Exception {
                        return Observable.fromIterable(records);
                    }
                })
                .map(new Function<VodSearchRes.Record, VODRecord>() {
                    @Override
                    public VODRecord apply(@NonNull VodSearchRes.Record record) throws Exception {
                        boolean hasTitle = false;
//                        Log.i("123","record starttime="+record.getStartTime());
                        if (!mLastVODTime.equals(record.getStartTime().substring(0,10))){
                            mLastVODTime = record.getStartTime().substring(0,10);
                            hasTitle = true;
                        }
                        return new VODRecord(
                                Util.ISODateString2Date(record.getStartTime()),
                                Util.ISODateString2Date(record.getEndTime()),
                                record.getStartTime(),
                                record.getEndTime(),
                                record.getFileSize(),
                                record.getDesc(),
                                hasTitle);
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<VODRecord>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onSuccess(@NonNull List<VODRecord> vodRecords) {
                        mView.onRecord(vodRecords);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onError(1);
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
                ApiManager.getInstance().getEcamService().catchPic(getT());
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
                ApiManager.getInstance().getEcamService().catchPic(getT());
            }
        });
    }

    @Override
    public void setSoundMute(final boolean setMute) {
        super.setSoundMute(setMute);
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                if (setMute){
                    AudioAction.getInstance().audioSoundMute();
                }else{
                    AudioAction.getInstance().audioSoundUnmute();
                }
                e.onNext(setMute);
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
                        mView.onSoundMute(aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void talkFun(boolean bTalking) {
        super.talkFun(bTalking);
        if (bTalking) {
            AudioAction.getInstance().pauseAudio();
            AudioAction.getInstance().startAudioRecord(new AudioAction.AudioRecordHelp() {
                @Override
                public void sendAudioData(byte[] buf, int len) {
                    ApiManager.getInstance().getEcamService().soundSendBuf(buf,len);
                }
            });
        }else{
            AudioAction.getInstance().stopAudioRecord();
            AudioAction.getInstance().playAudio();
        }
    }

    @Override
    protected void startTimeTask() {
        super.startTimeTask();
        ThreadUtil.scheduledSingleThreadStart(new Runnable() {
            @Override
            public void run() {
                boolean bWait = true;
                int streamLen = ApiManager.getInstance().getEcamService().getStreamLen();
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
                long timestamp = ApiManager.getInstance().getEcamService().getTimestamp();
                long firstTimestamp = ApiManager.getInstance().getEcamService().getFirstTimestamp();
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
    public void lampOn(boolean isOn) {
        ApiManager.getInstance().getSoapService()
            .setAuxiliary(new SetAuxiliaryReq(
                    mAccount,
                    ApiManager.SoapHelp.getsSession(),
                    mBean.getDeviceId(),
                    "Lighting",//"手动照明"
                    isOn?"Active":"Inactive"
            ))
                .map(new Function<Result, Boolean>() {
                    @Override
                    public Boolean apply(Result result) throws Exception {
                        return result.getResult().equalsIgnoreCase("ok");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.i("123","set lamp is ok="+aBoolean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("123","set lamp error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","set lamp finish");
                    }
                });
    }
}
