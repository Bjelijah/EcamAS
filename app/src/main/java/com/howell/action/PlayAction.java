package com.howell.action;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

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

    public CameraItemBean getPlayBean(){
        return mItemBean;
    }


    public void rePlay(final ICam mgr,final int isSub,@Nullable Handler handler){
        if (handler!=null)setHandler(handler);
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                mgr.stopViewCam();
                mgr.playViewCam(isSub);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (mHandler==null)return;
                Message msg = new Message();
                msg.what = BasePlayActivity.MSG_PLAY_RELINK_OK;
                msg.arg1 = isSub;
            }
        }.execute();
    }
    public void rePlay(final ICam mgr,final int isSub){
        rePlay(mgr,isSub,null);
    }

    public void catchPic(final ICam mgr){
        File destDir = new File("/sdcard/eCamera");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        final String nameDirPath = "/sdcard/eCamera/"+ FileUtils.getFileName()+".jpg";
        //TODO: catch picture;
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                mgr.catchPic(nameDirPath);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (mHandler==null)return;
                mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_SAVE_PICTURE);
            }
        }.execute();
    }

    public void catchPic(final ICam mgr,final String path){
        File destDir = new File(path);
        if (!destDir.exists()){
            destDir.mkdirs();
        }
        String nameDirPath = path+"/"+mItemBean.getDeviceId()+".jpg";
        mgr.catchPic(nameDirPath);
    }



}
