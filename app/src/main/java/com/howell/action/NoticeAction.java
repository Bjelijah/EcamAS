package com.howell.action;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.howell.protocol.FlaggedNoticeStatusReq;
import com.howell.protocol.FlaggedNoticeStatusRes;
import com.howell.protocol.QueryNoticesReq;
import com.howell.protocol.QueryNoticesRes;
import com.howell.protocol.SoapManager;
import com.howellsdk.api.ApiManager;

import java.util.Date;

/**
 * Created by howell on 2016/11/28.
 */

public class NoticeAction {
    private static NoticeAction mInstance = null;
    public static NoticeAction getInstance(){
        if (mInstance == null){
            mInstance = new NoticeAction();
        }
        return mInstance;
    }
    private NoticeAction(){}

    private int curPage;
    private int totalPage;
    private final int pageSize = 20;//// FIXME: 2017/5/5
    private SoapManager mSoapManager = SoapManager.getInstance();
    private OnNoticeRes mListener;
    private String mTime = null;
    private String mStatus = null;
    private String mSender = null;

    public NoticeAction init(){
        curPage = 1;
        totalPage = 1;
        return this;
    }

    public NoticeAction reset(){
        return init();
    }


    public NoticeAction setListener(OnNoticeRes l){
        this.mListener = l;
        return this;
    }

    public void searchNotices(@Nullable String day){
        mTime = day;
        init();
        getNoticesTask();
    }

    public void searchNotices(int status){
        switch (status){
            case 0:
                mStatus = "Unread";
                break;
            case 1:
                mStatus = "Read";
                break;
            case 2:
                mStatus = null;
                break;
            default:
                mStatus = null;
                break;
        }
        init();
        getNoticesTask();
    }


    public void getNoticesTask(){
        new AsyncTask<Void,Void,Boolean>(){
            QueryNoticesRes mRes = null;
            @Override
            protected Boolean doInBackground(Void... voids) {


                try {
                    String account = LoginAction.getInstance().getmInfo().getAccount();
                    String session = LoginAction.getInstance().getmInfo().getLr().getLoginSession();

                    mRes = mSoapManager.getQueryNoticesRes(new QueryNoticesReq(account,session,curPage,pageSize,mStatus,mTime,mSender));//// FIXME: 2017/5/5

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                if (curPage == 1 && mRes!=null && mRes.getResult().equalsIgnoreCase("OK")){
                    totalPage = mRes.getPageCount();
                }
                if (mRes == null) {
                    return true;
                }
                curPage++;
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                if (mListener==null)return;

                if (!aVoid){
                    mListener.OnNoticeError();
                }else{
                    mListener.OnNoticeRes(mRes);
                }



            }
        }.execute();
    }

    public void setNoticesStatus(final String id, final boolean isRead){
        new AsyncTask<Void,Void,Void>(){
            FlaggedNoticeStatusRes res;
            @Override
            protected Void doInBackground(Void... params) {
//                String account = LoginAction.getInstance().getmInfo().getAccount();
//                String session = LoginAction.getInstance().getmInfo().getLr().getLoginSession();
                String account = "10086012";
                String session = ApiManager.SoapHelp.getsSession();
                String [] ids = new String[1];
                ids[0] = id;
                try{
                    res = mSoapManager.getFlaggedNoticeStatusRes(new FlaggedNoticeStatusReq(account,session, isRead?"Read":"Unread",ids));
                    Log.i("123","flaggedNoticeStat res="+res.getResult().toString());
                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    public interface OnNoticeRes{
        void OnNoticeError();
        void OnNoticeRes(QueryNoticesRes res);
    }

}
