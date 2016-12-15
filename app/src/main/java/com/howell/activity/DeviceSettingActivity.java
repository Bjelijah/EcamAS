package com.howell.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.howell.ecam.R;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

/**
 * Created by howell on 2016/12/8.
 */

public class DeviceSettingActivity extends AppCompatActivity {

    Toolbar mTb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_setting);

        initToolbar();

    }

    private void initToolbar(){
        mTb = (Toolbar) findViewById(R.id.camera_setting_toolbar);

//        Octicons.Icon.oct_chevron_left
//        GoogleMaterial.Icon.gmd_arrow_left_bottom
        mTb.setNavigationIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_chevron_left).actionBar().color(Color.WHITE));

//        mTb.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_theaters_white_24dp));
       // mTb.showOverflowMenu();
        mTb.setTitle(getString(R.string.camera_settting_title));
//        mTb.setSubtitle(getString(R.string.add_listen_subtitle));
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

}
