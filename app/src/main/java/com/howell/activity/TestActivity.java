package com.howell.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.android.howell.webcam.R;
import com.howell.modules.pdc.IPDCContract;
import com.howell.modules.pdc.presenter.PDCPresenter;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/11/23.
 */

public class TestActivity extends AppCompatActivity implements IPDCContract.IVew{
    IPDCContract.IPresent mPresent;
    Button btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        bindPresenter();
        btn = (Button) findViewById(R.id.test_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresent.test();
            }
        });
    }

    @Override
    protected void onDestroy() {
        unbindPresenter();
        super.onDestroy();
    }




    @Override
    public void bindPresenter() {
        if (mPresent==null){
            mPresent = new PDCPresenter();
        }
        mPresent.bindView(this);
        mPresent.init(this);
    }

    @Override
    public void unbindPresenter() {
        if (mPresent!=null){
            mPresent.unbindView();
            mPresent=null;
        }
    }
}
