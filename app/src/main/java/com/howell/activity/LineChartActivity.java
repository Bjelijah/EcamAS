package com.howell.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.howell.webcam.R;
import com.howell.activity.fragment.LineChartFragment;

/**
 * Created by Administrator on 2017/10/27.
 */

public class LineChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().add(R.id.charts_container,new LineChartFragment()).commit();
        }
    }
}
