package com.howell.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.howell.activity.view.SwipeFrameLayout;
import com.howell.activity.view.SwipeLinearLayout;
import com.howell.bean.CameraItemBean;
import com.howell.ecam.R;
import com.howell.utils.PhoneConfig;
import com.howell.utils.ScaleImageUtils;

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
            holder.ivCamera.setImageResource(R.mipmap.card_camera_default_image);
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
        }

    }






    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        CameraItemBean item = mList.get(position);

        init(holder,item);



        if (mClickListener!=null){
//            holder.getItemView().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mClickListener.onItemClickListener(view,position);
//                }
//            });
        }

      //  Log.i("123","ll_left width="+holder.ll_left.getWidth());

      //  holder.sll.scrollTo(holder.ll_left.getWidth(),0);
        final ViewHolder finalHolder = holder;

        holder.ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("123","ll_left click");
                finalHolder.sll.scrollAuto(SwipeFrameLayout.DIRECTION_SHRINK);
            }
        });
        holder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("123","delte item:"+position);
                finalHolder.sll.scrollAuto(SwipeFrameLayout.DIRECTION_SHRINK);
            }
        });


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
        void onItemClickListener(View v,int pos);
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

        View tv_delete;//// FIXME: 2016/11/18


        public View getItemView(){
            return  this.itemView;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            sll = (SwipeLinearLayout) itemView.findViewById(R.id.item_camera_sll);
            ll_back = itemView.findViewById(R.id.item_camera_bk);
            ll_top = itemView.findViewById(R.id.item_camera_top);
            tv_delete = itemView.findViewById(R.id.tv_delete);
            tvName = (TextView) itemView.findViewById(R.id.item_camera_name);
            this.itemView = itemView;
            sll.setOnSwipeListener(DeviceRecyclerViewAdapter.this);
            ivCamera = (ImageView) itemView.findViewById(R.id.item_camera_iv_picture);
            ivWifi = (ImageView) itemView.findViewById(R.id.item_camera_iv_wifi_idensity);
            ivInOffLine = (ImageView) itemView.findViewById(R.id.item_camera_iv_offline);

        }
    }

}
