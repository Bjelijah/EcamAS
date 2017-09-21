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
import com.howellsdk.utils.ThreadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/9/20.
 */

public class PlayTurnPresenter extends PlayBasePresenter {
    private int mCurPage;
    private final int mPageSize = 20;
    private String mLastVODTime = "";

    @Override
    public void init(Context context, CameraItemBean bean) {
        super.init(context, bean);
        ApiManager.getInstance()
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
                        });
    }

    @Override
    public void play(boolean isSub) {
        ApiManager.getInstance()
                .getTurnService()
                .play(isSub);
        startTimeTask();
    }

    @Override
    public void playback(boolean isSub, String beg, String end) {
        ApiManager.getInstance()
                .getTurnService()
                .playback(isSub,beg,end);
        startTimeTask();
    }

    @Override
    public void stop() {
        ApiManager.getInstance()
                .getTurnService()
                .stop();
        stopTimeTask();
    }

    @Override
    public boolean pause() {
        return ApiManager.getInstance()
                .getTurnService()
                .playPause();
    }

    @Override
    public void playMoveTo(boolean isSub, String beg, String end) {
        ApiManager.getInstance()
                .getTurnService()
                .reLink(true,beg,end);
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

        ApiManager.getInstance().getTurnService()
                .ptzControl(turnCMD,15,null);
    }

    @Override
    public IPlayContract.IPresent vodReset() {
        mCurPage = 1;
        return this;
    }

    @Override
    public void getVODRecord(boolean isSub, String beg, String end) {
        ApiManager.getInstance().getTurnService()
                .getRecordedFiles(beg,end,mCurPage,mPageSize);
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
