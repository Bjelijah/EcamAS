package com.howell.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.howell.ecam.R;

import java.util.List;

/**
 * Created by howell on 2016/11/11.
 */

public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder> {

    private OnItemClickListener mClickListener;
    List<String> mList;

    public DeviceRecyclerViewAdapter(){}

    public DeviceRecyclerViewAdapter(List<String> l,OnItemClickListener o ){
        this.mClickListener = o;
        this.mList = l;
    }

    public void setData(List<String>l){
        mList = l;
        notifyDataSetChanged();
    }

    public List<?>getData(){
        return mList;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera,parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        holder.getTv().setText(mList.get(position));
        if (mClickListener!=null){
            holder.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onItemClickListener(view,position);
                }
            });





        }
    }

    @Override
    public int getItemCount() {
        return null==mList?0:mList.size();
    }

    public interface OnItemClickListener{
        void onItemClickListener(View v,int pos);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv;
        private View itemView;
        public TextView getTv() {
            return tv;
        }
        public  void setTv(TextView tv) {
            this.tv = tv;
        }
        public View getItemView(){
            return  this.itemView;
        }
        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.item_tv);
            this.itemView = itemView;
        }
    }

}
