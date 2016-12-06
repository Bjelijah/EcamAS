package com.howell.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.howell.ecam.R;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.wyy.twodimcode.CaptureActivity;

/**
 * Created by howell on 2016/11/21.
 */

public class AddNewCamera extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        init();
//        animator();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }




    private void init(){
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.add_camera);
//        Bitmap bitmap = getIntent().getParcelableExtra("bitmap_bk");
        Bitmap bitmap = HomeExActivity.sBkBitmap;
      //  blur(bitmap,layout);
        layout.setBackground(new BitmapDrawable(bitmap));

        FloatingActionButton fabBack = (FloatingActionButton) findViewById(R.id.add_camera_fab_back);
        fabBack.setImageDrawable(new IconicsDrawable(this,  Octicons.Icon.oct_sign_out).actionBar().color(Color.WHITE));
        fabBack.setOnClickListener(this);

        FloatingActionButton fabAp = (FloatingActionButton) findViewById(R.id.add_camera_fab_ap);
        fabAp.setImageDrawable(new IconicsDrawable(this,  Octicons.Icon.oct_device_camera).actionBar().color(Color.WHITE));
        fabAp.setOnClickListener(this);
        Button btnAp = (Button) findViewById(R.id.add_camera_btn_ap);
        btnAp.setOnClickListener(this);

        FloatingActionButton fabFlash = (FloatingActionButton) findViewById(R.id.add_camera_fab_flash);
        fabFlash.setImageDrawable(new IconicsDrawable(this,  Octicons.Icon.oct_zap).actionBar().color(Color.WHITE));
        fabFlash.setOnClickListener(this);
        Button btnFlash = (Button) findViewById(R.id.add_camera_btn_flash);
        btnFlash.setOnClickListener(this);

        FloatingActionButton fabListen = (FloatingActionButton) findViewById(R.id.add_camera_fab_listen);
        fabListen.setImageDrawable(new IconicsDrawable(this,  Octicons.Icon.oct_megaphone).actionBar().color(Color.WHITE));
        fabListen.setOnClickListener(this);
        Button btnListen = (Button) findViewById(R.id.add_camera_btn_listen);
        btnListen.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_camera_fab_back:
                onBackPressed();
                break;
            case R.id.add_camera_fab_ap://添加ap
            case R.id.add_camera_btn_ap:
                Intent apIntent = new Intent(this,ApActivity.class);
                startActivity(apIntent);
                break;
            case R.id.add_camera_fab_flash://添加扫一扫
            case R.id.add_camera_btn_flash:
                Intent captureIntent = new Intent(this,CaptureActivity.class);
                startActivity(captureIntent);
                break;
            case R.id.add_camera_fab_listen://添加听一听
            case R.id.add_camera_btn_listen:
                Intent intent = new Intent(this,DeviceWifiActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}