package com.howell.action;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.howell.bean.APDeviceDBBean;
import com.howell.bean.CamFactory;
import com.howell.bean.CameraItemBean;
import com.howell.bean.Custom;
import com.howell.bean.ICam;
import com.howell.bean.PlayType;
import com.howell.bean.UserLoginDBBean;
import com.howell.db.ApDeviceDao;
import com.howell.db.UserLoginDao;
import com.howell.entityclass.NodeDetails;
import com.howell.entityclass.StreamReqContext;
import com.howell.protocol.QueryDeviceReq;
import com.howell.protocol.SoapManager;
import com.howell.utils.ServerConfigSp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howell on 2016/11/18.
 */

public class HomeAction {
    private static HomeAction mInstance = null;
    public static HomeAction getInstance(){
        if (mInstance == null){
            mInstance = new HomeAction();
        }
        return mInstance;
    }
    private Context mContext;
    private String serviceIP;
    private int servicePort,videoPort;
    private boolean isUseTurn;
    private boolean isUseCrypto;
    public HomeAction setServiceIPAndPort(String ip,int port){
        this.serviceIP = ip;
        this.servicePort = port;
        return this;
    }
    public HomeAction setServiceVideoPort(int videoPort){
        this.videoPort = videoPort;
        return this;
    }
    public int getServiceVideoPort(){
        return videoPort;
    }

    public HomeAction init(){
        isUseTurn = ServerConfigSp.loadServerIsTurn(mContext);
        isUseCrypto = ServerConfigSp.loadServerIsCrypto(mContext);
        serviceIP = ServerConfigSp.loadServerIP(mContext);
        servicePort = ServerConfigSp.loadServerPort(mContext);
        return this;
    }

    public boolean isUseTurn() {
        return isUseTurn;
    }

    public void setUseTurn(boolean useTurn) {
        isUseTurn = useTurn;
    }

    public boolean isUseCrypto() {
        return isUseCrypto;
    }

    public void setUseCrypto(boolean useCrypto) {
        isUseCrypto = useCrypto;
    }

    public String getServiceIP(){
        return this.serviceIP;
    }
    public int getServicePort(){
        return servicePort;
    }

    public HomeAction setContext(Context c){
        this.mContext = c;
        return this;
    }
    private HomeAction(){}

    private QueryDeviceCallback mQueryDeviceCallback;
    private ChangeUser mChangeUserCallback;

    public HomeAction registChangerUserCallback(ChangeUser cb){
        this.mChangeUserCallback = cb;
        return this;
    }

    public void unregistChangerUserCallback(){
        this.mChangeUserCallback = null;
    }

    public QueryDeviceCallback unregistQueryDeviceCallback() {
        mQueryDeviceCallback = null;
        return mQueryDeviceCallback;
    }

    public HomeAction registQueryDeviceCallback(QueryDeviceCallback mQueryDeviceCallback) {
        this.mQueryDeviceCallback = mQueryDeviceCallback;
        return this;
    }

    private SoapManager mSoapManager = SoapManager.getInstance();
    private ArrayList<NodeDetails> mList;

    private List<APDeviceDBBean> getAPCameraList(Context context,String userName){
        ApDeviceDao dao = new ApDeviceDao(context,"user.db",1);
        List<APDeviceDBBean> beanList =  dao.queryByName(userName);
        dao.close();
        return beanList;
    }

    private boolean isAPOnLine(String ip){
        return ApAction.getInstance().isAPOnLine(ip);
    }


    public boolean addApCam2List(Context context, String userName,ApAction.QueryApDevice cb){
        ApAction.getInstance().registQueryApDeviceCallback(cb).getApCameraList(context,userName);
        return true;
    }



    public boolean addApCam2List(Context context,String userName,List<CameraItemBean> list){
        list.clear();
        if (list==null)return false;

        List<APDeviceDBBean> apList = getAPCameraList(context,userName);
        for (APDeviceDBBean apBean:apList){
            CameraItemBean camBean = new CameraItemBean()
                    .setType(PlayType.HW5198)
                    .setCameraName(apBean.getDeviceName())
                    .setCameraDescription("AP:"+apBean.getDeviceIP())
                    .setOnline(true)
                    .setIndensity(0)
                    .setStore(true)
                    .setPtz(true)
                    .setUpnpIP(apBean.getDeviceIP())
                    .setUpnpPort(apBean.getDevicePort())
                    .setDeviceId(apBean.getDeviceIP())
                    .setPicturePath("/sdcard/eCamera/cache/"+apBean.getDeviceIP()+".jpg");
            list.add(camBean);
        }
        return true;
    }

    public boolean sort(ArrayList<CameraItemBean> list){
        if (list==null)return false;
        for (int i=0;i<list.size();i++){
            if (list.get(i).isOnline() ){
                list.add(0, list.get(i));
                list.remove(i+1);
            }
        }
        return true;
    }

    public boolean removeCam(Context context,CameraItemBean bean){
        Log.e("123","type="+bean.getType());
        ICam cam = CamFactory.buildCam(bean.getType());
        if (null==cam)return false;
        cam.init(context,bean);
        return cam.unBind();
    }




    public void queryDevice(final String account,final String session){
        new AsyncTask<Void,Void,Boolean>(){
            private void sort(ArrayList<NodeDetails> list){
                if(list != null){
                    int length = list.size();
                    for(int i = 0 ; i < length ; i++){
                        System.out.println(i+":"+list.get(i).toString());
                        if(list.get(i).isOnLine()){
                            list.add(0, list.get(i));
                            list.remove(i+1);
                        }else{
                            //System.out.println(i);
                        }
                    }
                }
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    mSoapManager.getQueryDeviceRes(new QueryDeviceReq(account,session));
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                mList =  mSoapManager.getNodeDetails();
                sort(mList);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (mQueryDeviceCallback==null)return;
                if (aBoolean){
                    mQueryDeviceCallback.onQueryDeviceSuccess(mList);
                }else{
                    mQueryDeviceCallback.onQueryDeviceError();
                }
            }
        }.execute();
    }

    public void changeUser(Context context, String userName,String email){
        Bundle bundle = getUserPwdByDB(userName,email);
        String userPwd = bundle.getString("pwd");
        Custom c = (Custom) bundle.getSerializable("custom");
        LoginRes res = new LoginRes();
        LoginAction.getInstance().setContext(context).regLoginResCallback(res).Login(userName,userPwd,c);
    }

    private Bundle getUserPwdByDB(String name,String email){
        String pwd = null;
        Custom c = null;
        UserLoginDao dao = new UserLoginDao(mContext, "user.db", 1);
        List<UserLoginDBBean> b = dao.queryByNameAndEmail(name,email);
        dao.close();
        if (b.size()>0){
            pwd = b.get(0).getUserPassword();
            c = b.get(0).getC();
        }
        Bundle bundle = new Bundle();
        bundle.putString("pwd",pwd);
        bundle.putSerializable("custom",c);
        return bundle;
    }

    class LoginRes implements LoginAction.IloginRes{

        @Override
        public void onLoginSuccess() {
            if (mChangeUserCallback!=null){
                mChangeUserCallback.onChangeOk();
            }
            LoginAction.getInstance().unRegLoginResCallback();
        }

        @Override
        public void onLoginError(int e) {
            if (mChangeUserCallback!=null){
                mChangeUserCallback.onChangeError();
            }
            LoginAction.getInstance().unRegLoginResCallback();
        }
    }


    public interface QueryDeviceCallback{
        void onQueryDeviceSuccess(ArrayList<NodeDetails> l);
        void onQueryDeviceError();
    }

    public interface ChangeUser{
        void onChangeOk();
        void onChangeError();
    }

}
