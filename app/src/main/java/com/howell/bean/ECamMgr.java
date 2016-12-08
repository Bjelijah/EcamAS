package com.howell.bean;

import android.content.Context;
import android.util.Log;

import com.howell.action.LoginAction;
import com.howell.protocol.NullifyDeviceReq;
import com.howell.protocol.NullifyDeviceRes;
import com.howell.protocol.SoapManager;
import com.howell.utils.IConst;

/**
 * Created by howell on 2016/12/6.
 */

public class ECamMgr implements ICam,IConst {
    Context mContext;
    CameraItemBean mCamBean;

    @Override
    public void init(Context context, CameraItemBean bean) {
        this.mCamBean = bean;
        this.mContext = context;
    }

    @Override
    public void deInit() {

    }

    @Override
    public boolean bind() {
        return true;
    }

    @Override
    public boolean unBind() {
        NullifyDeviceRes res = null;
        Log.i("123","unBind eCam deviceId="+mCamBean.getDeviceId());
        try {
            NullifyDeviceReq req = new NullifyDeviceReq(LoginAction.getInstance().getmInfo().getAccount()
                    ,LoginAction.getInstance().getmInfo().getLr().getLoginSession(),mCamBean.getDeviceId(),mCamBean.getDeviceId());
            res = SoapManager.getInstance().getNullifyDeviceRes(req);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("removeDevice:"+res.getResult());
        Log.e("123","removeDevice:"+res.getResult());
        return true;
    }
}
