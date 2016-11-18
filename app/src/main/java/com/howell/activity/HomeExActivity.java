package com.howell.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
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
import com.howell.action.LoginAction;
import com.howell.activity.fragment.DeviceFragment;
import com.howell.activity.fragment.MediaFragment;
import com.howell.bean.UserLoginDBBean;
import com.howell.db.UserLoginDao;
import com.howell.ecam.R;
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

public class HomeExActivity extends AppCompatActivity {

    private final static long ID_DRAWER_UID = 0x01;
    private final static long ID_DRAWER_EXIT = 0x00;
    private final static long ID_DRAWER_SERVER_ADDRESS = 0x10;
    private final static long ID_DRAWER_SERVER_BIND = 0x11;
    private AccountHeader headerResult;
    private Drawer result;
    ViewPager mViewPager;
    private IProfile profile,profile2;
    private Toolbar toolbar;
    private int [] mUserIcon = {R.drawable.profile2,R.drawable.profile3,R.drawable.profile4,R.drawable.profile5,R.drawable.profile6};
    private boolean mbGuest;

    private DrawerCheckedChangeListener onCheckedChangerListener = new DrawerCheckedChangeListener();
    private DrawerListener onDrawerListener = new DrawerListener();
    private DrawerItemClickListener onDrawerItemClickListener = new DrawerItemClickListener();
    @SuppressLint("NewApi")
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_ex);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.app_name));

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
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
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
                        Log.i("123","on profile changed");
                        return false;
                    }
                })
                .withCloseDrawerOnProfileListClick(false)
                .build();
    }

    @SuppressLint("NewApi")
    private void buildDrawer(Bundle savedInstanceState){




        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withFullscreen(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_free_play).withIcon(FontAwesome.Icon.faw_gamepad),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye),
                        new SectionDrawerItem().withName(R.string.home_drawer_second_head),
                        new SecondaryDrawerItem().withName(R.string.home_drawer_server_address).withIcon(Octicons.Icon.oct_server).withIdentifier(ID_DRAWER_SERVER_ADDRESS),
                        new SecondaryDrawerItem().withName(R.string.home_drawer_server_bind).withIcon(GoogleMaterial.Icon.gmd_8tracks).withIdentifier(ID_DRAWER_SERVER_BIND),
                        new ExpandableDrawerItem().withName(R.string.home_drawer_connect).withIcon(FontAwesome.Icon.faw_connectdevelop).withSelectable(false)
                        .withSubItems(
                                new SwitchDrawerItem().withName(R.string.home_drawer_turn_server).withLevel(2).withIcon(Octicons.Icon.oct_tools).withChecked(false).withOnCheckedChangeListener(onCheckedChangerListener).withSelectable(false),
                                new SwitchDrawerItem().withName(R.string.home_drawer_encrypt).withLevel(2).withIcon(Octicons.Icon.oct_tools).withChecked(false).withOnCheckedChangeListener(onCheckedChangerListener).withSelectable(false)
                        ),
//                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_question).withEnabled(false)
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
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new DeviceFragment());
        fragments.add(new MediaFragment());
        fragments.add(new MediaFragment());
        MyFragmentPagerAdatper myFragmentPagerAdatper = new MyFragmentPagerAdatper(getSupportFragmentManager(),fragments);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(myFragmentPagerAdatper);
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    class DrawerCheckedChangeListener implements OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {

        }
    }

    class DrawerListener implements Drawer.OnDrawerListener{

        @Override
        public void onDrawerOpened(View drawerView) {
            Log.i("123","on drawerOpen");
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            Log.i("123","on drawer close");
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }
    }


    class DrawerItemClickListener implements Drawer.OnDrawerItemClickListener{

        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            if (drawerItem.getIdentifier()==ID_DRAWER_EXIT){

            }


            return false;
        }



    }



    class MyFragmentPagerAdatper extends FragmentPagerAdapter {
        List<Fragment> mList;
        List<String> strings;
        public MyFragmentPagerAdatper(FragmentManager fm) {
            super(fm);
        }

        public MyFragmentPagerAdatper(FragmentManager fm,List<Fragment> list){
            super(fm);
            mList = list;
            strings = new ArrayList<>();
            strings.add("DeviceList");
            strings.add("Others");
            strings.add("Others2");
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
