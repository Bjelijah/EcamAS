package com.howell.activity.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.howell.adapter.DeviceRecyclerViewAdapter;
import com.howell.ecam.R;

import java.util.ArrayList;
import java.util.List;

import pullrefreshview.layout.BaseFooterView;
import pullrefreshview.layout.BaseHeaderView;

/**
 * Created by howell on 2016/11/11.
 */

public class DeviceFragment extends Fragment implements BaseHeaderView.OnRefreshListener,BaseFooterView.OnLoadListener, DeviceRecyclerViewAdapter.OnItemClickListener  {
    public static final int MSG_RECEIVE_SIP = 0x0000;
    View mView;
    RecyclerView mRV;
    BaseHeaderView mbhv;
    BaseFooterView mbfv;
    List<String> mList = new ArrayList<String>();
    int page = 1;
    DeviceRecyclerViewAdapter adapter;


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_RECEIVE_SIP:
                    if (!isInList(msg.obj.toString())){
                        mList.add(msg.obj.toString());
                        adapter.setData(mList);
                    }
                    break;
            }
        }
    };



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_device,container,false);
        mRV = (RecyclerView) mView.findViewById(R.id.device_rv);
        mbhv = (BaseHeaderView) mView.findViewById(R.id.device_header);
        mbfv = (BaseFooterView) mView.findViewById(R.id.device_footer);
        mbhv.setOnRefreshListener(this);
        mbfv.setOnLoadListener(this);
        mList = getData(0);

        adapter = new DeviceRecyclerViewAdapter(mList,this);
        mRV.setLayoutManager(new LinearLayoutManager(getContext()));
        mRV.setAdapter(adapter);


        return mView;
    }


    private List<String> getData(int n) {
        List<String> datas = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            datas.add("第" + page + "页,第" + i + "条");
        }
        return datas;
    }

    @Override
    public void onLoad(BaseFooterView baseFooterView) {
        Log.i("123","onLoad");
        baseFooterView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mbfv.stopLoad();
            }
        },3000);

    }

    @Override
    public void onRefresh(BaseHeaderView baseHeaderView) {
        Log.i("123","onRefresh");
        baseHeaderView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mbhv.stopRefresh();
            }
        },3000);
    }

    @Override
    public void onItemClickListener(View v, int pos) {
        Log.i("123","onItemClickListener   pos="+pos);
        String ip = mList.get(pos);

    }

    private boolean isInList(String str){
        for (String s:mList){
            if (str.equals(s)){
                return true;
            }
        }
        return false;
    }
}
