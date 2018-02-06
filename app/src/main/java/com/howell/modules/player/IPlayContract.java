package com.howell.modules.player;

import android.content.Context;

import com.howell.bean.CameraItemBean;
import com.howell.modules.BasePresenter;
import com.howell.modules.ImpBasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.player.bean.PTZ;
import com.howell.modules.player.bean.VODRecord;

import java.util.List;

/**
 * Created by Administrator on 2017/9/20.
 */

public interface IPlayContract {
    interface IVew extends ImpBaseView{
        void onConnect(boolean isSuccess);
        void onSoundMute(boolean isMute);
        void onRecord(List<VODRecord> vodRecords);
        void onError(int flag);//0 error  1 need relink
        void onTime(int speed,long timestamp,long firstTimestamp,boolean bWait);
        void onPlaybackStartEndTime(long beg,long end);
    }
    interface IPresent extends ImpBasePresenter{
        void init(Context context, CameraItemBean bean);
        void deInit();
        void play(boolean isSub);
        void playback(boolean isSub,String beg,String end);
        void stop();
        boolean pause();
        void relink(boolean isSub);
        void playMoveTo(boolean isSub,String beg,String end);
        void playMoveTo(boolean isSub,long beg,long end);
        void ptzCtrl(PTZ cmd);
        IPresent vodReset();
        void getVODRecord(boolean isSub,String beg,String end);
        void lampOn(boolean isOn);
        void catchPic();
        void catchPic(String path);
        void setSoundMute(boolean setMute);
        void talkFun(boolean bTalking);
        IVew getView();
        void holdServer();
        void resumeServer();
        void clearServer();
    }
}
