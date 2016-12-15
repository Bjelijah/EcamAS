package com.howell.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.howell.ecam.R;
import com.howell.utils.ServerConfigSp;
import com.howell.utils.Util;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

/**
 * Created by howell on 2016/12/6.
 */

public class ServerSetActivity extends AppCompatActivity {
    Toolbar mTb;
    AutoCompleteTextView mIPView,mPortView;
    Button mbtn;
//    ImageButton mBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_address);
        initView();
        initToolbar();
    }

    private void initView(){
        mIPView = (AutoCompleteTextView) findViewById(R.id.server_set_et_ip);
        mPortView = (AutoCompleteTextView) findViewById(R.id.server_set_et_port);
        mbtn = (Button) findViewById(R.id.server_set_btn);
        mbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fun();
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
        mTb.setNavigationIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_chevron_left).actionBar().color(Color.WHITE));

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
        if (!Util.isIP(ip)){
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

    private void save(String ip,int port){
        ServerConfigSp.saveServerInfo(this,ip,port);
    }



}
