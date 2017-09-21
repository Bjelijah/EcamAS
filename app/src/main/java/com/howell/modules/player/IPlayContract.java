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
        void onRecord(List<VODRecord> vodRecords);
        void onError(int flag);
        void onTime(int speed,long timestamp,long firstTimestamp);
    }
    interface IPresent extends ImpBasePresenter{
        void init(Context context, CameraItemBean bean);
        void deInit();
        void play(boolean isSub);
        void playback(boolean isSub,String beg,String end);
        void stop();
        boolean pause();
        void playMoveTo(boolean isSub,String beg,String end);
        void ptzCtrl(PTZ cmd);
        IPresent vodReset();
        void getVODRecord(boolean isSub,String beg,String end);

    }
}
