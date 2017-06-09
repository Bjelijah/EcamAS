package com.howell.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.howell.webcam.R;
import com.howell.utils.JsonUtil;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import io.github.xudaojie.qrcodelib.CaptureActivity;

/**
 * Created by xdj on 16/9/17.
 */

public class SimpleCaptureActivity extends CaptureActivity {
    protected Activity mActivity = this;
    Toolbar mTb ;
    private AlertDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mActivity = this;
        super.onCreate(savedInstanceState);
        initToolbar();
    }

    @Override
    protected void handleResult(String resultString) {
        Log.i("123","resultString="+resultString);
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(mActivity, io.github.xudaojie.qrcodelib.R.string.scan_failed, Toast.LENGTH_SHORT).show();
            restartPreview();
        } else {
//            if (mDialog == null) {
//                mDialog = new AlertDialog.Builder(mActivity)
//                        .setMessage(resultString)
//                        .setPositiveButton("确定", null)
//                        .create();
//                mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        restartPreview();
//                    }
//                });
//            }
//            if (!mDialog.isShowing()) {
//                mDialog.setMessage(resultString);
//                mDialog.show();
//            }



            byte [] data = Base64.decode(resultString.getBytes(),Base64.DEFAULT);
            String str = null;
            str = new String(data, StandardCharsets.UTF_8);
            Log.i("123","str="+str);
            foo(str);

        }
    }


    private void initToolbar(){
        mTb = (Toolbar) findViewById(R.id.add_scan_toolbar);
//        mTb.setNavigationIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_chevron_left).actionBar().color(Color.WHITE));

//        mTb.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_theaters_white_24dp));
        // mTb.showOverflowMenu();
        mTb.setTitle(getString(R.string.add_camera_title));
        mTb.setSubtitle(getString(R.string.add_scan));
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
        mTb.setBackgroundColor(getResources().getColor(R.color.black));
    }


    private void foo(String jsonStr){
        Bundle bundle = null;
        String id=null,key=null,serial=null;
        try {
            bundle = JsonUtil.getDeviceIdANDKey(jsonStr);
            id = bundle.getString("id");
            key = bundle.getString("key");
            serial = bundle.getString("serial");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (id==null||key==null)return;
        Log.i("123","id="+id+"   key="+key);
        //todo add device
        Intent intent = new Intent(this,AddNormalCameraActivity.class);
        intent.putExtra("devID",id);
        intent.putExtra("devKey",key);
        intent.putExtra("serial",serial);
        startActivity(intent);
        finish();
    }


}
