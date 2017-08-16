package com.howell.utils;


import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Administrator on 2017/8/7.
 */

public class RxTaskUtil {

    public  static <T> Disposable doInUIThread(final RxTask<T> uiTask){
        return Flowable.just(uiTask)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RxTask<T>>() {
                    @Override
                    public void accept(@NonNull RxTask<T> uiTask) throws Exception {
                        uiTask.doTask(uiTask.getT());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        uiTask.doError(throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        uiTask.doFinish();
                    }
                });
    }

    public static <T> Disposable doInIOTthread(final RxTask<T> ioTask){
        return Flowable.just(ioTask)
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<RxTask<T>>() {
                    @Override
                    public void accept(RxTask<T> ioTask) throws Exception {
                        ioTask.doTask(ioTask.getT());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ioTask.doError(throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        ioTask.doFinish();
                    }
                });
    }

    public static <T> Disposable doRxTask(final CommonTask<T> t){
        return Flowable.create(new FlowableOnSubscribe<CommonTask>() {

            @Override
            public void subscribe(@NonNull FlowableEmitter<CommonTask> e) throws Exception {
                t.doInIOThread();
                e.onNext(t);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CommonTask>() {
                    @Override
                    public void accept(CommonTask commonTask) throws Exception {
                        commonTask.doInUIThread();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        t.doError(throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        t.doFinish();
                    }
                });
    }


    public static abstract class RxTask<T>{
        private T t;

        public T getT() {
            return t;
        }

        public void setT(T t) {
            this.t = t;
        }

        public abstract void doTask(T t);
        public void doError(Throwable throwable){}
        public void doFinish(){}
        public RxTask(T t){
            setT(t);
        }

        public RxTask() {
        }



    }



    public static abstract class CommonTask<T>{
        private T t;

        public CommonTask() {
        }

        public CommonTask(T t) {

            this.t = t;
        }

        public T getT() {

            return t;
        }

        public void setT(T t) {
            this.t = t;
        }

        public abstract void doInIOThread();

        public abstract void doInUIThread();

        public void doError(Throwable throwable){}

        public void doFinish(){};

        public static abstract class MyOnSubscribe<T> implements Subscriber<T>{
            private T t;

            public T getT() {
                return t;
            }

            public void setT(T t) {
                this.t = t;
            }

            public MyOnSubscribe() {

            }

            public MyOnSubscribe(T t) {

                this.t = t;
            }

            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        }
    }

}
