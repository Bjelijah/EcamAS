package com.howell.action;

import android.content.Context;

import com.howell.bean.CamFactory;
import com.howell.bean.CameraItemBean;
import com.howell.bean.ICam;
import com.howell.bean.PlayType;

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

}
