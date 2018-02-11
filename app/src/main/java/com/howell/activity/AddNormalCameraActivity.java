package com.howell.activity;

import android.content.Intent;
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
import com.howell.bean.CameraItemBean;
import com.howell.bean.PlayType;
import com.howell.modules.device.IDeviceContract;
import com.howell.modules.device.presenter.DeviceSoapPresenter;

import java.util.List;

/**
 * Created by Administrator on 2017/6/5.
 */

public class AddNormalCameraActivity extends AppCompatActivity implements IDeviceContract.IVew {
    private static final int MSG_OK = 0xa0;
    private static final int MSG_FAIL = 0xa1;
    String mDevId,mDevKey,mSerial;
    View mView;
    Toolbar mTb;
    AutoCompleteTextView mName;
    Button mBtn;
    IDeviceContract.IPresenter mPresenter;

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

        mPresenter.addDevice(new CameraItemBean()
                .setType(PlayType.ECAM)
                .setDeviceId(mDevId)
                .setDevKey(mDevKey)
                .setCameraName(devName)
        );


    }


    @Override
    public void bindPresenter() {
        if (mPresenter==null){
            mPresenter = new DeviceSoapPresenter();
        }
        mPresenter.bindView(this);
        mPresenter.init(this);
    }

    @Override
    public void unbindPresenter() {
        if (mPresenter!=null){
            mPresenter.unbindView();
            mPresenter = null;
        }
    }

    @Override
    public void onQueryResult(List<CameraItemBean> beanList) {

    }

    @Override
    public void onAddResult(boolean isSuccess, PlayType type) {
        if (isSuccess) {
            mHandler.sendEmptyMessage(MSG_OK);
        } else {
            mHandler.sendEmptyMessage(MSG_FAIL);
        }
    }

    @Override
    public void onRemoveResult(boolean isSuccess, int pos) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onUpdateCamBean(@Nullable Boolean isTurn, @Nullable Boolean isCrypto) {

    }

    @Override
    public void onDeviceMatchCode(String s) {

    }
}
