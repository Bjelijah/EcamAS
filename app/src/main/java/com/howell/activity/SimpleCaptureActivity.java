package com.howell.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.howell.utils.JsonUtil;

import org.json.JSONException;

import io.github.xudaojie.qrcodelib.CaptureActivity;

/**
 * Created by xdj on 16/9/17.
 */

public class SimpleCaptureActivity extends CaptureActivity {
    protected Activity mActivity = this;

    private AlertDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mActivity = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void handleResult(String resultString) {
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
            foo(resultString);

        }
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
