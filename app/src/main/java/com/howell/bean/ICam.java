package com.howell.bean;

import android.content.Context;
import android.os.Handler;

/**
 * Created by howell on 2016/12/6.
 */

public interface ICam {
    void init(Context context,CameraItemBean bean);
    void deInit();
    void setHandler(Handler handler);
    void registStreamLenCallback(IStream cb);
    void unregistStreamLenCallback();
    void setStreamBSub(boolean isSub);
    void setPlayBack(boolean isPlayback);
    void setPlayBackTime(long startTime,long endTime);



    boolean bind();//添加相机  将相机绑定到当前帐号
    boolean unBind();//删除相机  将相机解绑

    boolean loginCam();
    boolean logoutCam();

    boolean playViewCam();
    boolean stopViewCam();
    boolean reLink();



    //功能
    boolean catchPic(String path);
    boolean soundSetData(byte [] buf,int len);



    interface IStream{
        void showStreamSpeed(final int kbitPerSec);
    }


}
