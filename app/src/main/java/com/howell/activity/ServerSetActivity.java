package com.howell.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.howell.action.HomeAction;
import com.howell.ecam.R;
import com.howell.utils.IConst;
import com.howell.utils.ServerConfigSp;
import com.howell.utils.Util;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

/**
 * Created by howell on 2016/12/6.
 */

public class ServerSetActivity extends AppCompatActivity implements IConst{
    public static final int MSG_SERVER_SET_WAIT = 0x00;
    Toolbar mTb;
    AutoCompleteTextView mIPView,mPortView;
    Button mbtnSave,mbtnDefault;
    private ProgressDialog mPd;
//    ImageButton mBack;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SERVER_SET_WAIT:
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
        setContentView(R.layout.activity_server_address);
        initView();
        initToolbar();
        initSetting();
    }

    private void initView(){
        mIPView = (AutoCompleteTextView) findViewById(R.id.server_set_et_ip);
        mPortView = (AutoCompleteTextView) findViewById(R.id.server_set_et_port);
        mbtnSave = (Button) findViewById(R.id.server_set_btn);
        mbtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fun();
            }
        });
        mbtnDefault = (Button) findViewById(R.id.server_set_default_btn);
        mbtnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIPView.setText(DEFAULT_TURN_SERVER_IP);
                mPortView.setText(DEFAULT_TURN_SERVER_PORT+"");
            }
        });
//        mBack = (ImageButton) findViewById(R.id.server_set_ib_back);
//        mBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
    }

    private void initToolbar(){
        mTb = (Toolbar) findViewById(R.id.server_set_toolbar);
//        mTb.setNavigationIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_chevron_left).actionBar().color(Color.WHITE));

//        mTb.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_theaters_white_24dp));
        // mTb.showOverflowMenu();
        mTb.setTitle(getString(R.string.server_setting_title));
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

        mIPView.setText(ServerConfigSp.loadServerIP(this));
        mPortView.setText(ServerConfigSp.loadServerPort(this)+"");

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

    private void waitShow(String title,String msg,long autoDismissMS){
        mPd = new ProgressDialog(this);
        mPd.setTitle(title);
        mPd.setMessage(msg);
        mPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPd.show();
        if (autoDismissMS>0){
            mHandler.sendEmptyMessageDelayed(MSG_SERVER_SET_WAIT,autoDismissMS);
        }
    }





    private void save(String ip,int port){
        String _ip;
        if (Util.isIP(ip)){
            _ip = ip;
        }else{
            _ip = Util.parseIP(ip);
        }
        HomeAction.getInstance().setServiceIPAndPort(_ip,port);
        ServerConfigSp.saveServerInfo(this,_ip,port);
        waitShow(getResources().getString(R.string.camera_setting_save_title),getResources().getString(R.string.camera_setting_save_msg),1000);
    }



}
