package com.howell.action;

import com.howell.bean.CameraItemBean;
import com.howell.bean.IPlay;
import com.howell.bean.PlayFactory;

/**
 * Created by howell on 2016/11/29.
 */

public class PlayAction {
    private static PlayAction mInstance = null;
    public PlayAction getInstance(){
        if (mInstance == null){
            mInstance = new PlayAction();
        }
        return mInstance;
    }
    private PlayAction(){}
    private CameraItemBean mItemBean;
    public PlayAction setPlayBean(CameraItemBean bean){
        this.mItemBean = bean;
        return this;
    }
    public CameraItemBean getPlayBean(){
        return mItemBean;
    }

    private IPlay mPlay;

    public void init(){
        mPlay = PlayFactory.buildPlay(mItemBean.getType());
    }







}
