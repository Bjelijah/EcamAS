package com.howell.action;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.howell.activity.fragment.VodFragment;
import com.howell.bean.CamFactory;
import com.howell.bean.CameraItemBean;
import com.howell.bean.ICam;
import com.howell.entityclass.VODRecord;
import com.howell.protocol.GetDevVerReq;
import com.howell.protocol.GetDevVerRes;
import com.howell.protocol.SoapManager;
import com.howell.utils.DeviceVersionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/1/4.
 */

public class PlayBackVideoListAction {
    private static PlayBackVideoListAction mInstance = null;
    public static PlayBackVideoListAction getInstance(){
        if (mInstance == null){
            mInstance = new PlayBackVideoListAction();
        }
        return mInstance;
    }
    private PlayBackVideoListAction(){}

    Context mContext;
    CameraItemBean mBean;
    Handler mHandler;
    ICam mCam;

    int mCurPage;
    int mTotalPage;

    ArrayList<VODRecord> mVodList = null;
    String mLastVODTime = "";
    String mStartTime=null,mEndTime=null;


    public ArrayList<VODRecord> getVodList(){
        return mVodList;
    }

    public PlayBackVideoListAction setBean(CameraItemBean bean){
        mBean = bean;
        if (mCam==null){
            mCam = CamFactory.buildCam(bean.getType());
            mCam.init(mContext,mBean);
        }
        return this;
    }
    public PlayBackVideoListAction setHandler(Handler h){
        mHandler = h;
        if (mCam!=null){
            mCam.setHandler(h);
        }
        return this;
    }
    public PlayBackVideoListAction setCam(ICam c){
        mCam = c;
        return this;
    }

    public PlayBackVideoListAction init(Context context){
        mContext = context;
       return reset();
    }
    public PlayBackVideoListAction reset(){
        mCurPage = 1;//从1开始计算
        mTotalPage = 1;
        mLastVODTime = "";
        return this;
    }

    public void setSearchTime(String startTime,String endTime){
        mStartTime = startTime;
        mEndTime = endTime;
    }

    private void initNowTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date endDate = new Date();
        Date begDate = new Date(1970 - 1900, 1 - 1, 1, 0, 0, 0);
        mStartTime = sdf.format(begDate);
        mEndTime = sdf.format(endDate);
    }


    public void searchVODList(){
        if (mStartTime==null||mEndTime==null){
            initNowTime();
        }

        new AsyncTask<Void,Void,Boolean>(){

            private void listFun(ArrayList<VODRecord> list){
                for (VODRecord v:list){
                    if (!mLastVODTime.equals(v.getTimeZoneStartTime().substring(0,10))){
                        mLastVODTime = v.getTimeZoneStartTime().substring(0,10);
                        v.setHasTitle(true);
                    }
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                if (mCam==null){
                    Log.e("123","mCam=null");
                    return false;
                }
                if (mCurPage>mTotalPage){
                    Log.e("123","mCurPage>mTotalPage");
                    return false;
                }
                Log.i("123","mCurpage");
                mCam.setVideoListTime(mStartTime,mEndTime);
                mTotalPage = mCam.getVideoListPageCount(mCurPage, 20);
                Log.i("123","mTotalPage="+mTotalPage);
                mVodList = mCam.getVideoList();
                listFun(mVodList);
                return mVodList==null?false:true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (mHandler==null)return;
                if (aBoolean){
                    mHandler.sendEmptyMessage(VodFragment.MSG_VIDEO_LIST_DATA_UPDATE);
                }else {
                    mHandler.sendEmptyMessage((mCurPage==mTotalPage+1)?VodFragment.MSG_VIDEO_LIST_DATA_LAST:VodFragment.MSG_VIDEO_LIST_DATA_UPDATE_ERROR);
                }
            }
        }.execute();
    }



    public void refreashVODList(){
        init(mContext);
        initNowTime();
        searchVODList();
    }

    public void loadVODList(){
        ++mCurPage;
        searchVODList();
    }




}
