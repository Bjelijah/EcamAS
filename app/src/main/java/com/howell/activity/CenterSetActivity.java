package com.howell.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.howell.action.CenterAction;
import com.howell.action.HomeAction;
import com.howell.ecam.R;
import com.howell.utils.IConst;
import com.howell.utils.ServerConfigSp;
import com.howell.utils.Util;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

/**
 * Created by Administrator on 2017/2/6.
 */

public class CenterSetActivity extends AppCompatActivity implements View.OnClickListener,IConst{
    public static final int MSG_CENTER_SET_WAIT = 0x00;
    AutoCompleteTextView mIPView,mPortView;
    Toolbar mTb;
    Button mbtnSave,mbtnDefault;
    private ProgressDialog mPd;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CENTER_SET_WAIT:
                    mPd.dismiss();
                    finish();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_setting);
        initView();
        initToolbar();
        initSetting();
    }

    private void initView(){
        mIPView = (AutoCompleteTextView) findViewById(R.id.center_set_et_ip);
        mPortView = (AutoCompleteTextView) findViewById(R.id.center_set_et_port);
        mbtnDefault = (Button) findViewById(R.id.center_set_default_btn);
        mbtnSave = (Button) findViewById(R.id.center_set_btn);
        mbtnDefault.setOnClickListener(this);
        mbtnSave.setOnClickListener(this);
    }

    private void initToolbar(){
        mTb = (Toolbar) findViewById(R.id.center_set_toolbar);
        //FIXME we ues default navigation icon for back button
//        mTb.setNavigationIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_chevron_left).actionBar().color(Color.WHITE));

//        mTb.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_theaters_white_24dp));
        // mTb.showOverflowMenu();
        mTb.setTitle(getString(R.string.center_setting_title));
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

    private void initSetting(){
        mIPView.setText(ServerConfigSp.loadCenterIP(this));
        mPortView.setText(ServerConfigSp.loadCenterPort(this)+"");
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.center_set_default_btn:
                defaultFun();
                break;
            case R.id.center_set_btn:
                fun();
                break;
            default:
                break;
        }
    }

    private void waitShow(String title,String msg,long autoDismissMS){
        mPd = new ProgressDialog(this);
        mPd.setTitle(title);
        mPd.setMessage(msg);
        mPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPd.show();
        if (autoDismissMS>0){
            mHandler.sendEmptyMessageDelayed(MSG_CENTER_SET_WAIT,autoDismissMS);
        }
    }

    private void fun(){
        mIPView.setError(null);
        mPortView.setError(null);

        String ip = mIPView.getText().toString();
        String port = mPortView.getText().toString();

        View v = null;
        if (!Util.isInteger(port)){
            mPortView.setError(getString(R.string.add_ap_port_error));
            v = mPortView;
        }
        if (port.equals("")){
            mPortView.setError(getString(R.string.reg_field_empty));
            v= mPortView;
        }
        if (!Util.hasDot(ip)){
            mIPView.setError(getString(R.string.add_ap_ip_error));
            v = mIPView;
        }
        if (ip.equals("")){
            mIPView.setError(getString(R.string.reg_field_empty));
            v = mIPView;
        }
        if (v!=null){
            Log.i("123","v!=null");
            v.requestFocus();
            return;
        }else{
            Log.i("123","v=null");
        }
        save(ip,Integer.valueOf(port));
    }

    private void defaultFun(){
        mIPView.setText(DEFAULT_CENTER_IP);
        mPortView.setText(DEFAULT_CENTER_PORT+"");
    }


    private void save(String ip,int port){
        String _ip;
        if (Util.isIP(ip)){
            _ip = ip;
        }else{
            _ip = Util.parseIP(ip);
        }
        CenterAction.getInstance().setIp(_ip).setPort(port);
        ServerConfigSp.saveCenterInfo(this,_ip,port);
        waitShow(getResources().getString(R.string.camera_setting_save_title),getResources().getString(R.string.camera_setting_save_msg),1000);
    }

}
