package com.howell.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.howell.webcam.R;
import com.howell.activity.fragment.ShareApplicationFragment;
import com.howell.activity.fragment.ShareAddFragment;
import com.howell.activity.fragment.ShareBaseFragment;
import com.howell.activity.fragment.ShareHistoryFragment;
import com.howell.utils.UserConfigSp;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/13.
 */

public class DeviceShareActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    Toolbar mTb;
    ArrayList<ShareBaseFragment> mFragments;
    ViewPager mViewPager;
    TabLayout mtabLayout;
    String mDevID;
    String mDevName;
    int mChannelNo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mDevID = getIntent().getStringExtra("devID");
        mDevName = getIntent().getStringExtra("devName");
        mChannelNo = getIntent().getIntExtra("channelNo",0);
        Log.e("123","devid="+mDevID+"   devName="+mDevName);
        initToobar();
        initFragment();
    }
    private void initToobar(){
        mTb = (Toolbar) findViewById(R.id.share_toolbar);
        mTb.showOverflowMenu();
        mTb.setTitle(getString(R.string.share_title));
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

    private void initFragment(){
        if (mFragments==null){
            mFragments = new ArrayList<>();
        }
        mFragments.add(new ShareApplicationFragment());
        mFragments.add(new ShareAddFragment(mDevID,mDevName,mChannelNo));
        mFragments.add(new ShareHistoryFragment());
        ShareFragmentPagerAdapter sfa = new ShareFragmentPagerAdapter(this,getSupportFragmentManager(),mFragments);
        mViewPager = (ViewPager) findViewById(R.id.share_viewpager);
        mViewPager.setAdapter(sfa);
        mtabLayout = (TabLayout) findViewById(R.id.share_tabLayout);
        mtabLayout.setupWithViewPager(mViewPager);
        mtabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (int i=0;i<sfa.getCount();i++){
            TabLayout.Tab tab = mtabLayout.getTabAt(i);
            tab.setCustomView(sfa.getPageView(i));
        }
        mViewPager.addOnPageChangeListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_action_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (mViewPager.getCurrentItem()){
            case 0:
                menu.findItem(R.id.menu_share_del_app).setVisible(true);
                menu.findItem(R.id.menu_share_del_add).setVisible(false);
                menu.findItem(R.id.menu_share_mode).setVisible(false);
                break;
            case 1:
                menu.findItem(R.id.menu_share_del_app).setVisible(false);
                menu.findItem(R.id.menu_share_del_add).setVisible(true);
                menu.findItem(R.id.menu_share_mode).setVisible(false);
                break;
            case 2:
                menu.findItem(R.id.menu_share_del_app).setVisible(false);
                menu.findItem(R.id.menu_share_del_add).setVisible(false);
                menu.findItem(R.id.menu_share_mode).setVisible(true);
                int mode = UserConfigSp.loadShareMgrMode(this);
                if (mode==0){
                    menu.findItem(R.id.menu_share_mode).setIcon(getDrawable(R.mipmap.ic_view_headline_white_24dp));
                }else if(mode==1){
                    menu.findItem(R.id.menu_share_mode).setIcon(getDrawable(R.mipmap.ic_view_list_white_24dp));
                }

                break;
            default:
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_share_del_app:
                mFragments.get(0).fun(0);
                break;
            case R.id.menu_share_del_add:
                mFragments.get(1).fun(0);
                break;
            case R.id.menu_share_mode:
                int mode = UserConfigSp.loadShareMgrMode(this);
                if (mode==0){
                    item.setIcon(getDrawable(R.mipmap.ic_view_list_white_24dp));
                    UserConfigSp.saveShareMgrMode(this,1);
                }else if (mode==1){
                    item.setIcon(getDrawable(R.mipmap.ic_view_headline_white_24dp));
                    UserConfigSp.saveShareMgrMode(this,0);
                }

                mFragments.get(2).fun(0);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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

    class ShareFragmentPagerAdapter extends FragmentPagerAdapter{
        Context mContext;
        String tabTitle[];
        ArrayList<ShareBaseFragment> mlist;

        public ShareFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public ShareFragmentPagerAdapter(Context context, FragmentManager fm, ArrayList<ShareBaseFragment> list){
            super(fm);
            this.mContext = context;
            mlist = list;
            tabTitle = context.getResources().getStringArray(R.array.share_item);
        }


        @Override
        public Fragment getItem(int position) {
            return mlist.get(position);
        }

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
//            return tabTitle[position];
        }

        public View getPageView(int position){
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_share_tablayout,null);
            TextView tv = (TextView) v.findViewById(R.id.share_item_tablayout_tv);
            ImageView iv = (ImageView) v.findViewById(R.id.share_item_tablayout_iv);
            tv.setText(tabTitle[position]);
            return v;
        }

    }

}
