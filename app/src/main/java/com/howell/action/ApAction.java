package com.howell.action;

import android.content.Context;

import com.howell.bean.APDeviceDBBean;
import com.howell.db.ApDeviceDao;

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




    public boolean addAP2DB(Context context,String userName, String deviceName, String ip, String port){
        int portNum = 0;
        try {
            portNum  = Integer.valueOf(port);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
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
