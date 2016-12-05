package com.howell.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howell on 2016/11/28.
 */

public class NoticeRecyclerViewAdapter extends RecyclerView.Adapter<NoticeRecyclerViewAdapter.ViewHoder> {

    Context mContext;
    List<NoticeItemBean> mList;
    NoticeRecyclerViewAdapter.OnItemClickListener mListener;
    private int requiredWidthSize = 0;
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

    private void init(ViewHoder holder,NoticeItemBean bean){
        holder.title.setText(bean.getTitle());
        holder.description.setText(bean.getDescription());
        holder.noticeTime.setText(bean.getTime());
        List<String> picID = bean.getPicID();
        Log.i("123"," pic id size="+picID.size());

        ShowPicThread thread = new ShowPicThread(holder,picID);
        thread.start();

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
                mListener.onPicClickListener(pos,1,holder.picPath);
            }
        });
        holder.iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPicClickListener(pos,2,holder.picPath);
            }
        });
        holder.iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPicClickListener(pos,3,holder.picPath);
            }
        });
        holder.iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPicClickListener(pos,4,holder.picPath);
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

    class ShowPicThread extends Thread{
        ViewHoder hoder;
        List<String> ids;
        List<ImageView> ivs;

        public ShowPicThread(ViewHoder hoder,List<String>list){
            this.hoder = hoder;
            this.ids = list;
        }

        private void showPic(String account,String session, int index){
            final String id = ids.get(index);
            final ImageView iv = ivs.get(index);
            Log.i("123","pic  id="+id);

            if(!SDCardUtils.isBitmapExist(id)){
                GetPictureReq req = new GetPictureReq(account,session,id);
                SoapManager mSoapManager = SoapManager.getInstance();
                GetPictureRes res = mSoapManager.getGetPictureRes(req);
                final Bitmap bm = ScaleImageUtils.decodeByteArray(requiredWidthSize, requiredWidthSize, Base64.decode(res.getPicture()));
                hoder.picPath.add(id+HD);

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        iv.setImageBitmap(bm);
                    }
                });
                SDCardUtils.saveBmpToSd(bm,id);
                SDCardUtils.saveBmpToSd(BitmapFactory.decodeByteArray(Base64.decode(res.getPicture()), 0, Base64.decode(res.getPicture()).length),id+HD);
            }else{
                hoder.picPath.add(id+HD);


              new Handler().post(new Runnable() {
                  @Override
                  public void run() {
                      iv.setImageBitmap(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+id));
                  }
              });
            }
        }


        @Override
        public void run() {
            super.run();
            Looper.prepare();
            final int num = ids.size();
            ivs = new ArrayList<ImageView>();
                    switch (num){
                        case 0:
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    hoder.lliv.setVisibility(View.GONE);
                                    hoder.iv1.setVisibility(View.GONE);
                                    hoder.iv2.setVisibility(View.GONE);
                                    hoder.iv3.setVisibility(View.GONE);
                                    hoder.iv4.setVisibility(View.GONE);
                                }
                            });
                            break;
                        case 1:
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    hoder.lliv.setVisibility(View.VISIBLE);
                                    hoder.iv1.setVisibility(View.VISIBLE);
                                    hoder.iv2.setVisibility(View.GONE);
                                    hoder.iv3.setVisibility(View.GONE);
                                    hoder.iv4.setVisibility(View.GONE);
                                }
                            });
                            ivs.add(hoder.iv1);
                            break;
                        case 2:
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    hoder.lliv.setVisibility(View.VISIBLE);
                                    hoder.iv1.setVisibility(View.VISIBLE);
                                    hoder.iv2.setVisibility(View.VISIBLE);
                                    hoder.iv3.setVisibility(View.GONE);
                                    hoder.iv4.setVisibility(View.GONE);
                                }
                            });
                            ivs.add(hoder.iv1);
                            ivs.add(hoder.iv2);
                            break;
                        case 3:
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    hoder.lliv.setVisibility(View.VISIBLE);
                                    hoder.iv1.setVisibility(View.VISIBLE);
                                    hoder.iv2.setVisibility(View.VISIBLE);
                                    hoder.iv3.setVisibility(View.VISIBLE);
                                    hoder.iv4.setVisibility(View.GONE);
                                }
                            });
                            ivs.add(hoder.iv1);
                            ivs.add(hoder.iv2);
                            ivs.add(hoder.iv3);
                            break;
                        case 4:
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    hoder.lliv.setVisibility(View.VISIBLE);
                                    hoder.iv1.setVisibility(View.VISIBLE);
                                    hoder.iv2.setVisibility(View.VISIBLE);
                                    hoder.iv3.setVisibility(View.VISIBLE);
                                    hoder.iv4.setVisibility(View.VISIBLE);
                                }
                            });
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
            for (int i=0;i<num;i++){
                showPic(account,session,i);
            }
        }
    }



    class ViewHoder extends RecyclerView.ViewHolder{
        View mView;
        TextView title;
        TextView description;
        TextView noticeTime;
        ImageView iv1,iv2,iv3,iv4;
        LinearLayout lliv;
        ArrayList<String>picPath;
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
            picPath = new ArrayList<String>();
        }
    }

    public interface OnItemClickListener{
        void onItemClickListener(int pos);
        void onPicClickListener(int pos,int index,ArrayList<String> picPath);
    }
}
