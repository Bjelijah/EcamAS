package com.howell.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.howell.webcam.R;
import com.howell.di.ui.activity.AddNewCameraModule;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by howell on 2016/11/21.
 */

public class AddNewCameraActivity extends DaggerAppCompatActivity implements View.OnClickListener {

    @Inject @Named(AddNewCameraModule.INTENT_AP)
    Intent mAPIntent;

    @Inject @Named(AddNewCameraModule.INTENT_QR)
    Intent mQrIntent;

    @Inject @Named(AddNewCameraModule.INTENT_WIFI)
    Intent mWifiIntent;



    RelativeLayout mrl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        init();
        initBk();
        Activities.getInstance().addActivity(this.getLocalClassName(),this);
    }

    @Override
    protected void onDestroy() {
        Activities.getInstance().removeActivity(this.getLocalClassName());
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initBk(){
        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Bitmap> e) throws Exception {
                e.onNext(bulrBitmap(HomeExActivity.sBkBitmap));
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        TransitionDrawable transition = new TransitionDrawable(new Drawable[]{
                                new ColorDrawable(0x00000000),
                                new BitmapDrawable(getResources(),bitmap)});
                        mrl.setBackground(transition);
                        transition.startTransition(1000);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void init(){
        mrl = (RelativeLayout) findViewById(R.id.add_camera);
//        Bitmap bitmap = getIntent().getParcelableExtra("bitmap_bk");
//        Bitmap bitmap = HomeExActivity.sBkBitmap;
        //  blur(bitmap,layout);
//        mrl.setBackground(new BitmapDrawable(bitmap));

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
//                Intent apIntent = new Intent(this,ApActivity.class);
                startActivity(mAPIntent);
                break;
            case R.id.add_camera_fab_flash://添加扫一扫
            case R.id.add_camera_btn_flash:
//                Intent captureIntent = new Intent(this,SimpleCaptureActivity.class);
                startActivity(mQrIntent);
                break;
            case R.id.add_camera_fab_listen://添加听一听
            case R.id.add_camera_btn_listen:
//                Intent intent = new Intent(this,DeviceWifiActivity.class);
                startActivity(mWifiIntent);
                break;
            default:
                break;
        }
    }
    private Bitmap bulrBitmap(Bitmap bitmap){
        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),Bitmap.Config.ARGB_8888);
        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(getApplicationContext());

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur
        blurScript.setRadius(25.f);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
        bitmap.recycle();

        //After finishing everything, we destroy the Renderscript.
        rs.destroy();
        return outBitmap;
    }




}
