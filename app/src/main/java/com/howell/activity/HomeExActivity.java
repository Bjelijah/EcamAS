package com.howell.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.howell.action.HomeAction;
import com.howell.action.LoginAction;
import com.howell.activity.fragment.DeviceFragment;
import com.howell.activity.fragment.HomeBaseFragment;
import com.howell.activity.fragment.MediaFragment;
import com.howell.activity.fragment.NoticeFragment;
import com.howell.bean.UserLoginDBBean;
import com.howell.db.UserLoginDao;
import com.howell.ecam.R;
import com.howell.utils.ServerConfigSp;
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
import java.util.List;

/**
 * Created by howell on 2016/11/15.
 */

public class HomeExActivity extends AppCompatActivity implements HomeAction.ChangeUser {

    private final static long ID_DRAWER_UID = 0x00;
    private final static long ID_DRAWER_HOME = 0x01;

    private final static long ID_DRAWER_EXIT = 0xe0;
    private final static long ID_DRAWER_SERVER_ADDRESS = 0x10;
    private final static long ID_DRAWER_SERVER_BIND = 0x11;
    private final static long ID_DRAWER_SERVER_TURN = 0x11a;
    private final static long ID_DRAWER_SERVER_ENCRYPT = 0x11b;
    private final static long ID_DRAWER_HELP = 0x12;

    private final static int MSG_HOME_EXIT = 0xf0;
    private final static int MSG_HOME_HOME = 0xf1;
    private final static int MSG_HOME_IP = 0xf2;
    private final static int MSG_HOME_BIND = 0xf3;
    private final static int MSG_HOME_HELP = 0xf4;
    private AccountHeader headerResult;
    private Drawer result;
    ViewPager mViewPager;
    private FloatingActionButton mAddbtn;
    private IProfile profile,profile2;
    private Toolbar toolbar;
    private int [] mUserIcon = {R.drawable.profile2,R.drawable.profile3,R.drawable.profile4,R.drawable.profile5,R.drawable.profile6};
    private boolean mbGuest;

    private DrawerCheckedChangeListener onCheckedChangerListener = new DrawerCheckedChangeListener();
    private DrawerListener onDrawerListener = new DrawerListener();
    private DrawerItemClickListener onDrawerItemClickListener = new DrawerItemClickListener();
    public static Bitmap sBkBitmap;
    private List<HomeBaseFragment> mFragments;


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
                case MSG_HOME_IP:
                     break;
                case MSG_HOME_BIND:
                    break;
                case MSG_HOME_HELP:
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.app_name));
        final View rootView = findViewById(R.id.main_content);
        mAddbtn = (FloatingActionButton) findViewById(R.id.floating_action_button);
        mAddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               BulrTask task = new BulrTask(rootView);
                task.execute();
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
//        mbGuest = getIntent().getBooleanExtra("isGuest",true);
        mbGuest = LoginAction.getInstance().ismIsGuest();
        HomeAction.getInstance().setContext(this).init();
        buildHead(false,savedInstanceState);
        buildDrawer(savedInstanceState);
        fillFab();
        loadBackdrop();
        initFragment();
    }
    private Drawable getRamdomUserIcon(){
        int id = (int)(Math.random()*5);
        Log.i("123","random = "+id);
        return getDrawable(mUserIcon[id]);
    }

    private List<IProfile> getProfile(){
        List<IProfile> mList = new ArrayList<>();
        if (mbGuest){
            Drawable guestIcon =  new IconicsDrawable(this, GoogleMaterial.Icon.gmd_account_circle).actionBar().color(Color.WHITE);
            IProfile mine = new ProfileDrawerItem().withName(getString(R.string.home_guest)).withEmail(getString(R.string.home_guest_email)).withIcon(guestIcon);
            mList.add(mine);
            return mList;
        }
        String userName = LoginAction.getInstance().getmInfo().getAccount();
        String usermail = LoginAction.getInstance().getmInfo().getAr().getEmail();
        IProfile mine = new ProfileDrawerItem().withName(userName).withEmail(usermail).withIcon(getRamdomUserIcon());
        mList.add(mine);

        UserLoginDao dao = new UserLoginDao(this, "user.db", 1);
        List<UserLoginDBBean> list = dao.queryAll();
        for (UserLoginDBBean b:list){
            if (!b.getUserName().equals(userName)){
                IProfile profile = new ProfileDrawerItem().withName(b.getUserName()).withEmail(b.getUserEmail()).withIcon(getRamdomUserIcon());
                mList.add(profile);
            }
        }
        dao.close();
        return mList;
    }


    private void buildHead(boolean compact, Bundle savedInstanceState){
        List<IProfile> profileList = getProfile();

//        com.mikepenz.materialdrawer.holder.ImageHolder imageHolder = new com.mikepenz.materialdrawer.holder.ImageHolder("https://unsplash.it/600/300/?random");

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
//                .withHeaderBackground(R.mipmap.background_poly)
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

                        HomeAction.getInstance().changeUser(HomeExActivity.this,profile.getName()+"");



                        return false;
                    }
                })
                .withCloseDrawerOnProfileListClick(false)
                .build();
    }

    @SuppressLint("NewApi")
    private void buildDrawer(Bundle savedInstanceState){
        final boolean isTurn = HomeAction.getInstance().isUseTurn();
        final boolean isCrypto = HomeAction.getInstance().isUseCrypto();
        HomeAction.getInstance().registChangerUserCallback(this);
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withFullscreen(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.home_drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(ID_DRAWER_HOME),
//                        new PrimaryDrawerItem().withName(R.string.drawer_item_free_play).withIcon(FontAwesome.Icon.faw_gamepad),
//                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye),
                        new SectionDrawerItem().withName(R.string.home_drawer_second_head),
                        new SecondaryDrawerItem().withName(R.string.home_drawer_server_address).withIcon(Octicons.Icon.oct_server).withIdentifier(ID_DRAWER_SERVER_ADDRESS),
                        new SecondaryDrawerItem().withName(R.string.home_drawer_server_bind).withIcon(GoogleMaterial.Icon.gmd_8tracks).withIdentifier(ID_DRAWER_SERVER_BIND),
                        new ExpandableDrawerItem().withName(R.string.home_drawer_connect).withIcon(FontAwesome.Icon.faw_connectdevelop).withSelectable(false)
                        .withSubItems(
                                new SwitchDrawerItem().withName(R.string.home_drawer_turn_server).withLevel(2).withIcon(Octicons.Icon.oct_tools).withChecked(isTurn).withOnCheckedChangeListener(onCheckedChangerListener).withSelectable(false).withIdentifier(ID_DRAWER_SERVER_TURN),
                                new SwitchDrawerItem().withName(R.string.home_drawer_encrypt).withLevel(2).withIcon(Octicons.Icon.oct_tools).withChecked(isCrypto).withOnCheckedChangeListener(onCheckedChangerListener).withSelectable(false).withIdentifier(ID_DRAWER_SERVER_ENCRYPT)
                        ),
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


    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load("https://unsplash.it/600/300/?random").centerCrop().into(imageView);



    }

    private void fillFab() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        fab.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_camera_add).actionBar().color(Color.WHITE));
    }

    private void initFragment(){
        mFragments = new ArrayList<>();
        mFragments.add(new DeviceFragment());
        mFragments.add(new MediaFragment());
        mFragments.add(new NoticeFragment());
        MyFragmentPagerAdatper myFragmentPagerAdatper = new MyFragmentPagerAdatper(getSupportFragmentManager(),mFragments);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(myFragmentPagerAdatper);
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

    private void funExit(){
        Intent intent = new Intent(HomeExActivity.this,LoginActivity.class);
        HomeExActivity.this.startActivity(intent);
        finish();
    }
    private void funHome(){
        mViewPager.setCurrentItem(0);
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
    public void onChangeOk() {
        Log.e("123","on changeOK");
        for(HomeBaseFragment fragment:mFragments){
            fragment.getData();
        }
    }

    @Override
    public void onChangeError() {
        Snackbar.make(mViewPager,getString(R.string.home_drawer_changer_user_error),Snackbar.LENGTH_LONG).show();
    }

    class DrawerCheckedChangeListener implements OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem.getIdentifier()==ID_DRAWER_SERVER_TURN){
                //TODO isChecked:
                HomeAction.getInstance().setUseTurn(isChecked);

            }else if(drawerItem.getIdentifier()==ID_DRAWER_SERVER_ENCRYPT){
                //TODO isChecked:
                HomeAction.getInstance().setUseCrypto(isChecked);
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
            ServerConfigSp.saveCommunicationInfo(HomeExActivity.this, HomeAction.getInstance().isUseTurn(), HomeAction.getInstance().isUseCrypto());
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

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
            } else if (drawerItem.getIdentifier() == ID_DRAWER_SERVER_ADDRESS){
                msg.what = MSG_HOME_IP;
            } else if (drawerItem.getIdentifier() == ID_DRAWER_SERVER_BIND){
                msg.what = MSG_HOME_BIND;
            } else if(drawerItem.getIdentifier() == ID_DRAWER_HELP){
                msg.what = MSG_HOME_HELP;
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

    class BulrTask extends AsyncTask<Void,Void,Void>{
        View v;
        Bitmap bitmap;
        BulrTask(View v){
            this.v = v;
        }

        @Override
        protected void onPreExecute() {
            bitmap = getViewBitmap(v);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Bitmap bulrBitmap = bulrBitmap(bitmap);
            sBkBitmap = bulrBitmap;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(HomeExActivity.this,AddNewCamera.class);
            HomeExActivity.this.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(HomeExActivity.this,mAddbtn,"mybtn").toBundle());
        }
    }


}
