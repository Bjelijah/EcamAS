package com.howell.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.howell.webcam.R;
import com.howell.activity.fragment.LineChartFragment;

/**
 * Created by Administrator on 2017/10/27.
 */

public class LineChartActivity extends AppCompatActivity {

    Button btn1,btn2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        btn1 = (Button) findViewById(R.id.lc_btn1);
//        btn1.setOnClickListener(v->{
//            startActivity(new Intent(LineChartActivity.this,PreviewLineChartActivity.class));
//        });
//        btn2 = (Button) findViewById(R.id.lc_btn2);
//        btn2.setOnClickListener(v->{
//            startActivity(new Intent(LineChartActivity.this,PreviewLineChartActivity.class));
//        });

        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().add(R.id.charts_container,new LineChartFragment()).commit();
        }
    }
}
