package com.howell.activity.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.howell.webcam.R;
import com.howell.protocol.QueryDeviceSharerReq;
import com.howell.protocol.SoapManager;

/**
 * Created by Administrator on 2017/7/14.
 */

public class ShareHistoryFragment extends ShareBaseFragment {
    View mView;
    RecyclerView mRV;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_share3,container,false);
        mRV = (RecyclerView) mView.findViewById(R.id.share_item3_lv);

        init();
        return mView;
    }

    private void init(){
        getData();
    }

    private void getData(){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                //

//                SoapManager.getInstance().getQueryDeviceSharerRes(new QueryDeviceSharerReq(
//
//                ))
                return null;
            }
        }.execute();



    }




    @Override
    public void fun(int flag) {

    }
}
