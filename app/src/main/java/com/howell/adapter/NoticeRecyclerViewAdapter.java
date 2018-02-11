package com.howell.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howell.bean.NoticeItemBean;
import com.android.howell.webcam.R;
import com.howell.modules.notice.INoticeContract;
import com.howell.modules.notice.presenter.NoticeSoapPresenter;

import com.howell.utils.PhoneConfig;
import com.howell.utils.SDCardUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by howell on 2016/11/28.
 */

public class NoticeRecyclerViewAdapter extends RecyclerView.Adapter<NoticeRecyclerViewAdapter.ViewHoder> implements INoticeContract.IVew {

    private static final int MSG_DRAW_PIC = 5;

    private static final int MSG_INIT = 6;

    Context mContext;
    List<NoticeItemBean> mList;
    NoticeRecyclerViewAdapter.OnItemClickListener mListener;
    INoticeContract.IPresenter mPresent;
    private int requiredWidthSize = 0;
    private int requiredHeightSize = 0;
    private int mPhoneWidth = 0;
    String mPath;
//    private ViewHoder mHoder;
    private Handler mHandle= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ViewHoder hoder = (ViewHoder) msg.getData().getSerializable("hoder");
            switch (msg.what){
                case MSG_DRAW_PIC:
                    ImageView iv = (ImageView) msg.obj;
                    Bundle bundle = msg.getData();
                    Bitmap b = bundle.getParcelable("bitmap");
                    if (b != null) {
                        Log.e("321", "set image bitmap:");
                        iv.setImageBitmap(b);
                    }

                    if (hoder.picPath.size() < hoder.picNum) {
                        Log.i("123","pic path add");
                        hoder.picPath.add(mPath);
                    }
//                    mHoder.pos = msg.arg1;

                    break;
                case MSG_INIT:
                    switch (msg.arg1){
                        case 0:
                            hoder.lliv.setVisibility(View.GONE);
                            hoder.iv1.setVisibility(View.GONE);
                            hoder.iv2.setVisibility(View.GONE);
                            hoder.iv3.setVisibility(View.GONE);
                            hoder.iv4.setVisibility(View.GONE);
                            break;
                        case 1:
                            hoder.lliv.setVisibility(View.VISIBLE);
                            hoder.iv1.setVisibility(View.VISIBLE);
                            hoder.iv2.setVisibility(View.GONE);
                            hoder.iv3.setVisibility(View.GONE);
                            hoder.iv4.setVisibility(View.GONE);
                            break;
                        case 2:
                            hoder.lliv.setVisibility(View.VISIBLE);
                            hoder.iv1.setVisibility(View.VISIBLE);
                            hoder.iv2.setVisibility(View.VISIBLE);
                            hoder.iv3.setVisibility(View.GONE);
                            hoder.iv4.setVisibility(View.GONE);
                            break;
                        case 3:
                            hoder.lliv.setVisibility(View.VISIBLE);
                            hoder.iv1.setVisibility(View.VISIBLE);
                            hoder.iv2.setVisibility(View.VISIBLE);
                            hoder.iv3.setVisibility(View.VISIBLE);
                            hoder.iv4.setVisibility(View.GONE);
                            break;
                        case 4:
                            hoder.lliv.setVisibility(View.VISIBLE);
                            hoder.iv1.setVisibility(View.VISIBLE);
                            hoder.iv2.setVisibility(View.VISIBLE);
                            hoder.iv3.setVisibility(View.VISIBLE);
                            hoder.iv4.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }

                    initClick(hoder);

                    break;

            }

        }
    };


    private String HD = "HD";
    public NoticeRecyclerViewAdapter(Context context, List<NoticeItemBean> mList, OnItemClickListener mListener) {

        this.mList = mList;
        this.mListener = mListener;
        this.mContext = context;
        int width = PhoneConfig.getPhoneWidth(mContext);
        requiredWidthSize  = (width-32)/4;
    }

    public NoticeRecyclerViewAdapter(Context context, OnItemClickListener mListener ) {
        Log.i("123","NoticeRecyclerViewAdapter");

        this.mListener = mListener;
        this.mContext = context;
        mPhoneWidth = PhoneConfig.getPhoneWidth(mContext);
        requiredWidthSize  = (mPhoneWidth-32)/4;
//        bindPresenter();
    }

    @Override
    public ViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice,parent,false);
        ViewHoder viewHoder = new ViewHoder(v);
        return viewHoder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Log.i("123","onAttachedToRecyclerView");
        bindPresenter();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        Log.i("123","onDetachedFromRecyclerView");
        unbindPresenter();
    }

    public List<?> getData(){
        return mList;
    }

    public void setData(List<NoticeItemBean> list){

        this.mList = list;
        if (mList.size()>1){
            Log.i("123","data0="+mList.get(0).getTitle());
        }

    }

    private void initPicView(ViewHoder hoder){
        switch (hoder.picNum){
            case 0:
                hoder.lliv.setVisibility(View.GONE);
                hoder.iv1.setVisibility(View.GONE);
                hoder.iv2.setVisibility(View.GONE);
                hoder.iv3.setVisibility(View.GONE);
                hoder.iv4.setVisibility(View.GONE);
                break;
            case 1:
                hoder.lliv.setVisibility(View.VISIBLE);
                hoder.iv1.setVisibility(View.VISIBLE);
                hoder.iv2.setVisibility(View.GONE);
                hoder.iv3.setVisibility(View.GONE);
                hoder.iv4.setVisibility(View.GONE);
                requiredWidthSize = (mPhoneWidth-32) /1;

                break;
            case 2:
                hoder.lliv.setVisibility(View.VISIBLE);
                hoder.iv1.setVisibility(View.VISIBLE);
                hoder.iv2.setVisibility(View.VISIBLE);
                hoder.iv3.setVisibility(View.GONE);
                hoder.iv4.setVisibility(View.GONE);
                requiredWidthSize = (mPhoneWidth-32) /2;
                break;
            case 3:
                hoder.lliv.setVisibility(View.VISIBLE);
                hoder.iv1.setVisibility(View.VISIBLE);
                hoder.iv2.setVisibility(View.VISIBLE);
                hoder.iv3.setVisibility(View.VISIBLE);
                hoder.iv4.setVisibility(View.GONE);
                requiredWidthSize = (mPhoneWidth-32) /3;
                break;
            case 4:
                hoder.lliv.setVisibility(View.VISIBLE);
                hoder.iv1.setVisibility(View.VISIBLE);
                hoder.iv2.setVisibility(View.VISIBLE);
                hoder.iv3.setVisibility(View.VISIBLE);
                hoder.iv4.setVisibility(View.VISIBLE);
                requiredWidthSize = (mPhoneWidth-32) /4;
                break;
            default:
                break;

        }
        requiredHeightSize = requiredWidthSize/2;
    }



    private void init(ViewHoder holder,NoticeItemBean bean,int pos){
        holder.title.setText(bean.getTitle());
        holder.description.setText(bean.getDescription());
        holder.noticeTime.setText(bean.getTime());
        List<String> picID = bean.getPicID();
        holder.isRead = bean.isHasRead();
        holder.ivRead.setImageResource(holder.isRead?R.mipmap.ic_drafts_white_24dp:R.mipmap.ic_mail_white_24dp);
//        Log.i("123"," pic id size="+picID.size());
        holder.picNum = picID.size();
        holder.pos = pos;
        holder.picPath.clear();
//        holder.picPath.addAll(picID);
        for (int i=0;i<picID.size();i++){
            holder.picPath.add(SDCardUtils.getBitmapCachePath() + picID.get(i) + HD);
        }
        Log.i("123","picPath="+holder.picPath);
        initPicView(holder);
        initClick(holder);
        mPresent.getPicture(holder,picID,requiredWidthSize,requiredHeightSize);


    }

    private void initClick(final ViewHoder holder){
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("123","item click pos="+holder.pos);
                mListener.onItemClickListener(holder.pos);
            }
        });
        holder.iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("123","item pos="+holder.pos+"  holder pos="+holder.pos);
                Log.i("123","on item click size="+holder.picPath.size());
//                Log.i("123","on item click   picPath="+holder.picPath.get(0));
                mListener.onPicClickListener(holder.pos,0,holder.picPath,holder.noticeTime.getText().toString());
            }
        });
        holder.iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPicClickListener(holder.pos,1,holder.picPath,holder.noticeTime.getText().toString());
            }
        });
        holder.iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPicClickListener(holder.pos,2,holder.picPath,holder.noticeTime.getText().toString());
            }
        });
        holder.iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPicClickListener(holder.pos,3,holder.picPath,holder.noticeTime.getText().toString());
            }
        });
        holder.ivRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.isRead = !holder.isRead;
                holder.ivRead.setImageResource(holder.isRead?R.mipmap.ic_drafts_white_24dp:R.mipmap.ic_mail_white_24dp);
                mListener.onNoticeReadClickListener(holder.pos);
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHoder holder,int position) {
        NoticeItemBean bean = mList.get(position);
//        mHoder = holder;
        init(holder,bean,position);
    }


    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }


    @Override
    public void bindPresenter() {
        if (mPresent == null){
            mPresent = new NoticeSoapPresenter();
        }
        Log.e("123","Notice RecyclerView adapter bindView");
        mPresent.bindView(this);
        Log.e("123","Notice RecyclerView adapter bindView finish");
        mPresent.init(mContext);
    }

    @Override
    public void unbindPresenter() {
        if (mPresent!=null) {
            mPresent.unbindView();
            mPresent = null;
        }
    }

    @Override
    public void onQueryResult(List<NoticeItemBean> lists) {

    }

    @Override
    public void onError(int flag) {

    }

    @Override
    public void onStatusError() {

    }

    @Override
    public void onPicture(final ViewHoder hoder, Bitmap bit, String path, final int index) {
        ImageView iv = null;
        switch (index){
            case 0:
                iv = hoder.iv1;
                break;
            case 1:
                iv = hoder.iv2;
                break;
            case 2:
                iv = hoder.iv3;
                break;
            case 3:
                iv = hoder.iv4;
                break;
            default:break;
        }
        Log.e("123","onPicture  bit="+bit+"  path="+path+"  index="+index);
        iv.setImageBitmap(bit);
//        if (bit!=null) {
//            iv.setImageBitmap(bit);
//        }else{
//            iv.setBackgroundColor(R.color.bg_color);
//        }
    }

    public class ViewHoder  extends RecyclerView.ViewHolder implements Serializable{
        View mView;
        TextView title;
        TextView description;
        TextView noticeTime;
        ImageView iv1,iv2,iv3,iv4,ivRead;
        LinearLayout lliv;
        boolean isRead;
        final ArrayList<String>picPath=new ArrayList<>();

        int picNum;
        int pos;
        public ViewHoder(View itemView) {
            super(itemView);
            mView = itemView;
            title = (TextView) itemView.findViewById(R.id.item_notice_title);
            description = (TextView) itemView.findViewById(R.id.item_notice_description);
            noticeTime = (TextView) itemView.findViewById(R.id.item_notice_time);
            iv1 = (ImageView) itemView.findViewById(R.id.item_notice_iv_1);
            iv2 = (ImageView) itemView.findViewById(R.id.item_notice_iv_2);
            iv3 = (ImageView) itemView.findViewById(R.id.item_notice_iv_3);
            iv4 = (ImageView) itemView.findViewById(R.id.item_notice_iv_4);
            lliv = (LinearLayout) itemView.findViewById(R.id.item_notice_ll_iv);
            ivRead = (ImageView) itemView.findViewById(R.id.item_notice_read);
           // Log.e("123","new pci path array");
        }
    }

    public interface OnItemClickListener{
        void onItemClickListener(int pos);
        void onPicClickListener(int pos,int index,ArrayList<String> picPath,String strtest);
        void onNoticeReadClickListener(int pos);
    }
}
