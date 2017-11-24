package com.howell.modules.pdc.presenter;

import android.util.Log;

import com.howell.utils.Util;
import com.howellsdk.api.ApiManager;
import com.howellsdk.net.http.bean.EventRecordedList;
import com.howellsdk.net.http.bean.MainPageLayout;
import com.howellsdk.net.http.bean.PDCDevice;
import com.howellsdk.net.http.bean.PDCDeviceGroup;
import com.howellsdk.net.http.bean.PDCDeviceGroupList;
import com.howellsdk.net.http.bean.PDCDeviceGroupStatus;
import com.howellsdk.net.http.bean.PDCDeviceList;
import com.howellsdk.net.http.bean.PDCDeviceStatus;
import com.howellsdk.net.http.bean.PDCFlavourList;
import com.howellsdk.net.http.bean.PDCSampleList;
import com.howellsdk.net.http.bean.PDCServiceVersion;
import com.howellsdk.net.http.bean.PDCThreshold;
import com.howellsdk.net.http.bean.PDCUserList;
import com.howellsdk.net.http.bean.WeeklySchedule;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;


import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/11/23.
 */

public class PDCPresenter extends PDCBasePresenter {

    @Override
    public void test() {
        try {
//            testGroup();
//            testEventRecords();
            testUsers();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    void testUsers() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcUsers(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_USERS),
                        null,
                        null
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PDCUserList>() {
                    @Override
                    public void accept(PDCUserList pdcUserList) throws Exception {
                        Log.i("123",pdcUserList.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }


    void testEventRecords() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Date nowDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        calendar.add(Calendar.DATE,-1);
        Date beforeDate = calendar.getTime();
        String nowStr = Util.Date2ISODate(nowDate);
        String beforeStr = Util.Date2ISODate(beforeDate);
        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcEventRecords(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_EVENTS_RECORDS),
                        beforeStr,
                        nowStr,
                        null,
                        null,
                        null,
                        null
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<EventRecordedList>() {
                    @Override
                    public void accept(EventRecordedList eventRecordedList) throws Exception {
                        Log.i("123",eventRecordedList.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    void testFlavours() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcFlavours(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_FLAVOURS),
                        null,
                        null
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PDCFlavourList>() {
                    @Override
                    public void accept(PDCFlavourList pdcFlavourList) throws Exception {
                        Log.i("123",pdcFlavourList.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    void testGroupThreshold(String id) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcGroupThreshold(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_GROUP_THRESHOLD,new String[]{id}),
                        id
                ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PDCThreshold>() {
                    @Override
                    public void accept(PDCThreshold pdcThreshold) throws Exception {
                        Log.i("123",pdcThreshold.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }


    void testGroupSamples(String id) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Date nowDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        calendar.add(Calendar.DATE,-1);
        Date beforeDate = calendar.getTime();
        String nowStr = Util.Date2ISODate(nowDate);
        String beforeStr = Util.Date2ISODate(beforeDate);


        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcGroupSamples(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_GROUP_SAMPLES,new String[]{id}),
                        id,
                        "Minute",
                        beforeStr,
                        nowStr
                ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PDCSampleList>() {
                    @Override
                    public void accept(PDCSampleList pdcSampleList) throws Exception {
                        Log.i("123",pdcSampleList.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }


    void testGroupDevice(String id,String devId) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcGroupDevices(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_GROUP_DEVICES,new String[]{id,devId}),
                        id,
                        devId
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PDCDevice>() {
                    @Override
                    public void accept(PDCDevice pdcDevice) throws Exception {
                        Log.i("123",pdcDevice.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    void testGroupDevice(final String id) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcGroupDevices(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_GROUP_DEVICES,new String[]{id}),
                        id,
                        null,
                        null,
                        null
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PDCDeviceList>() {
                    @Override
                    public void accept(PDCDeviceList pdcDeviceList) throws Exception {
                        Log.i("123",pdcDeviceList.toString());
                        String devId = pdcDeviceList.getPdcDevices().get(0).getId();
                        testGroupDevice(id,devId);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    void testGroupStatus(String id) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcGroupStatus(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_GROUP_STATUS,new String[]{id}),
                        id
                ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PDCDeviceGroupStatus>() {
                    @Override
                    public void accept(PDCDeviceGroupStatus pdcDeviceGroupStatus) throws Exception {
                        Log.i("123",pdcDeviceGroupStatus.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    void testGroup(String id) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcGroups(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_GROUP,new String[]{id}),
                        id
                ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PDCDeviceGroup>() {
                    @Override
                    public void accept(PDCDeviceGroup pdcDeviceGroup) throws Exception {
                        Log.i("123","pdcgroup="+pdcDeviceGroup.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    void testGroup() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcGroups(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_GROUP),
                        null,
                        null,
                        null
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PDCDeviceGroupList>() {
                    @Override
                    public void accept(PDCDeviceGroupList pdcDeviceGroupList) throws Exception {
                        Log.i("123","group="+pdcDeviceGroupList.toString());
                        String id = pdcDeviceGroupList.getPdcDeviceGroups().get(0).getId();
//                        testGroup(id);
//                        testGroupStatus(id);
//                        testGroupDevice(id);
//                        testGroupSamples(id);
//                        testGroupThreshold(id);
                        testFlavours();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }


    void testDeviceSchedule(String id) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcDeviceSchedule(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_DEVICE_SCHEDULE,new String []{id}),
                        id
                ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeeklySchedule>() {
                    @Override
                    public void accept(WeeklySchedule weeklySchedule) throws Exception {
                        Log.i("123","weeklySchedule="+weeklySchedule.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }


    void testDeviceThreshold(String id) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcDeviceThreshold(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_DEVICE_THRESHOLD,new String []{id})
                        ,id
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PDCThreshold>() {
                    @Override
                    public void accept(PDCThreshold pdcThreshold) throws Exception {
                        Log.i("123","threshold="+pdcThreshold.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    void testDeviceSamples(String id) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Date nowDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        calendar.add(Calendar.DATE,-1);
        Date beforeDate = calendar.getTime();
        String nowStr = Util.Date2ISODate(nowDate);
        String beforeStr = Util.Date2ISODate(beforeDate);

        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcDeviceSamples(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_DEVICE_SAMPLES, new String[]{id}),
                        id,
                        "Minute",
                        beforeStr,
                        nowStr,
                        null,
                        null
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PDCSampleList>() {
                    @Override
                    public void accept(PDCSampleList pdcSampleList) throws Exception {
                        Log.i("123","sample="+pdcSampleList.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }


    void testDeviceStatus(String id) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcDeviceStatus(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_DEVICE_STATUS,new String[]{id}),
                        id
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PDCDeviceStatus>() {
                    @Override
                    public void accept(PDCDeviceStatus pdcDeviceStatus) throws Exception {
                        Log.i("123","status="+pdcDeviceStatus.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }



    void testDevice() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcDevices(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_DEVICE),
                        null,
                        null,
                        null
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PDCDeviceList>() {
                    @Override
                    public void accept(PDCDeviceList pdcDeviceList) throws Exception {
                        Log.i("123","device="+pdcDeviceList.toString());
                        String id = pdcDeviceList.getPdcDevices().get(0).getId();
//                        testDeviceStatus(id);
//                        testDeviceSamples(id);
//                        testDeviceThreshold(id);
                        testDeviceSchedule(id);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    void testLayout() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ApiManager.getInstance().getHWHttpService(mUrl)
                .queryMainPageLayout(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_MAINPAGE_LAYOUT)
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MainPageLayout>() {
                    @Override
                    public void accept(MainPageLayout mainPageLayout) throws Exception {
                        Log.i("123","layout="+mainPageLayout.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });

    }

    void testVersion() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        addDisposable(ApiManager.getInstance().getHWHttpService(mUrl)
                .queryPdcServiceVersion(
                        ApiManager.HttpHelp.getCookie(ApiManager.HttpHelp.Type.PDC_VERSION)
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PDCServiceVersion>() {
                    @Override
                    public void accept(PDCServiceVersion pdcServiceVersion) throws Exception {
                        Log.i("123","version="+pdcServiceVersion.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }));
    }



}
