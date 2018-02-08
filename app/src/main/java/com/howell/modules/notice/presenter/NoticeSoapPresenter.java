package com.howell.modules.notice.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import android.util.Log;
import android.widget.ImageView;

import com.howell.action.ConfigAction;
import com.howell.adapter.NoticeRecyclerViewAdapter;
import com.howell.bean.NoticeItemBean;
import com.howell.utils.PhoneConfig;
import com.howell.utils.SDCardUtils;
import com.howell.utils.ScaleImageUtils;
import com.howellsdk.api.ApiManager;
import com.howellsdk.net.soap.bean.FlaggedNoticeStatusReq;
import com.howellsdk.net.soap.bean.LoginRequest;
import com.howellsdk.net.soap.bean.LoginResponse;
import com.howellsdk.net.soap.bean.NoticeList;
import com.howellsdk.net.soap.bean.NoticesReq;
import com.howellsdk.net.soap.bean.NoticesRes;
import com.howellsdk.net.soap.bean.PictureReq;
import com.howellsdk.net.soap.bean.PictureRes;
import com.howellsdk.net.soap.bean.Result;

import org.kobjects.base64.Base64;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/9/18.
 */

public class NoticeSoapPresenter extends NoticeBasePresenter {

    private int mCurPage = 1;
    private final int mPageSize = 20;
    private int mTotalPage;

    private boolean loginFlag = false;
    synchronized private void login(){
        if (loginFlag){
            return;
        }
        ApiManager.getInstance().getSoapService()
                .userLogin(new LoginRequest(
                        ConfigAction.getInstance(mContext).getName(),
                        ConfigAction.getInstance(mContext).getPassword(),
                        PhoneConfig.getIMEI(mContext)
                ))
                .map(new Function<LoginResponse, String>() {
                    @Override
                    public String apply(LoginResponse loginResponse) throws Exception {
                        ApiManager.SoapHelp.setsSession(loginResponse.getLoginSession());
                        return loginResponse.getResult();
                    }
                })
                .subscribeOn(Schedulers.trampoline())
                .observeOn(Schedulers.trampoline())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i("123","s="+s);
                        if (s.equalsIgnoreCase("ok")){

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","login finish");
                    }
                });
        loginFlag = true;
    }

    @Override
    public void init(Context context) {
        super.init(context);
        mCurPage = 1;
        mTotalPage = 1;
    }

    @Override
    public NoticeSoapPresenter reset() {
        mCurPage = 1;
        mTotalPage = 1;
        return this;
    }

    @Override
    public void queryNotice(@Nullable final String searchID, @Nullable final Boolean isRead, @Nullable final String time, @Nullable final String sender, final boolean b) {
        if (mCurPage>mTotalPage){
            mView.onError(0);
            return;
        }

        ApiManager.getInstance()
                .getSoapService(mURL)
                .queryNoticesRes(new NoticesReq(mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        mCurPage,
                        mPageSize,
                        searchID,
                        isRead==null?null:(isRead?"Read":"Unread"),
                        time,
                        sender))
                .map(new Function<NoticesRes, ArrayList<NoticeList>>() {
                    @Override
                    public ArrayList<NoticeList> apply(@NonNull NoticesRes noticesRes) throws Exception {

                        if (noticesRes.getResult().equalsIgnoreCase("SessionExpired")){// session 过期 重新登入
                            loginFlag = false;
                            login();
                            queryNotice(searchID,isRead,time,sender,b);
                            return null;
                        }
                        if (noticesRes.getResult().equalsIgnoreCase("NoRecord")){
                            mView.onQueryResult(null,b);
                            return null;
                        }


                        if (mCurPage==1) {
                            mTotalPage = noticesRes.getPageCount();
                        }
                        mCurPage++;
                        return noticesRes.getNodeList();
                    }
                })
                .concatMap(new Function<ArrayList<NoticeList>, ObservableSource<NoticeList>>() {
                    @Override
                    public ObservableSource<NoticeList> apply(@NonNull ArrayList<NoticeList> noticeLists) throws Exception {
                        return Observable.fromIterable(noticeLists);
                    }
                })
                .map(new Function<NoticeList, NoticeItemBean>() {
                    @Override
                    public NoticeItemBean apply(@NonNull NoticeList o) throws Exception {
                      //  Log.e("123","picture id "+o.getPictureID());
                        return new NoticeItemBean()
                                .setTitle(o.getName())
                                .setDescription(o.getMessage())
                                .setTime(o.getTime().substring(0, 10)+" "+o.getTime().substring(11,19))
                                .setPicID(o.getPictureID())
                                .setId(o.getiD())
                                .setHasRead(o.getStatus().equalsIgnoreCase("Read")?true:false);
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<NoticeItemBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onSuccess(@NonNull List<NoticeItemBean> noticeItemBeen) {
                        mView.onQueryResult(noticeItemBeen,b);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mView.onError(1);
                    }
                });



    }

    @Override
    public void setNoticeStatus(final String id, final boolean isRead) {

        ApiManager.getInstance()
                .getSoapService()
                .flaggedNoticeStatusRes(new FlaggedNoticeStatusReq(mAccount,
                        ApiManager.SoapHelp.getsSession(),
                        isRead?"Read":"Unread",
                        new String[]{id}))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Result>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Result result) {
                        if (!result.getResult().equalsIgnoreCase("ok")){
                            mView.onStatusError();
                        }
                        if (result.getResult().equalsIgnoreCase("SessionExpired")){
                            loginFlag = false;
                            login();
                            setNoticeStatus(id,isRead);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mView.onStatusError();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","status finish");
                    }
                });
    }


    private void getPic(final NoticeRecyclerViewAdapter.ViewHoder hoder,final String id, final int width, final int height,final int index){
        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Bitmap> e) throws Exception {
                Log.e("123","bitmap Exist  start setImage");
                e.onNext(ScaleImageUtils.resizeImage(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+id),width,height) );
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Bitmap bitmap) {
                        mView.onPicture(hoder,bitmap, SDCardUtils.getBitmapCachePath() + id + "HD",index);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        Log.e("123","get image error");
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","get image finish");
                    }
                });




    }

    @Override
    public void getPicture(NoticeRecyclerViewAdapter.ViewHoder hoder, List<String> picIDs,int width,int height) {

        for (int i=0;i<picIDs.size();i++){
            String id = picIDs.get(i);
            if(!SDCardUtils.isBitmapExist(id)){
                Log.i("123","is not bitmap exit id="+id);
                downloadPic(hoder,id,width,height,i);
            }else{
                Log.i("123","is bitmap exit id="+id);
                getPic(hoder,id,width,height,i);
            }
        }
    }


    private void downloadPic(final NoticeRecyclerViewAdapter.ViewHoder hoder, final String id, final int width, final int height,final int index){

        ApiManager.getInstance()
                .getSoapService()
                .getGetPictureRes(new PictureReq(mAccount,ApiManager.SoapHelp.getsSession(),id))
                .map(new Function<PictureRes, Bitmap >() {
                    @Override
                    public Bitmap apply(@NonNull PictureRes pictureRes) throws Exception {
                        Log.i("123","downloadPic  res="+pictureRes.toString());
                        Bitmap bitmap = null;
                        if (pictureRes.getResult().equalsIgnoreCase("ok")) {
                            byte[] data = Base64.decode(pictureRes.getPicture());
                            bitmap = ScaleImageUtils.decodeByteArray(width, height, data);
                            //save
                            SDCardUtils.saveBmpToSd(bitmap, id);
                            SDCardUtils.saveBmpToSd(BitmapFactory.decodeByteArray(data, 0, data.length), id + "HD");
                        }
                        if (pictureRes.getResult().equalsIgnoreCase("SessionExpired")){
                            loginFlag = false;
                            login();
                            downloadPic(hoder, id, width, height, index);
                        }
                        return bitmap;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Bitmap bitmap) {
                        //draw

                        mView.onPicture(hoder,bitmap, SDCardUtils.getBitmapCachePath() + id + "HD",index);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        Log.e("123","download pic error");
                        mView.onPicture(hoder,null, null,index);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("123","download pic finish");
                    }
                });



    }
}
