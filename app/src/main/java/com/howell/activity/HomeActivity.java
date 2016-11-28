package com.howell.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.howell.activity.fragment.DeviceFragment;
import com.howell.activity.fragment.MediaFragment;
import com.howell.ecam.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howell on 2016/11/11.
 */

public class HomeActivity extends AppCompatActivity {
    Toolbar mtoolbar;
    ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    private void initView(){
        initToolbar();
        initDrawerLayout();
        initFragment();
    }
    private void initToolbar(){
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        mtoolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.action_refresh:
//                        Log.i("123","on menu click");
//                        SearchAction.getInstance().searchMyDevice();
//                        break;
//                }
                return true;
            }
        });
    }

    private void initDrawerLayout(){

        DrawerLayout dl = (DrawerLayout) findViewById(R.id.dl);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,dl,mtoolbar,R.string.app_name,R.string.app_name){
            @Override
            public void onDrawerOpened(View drawerView) {
                Log.i("123","onDrawerOpened");
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Log.i("123","onDrawerClosed");
                super.onDrawerClosed(drawerView);
            }
        };

        drawerToggle.syncState();
        dl.addDrawerListener(drawerToggle);
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
            strings.add("Media");
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
