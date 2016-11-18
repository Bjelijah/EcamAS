package com.howell.action;

import android.content.Context;
import android.os.AsyncTask;

import com.howell.entityclass.NodeDetails;
import com.howell.protocol.QueryDeviceReq;
import com.howell.protocol.SoapManager;

import java.util.ArrayList;

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
    public HomeAction setContext(Context c){
        this.mContext = c;
        return this;
    }
    private HomeAction(){}

    private QueryDeviceCallback mQueryDeviceCallback;

    public QueryDeviceCallback unregistQueryDeviceCallback() {
        return mQueryDeviceCallback;
    }

    public HomeAction registQueryDeviceCallback(QueryDeviceCallback mQueryDeviceCallback) {
        this.mQueryDeviceCallback = mQueryDeviceCallback;
        return this;
    }

    private SoapManager mSoapManager = SoapManager.getInstance();
    private ArrayList<NodeDetails> mList;

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

    public interface QueryDeviceCallback{
        void onQueryDeviceSuccess(ArrayList<NodeDetails> l);
        void onQueryDeviceError();
    }

}
