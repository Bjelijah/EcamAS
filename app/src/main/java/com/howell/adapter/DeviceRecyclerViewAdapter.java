package com.howell.adapter;

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
import android.widget.TextView;

import com.howell.action.LoginAction;
import com.howell.activity.view.SwipeLinearLayout;
import com.howell.bean.CameraItemBean;
import com.howell.ecam.R;
import com.howell.utils.PhoneConfig;
import com.howell.utils.ScaleImageUtils;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by howell on 2016/11/11.
 */

public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder> implements SwipeLinearLayout.OnSwipeListener {
//    List<SwipeLinearLayout> mSllList = new ArrayList<>();
    List<SwipeLinearLayout> mSllList = new ArrayList<>();
    private OnItemClickListener mClickListener;
    List<CameraItemBean> mList;
    int imageWidth,imageHeight;
    Context mContext;
    private int country;//中国 0 ，别的国家 1
    public DeviceRecyclerViewAdapter(){}

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
                holder.ivWifi.setImageResource(R.mipmap.wifi_0);
            }
        }else{
            holder.ivInOffLine.setImageResource(country==0?R.mipmap.card_offline_image_gray:R.mipmap.card_offline_image_gray_english);
            holder.ivWifi.setImageResource(R.mipmap.wifi_0);
        }

        //back


//        Octicons.Icon.oct_megaphone
        holder.ivReplay.setImageDrawable(new IconicsDrawable(mContext,   GoogleMaterial.Icon.gmd_videocam).actionBar().color(Color.WHITE));
        holder.ivSetting.setImageDrawable(new IconicsDrawable(mContext,    Octicons.Icon.oct_settings).actionBar().color(Color.WHITE));
        holder.ivInfo.setImageDrawable(new IconicsDrawable(mContext,    Octicons.Icon.oct_info).actionBar().color(Color.WHITE));
        holder.ivDelete.setImageDrawable(new IconicsDrawable(mContext,    Octicons.Icon.oct_trashcan).actionBar().color(Color.WHITE));
        holder.ivDelete.setVisibility(LoginAction.getInstance().ismIsGuest()?View.GONE:View.VISIBLE);



    }

    private void initClick(ViewHolder holder,final int pos,final CameraItemBean item){

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
        holder.ivCamera.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemVideoClickListener(view,pos);
                return false;
            }
        });
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




    public interface OnItemClickListener{
        void onItemVideoClickListener(View v,int pos);
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



        //back
        FloatingActionButton ivReplay,ivSetting,ivInfo,ivDelete;


        public View getItemView(){
            return  this.itemView;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ivReplay = (FloatingActionButton) itemView.findViewById(R.id.item_camera_iv_replay);
            ivSetting = (FloatingActionButton) itemView.findViewById(R.id.item_camera_iv_setting);
            ivInfo = (FloatingActionButton) itemView.findViewById(R.id.item_camera_iv_info);
            ivDelete = (FloatingActionButton) itemView.findViewById(R.id.item_camera_iv_delete);
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

}
