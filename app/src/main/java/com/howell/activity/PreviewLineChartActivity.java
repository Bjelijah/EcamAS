package com.howell.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.howell.webcam.R;

import com.howell.activity.fragment.PreviewLineChartFragment;

/**
 * Created by Administrator on 2017/10/30.
 */

public class PreviewLineChartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_line_chart);
        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().add(R.id.plc_container,new PreviewLineChartFragment()).commit();
        }
    }
}
