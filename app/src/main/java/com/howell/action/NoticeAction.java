package com.howell.action;

import android.os.AsyncTask;

import com.howell.protocol.QueryNoticesReq;
import com.howell.protocol.QueryNoticesRes;
import com.howell.protocol.SoapManager;

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
    private int pageSize = 20;
    private SoapManager mSoapManager = SoapManager.getInstance();
    private OnNoticeRes mListener;


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


    public void getNoticesTask(){
        new AsyncTask<Void,Void,Boolean>(){
            QueryNoticesRes mRes = null;
            @Override
            protected Boolean doInBackground(Void... voids) {


                try {
                    String account = LoginAction.getInstance().getmInfo().getAccount();
                    String session = LoginAction.getInstance().getmInfo().getLr().getLoginSession();
                    mRes = mSoapManager.getQueryNoticesRes(new QueryNoticesReq(account,session,curPage,pageSize));
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


    public interface OnNoticeRes{
        void OnNoticeError();
        void OnNoticeRes(QueryNoticesRes res);
    }

}
