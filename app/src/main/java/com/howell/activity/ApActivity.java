package com.howell.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.howell.utils.AlerDialogUtils;
import com.howell.utils.Util;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * Created by howell on 2016/12/2.
 */

public class ApActivity extends DaggerAppCompatActivity implements IDeviceContract.IVew {

//    ImageButton mBack;
    Toolbar mTb;
    AutoCompleteTextView mName,mIP,mPort;
    Button mBtn;
    MyPostListener mMyPostListener = new MyPostListener();
    boolean mSuccess = false;

    @Inject
    IDeviceContract.IPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ap);
        bindPresenter();
        initView();
        initToolbar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindPresenter();
    }

    private void initView(){
//        mBack = (ImageButton) findViewById(R.id.add_ap_ib_back);
//        mBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
        mName = (AutoCompleteTextView) findViewById(R.id.add_ap_et_deviceName);
        mIP = (AutoCompleteTextView) findViewById(R.id.add_ap_et_ip);
        mPort = (AutoCompleteTextView) findViewById(R.id.add_ap_et_port);
        mBtn = (Button) findViewById(R.id.add_ap_btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFun();
            }
        });
    }

    private void initToolbar(){
        mTb = (Toolbar) findViewById(R.id.add_ap_toolbar);
//        mTb.setNavigationIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_chevron_left).actionBar().color(Color.WHITE));

//        mTb.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_theaters_white_24dp));
        // mTb.showOverflowMenu();
        mTb.setTitle(getString(R.string.add_camera_title));
        mTb.setSubtitle(getString(R.string.add_ap));
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
        String name = mName.getText().toString();
        String ip = mIP.getText().toString();
        String port = mPort.getText().toString();
        
        mName.setError(null);
        mIP.setError(null);
        mPort.setError(null);
        View focusView = null;
        if (!Util.isInteger(port)){
            mPort.setError(getString(R.string.add_ap_port_error));
            focusView = mPort;
        }
        if (port.equals("")){
            mPort.setError(getString(R.string.reg_field_empty));
            focusView = mPort;
        }
        if (!Util.isIP(ip)){
            mIP.setError(getString(R.string.add_ap_ip_error));
            focusView = mIP;
        }
        if (ip.equals("")){
            mIP.setError(getString(R.string.reg_field_empty));
            focusView = mIP;
        }
        if (name.equals("")){
            mName.setError(getString(R.string.reg_field_empty));
            focusView = mName;
        }

        if (focusView!=null){
            focusView.requestFocus();
            return;
        }
        //// TODO: 2016/12/2
        Log.i("123","add device");
        mPresenter.addDevice(new CameraItemBean()
                                .setType(PlayType.HW5198)
                                .setUpnpIP(ip).setUpnpPort(Integer.valueOf(port))
                                .setCameraName(name));
    }

    @Override
    public void bindPresenter() {
//        if (mPresenter==null){
//            mPresenter = new DeviceSoapPresenter();
//        }
        mPresenter.bindView(this);
        mPresenter.init(this);
    }

    @Override
    public void unbindPresenter() {
        if (mPresenter!=null){
            mPresenter.unbindView();
        }
    }

    @Override
    public void onQueryResult(List<CameraItemBean> beanList) {

    }

    @Override
    public void onAddResult(boolean isSuccess, PlayType type) {
        if (type!=PlayType.HW5198)return;
        mSuccess = isSuccess;
        AlerDialogUtils.postDialogMsg(this,getString(R.string.add_ap_dialog_title),getString(R.string.match_activity_success_dialog_title),mMyPostListener);
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

    class MyPostListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (mSuccess){
                finish();
            }else{
                finish();
            }
        }
    }
}
