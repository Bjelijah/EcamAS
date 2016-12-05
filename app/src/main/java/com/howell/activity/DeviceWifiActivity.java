package com.howell.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.howell.ecam.R;
import com.howell.utils.NetWorkUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;

/**
 * Created by howell on 2016/12/2.
 */

public class DeviceWifiActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION= 0x1234;
    Toolbar mTb;
    ImageButton mBack;
    LinearLayout mTip;
    AutoCompleteTextView mDeviceName;
    AutoCompleteTextView mWifiPwd;
    Spinner mSpinner;
    ImageView mIvTip;
    Button mBtn;
    MyWifiSearchReceive mReceive = new MyWifiSearchReceive();
    private NetWorkUtils mWifiAdmin;
    private String[] mWifiMember;
    private ArrayAdapter<String> myAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Window window = getWindow();
//        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_listen_device);
        initView();
//        initToobar();
        initFun();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: search wifi
        mWifiAdmin.scan(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getScanningResults();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceive);
        super.onDestroy();
    }

    private void initView(){
        mBack = (ImageButton) findViewById(R.id.ib_set_device_wifi_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTip = (LinearLayout) findViewById(R.id.add_listen_ll_wifi_tip);
        mTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                DeviceWifiActivity.this.startActivity(intent);
            }
        });
        mDeviceName = (AutoCompleteTextView) findViewById(R.id.add_listen_et_deviceName);
        mWifiPwd = (AutoCompleteTextView) findViewById(R.id.add_listen_et_wifiPassword);
        mSpinner = (Spinner) findViewById(R.id.add_listen_spinner_wifi);
        mIvTip = (ImageView) findViewById(R.id.add_listen_iv_wifi_tip);
        mIvTip.setImageDrawable(new IconicsDrawable(this,  Octicons.Icon.oct_location).actionBar().color(getResources().getColor(R.color.accent)));
        //image:
//        GoogleMaterial.Icon.gmd_gps
        mBtn = (Button) findViewById(R.id.add_listen_btn_next);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextFun();
            }
        });
    }

    private void initFun(){
        registerReceiver(mReceive,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiAdmin = new NetWorkUtils(this);
        mWifiAdmin.scan(this);
//        mWifiMember = new String[0];
//        myAdapter =  new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mWifiMember);
//        myAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//        mSpinner.setAdapter(myAdapter);
    }

    private void initToobar(){
//        mTb = (Toolbar) findViewById(R.id.listen_device_tb);
        mTb.setNavigationIcon(new IconicsDrawable(this, Octicons.Icon.oct_chevron_left).actionBar().color(Color.WHITE));

        mTb.showOverflowMenu();
        mTb.setTitle(getString(R.string.add_listen));
        mTb.setSubtitle(getString(R.string.add_listen_subtitle));
        setSupportActionBar(mTb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mTb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void getScanningResults(){
        ArrayList<String> list = mWifiAdmin.getSSIDResultList();
        if (list.size()==0){
            showTip(true);
            return;
        }else{
            showTip(false);
        }

        mWifiMember = new String[list.size()];
        list.toArray(mWifiMember);
        myAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mWifiMember);
        myAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner.setAdapter(myAdapter);
    }


    private void showTip(boolean bShow){
        mSpinner.setVisibility(bShow?View.GONE:View.VISIBLE);
        mTip.setVisibility(bShow? View.VISIBLE: View.GONE);
    }

    private void nextFun(){
        View focusView = null;
        mDeviceName.setError(null);
        mWifiPwd.setError(null);
        String name = mDeviceName.getText().toString();
        String pwd = mWifiPwd.getText().toString();
        if (mSpinner.getSelectedItem()==null || mSpinner.getSelectedItem().toString().equals("")){
            Snackbar.make(mSpinner,getString(R.string.add_listen_device_error_wifi), Snackbar.LENGTH_LONG).show();
            return;
        }
        if (name.equals("")){
            mDeviceName.setError(getString(R.string.reg_field_empty));
            focusView = mDeviceName;
        }
        if (pwd.equals("")){
            mWifiPwd.setError(getString(R.string.reg_field_empty));
            focusView = mWifiPwd;
        }
        if (focusView!=null){
            focusView.requestFocus();
            return;
        }
        //todo: click
        Intent intent = new Intent(this,FlashLighting.class);
        intent.putExtra("wifi_ssid", mSpinner.getSelectedItem().toString());
        intent.putExtra("wifi_password", mWifiPwd.getText().toString());
        intent.putExtra("device_name", mDeviceName.getText().toString());
        startActivity(intent);
    }


    class MyWifiSearchReceive extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                }else{
                    getScanningResults();
                    //do something, permission was previously granted; or legacy device
                }
            }
        }
    }


}
