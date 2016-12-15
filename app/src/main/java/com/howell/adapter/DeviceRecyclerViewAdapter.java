package com.howell.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howell.action.LoginAction;
import com.howell.activity.view.SwipeLinearLayout;
import com.howell.bean.CameraItemBean;
import com.howell.bean.PlayType;
import com.howell.ecam.R;
import com.howell.utils.IConst;
import com.howell.utils.PhoneConfig;
import com.howell.utils.ScaleImageUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.zys.brokenview.BrokenCallback;
import com.zys.brokenview.BrokenTouchListener;
import com.zys.brokenview.BrokenView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by howell on 2016/11/11.
 */

public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder> implements SwipeLinearLayout.OnSwipeListener,IConst {
//    List<SwipeLinearLayout> mSllList = new ArrayList<>();
    public static int VIEW_TAG = 0xff;

    List<SwipeLinearLayout> mSllList = new ArrayList<>();
    private OnItemClickListener mClickListener;
    List<CameraItemBean> mList;
    int imageWidth,imageHeight;
    Context mContext;
    private int country;//中国 0 ，别的国家 1
    BrokenTouchListener mBrokenTouchListener;
    Activity mActivity;

    MyBrokenCallback mBrokenCallback = new MyBrokenCallback();

    public DeviceRecyclerViewAdapter(){}

    public DeviceRecyclerViewAdapter(Context c, OnItemClickListener o, Activity a){
        mActivity = a;
        this.mClickListener = o;
        mContext = c;
        imageWidth = PhoneConfig.getPhoneWidth(c)/2;
        imageHeight = imageWidth * 10 / 16;
        country = mContext.getResources().getConfiguration().locale.getCountry().equals("CN")?0:1;
    }



    public DeviceRecyclerViewAdapter(Context c, OnItemClickListener o, BrokenTouchListener l){
        mBrokenTouchListener = l;
        this.mClickListener = o;
        mContext = c;
        imageWidth = PhoneConfig.getPhoneWidth(c)/2;
        imageHeight = imageWidth * 10 / 16;
        country = mContext.getResources().getConfiguration().locale.getCountry().equals("CN")?0:1;
    }


    public DeviceRecyclerViewAdapter(Context c, OnItemClickListener o){
        this.mClickListener = o;
        mContext = c;
        imageWidth = PhoneConfig.getPhoneWidth(c)/2;
        imageHeight = imageWidth * 10 / 16;
        country = mContext.getResources().getConfiguration().locale.getCountry().equals("CN")?0:1;
    }

    public DeviceRecyclerViewAdapter(List<CameraItemBean> l,Context c,OnItemClickListener o ){
        this.mClickListener = o;
        mContext = c;
        this.mList = l;
        imageWidth = PhoneConfig.getPhoneWidth(c)/2;
        imageHeight = imageWidth * 10 / 16;
        country = mContext.getResources().getConfiguration().locale.getCountry().equals("CN")?0:1;
    }

    public void setData(List<CameraItemBean>l){
        mList = l;
        notifyDataSetChanged();
    }

    public List<?>getData(){
        return mList;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera_swipebk,parent,false);

        ViewHolder holder = new ViewHolder(v);
        mSllList.add(holder.sll);
        return holder;
    }

    private void init(ViewHolder holder,CameraItemBean item){

        holder.ivCamera.setLayoutParams(new FrameLayout.LayoutParams(imageWidth, imageHeight));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bm = null;
        try {
            bm = ScaleImageUtils.decodeFile(imageWidth, imageHeight, new File(item.getPicturePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(bm == null){
//            holder.ivCamera.setImageResource(R.mipmap.card_camera_default_image);
            holder.ivCamera.setImageBitmap(null);
            holder.ivCamera.setBackgroundColor(mContext.getResources().getColor(R.color.item_camera_video));
        }else{
            holder.ivCamera.setImageBitmap(bm);
        }
        holder.tvName.setText(item.getCameraName());

        if (item.isOnline()){
            holder.ivInOffLine.setImageResource(country==0?R.mipmap.card_online_image_blue:R.mipmap.card_online_image_blue_english);
            if (item.getIndensity()>75){
                holder.ivWifi.setImageResource(R.mipmap.wifi_4);
            }else if(item.getIndensity()>50){
                holder.ivWifi.setImageResource(R.mipmap.wifi_3);
            }else if(item.getIndensity()>25){
                holder.ivWifi.setImageResource(R.mipmap.wifi_2);
            }else if(item.getIndensity()>0){
                holder.ivWifi.setImageResource(R.mipmap.wifi_1);
            }else{
                holder.ivWifi.setImageResource(item.getType()==PlayType.HW5198?R.mipmap.wifi_0:R.mipmap.wifi_1);
            }
        }else{
            holder.ivInOffLine.setImageResource(country==0?R.mipmap.card_offline_image_gray:R.mipmap.card_offline_image_gray_english);
            holder.ivWifi.setImageResource(R.mipmap.wifi_0);
        }
        //back
//        Octicons.Icon.oct_megaphone
//         GoogleMaterial.Icon.gmd_videocam
        //FontAwesome.Icon.faw_video_camera    FontAwesome.Icon.faw_film
        holder.ivReplay.setImageDrawable(mContext.getDrawable(R.mipmap.ic_theaters_white_24dp));
//        holder.ivReplay.setImageDrawable(new IconicsDrawable(mContext,      GoogleMaterial.Icon.gmd_thumb_up_down).actionBar().color(Color.WHITE));
        holder.ivSetting.setImageDrawable(new IconicsDrawable(mContext,    Octicons.Icon.oct_settings).actionBar().color(Color.WHITE));
        holder.ivInfo.setImageDrawable(new IconicsDrawable(mContext,    Octicons.Icon.oct_info).actionBar().color(Color.WHITE));
        holder.ivDelete.setImageDrawable(new IconicsDrawable(mContext,    Octicons.Icon.oct_trashcan).actionBar().color(Color.WHITE));
        holder.llDelete.setBackground(new IconicsDrawable(mContext,    Octicons.Icon.oct_trashcan).actionBar().colorRes(R.color.item_camera_detele_bk));

        holder.ivDelete.setVisibility(View.GONE );
        holder.llDelete.setVisibility(LoginAction.getInstance().ismIsGuest() ? View.GONE : View.VISIBLE);

       // GoogleMaterial.Icon.

        if (!IS_TEST) {
         //   holder.ivDelete.setVisibility(LoginAction.getInstance().ismIsGuest() ? View.GONE : View.VISIBLE);
        }else{
        }
    }

    private void initClick(final ViewHolder holder, final int pos, final CameraItemBean item){

        holder.ivReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("123","ivReplay set onclick");
                mClickListener.onItemReplayClickListener(view,pos);
            }
        });
        holder.ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemSettingClickListener(view,pos);
            }
        });
        holder.ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemInfoClickListener(view,pos);
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemDeleteClickListener(view,pos);
            }
        });

//        holder.ivDelete.setClickable(false);


        holder.ivCamera.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemVideoClickListener(view,holder.getItemView(),pos);
                return false;
            }
        });

        if (!LoginAction.getInstance().ismIsGuest()){
            holder.getItemView().setTag(pos);
            holder.getItemView().setOnTouchListener(mBrokenTouchListener);
        }
    }




    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        CameraItemBean item = mList.get(position);
        init(holder,item);
        initClick(holder,position,item);
    }

    @Override
    public int getItemCount() {
        return null==mList?0:mList.size();
    }

    @Override
    public void onDirectionJudged(SwipeLinearLayout thisSll, boolean isHorizontal) {

        if (false == isHorizontal){
            for (SwipeLinearLayout sll: mSllList){
                if (null == sll){
                    continue;
                }
                if (!sll.isClose()){
                    sll.scrollAuto(SwipeLinearLayout.DIRECTION_SHRINK);
                }
            }
        }else{
            for (SwipeLinearLayout sll:mSllList){
                if (null == sll){
                    continue;
                }
                if (!sll.equals(thisSll)) {
                    //划开一个sll， 其他收缩
                    if (!sll.isClose()) {
                        sll.scrollAuto(SwipeLinearLayout.DIRECTION_SHRINK);
                    }
                }
            }
        }
    }

    public void closeAllSwipe(){
        for (SwipeLinearLayout sll:mSllList){
            if (null==sll){
                continue;
            }
            if (!sll.isClose()){
                sll.scrollAuto(SwipeLinearLayout.DIRECTION_SHRINK);
            }
        }
    }


    public interface OnItemClickListener{
        void onItemVideoTouchListener(View v,View itemView,int pos);
        void onItemVideoClickListener(View v,View itemView,int pos);
        void onItemReplayClickListener(View v,int pos);
        void onItemSettingClickListener(View v,int pos);
        void onItemInfoClickListener(View v,int pos);
        void onItemDeleteClickListener(View v,int pos);
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        SwipeLinearLayout sll;
//        SwipeFrameLayout sll;
        View ll_back;
        View ll_top;
        View itemView;

        TextView tvName;
        ImageView ivCamera;
        ImageView ivWifi;
        ImageView ivInOffLine;



        BrokenView mBrokenView;
        BrokenTouchListener mColorfulListener;



        //back
        FloatingActionButton ivReplay,ivSetting,ivInfo,ivDelete;
        LinearLayout llDelete;

        public View getItemView(){
            return  this.itemView;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ivReplay = (FloatingActionButton) itemView.findViewById(R.id.item_camera_iv_replay);
            ivSetting = (FloatingActionButton) itemView.findViewById(R.id.item_camera_iv_setting);
            ivInfo = (FloatingActionButton) itemView.findViewById(R.id.item_camera_iv_info);
            ivDelete = (FloatingActionButton) itemView.findViewById(R.id.item_camera_iv_delete);
            llDelete = (LinearLayout) itemView.findViewById(R.id.item_camera_ll_delete);
            sll = (SwipeLinearLayout) itemView.findViewById(R.id.item_camera_sll);
            ll_back = itemView.findViewById(R.id.item_camera_bk);
            ll_top = itemView.findViewById(R.id.item_camera_top);

            tvName = (TextView) itemView.findViewById(R.id.item_camera_name);
            this.itemView = itemView;
            sll.setOnSwipeListener(DeviceRecyclerViewAdapter.this);
            ivCamera = (ImageView) itemView.findViewById(R.id.item_camera_iv_picture);
            ivWifi = (ImageView) itemView.findViewById(R.id.item_camera_iv_wifi_idensity);
            ivInOffLine = (ImageView) itemView.findViewById(R.id.item_camera_iv_offline);





        }
    }



    class MyBrokenCallback extends BrokenCallback {
        @Override
        public void onStart(View v) {

            super.onStart(v);
            Log.e("123","BrokenCallback onStart");
        }

        @Override
        public void onCancel(View v) {
            super.onCancel(v);
            Log.e("123","BrokenCallback onCancel");
        }

        @Override
        public void onRestart(View v) {
            super.onRestart(v);
        }

        @Override
        public void onFalling(View v) {
            super.onFalling(v);
            Log.e("123","BrokenCallback onFalling");
            //开始删除
            //FIXME no use

        }

        @Override
        public void onFallingEnd(View v) {
            super.onFallingEnd(v);
            Log.e("123","BrokenCallback onFallingEnd");
            //更新adapter
            //FIXME no use

        }

        @Override
        public void onCancelEnd(View v) {
            super.onCancelEnd(v);
            Log.e("123","BrokenCallback onCancelEnd");
        }
    }

}
