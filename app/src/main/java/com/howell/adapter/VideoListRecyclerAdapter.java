package com.howell.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.howell.webcam.R;

import com.howell.modules.player.bean.VODRecord;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/4.
 */

public class VideoListRecyclerAdapter extends RecyclerView.Adapter<VideoListRecyclerAdapter.ViewHolder>{


    ArrayList<VODRecord> mList;

    Context mContext;
    OnItemClick mListener;
    public VideoListRecyclerAdapter(OnItemClick o){
        this.mListener = o;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vod,parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        init( holder,  position);
    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    private void init(ViewHolder holder,final int pos){
        VODRecord record = mList.get(pos);
        holder.title.setText(record.getTimeZoneStartTime().substring(0,10));
        holder.tv.setText(record.getTimeZoneStartTime().substring(11) + " --> "
                + record.getTimeZoneEndTime().substring(11));
        holder.title.setVisibility((record.hasTitle()||pos==0)?View.VISIBLE:View.GONE);
        holder.tv.setTextColor(record.isWatched()? Color.GRAY:Color.BLACK);

        holder.iv.setImageDrawable(new IconicsDrawable(mContext, GoogleMaterial.Icon.gmd_chevron_right).actionBar().color(Color.BLACK));
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener==null)return;
                mListener.onItemClickListener(v,pos);
            }
        });
    }

    public void setData(ArrayList<VODRecord> list){
        this.mList = list;
        notifyDataSetChanged();
    }


    public interface OnItemClick{
        void onItemClickListener(View v,int pos);
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView tv;
        ImageView iv;
        RelativeLayout ll;
        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_vod_title);
            tv = (TextView) itemView.findViewById(R.id.item_vod_tv);
            iv = (ImageView) itemView.findViewById(R.id.item_vod_iv);
            ll = (RelativeLayout) itemView.findViewById(R.id.item_vod_ll);
        }
    }

}
