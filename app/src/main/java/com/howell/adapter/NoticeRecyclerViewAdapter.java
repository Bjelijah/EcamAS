package com.howell.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howell.action.LoginAction;
import com.howell.bean.NoticeItemBean;
import com.howell.ecam.R;
import com.howell.protocol.GetPictureReq;
import com.howell.protocol.GetPictureRes;
import com.howell.protocol.SoapManager;
import com.howell.utils.PhoneConfig;
import com.howell.utils.SDCardUtils;
import com.howell.utils.ScaleImageUtils;

import org.kobjects.base64.Base64;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by howell on 2016/11/28.
 */

public class NoticeRecyclerViewAdapter extends RecyclerView.Adapter<NoticeRecyclerViewAdapter.ViewHoder> {

    private static final int MSG_DRAW_PIC = 5;

    private static final int MSG_INIT = 6;

    Context mContext;
    List<NoticeItemBean> mList;
    NoticeRecyclerViewAdapter.OnItemClickListener mListener;
    private int requiredWidthSize = 0;

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

                    initClick(hoder, hoder.pos);

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

    public NoticeRecyclerViewAdapter(Context context, OnItemClickListener mListener) {
        this.mListener = mListener;
        this.mContext = context;
        int width = PhoneConfig.getPhoneWidth(mContext);
        requiredWidthSize  = (width-32)/4;
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
        if (mList.size()>1){
            Log.i("123","data0="+mList.get(0).getTitle());
        }

    }

    private void init(ViewHoder holder,NoticeItemBean bean,int pos){
        holder.title.setText(bean.getTitle());
        holder.description.setText(bean.getDescription());
        holder.noticeTime.setText(bean.getTime());
        List<String> picID = bean.getPicID();
        holder.isRead = bean.isHasRead();
        holder.ivRead.setImageResource(holder.isRead?R.mipmap.ic_drafts_white_24dp:R.mipmap.ic_mail_white_24dp);
        Log.i("123"," pic id size="+picID.size());
        holder.picNum = picID.size();
//        for (int i=0;i<picID.size();i++){
//            if (holder.picPath.size()<holder.picNum) {
//                holder.picPath.add(SDCardUtils.getBitmapCachePath() + picID.get(i) + HD);
//            }
//        }
        ShowPicThread thread = new ShowPicThread(holder, picID,pos);
        thread.start();
//        showPic(holder,(ArrayList<String>)picID);


    }

    private void initClick(final ViewHoder holder, final int pos){
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("123","item click pos="+pos);
                mListener.onItemClickListener(pos);
            }
        });
        holder.iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("123","item pos="+pos+"  holder pos="+holder.pos);
                Log.i("123","on item click size="+holder.picPath.size());
//                Log.i("123","on item click   picPath="+holder.picPath.get(0));
                mListener.onPicClickListener(pos,0,holder.picPath,holder.noticeTime.getText().toString());
            }
        });
        holder.iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPicClickListener(pos,1,holder.picPath,holder.noticeTime.getText().toString());
            }
        });
        holder.iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPicClickListener(pos,2,holder.picPath,holder.noticeTime.getText().toString());
            }
        });
        holder.iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPicClickListener(pos,3,holder.picPath,holder.noticeTime.getText().toString());
            }
        });
        holder.ivRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.isRead = !holder.isRead;
                holder.ivRead.setImageResource(holder.isRead?R.mipmap.ic_drafts_white_24dp:R.mipmap.ic_mail_white_24dp);
                mListener.onNoticeReadClickListener(pos);
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


    private void showPic(ViewHoder hoder,ArrayList<String> ids){
        int num = ids.size();
        ArrayList<ImageView> ivs = new ArrayList<>();
        switch (num){
            case 0:
//                mHandle.sendEmptyMessage(MSG_0_PIC);
                break;
            case 1:
//                mHandle.sendEmptyMessage(MSG_1_PIC);
                ivs.add(hoder.iv1);
                break;
            case 2:
//                mHandle.sendEmptyMessage(MSG_2_PIC);
                ivs.add(hoder.iv1);
                ivs.add(hoder.iv2);
                break;
            case 3:
//                mHandle.sendEmptyMessage(MSG_3_PIC);
                ivs.add(hoder.iv1);
                ivs.add(hoder.iv2);
                ivs.add(hoder.iv3);
                break;
            case 4:
//                mHandle.sendEmptyMessage(MSG_4_PIC);
                ivs.add(hoder.iv1);
                ivs.add(hoder.iv2);
                ivs.add(hoder.iv3);
                ivs.add(hoder.iv4);
                break;
            default:
                break;
        }

        String account = LoginAction.getInstance().getmInfo().getAccount();
        String session = LoginAction.getInstance().getmInfo().getLr().getLoginSession();
        hoder.picNum = num;
        for (int i=0;i<num;i++){
            drawPic(hoder,account,session,ivs.get(i),ids.get(i));
        }


    }

    private void  drawPic(ViewHoder hoder,String account,String session,ImageView iv,String id){

        if(!SDCardUtils.isBitmapExist(id)){
            Log.i("123","getPicReq a="+account+"  s="+session+"  id="+id);
            GetPictureReq req = new GetPictureReq(account,session,id);
            SoapManager mSoapManager = SoapManager.getInstance();
            GetPictureRes res = mSoapManager.getGetPictureRes(req);
            Log.i("123","res="+res.toString());
            if (!res.getResult().equalsIgnoreCase("OK")){
                Log.e("123","getPicture res="+res.getResult());
                return;
            }
            Log.i("123","requiredWidthSize="+requiredWidthSize+"requiredWidthSize="+requiredWidthSize+"res.getpicture="+res.getPicture());
            Bitmap bitmap = ScaleImageUtils.decodeByteArray(requiredWidthSize, requiredWidthSize, Base64.decode(res.getPicture()));
//                final Bitmap bm = ScaleImageUtils.decodeByteArray(requiredWidthSize, requiredWidthSize, Base64.decode(res.getPicture()));
            if (hoder.picPath.size()<hoder.picNum) {
                Log.e("123","hoder.pic path add");
                hoder.picPath.add(SDCardUtils.getBitmapCachePath() + id + HD);
            }
            Message msg = new Message();
            msg.what = MSG_DRAW_PIC;
            msg.obj = iv;

            Bundle bundle = new Bundle();
            bundle.putParcelable("bitmap",bitmap);
            msg.setData(bundle);
            mHandle.sendMessage(msg);
//                new Handler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e("123","iv set imageBitmap  bm="+mBitmap);
//                        iv.setImageBitmap(mBitmap);
//                    }
//                });



            SDCardUtils.saveBmpToSd(bitmap,id);
            SDCardUtils.saveBmpToSd(BitmapFactory.decodeByteArray(Base64.decode(res.getPicture()), 0, Base64.decode(res.getPicture()).length),id+HD);
        }else{
            if(hoder.picPath.size()<hoder.picNum) {
                Log.e("123","hoder.pic path add");
                hoder.picPath.add(SDCardUtils.getBitmapCachePath() + id + HD);
            }
            Log.e("123","bitmap Exist  start setImage");
            Bitmap bitmap = ScaleImageUtils.resizeImage(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+id),requiredWidthSize,requiredWidthSize)  ;
            Message msg = new Message();
            msg.what = MSG_DRAW_PIC;
            msg.obj = iv;
            Bundle bundle = new Bundle();
            bundle.putParcelable("bitmap",bitmap);
            mHandle.sendMessage(msg);


//              new Handler().post(new Runnable() {
//                  @Override
//                  public void run() {
//                      Log.e("123","iv set imageBitmap  bm  decodefile= "+id);
//                      iv.setImageBitmap(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+id));
//                  }
//              });
        }




    }



    class ShowPicThread extends Thread{
        ViewHoder hoder;
        List<String> ids;
        List<ImageView> ivs;
        int pos;
        public ShowPicThread(ViewHoder hoder,List<String>list,int pos){
            this.hoder = hoder;
            this.ids = list;
            this.pos = pos;
        }

        private void showPic(String account,String session, int index){
            final String id = ids.get(index);
            final ImageView iv = ivs.get(index);
            Log.i("123","pic  index="+index+"  id="+id );
            Bitmap bitmap = null;
            if(!SDCardUtils.isBitmapExist(id)){
                Log.i("123","getPicReq a="+account+"  s="+session+"  id="+id);
                GetPictureReq req = new GetPictureReq(account,session,id);
                SoapManager mSoapManager = SoapManager.getInstance();
                GetPictureRes res = mSoapManager.getGetPictureRes(req);
                Log.i("123","res="+res.toString());
                if (!res.getResult().equalsIgnoreCase("OK")){

//                    ScaleImageUtils.resizeImage()//FIXME
//                    iv.setImageResource(R.mipmap.local_file_bg2);
                    Log.e("123","getPicture res="+res.getResult());
                    return;
                }
                Log.i("123","requiredWidthSize="+requiredWidthSize+"requiredWidthSize="+requiredWidthSize+"res.getpicture="+res.getPicture());
                bitmap = ScaleImageUtils.decodeByteArray(requiredWidthSize, requiredWidthSize, Base64.decode(res.getPicture()));
//                final Bitmap bm = ScaleImageUtils.decodeByteArray(requiredWidthSize, requiredWidthSize, Base64.decode(res.getPicture()));
                mPath = SDCardUtils.getBitmapCachePath() + id + HD;
//                if (hoder.picPath.size()<hoder.picNum) {
//
//                    Log.e("123","hoder.pic path add");
//                    hoder.picPath.add(SDCardUtils.getBitmapCachePath() + id + HD);
//                }

//                new Handler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e("123","iv set imageBitmap  bm="+mBitmap);
//                        iv.setImageBitmap(mBitmap);
//                    }
//                });



                SDCardUtils.saveBmpToSd(bitmap,id);
                SDCardUtils.saveBmpToSd(BitmapFactory.decodeByteArray(Base64.decode(res.getPicture()), 0, Base64.decode(res.getPicture()).length),id+HD);
            }else{
//                if(hoder.picPath.size()<hoder.picNum) {
//                    Log.e("123","hoder.pic path add");
//                    hoder.picPath.add(SDCardUtils.getBitmapCachePath() + id + HD);
//                }
                Log.e("123","bitmap Exist  start setImage");
                bitmap = ScaleImageUtils.resizeImage(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+id),requiredWidthSize,requiredWidthSize)  ;
                mPath = SDCardUtils.getBitmapCachePath() + id + HD;


//              new Handler().post(new Runnable() {
//                  @Override
//                  public void run() {
//                      Log.e("123","iv set imageBitmap  bm  decodefile= "+id);
//                      iv.setImageBitmap(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+id));
//                  }
//              });
            }
            Message msg = new Message();
            msg.what = MSG_DRAW_PIC;
            Bundle bundle = new Bundle();
            bundle.putSerializable("hoder",hoder);
            bundle.putString("picPath",SDCardUtils.getBitmapCachePath() + id + HD);
            bundle.putParcelable("bitmap",bitmap);
            msg.setData(bundle);
            msg.obj = iv;
            msg.arg1 = pos;
            mHandle.sendMessage(msg);
        }



        @Override
        public void run() {
            super.run();
            Looper.prepare();
            final int num = ids.size();
            ivs = new ArrayList<ImageView>();

                    switch (num){
                        case 0:

                            break;
                        case 1:

                            ivs.add(hoder.iv1);
                            break;
                        case 2:


                            ivs.add(hoder.iv1);
                            ivs.add(hoder.iv2);
                            break;
                        case 3:

                            ivs.add(hoder.iv1);
                            ivs.add(hoder.iv2);
                            ivs.add(hoder.iv3);
                            break;
                        case 4:

                            ivs.add(hoder.iv1);
                            ivs.add(hoder.iv2);
                            ivs.add(hoder.iv3);
                            ivs.add(hoder.iv4);
                            break;
                        default:
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    hoder.lliv.setVisibility(View.GONE);
                                }
                            });
                            break;
                    }

            String account = LoginAction.getInstance().getmInfo().getAccount();
            String session = LoginAction.getInstance().getmInfo().getLr().getLoginSession();
            hoder.picNum = num;
            hoder.pos = pos;
            for (int i=0;i<num;i++){
                showPic(account,session,i);
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable("hoder",hoder);
            Message msg2 = new Message();
            msg2.what = MSG_INIT;
            msg2.arg1 = num;
            msg2.setData(bundle);
            mHandle.sendMessage(msg2);
        }
    }



    class ViewHoder  extends RecyclerView.ViewHolder implements Serializable{
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
