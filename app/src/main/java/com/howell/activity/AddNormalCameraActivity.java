package com.howell.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.android.howell.webcam.R;
import com.howell.action.LoginAction;
import com.howell.protocol.AddDeviceReq;
import com.howell.protocol.AddDeviceRes;
import com.howell.protocol.SoapManager;

/**
 * Created by Administrator on 2017/6/5.
 */

public class AddNormalCameraActivity extends AppCompatActivity {
    private static final int MSG_OK = 0xa0;
    private static final int MSG_FAIL = 0xa1;
    String mDevId,mDevKey,mSerial;
    View mView;
    Toolbar mTb;
    AutoCompleteTextView mName;
    Button mBtn;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_OK:
                    finish();
                    break;
                case MSG_FAIL:
                    Snackbar.make(mView,getString(R.string.add_fail),Snackbar.LENGTH_LONG).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_normal_device);
        Intent intent = getIntent();
        mDevId = intent.getStringExtra("devID");
        mDevKey = intent.getStringExtra("devKey");
        mSerial = intent.getStringExtra("serial");
        //fixme for test
//        mDevId = "007195c81ca747dc850e";
//        mDevKey = "309d38100f32";



        Log.i("123","id="+mDevId+"  key="+mDevKey+"   serial="+mSerial);
        initView();
        initToolbar();
    }

    private void initView(){
        mName = (AutoCompleteTextView) findViewById(R.id.add_normal_et_deviceName);
        mBtn = (Button) findViewById(R.id.add_normal_btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFun();
            }
        });
        mView = findViewById(R.id.normal_form);
    }


    private void initToolbar(){
        mTb = (Toolbar) findViewById(R.id.add_normal_toolbar);
//        mTb.setNavigationIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_chevron_left).actionBar().color(Color.WHITE));

//        mTb.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_theaters_white_24dp));
        // mTb.showOverflowMenu();
        mTb.setTitle(getString(R.string.add_camera_title));
        mTb.setSubtitle(getString(R.string.add_normal));
        setSupportActionBar(mTb);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        mTb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void addFun(){
        final String devName = mName.getText().toString();
        new AsyncTask<Void,Void,Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                String account = LoginAction.getInstance().getmInfo().getAccount();
                String session = LoginAction.getInstance().getmInfo().getLr().getLoginSession();
                AddDeviceRes res =  SoapManager.getInstance().getAddDeviceRes(new AddDeviceReq(account,session,mDevId,mDevKey,devName,false));
                Log.i("123","add fun res = "+res.getResult());
                String result = res.getResult();
                if (result.equalsIgnoreCase("ok")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean){
                    mHandler.sendEmptyMessage(MSG_OK);
                }else{
                    mHandler.sendEmptyMessage(MSG_FAIL);
                }
            }
        }.execute();
    }



}
