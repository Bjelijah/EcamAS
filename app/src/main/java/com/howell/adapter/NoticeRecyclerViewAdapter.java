package com.howell.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.howell.bean.NoticeItemBean;
import com.howell.ecam.R;

import java.util.List;

/**
 * Created by howell on 2016/11/28.
 */

public class NoticeRecyclerViewAdapter extends RecyclerView.Adapter<NoticeRecyclerViewAdapter.ViewHoder> {

    Context mContext;
    List<NoticeItemBean> mList;
    NoticeRecyclerViewAdapter.OnItemClickListener mListener;

    public NoticeRecyclerViewAdapter(Context context, List<NoticeItemBean> mList, OnItemClickListener mListener) {
        this.mList = mList;
        this.mListener = mListener;
        this.mContext = context;
    }

    public NoticeRecyclerViewAdapter(Context context, OnItemClickListener mListener) {
        this.mListener = mListener;
        this.mContext = context;
    }

    @Override
    public ViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice,parent,false);
        ViewHoder viewHoder = new ViewHoder(v);
        return viewHoder;
    }

    public List<?> getData(){
        return mList;
    }

    public void setData(List<NoticeItemBean> list){
        this.mList = list;
    }

    private void init(ViewHoder holder,NoticeItemBean bean){
        holder.title.setText(bean.getNotice());
        holder.description.setText(bean.getDescription());
        holder.noticeTime.setText(bean.getTime());

    }

    private void initClick(ViewHoder holder,final int pos){
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("123","item click pos="+pos);
                mListener.onItemClickListener(pos);
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHoder holder, int position) {
        NoticeItemBean bean = mList.get(position);
        init(holder,bean);
        initClick(holder,position);
    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    class ViewHoder extends RecyclerView.ViewHolder{
        View mView;
        TextView title;
        TextView description;
        TextView noticeTime;
        public ViewHoder(View itemView) {
            super(itemView);
            mView = itemView;
            title = (TextView) itemView.findViewById(R.id.item_notice_title);
            description = (TextView) itemView.findViewById(R.id.item_notice_description);
            noticeTime = (TextView) itemView.findViewById(R.id.item_notice_time);
        }
    }

    public interface OnItemClickListener{
        void onItemClickListener(int pos);
    }
}
