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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.howell.webcam.R;
import com.howell.activity.fragment.ShareApplicationFragment;
import com.howell.activity.fragment.ShareAddFragment;
import com.howell.activity.fragment.ShareBaseFragment;
import com.howell.activity.fragment.ShareHistoryFragment;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/13.
 */

public class DeviceShareActivity extends AppCompatActivity {

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
