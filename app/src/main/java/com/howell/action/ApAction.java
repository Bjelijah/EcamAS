package com.howell.action;

import android.content.Context;

import com.howell.bean.APDeviceDBBean;
import com.howell.bean.CamFactory;
import com.howell.bean.CameraItemBean;
import com.howell.bean.ICam;
import com.howell.bean.PlayType;
import com.howell.db.ApDeviceDao;
import com.howell.jni.JniUtil;

import java.util.List;

/**
 * Created by howell on 2016/12/2.
 */

public class ApAction {
    private static ApAction mInstance = null;
    public static ApAction getInstance(){
        if (mInstance == null){
            mInstance = new ApAction();
        }
        return mInstance;
    }
    private ApAction(){}

    QueryApDevice mCb;

    public ApAction registQueryApDeviceCallback(QueryApDevice cb){
        this.mCb = cb;
        return this;
    }

    public void unRegistQueryApDeviceCallBack(){
        this.mCb = null;
    }

    public boolean addAP2DB(Context context, String deviceName, String ip, String port){
        int portNum = 0;
        try {
            portNum  = Integer.valueOf(port);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        CameraItemBean tmp = new CameraItemBean()
                .setCameraName(deviceName)
                .setUpnpIP(ip)
                .setUpnpPort(portNum);

        ICam cam = CamFactory.buildCam(PlayType.HW5198);
        cam.init(context,tmp);
        return cam.bind();
    }


    private List<APDeviceDBBean> getAPCameraList(Context context, String userName){
        ApDeviceDao dao = new ApDeviceDao(context,"user.db",1);
        List<APDeviceDBBean> beanList =  dao.queryByName(userName);
        dao.close();
        return beanList;
    }


    public boolean isAPOnLine(String ip){
        boolean isOnLine = false;
        JniUtil.netInit();
        isOnLine = JniUtil.login(ip);
        JniUtil.loginOut();
        JniUtil.netDeinit();
        return isOnLine;
    }


    public void getApCameraList(final Context context,final String userName  ){
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<APDeviceDBBean> apList = getAPCameraList(context,userName);
                for (APDeviceDBBean b:apList){
                    b.setOnLine(isAPOnLine(b.getDeviceIP()));
//                    b.setOnLine(true);
                }
                if (mCb!=null && apList.size()>=1){
                    mCb.onQueryApDevice(apList);
                }
            }
        }.start();
    }

    public interface QueryApDevice{
        void onQueryApDevice(List<APDeviceDBBean> list);
    }


}
