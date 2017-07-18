package com.howell.activity.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import com.android.howell.webcam.R;
import com.howell.action.LoginAction;
import com.howell.protocol.AddDeviceSharerReq;
import com.howell.protocol.AddDeviceSharerRes;
import com.howell.protocol.SoapManager;

import java.util.concurrent.ExecutorService;

/**
 * Created by Administrator on 2017/7/14.
 */

public class ShareAddFragment extends ShareBaseFragment {
    private static final int MSG_SHARE_ADD_SHARE_OK = 0x20;
    private static final int MSG_SHARE_ADD_SHARE_ERROR = 0x21;
    EditText mEt;
//    ImageButton mIb;
    Button mBtn;
    View mView;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public ShareAddFragment(String devID, String devName,int channelNo) {
        super(devID, devName,channelNo);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_share2,container,false);
        mEt = (EditText) mView.findViewById(R.id.share_item2_et);
//        mIb = (ImageButton) mView.findViewById(R.id.share_item2_ib);
        mBtn = (Button) mView.findViewById(R.id.share_item2_btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFun();
            }
        });
        return mView;
    }

    private void shareFun(){
        final String shareAccount = mEt.getText().toString();
        new AsyncTask<Void,Void,Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                SoapManager s = SoapManager.getInstance();
                AddDeviceSharerRes res = s.getAddDeviceSharerRes(new AddDeviceSharerReq(
                        LoginAction.getInstance().getmInfo().getAccount(),
                        LoginAction.getInstance().getmInfo().getLr().getLoginSession(),
                        mDevID,
                        mChannelNo,
                        shareAccount));
                Log.e("123","res="+res.toString());
                if (res.getResult().equalsIgnoreCase("ok"))return true;

                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                mHandler.sendEmptyMessage(aBoolean?MSG_SHARE_ADD_SHARE_OK:MSG_SHARE_ADD_SHARE_ERROR);
                if (aBoolean){
                    //TODO add db,

                }
            }
        }.execute();








    }

}
