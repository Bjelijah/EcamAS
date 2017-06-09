package com.howell.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.howell.webcam.R;
import com.howell.pushlibrary.DaemonEnv;
import com.howell.pushlibrary.IntentWrapper;
import com.howell.service.MyService;
import com.howell.utils.ServerConfigSp;
//import com.xdandroid.hellodaemon.IntentWrapper;


/**
 * Created by Administrator on 2017/6/8.
 */

public class PushSettingActivity extends AppCompatActivity {
    TextView mTv;
    Switch mSwith;
    Toolbar mTb;
    boolean mIsPush;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_setting);
        initToolbar();
        initView();
        initFun();
    }

    private void initToolbar(){
        mTb = (Toolbar) findViewById(R.id.push_set_toolbar);
//        mTb.setNavigationIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_chevron_left).actionBar().color(Color.WHITE));

//        mTb.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_theaters_white_24dp));
        // mTb.showOverflowMenu();
        mTb.setTitle(getString(R.string.push_settting_title));
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

    private void initView(){
        mTv = (TextView) findViewById(R.id.push_set_tv);
        mSwith = (Switch) findViewById(R.id.push_set_sw);


        mSwith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsPush =  mSwith.isChecked();
                Log.e("123","onchecked changed ischecked="+mIsPush);
                IntentWrapper.whiteListMatters(PushSettingActivity.this,null);
            }
        });


    }

    private void initFun(){
        mIsPush = ServerConfigSp.loadPushOnOff(this);
        mSwith.setChecked(mIsPush);
    }


    private void foo(){
        ServerConfigSp.savePushOnOff(this,mIsPush);
        if (mIsPush){

            startService(new Intent(this, MyService.class));
        }else {
            MyService.stopService();
            stopService(new Intent(this,MyService.class));
            MyService.isWorking = false;
        }


    }

    @Override
    protected void onDestroy() {
        foo();
        super.onDestroy();
    }
}
