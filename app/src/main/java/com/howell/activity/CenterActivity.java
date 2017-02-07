package com.howell.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.howell.activity.fragment.WebFragment;
import com.howell.ecam.R;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

/**
 * Created by Administrator on 2017/1/25.
 */

public class CenterActivity extends AppCompatActivity {

    private static long ID_DRAWER_HOME = 0x00;
    private static long ID_DRAWER_CENTER = 0x01;

    private static final int MSG_CENTER_HOME = 0xc1;
    private static final int MSG_CENTER_CENTER = 0xc2;


    private Toolbar mToolbar;
    private WebFragment mWebFragment;
    private AppBarLayout mAppBar;
    private Drawer mDrawer = null;
    private DrawerItemClickListener onDrawerItemClickListener = new DrawerItemClickListener();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CENTER_HOME:
                    homeFun();
                    break;
                case MSG_CENTER_CENTER:
                    centerFun();
                    break;
                default:
                    break;
            }

        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center);
        initToobar();
        initView();


        buildDrawer(savedInstanceState);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = false;
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            ret = mWebFragment.clickBack();
        }
        return ret?true:super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//land
            Log.i("123","land");
//            mAppBar.setVisibility(View.GONE);
            getSupportActionBar().hide();
        }
        else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//port
            Log.i("123","port");
            getSupportActionBar().show();
//            mAppBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.center_setting_action_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.center_setting_action_set:
                //TODO setting activity
                Intent intent = new Intent(this,CenterSetActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView(){



        mAppBar = (AppBarLayout) findViewById(R.id.center_appbar);

//        setTitle(getString(R.string.center_title_name));
        setTitle("");
        mWebFragment = (WebFragment) getFragmentManager().findFragmentById(R.id.center_web_fragment);

    }

    private void buildDrawer(Bundle savedInstanceState){
        mDrawer = new DrawerBuilder()
                .withActivity(this)
//                .withRootView(R.id.center_toolbar)
                .withSavedInstance(savedInstanceState)
                .withDisplayBelowStatusBar(false)
                .withTranslucentStatusBar(true)
                .withToolbar(mToolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.home_drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(ID_DRAWER_HOME),
                        new PrimaryDrawerItem().withName(R.string.home_drawer_item_center).withIcon(FontAwesome.Icon.faw_cloud).withIdentifier(ID_DRAWER_CENTER)
                )
                .withOnDrawerItemClickListener(onDrawerItemClickListener).build();

    }

    private void initToobar(){
        mToolbar = (Toolbar) findViewById(R.id.center_toolbar);
        mToolbar.showOverflowMenu();
        mToolbar.inflateMenu(R.menu.center_setting_action_menu);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

    }


    private void homeFun(){
        finish();
        Intent intent = new Intent(this,HomeExActivity.class);
        startActivity(intent);
    }

    private void centerFun(){
        mWebFragment.returnHomePage();
    }

    class DrawerItemClickListener implements Drawer.OnDrawerItemClickListener{
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            Message msg = new Message();
            if (drawerItem.getIdentifier()==ID_DRAWER_HOME){
                msg.what = MSG_CENTER_HOME;
            }  else if(drawerItem.getIdentifier()==ID_DRAWER_CENTER){
                msg.what = MSG_CENTER_CENTER;
            }
            mHandler.sendMessageDelayed(msg,300);
            return false;
        }
    }









}
