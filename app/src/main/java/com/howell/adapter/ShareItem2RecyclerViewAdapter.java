package com.howell.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.howell.webcam.R;
import com.howell.bean.ShareItem2Bean;
import com.howell.utils.Util;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */

public class ShareItem2RecyclerViewAdapter extends RecyclerView.Adapter<ShareItem2RecyclerViewAdapter.ShareItem2Holder> {
    List<ShareItem2Bean> mList ;
    Context mContext;

    public ShareItem2RecyclerViewAdapter(Context context,List<ShareItem2Bean> l){
        mContext = context;
        mList=l;
    }

    public ShareItem2RecyclerViewAdapter(Context context){
        mContext = context;
    }


    public void setData(List<ShareItem2Bean> l){
        mList=l;
        Log.i("123","ml="+mList.toString());
        notifyDataSetChanged();
    }

    private void init(ShareItem2Holder holder,ShareItem2Bean b){
        Log.i("123","init~~~~");
        holder.tvDev.setText(b.getShareDevName());
        holder.devll.setVisibility(b.getShareDevName()==null?View.GONE:View.VISIBLE);
        holder.tvTime.setText(Util.ISODateString2Date(b.getShareTime()));
        holder.tvName.setText(b.getShareName());

    }

    @Override
    public ShareItem2Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("123","onCreateViewHolder");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share2,parent,false);
        ShareItem2Holder h = new ShareItem2Holder(v);
        return h;
    }

    @Override
    public void onBindViewHolder(ShareItem2Holder holder, int position) {
        Log.i("123","onBindView HOlder");
        ShareItem2Bean b = mList.get(position);
        init(holder,b);
    }


    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    class ShareItem2Holder extends RecyclerView.ViewHolder{
        TextView tvDev,tvName,tvTime;
        LinearLayout devll;
        public ShareItem2Holder(View itemView) {
            super(itemView);
            devll = (LinearLayout) itemView.findViewById(R.id.item_share2_ll_dev);
            tvDev = (TextView) itemView.findViewById(R.id.item_share2_tv_dev);
            tvName = (TextView) itemView.findViewById(R.id.item_share2_tv_name);
            tvTime = (TextView) itemView.findViewById(R.id.item_share2_tv_time);
        }
    }

}
