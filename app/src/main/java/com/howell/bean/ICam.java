package com.howell.bean;

import android.content.Context;

/**
 * Created by howell on 2016/12/6.
 */

public interface ICam {
    void init(Context context,CameraItemBean bean);
    void deInit();
    boolean bind();
    boolean unBind();



}
