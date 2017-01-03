package com.howell.action;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.howell.activity.BasePlayActivity;
import com.howell.bean.CameraItemBean;
import com.howell.bean.ICam;
import com.howell.utils.FileUtils;
import com.howell.utils.MessageUtiles;

import java.io.File;


/**
 * Created by howell on 2016/11/29.
 */

public class PlayAction {
    private static PlayAction mInstance = null;
    public static PlayAction getInstance(){
        if (mInstance == null){
            mInstance = new PlayAction();
        }
        return mInstance;
    }

    private Handler mHandler;
    private ICam mCamMgr;
    private PlayAction(){}
    private CameraItemBean mItemBean;
    private boolean bMute = false;
    public boolean isMute(){
        return bMute;
    }
    public void mute(){
        AudioAction.getInstance().audioSoundMute();
        bMute = true;
        if (mHandler!=null)
            mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_SOUND_MUTE);
    }
    public void unmute(){
        AudioAction.getInstance().audioSoundUnmute();
        bMute = false;
        if (mHandler!=null)
            mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_SOUND_UNMUTE);
    }

    public PlayAction setPlayBean(CameraItemBean bean){
        this.mItemBean = bean;
        return this;
    }
    public PlayAction setHandler(Handler handler){
        this.mHandler = handler;
        return this;
    }

    public PlayAction setCam(ICam cam){
        this.mCamMgr = cam;
        return this;
    }

    public ICam getCam(){
        return this.mCamMgr;
    }


    public CameraItemBean getPlayBean(){
        return mItemBean;
    }

    public boolean camLogin(){
        if (mHandler==null)return false;
        if (mCamMgr==null){
            mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_LOGIN_CAM_ERROR);
            return false;
        }

        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                return mCamMgr.loginCam();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (mHandler==null)return;
                if (aBoolean){
                    mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_LOGIN_CAM_OK);
                }else{
                    mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_LOGIN_CAM_ERROR);
                }
            }
        }.execute();


        return true;
    }

    public boolean camViewPlay(){
        if (mHandler==null)return false;
        if (mCamMgr==null){
            mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_PLAY_CAM_ERROR);
            return false;
        }
        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                return mCamMgr.playViewCam();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (mHandler==null)return;
                if (aBoolean){
                    mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_PLAY_CAM_OK);
                }else{
                    mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_PLAY_CAM_ERROR);
                }
            }
        }.execute();
        return true;
    }

    public boolean camBackPlay(){
        if (mHandler==null)return false;
        if (mCamMgr==null){
            mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_PLAY_CAM_ERROR);
            return false;
        }
        //TODO mCamMgr.playBackCam();
        return true;
    }

    public boolean camViewStop(){
        if (mHandler==null)return false;
        if (mCamMgr==null){
            return false;
        }
        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                return  mCamMgr.stopViewCam();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (mHandler==null)return;
                if (aBoolean){
                    mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_STOP_CAM_OK);
                }else {
                    Log.e("123","stop view cam errror");
                    mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_STOP_CAM_ERROR);
                }
            }
        }.execute();
        return true;
    }

    public boolean camBackStop(){
        if (mHandler==null)return false;
        if (mCamMgr==null){
            return false;
        }
        //TODO:
//        mCamMgr.stopBackCam();
        return true;
    }

    public boolean camLogout(){
        if (mHandler==null)return false;
        if (mCamMgr==null){
            return false;
        }
        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                return  mCamMgr.logoutCam();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (mHandler==null)return;
                if (aBoolean){
                    mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_LOGOUT_CAM_OK);
                }else{
                    Log.e("123"," logout cam error");
                    mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_LOGOUT_CAM_ERROR);
                }
            }
        }.execute();
        return true;
    }



    public void rePlay(final int isSub,@Nullable Handler handler){
        if (handler!=null)setHandler(handler);
        new AsyncTask<Void,Void,Boolean>(){//FIXME do not use task

            @Override
            protected Boolean doInBackground(Void... params) {
                mCamMgr.setStreamBSub(isSub==1?true:false);
                return mCamMgr.reLink();
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                if (mHandler==null)return;
                if (aVoid) {
                    Message msg = new Message();
                    msg.what = BasePlayActivity.MSG_PLAY_RELINK_OK;
                    msg.arg1 = isSub==1?1:2;
                    mHandler.sendMessage(msg);
                    Log.i("123","relink ok");
                }else{
                    Log.i("123","relink error");

                }
            }
        }.execute();
    }
    public void rePlay(final int isSub){
        rePlay(isSub,null);
    }


    /******************************
     * 功能
     *******************************/
    public void catchPic(){
        File destDir = new File("/sdcard/eCamera");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        final String nameDirPath = "/sdcard/eCamera/"+ FileUtils.getFileName()+".jpg";
        //TODO: catch picture;
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                mCamMgr.catchPic(nameDirPath);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (mHandler==null)return;
                mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_SAVE_PICTURE);
            }
        }.execute();
    }

    public void catchPic(final String path){
        File destDir = new File(path);
        if (!destDir.exists()){
            destDir.mkdirs();
        }
        String nameDirPath = path+"/"+mItemBean.getDeviceId()+".jpg";
        mCamMgr.catchPic(nameDirPath);
    }

    public boolean soundSendBuf(byte [] buf,int len){
        if (mCamMgr==null)return false;
        return mCamMgr.soundSetData(buf,len);
    }


}
