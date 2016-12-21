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
    void setStreamBSub(int isSub);
    boolean bind();//添加相机  将相机绑定到当前帐号
    boolean unBind();//删除相机  将相机解绑

    void loginCam();
    void logoutCam();

    void playViewCam(int is_sub);
    void stopViewCam();
    void reLink();


    void catchPic(String path);
}
