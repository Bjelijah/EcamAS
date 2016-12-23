package com.howell.bean;

import android.content.Context;
import android.os.Handler;

import com.howell.action.LoginAction;
import com.howell.db.ApDeviceDao;

/**
 * Created by howell on 2016/12/6.
 */

public class ApCamMgr implements ICam {
    Context mContext=null;
    CameraItemBean mCamBean=null;

    @Override
    public void init(Context context, CameraItemBean bean) {
        this.mContext = context;
        this.mCamBean = bean;
    }

    @Override
    public void deInit() {

    }

    @Override
    public void setHandler(Handler handler) {

    }

    @Override
    public void setStreamBSub(boolean isSub) {

    }

    @Override
    public void setPlayBack(boolean isPlayback) {

    }

    @Override
    public void setPlayBackTime(long startTime, long endTime) {

    }


    @Override
    public boolean bind() {
        if (!checkInit())return false;
        return addAP2DB(mContext
                ,LoginAction.getInstance().getmInfo().getAccount()
                ,mCamBean.getCameraName()
                ,mCamBean.getUpnpIP()
                ,mCamBean.getUpnpPort());
    }

    @Override
    public boolean unBind() {
        if (!checkInit())return false;
        ApDeviceDao dao = new ApDeviceDao(mContext,"user.db",1);
        dao.deleteByName(LoginAction.getInstance().getmInfo().getAccount(),mCamBean.getCameraName());
        dao.close();
        return true;
    }

    @Override
    public void loginCam() {

    }

    @Override
    public void logoutCam() {

    }

    @Override
    public void playViewCam() {

    }


    @Override
    public void stopViewCam() {

    }

    @Override
    public void reLink() {

    }

    @Override
    public void catchPic(String path) {

    }


    private boolean checkInit(){
        if (mCamBean==null||mContext==null)return false;
        return true;
    }

    private boolean addAP2DB(Context context,String userName, String deviceName, String ip, int port){
        int portNum = port;
        ApDeviceDao dao = new ApDeviceDao(context,"user.db",1);
        APDeviceDBBean bean = new APDeviceDBBean(userName,deviceName,ip,portNum);
        if (dao.findByName(userName,deviceName)){
            dao.updataByName(bean,userName,deviceName);
        }else{
            dao.insert(bean);
        }
        dao.close();
        return true;
    }
}
