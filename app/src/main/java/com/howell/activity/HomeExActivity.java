package com.howell.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.howell.action.ConfigAction;
import com.howell.action.FingerprintUiHelper;
import com.howell.action.HomeAction;
import com.howell.activity.fragment.DeviceFragment;
import com.howell.activity.fragment.FingerPrintSaveFragment;
import com.howell.activity.fragment.HomeBaseFragment;
import com.howell.activity.fragment.MediaFragment;
import com.howell.activity.fragment.NoticeFragment;
import com.howell.bean.UserLoginDBBean;
import com.howell.db.UserLoginDao;
import com.android.howell.webcam.R;
import com.howell.di.ui.activity.HomeModule;
import com.howell.modules.login.ILoginContract;
import com.howell.modules.login.bean.Type;
import com.howell.modules.login.presenter.LoginHttpPresenter;
import com.howell.modules.login.presenter.LoginSoapPresenter;

import com.howell.rxbus.RxBus;
import com.howell.rxbus.RxConstants;
import com.howell.utils.AlerDialogUtils;
import com.howell.utils.FileUtils;
import com.howell.utils.IConst;
import com.howell.utils.PhoneConfig;
import com.howell.utils.SDCardUtils;
import com.howell.utils.ServerConfigSp;
import com.howell.utils.ThreadUtil;
import com.howell.utils.UserConfigSp;
import com.howell.utils.Util;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by howell on 2016/11/15.
 */

public class HomeExActivity extends DaggerAppCompatActivity implements ILoginContract.IView,IConst, ViewPager.OnPageChangeListener {

    private final static long ID_DRAWER_UID = 0x00;
    private final static long ID_DRAWER_HOME = 0x01;
    private final static long ID_DRAWER_CENTER = 0x02;

    private final static long ID_DRAWER_EXIT = 0xe0;
    private final static long ID_DRAWER_SERVER_ADDRESS = 0x10;
    private final static long ID_DRAWER_TURN_ADDRESS = 0x11;
    private final static long ID_DRAWER_SERVER_BIND = 0x110;
    private final static long ID_DRAWER_SERVER_TURN = 0x11a;
    private final static long ID_DRAWER_SERVER_ENCRYPT = 0x11b;
    private final static long ID_DRAWER_HELP = 0x12;
    private final static long ID_DRAWER_PUSH = 0x13;
    private final static int MSG_HOME_EXIT = 0xf0;
    private final static int MSG_HOME_HOME = 0xf1;
    private final static int MSG_HOME_IP = 0xf2;
    private final static int MSG_HOME_BIND = 0xf3;
    private final static int MSG_HOME_HELP = 0xf4;
    private final static int MSG_HOME_CENTER = 0xf5;
    private final static int MSG_HOME_TURN = 0xf6;
    private final static int MSG_HOME_PUSH = 0xf7;
    private final static int MSG_HOME_UPDATA = 0xf8;
    public final static int MSG_HOME_UPDATE_ERROR = 0xf9;

    private AccountHeader headerResult;
    private Drawer result;
    ViewPager mViewPager;
    private FloatingActionButton mAddbtn;
    private IProfile profile,profile2;
    private Toolbar toolbar;

    private boolean mbGuest;
    private boolean mIsScope = false;
    private DrawerCheckedChangeListener onCheckedChangerListener = new DrawerCheckedChangeListener();
    private DrawerListener onDrawerListener = new DrawerListener();
    private DrawerItemClickListener onDrawerItemClickListener = new DrawerItemClickListener();
    public static Bitmap sBkBitmap;
    private String mUpdataUrl=null;

    private final CompositeDisposable mDisposables = new CompositeDisposable();
    private boolean bTypeTurn,bTypeCrypto,bSaveType=false;

    @Inject ILoginContract.IPresenter mPresenter;

    @Inject int [] mUserIcon;

    @Inject List<HomeBaseFragment> mFragments;

    @Inject @Named(HomeModule.INTENT_LOGIN) Intent mLoginIntent;

    @Inject @Named(HomeModule.INTENT_CENTER) Intent mCenterIntent;

    @Inject @Named(HomeModule.INTENT_PUSH_SET) Intent mPushSetIntent;

    @Inject @Named(HomeModule.INTENT_SERVER_SET) Intent mServerSetIntent;

    @Inject @Named(HomeModule.INTENT_ADD_CAM) Intent mAddCamIntent;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_HOME_EXIT:
                    funExit();
                    break;
                case MSG_HOME_HOME:
                    funHome();
                    break;
                case MSG_HOME_CENTER:
                    funCenter();
                    break;
                case MSG_HOME_IP:
                    funIP();
                    break;
                case MSG_HOME_BIND:
                    funBind();
                    break;
                case MSG_HOME_HELP:
                    break;
                case MSG_POST_SAVE_OK:
                    Snackbar.make(mViewPager,getString(R.string.save_db_ok),Snackbar.LENGTH_SHORT).show();
                    break;
                case FingerPrintSaveFragment.MSG_FINGERPRINT_ERROR:
                    Snackbar.make(mViewPager,getString(R.string.fingerprint_no_support),Snackbar.LENGTH_LONG).show();
                    break;
                case MSG_HOME_TURN:
                    funTurn();
                    break;
                case MSG_HOME_PUSH:
                    funPush();
                    break;
                case MSG_HOME_UPDATA:
                    funUpdata();
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("NewApi")
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_ex);
        bindPresenter();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.showOverflowMenu();
        toolbar.inflateMenu(R.menu.center_setting_action_menu);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.app_name));
//        collapsingToolbarLayout.setTitle(" ");//FIXME : just for screen shoot
        final View rootView = findViewById(R.id.main_content);
        mAddbtn = (FloatingActionButton) findViewById(R.id.floating_action_button);
        mAddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("123","add btn click time="+System.currentTimeMillis());
//                BulrTask task = new BulrTask(rootView);
//                task.execute();
                sBkBitmap = getViewBitmap(rootView);
                Log.i("123","add btn 2 click time="+System.currentTimeMillis());
//                Intent intent = new Intent(HomeExActivity.this,AddNewCameraActivity.class);
                HomeExActivity.this.startActivity(mAddCamIntent, ActivityOptions.makeSceneTransitionAnimation(HomeExActivity.this,mAddbtn,"mybtn").toBundle());

            }
        });
//        profile = new ProfileDrawerItem().withName("test name").withEmail("test email")
//                .withIcon(getResources().getDrawable(R.drawable.profile2));
//        profile2 = new ProfileDrawerItem().withName("test name2").withEmail("test email2")
//                .withIcon(getResources().getDrawable(R.drawable.profile2));

//        headerResult = new AccountHeaderBuilder()
//                .withActivity(this)
//                .withCompactStyle(false)
//                .withHeaderBackground(R.mipmap.background_poly)
//                .withSavedInstance(savedInstanceState)
//                .build();
        mbGuest = getIntent().getBooleanExtra("isGuest",false);
//        mbGuest = LoginAction.getInstance().ismIsGuest();
//        HomeAction.getInstance().setContext(this).init();
        bTypeTurn = ConfigAction.getInstance(this).isTurn();
        bTypeCrypto = ConfigAction.getInstance(this).isCrypto();


        buildHead(false,savedInstanceState);
        buildDrawer(savedInstanceState);
        fillFab();
        loadBackdrop();
        initFragment();

        if(getIntent().getBooleanExtra("notification",false)){
            mViewPager.setCurrentItem(2);
        }
        initHomeFun();
    }

    @Override
    protected void onDestroy() {
        mHandler = null;
        mDisposables.clear();
        unbindPresenter();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_action_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (mViewPager.getCurrentItem()){
            case 0:
                menu.findItem(R.id.menu_home_help).setVisible(true);
                menu.findItem(R.id.menu_home_like).setVisible(true);
                menu.findItem(R.id.menu_home_share).setVisible(true);
                menu.findItem(R.id.menu_home_chart).setVisible(true);
                if(UserConfigSp.loadLike(this)) {menu.findItem(R.id.menu_home_like).setIcon(getDrawable(R.mipmap.ic_favorite_white_24dp));}else{
                    menu.findItem(R.id.menu_home_like).setIcon(getDrawable(R.mipmap.ic_favorite_border_white_24dp));}
                menu.findItem(R.id.menu_home_notice_search).setVisible(false);
                menu.findItem(R.id.menu_home_scope).setVisible(false);
                menu.findItem(R.id.menu_home_setting).setVisible(false);
                menu.findItem(R.id.menu_home_notice_unread).setVisible(false);
                menu.findItem(R.id.menu_home_notice_read).setVisible(false);
                menu.findItem(R.id.menu_home_notice_all).setVisible(false);
                break;

            case 1:
                menu.findItem(R.id.menu_home_share).setVisible(false);
                menu.findItem(R.id.menu_home_like).setVisible(false);
                menu.findItem(R.id.menu_home_help).setVisible(false);
                menu.findItem(R.id.menu_home_chart).setVisible(true);
                menu.findItem(R.id.menu_home_notice_search).setVisible(false);
                menu.findItem(R.id.menu_home_scope).setVisible(true);
                menu.findItem(R.id.menu_home_setting).setVisible(false);
                menu.findItem(R.id.menu_home_notice_unread).setVisible(false);
                menu.findItem(R.id.menu_home_notice_read).setVisible(false);
                menu.findItem(R.id.menu_home_notice_all).setVisible(false);
                break;
            case 2:
                menu.findItem(R.id.menu_home_share).setVisible(false);
                menu.findItem(R.id.menu_home_like).setVisible(false);
                menu.findItem(R.id.menu_home_help).setVisible(false);
                menu.findItem(R.id.menu_home_chart).setVisible(true);
                menu.findItem(R.id.menu_home_notice_search).setVisible(true);
                menu.findItem(R.id.menu_home_scope).setVisible(false);
                menu.findItem(R.id.menu_home_setting).setVisible(false);
                menu.findItem(R.id.menu_home_notice_unread).setVisible(true);
                menu.findItem(R.id.menu_home_notice_read).setVisible(true);
                menu.findItem(R.id.menu_home_notice_all).setVisible(true);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_home_like:
                //TODO:
                if(UserConfigSp.loadLike(this)) {
                    item.setIcon(getDrawable(R.mipmap.ic_favorite_border_white_24dp));
                    UserConfigSp.saveLike(this,false);
                    removeLikeBk();
                }else{
                    item.setIcon(getDrawable(R.mipmap.ic_favorite_white_24dp));
                    UserConfigSp.saveLike(this,true);
                    saveLikeBk();
                }

                break;
            case R.id.menu_home_share:
                startActivity(new Intent(this,DeviceShareActivity.class));
                break;
            case R.id.menu_home_help:
                startActivity(new Intent(this,TestActivity.class));
                break;
            case R.id.menu_home_chart:
                startActivity(new Intent(this,LineChartActivity.class));
                break;
            case R.id.menu_home_notice_search:
                ((NoticeFragment)mFragments.get(2)).doSearchByTime();
                break;
            case R.id.menu_home_scope:
                if (!mIsScope){
                    item.setIcon(getDrawable(R.mipmap.ic_view_list_white_24dp));
                    mIsScope = true;
                    //TODO
                } else{
                    item.setIcon(getDrawable(R.mipmap.ic_view_headline_white_24dp));
                    mIsScope = false;
                    //TODO
                }

                break;
            case R.id.menu_home_setting:
                break;
            case R.id.menu_home_notice_unread:
                ((NoticeFragment)mFragments.get(2)).doSearchByState(0);
                break;
            case R.id.menu_home_notice_read:
                ((NoticeFragment)mFragments.get(2)).doSearchByState(1);
                break;
            case R.id.menu_home_notice_all:
                ((NoticeFragment)mFragments.get(2)).doSearchByState(2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Drawable getRamdomUserIcon(){
        int id = (int)(Math.random()*5);
        Log.i("123","random = "+id);
        return getDrawable(mUserIcon[id]);
    }

    private void buildHead(boolean compact, Bundle savedInstanceState){
        List<IProfile> profileList = getProfile();

//        com.mikepenz.materialdrawer.holder.ImageHolder imageHolder = new com.mikepenz.materialdrawer.holder.ImageHolder("https://unsplash.it/300/150/?random");


        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
//                .withHeaderBackground(R.drawable.header)
//                .withHeaderBackground(R.mipmap.background_poly)
                .withHeaderBackground(R.color.home_bk_dark)
                .withCompactStyle(compact)
                .addProfiles(profileList)
                .withSavedInstance(savedInstanceState)
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                        Log.i("123","onProfileImageClick");
                        return true;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        Log.i("123","onProfileImageLongClick");
                        return true;
                    }
                })
                .withOnlyMainProfileImageVisible(true)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        if (current)return false;
                        Log.i("123","on profile changed current="+current+" emal="+profile.getEmail()+" name="+profile.getName());
                        //FIXME change to new user
//                        HomeAction.getInstance().changeUser(HomeExActivity.this,profile.getName().toString(),profile.getEmail().toString());
                        mPresenter.changeUser(profile.getName().toString(),profile.getEmail().toString());
                        return false;
                    }
                })
                .withCloseDrawerOnProfileListClick(false)
                .build();
    }


    @SuppressLint("NewApi")
    private void buildDrawer(Bundle savedInstanceState){
        final boolean isTurn = bTypeTurn;//HomeAction.getInstance().isUseTurn();
        final boolean isCrypto = bTypeCrypto;//HomeAction.getInstance().isUseCrypto();
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withFullscreen(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.home_drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(ID_DRAWER_HOME),
                        new PrimaryDrawerItem().withName(R.string.home_drawer_item_center).withIcon(FontAwesome.Icon.faw_cloud).withIdentifier(ID_DRAWER_CENTER),
//                        new PrimaryDrawerItem().withName(R.string.drawer_item_free_play).withIcon(FontAwesome.Icon.faw_gamepad),
//                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye),
                        new SectionDrawerItem().withName(R.string.home_drawer_second_head),
                        new SecondaryDrawerItem().withName(R.string.home_drawer_server_address).withIcon(Octicons.Icon.oct_server).withIdentifier(ID_DRAWER_SERVER_ADDRESS),
//fixme          //              new SecondaryDrawerItem().withName(R.string.home_drawer_turn_address).withIcon(Octicons.Icon.oct_server).withIdentifier(ID_DRAWER_TURN_ADDRESS),
                        new SecondaryDrawerItem().withName(R.string.home_drawer_push_service).withIcon(Octicons.Icon.oct_alert).withIdentifier(ID_DRAWER_PUSH),
                        new ExpandableDrawerItem().withName(R.string.home_drawer_connect).withIcon(FontAwesome.Icon.faw_connectdevelop).withSelectable(false)
                                .withSubItems(
                                        new SwitchDrawerItem().withName(R.string.home_drawer_turn_server).withLevel(2).withIcon(Octicons.Icon.oct_tools).withChecked(isTurn).withOnCheckedChangeListener(onCheckedChangerListener).withSelectable(false).withIdentifier(ID_DRAWER_SERVER_TURN),
                                        new SwitchDrawerItem().withName(R.string.home_drawer_encrypt).withLevel(2).withIcon(Octicons.Icon.oct_tools).withChecked(isCrypto).withOnCheckedChangeListener(onCheckedChangerListener).withSelectable(false).withIdentifier(ID_DRAWER_SERVER_ENCRYPT)
                                ),
                        new SecondaryDrawerItem().withName(R.string.home_drawer_server_bind).withIcon(GoogleMaterial.Icon.gmd_8tracks).withIdentifier(ID_DRAWER_SERVER_BIND),
//                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_question).withEnabled(false).withIdentifier(ID_DRAWER_HELP)
//                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_github),
//                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_bullhorn)
                )
                .addStickyDrawerItems(
                        new SecondaryDrawerItem().withName(R.string.home_drawer_logout).withIcon(FontAwesome.Icon.faw_outdent).withIdentifier(ID_DRAWER_EXIT)
                )
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .withOnDrawerListener(onDrawerListener)
                .withOnDrawerItemClickListener(onDrawerItemClickListener)
//                .withStickyHeaderShadow(false)
//                .withSliderBackgroundColor(getColor(R.color.about_libraries_card_dark))
                .build();
    }

    private List<IProfile> getProfile(){
        List<IProfile> mList = new ArrayList<>();
        HashSet<IProfile> mSet = new HashSet<>();
        if (mbGuest){
            Drawable guestIcon =  new IconicsDrawable(this, GoogleMaterial.Icon.gmd_account_circle).actionBar().color(Color.WHITE);
            IProfile mine = new ProfileDrawerItem().withName(getString(R.string.home_guest)).withEmail(getString(R.string.home_guest_email)).withIcon(guestIcon);
            mList.add(mine);
            return mList;
        }
        String userName = getIntent().getStringExtra("account");
        String usermail = getIntent().getStringExtra("email");
        ConfigAction.getInstance(this).setEmail(usermail);
//        String userName = LoginAction.getInstance().getmInfo().getAccount();
//        String usermail = LoginAction.getInstance().getmInfo().getAr().getEmail();
        IProfile mine = new ProfileDrawerItem().withName(userName).withEmail(usermail).withIcon(getRamdomUserIcon());

        mList.add(mine);


        UserLoginDao dao = new UserLoginDao(this, "user.db", 1);
        List<UserLoginDBBean> list2 = dao.queryAll();
        for(UserLoginDBBean b:list2){
            Log.i("123","all b.name="+b.getUserName()+" email="+b.getUserEmail() +"  num="+b.getUserNum()+" ip="+b.getC().getCustomIP());
        }
        Log.i("123","~~~~~~~~");
        List<UserLoginDBBean> list = dao.queryByNum(0);
        for (UserLoginDBBean b:list){
            Log.i("123","dao b.name="+b.getUserName()   +" email="+b.getUserEmail()      +"  num="+b.getUserNum()+"  ip="+b.getC().getCustomIP());
            if (!b.getUserName().equals(userName)
                    || !b.getUserEmail().equals(usermail)){
                IProfile profile = new ProfileDrawerItem().
                        withName(b.getUserName()).
                        withEmail(b.getUserEmail()).
                        withIcon(getRamdomUserIcon());
                mList.add(profile);
            }
        }

        dao.close();
        return mList;
    }


    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);

        if (UserConfigSp.loadLike(this)){
            Bitmap bitmap = loadLikeBk();
            Log.i("123","bitmap = "+bitmap);
            imageView.setImageBitmap(bitmap);
        }else {
            Glide.with(this).load("https://unsplash.it/600/300/?random").centerCrop().into(imageView);
//        imageView.setImageDrawable(getDrawable(R.drawable.mm_bk));//FIXME I HATE THIS PICTURE AND SHOULD NEVER USE
        }
    }

    private void saveLikeBk(){
        ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = imageView.getDrawingCache();
        SDCardUtils.saveBmp2Cach(this,bitmap,"like.jpeg");
        imageView.setDrawingCacheEnabled(false);
    }

    private void removeLikeBk(){
        SDCardUtils.removeBmpFromCach(this,"like.jpeg");
    }

    private Bitmap loadLikeBk(){
        return BitmapFactory.decodeFile(SDCardUtils.getLikePath(this,"like.jpeg"));
    }

    private void fillFab() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        fab.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_camera_add).actionBar().color(Color.WHITE));
    }

    private void initFragment(){
//        mFragments = new ArrayList<>();
//        mFragments.add(new DeviceFragment());
//        mFragments.add(new MediaFragment());
//        mFragments.add(new NoticeFragment());
        MyFragmentPagerAdatper myFragmentPagerAdatper = new MyFragmentPagerAdatper(getSupportFragmentManager(),mFragments);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(myFragmentPagerAdatper);
        mViewPager.addOnPageChangeListener(this);
    }

    private void initHomeFun(){
        funCheckVersion();
    }


    private Bitmap getViewBitmap(View v){
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e("123", "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }





    private void funExit(){
//        Intent intent = new Intent(HomeExActivity.this,LoginActivity.class);
        HomeExActivity.this.startActivity(mLoginIntent);
        finish();
    }
    private void funHome(){
        mViewPager.setCurrentItem(0);
    }

    private void funCenter(){
//        Intent intent = new Intent(HomeExActivity.this,CenterActivity.class);
        HomeExActivity.this.startActivity(mCenterIntent);
    }

    private void funIP(){
//        Intent intent = new Intent(this,ServerSetActivity.class);
        startActivity(mServerSetIntent);
    }

    private void funTurn(){

    }

    private void funPush(){
//        Intent intent = new Intent(this,PushSettingActivity.class);
        startActivity(mPushSetIntent);
    }

    private void funBind(){
        if (!Util.isNewApi() || !FingerprintUiHelper.isFingerAvailable(this)){
            //TODO not support
            AlerDialogUtils.postDialogMsg(this,getString(R.string.home_drawer_server_bind),getString(R.string.save_db_no_support),null);
            return;
        }
        FingerPrintSaveFragment fragment = new FingerPrintSaveFragment();

        fragment.setHandler(mHandler)
                .setUserName(ConfigAction.getInstance(this).getName())
                .setUserPassword(ConfigAction.getInstance(this).getPassword())
                .setUserEmail(ConfigAction.getInstance(this).getEmail());
        fragment.show(getFragmentManager(), "fingerSave");


    }

    private void funCheckVersion(){
        mPresenter.queryClientVersion();
    }

    @Override
    public void onClientVersionResult(String res, String version, String downloadUrl) {
        if (res.equalsIgnoreCase("ok") && PhoneConfig.isNewVersion(this,version)){
            mUpdataUrl = new String(Base64.decode(downloadUrl,0));
            Log.e("123","url="+mUpdataUrl);
            mHandler.sendEmptyMessage(MSG_HOME_UPDATA);
        }
    }

    private void funUpdata(){
        AlerDialogUtils.postDialogMsg(this, getString(R.string.version_update_title), getString(R.string.version_update_msg),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileUtils.downLoadApk(HomeExActivity.this,mUpdataUrl);
                    }
                },null);

    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState, outPersistentState);
    }




    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        invalidateOptionsMenu();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void bindPresenter() {
//        if (mPresenter==null){
//            switch (ConfigAction.getInstance(this).getMode()){
//                case 0:
//                    mPresenter = new LoginSoapPresenter();
//                    break;
//                case 1:
//                    mPresenter = new LoginHttpPresenter();
//                    break;
//            }
//
//        }
        mPresenter.bindView(this);
        mPresenter.init(this);
    }

    @Override
    public void unbindPresenter() {
        if (mPresenter!=null){
            mPresenter.unbindView();
            mPresenter = null;
        }
    }

    @Override
    public void onError(Type type) {

    }

    @Override
    public void onLoginSuccess(String account, String email) {
        Log.e("123","on changeOK  rx send");
        RxBus.getDefault().postWithCode(RxConstants.RX_CONFIG_CODE,"");
        for(HomeBaseFragment fragment:mFragments){
            fragment.getData();
        }
    }

    @Override
    public void onLogoutResult(Type type) {

    }

    class DrawerCheckedChangeListener implements OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem.getIdentifier()==ID_DRAWER_SERVER_TURN){
                //TODO isChecked:
                bTypeTurn = isChecked;
                bSaveType = true;
//                HomeAction.getInstance().setUseTurn(isChecked);
            }else if(drawerItem.getIdentifier()==ID_DRAWER_SERVER_ENCRYPT){
                //TODO isChecked:
                bTypeCrypto = isChecked;
                bSaveType = true;
//                HomeAction.getInstance().setUseCrypto(isChecked);
            }
        }
    }

    class DrawerListener implements Drawer.OnDrawerListener{

        @Override
        public void onDrawerOpened(View drawerView) {
            Log.i("123","on drawerOpen");
            //TODO: get Drawer param form sp
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            Log.i("123","on drawer close");
            //TODO:1 save to sp  2 do
            ServerConfigSp.saveCommunicationInfo(HomeExActivity.this, bTypeTurn, bTypeCrypto);
            //TODO to set all device list if is TurnType and isUseCrypto
            onUpdataBean();
            bSaveType = false;
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }
    }

    private void onUpdataBean(){
        Log.i("123","on Updata bean bSavetype="+bSaveType);
       if (bSaveType){
           Log.i("123","rx send RX_PLAY_TYPE_CODE");
           final Bundle b = new Bundle();
           b.putBoolean("isTurn",bTypeTurn);
           b.putBoolean("isCrypto",bTypeCrypto);
           RxBus.getDefault().postWithCode(RxConstants.RX_PLAY_TYPE_CODE,b);
       }
    }


    class DrawerItemClickListener implements Drawer.OnDrawerItemClickListener{
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            Message msg = new Message();
            if (drawerItem.getIdentifier()==ID_DRAWER_EXIT){
                msg.what = MSG_HOME_EXIT;
            } else if (drawerItem.getIdentifier()==ID_DRAWER_HOME){
                msg.what = MSG_HOME_HOME;
            } else if(drawerItem.getIdentifier()==ID_DRAWER_CENTER){
                msg.what = MSG_HOME_CENTER;
            } else if (drawerItem.getIdentifier() == ID_DRAWER_SERVER_ADDRESS){
                msg.what = MSG_HOME_IP;
            } else if (drawerItem.getIdentifier() == ID_DRAWER_SERVER_BIND){
                msg.what = MSG_HOME_BIND;
            } else if(drawerItem.getIdentifier() == ID_DRAWER_HELP){
                msg.what = MSG_HOME_HELP;
            } else if(drawerItem.getIdentifier() == ID_DRAWER_TURN_ADDRESS){
                msg.what = MSG_HOME_TURN;
            } else if(ID_DRAWER_PUSH == drawerItem.getIdentifier()){
                msg.what = MSG_HOME_PUSH;
            }
            mHandler.sendMessageDelayed(msg,300);
            return false;
        }
    }



    class MyFragmentPagerAdatper extends FragmentPagerAdapter {
        List<HomeBaseFragment> mList;
        List<String> strings;
        public MyFragmentPagerAdatper(FragmentManager fm) {
            super(fm);
        }

        public MyFragmentPagerAdatper(FragmentManager fm,List<HomeBaseFragment> list){
            super(fm);
            mList = list;
            strings = new ArrayList<>();
            strings.add(getString(R.string.home_fragment_devices));
            strings.add(getString(R.string.home_fragment_medias));
            strings.add(getString(R.string.home_fragment_notice));
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position >= strings.size()) {
                return "第" + position + "个";
            }
            return strings.get(position);
        }
    }




}
