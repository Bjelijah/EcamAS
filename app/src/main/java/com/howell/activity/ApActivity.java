package com.howell.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import com.howell.action.ApAction;
import com.howell.ecam.R;
import com.howell.utils.AlerDialogUtils;
import com.howell.utils.Util;

/**
 * Created by howell on 2016/12/2.
 */

public class ApActivity extends AppCompatActivity {

    ImageButton mBack;
    AutoCompleteTextView mName,mIP,mPort;
    Button mBtn;
    MyPostListener mMyPostListener = new MyPostListener();
    boolean mSuccess = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ap);
        initView();
    }
    
    private void initView(){
        mBack = (ImageButton) findViewById(R.id.add_ap_ib_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

        if ( ApAction.getInstance().addAP2DB(this,name,ip,port)){
            mSuccess = true;
        }else{
            mSuccess = false;
        }
        AlerDialogUtils.postDialogMsg(this,getString(R.string.add_ap_dialog_title),getString(R.string.match_activity_success_dialog_title),mMyPostListener);
    }

    class MyPostListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (mSuccess){
                finish();
            }else{

            }
        }
    }
}
