package com.howell.bean;

/**
 * Created by howell on 2016/11/18.
 */

public class CameraItemBean {
    private PlayType type;
    private String cameraName;
    private String deviceId;
    private int channelNo;
    private boolean isOnline;
    private boolean isPtz;
    private boolean isStore;
    private String deVer;
    private String model;
    private int indensity;
    private String picturePath;

    public PlayType getType() {
        return type;
    }

    public void setType(PlayType type) {
        this.type = type;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public CameraItemBean setPicturePath(String picturePath) {
        this.picturePath = picturePath;
        return this;
    }

    public String getCameraName() {
        return cameraName;
    }

    public CameraItemBean setCameraName(String cameraName) {
        this.cameraName = cameraName;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public CameraItemBean setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public int getChannelNo() {
        return channelNo;
    }

    public CameraItemBean setChannelNo(int channelNo) {
        this.channelNo = channelNo;
        return this;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public CameraItemBean setOnline(boolean online) {
        isOnline = online;
        return this;
    }

    public boolean isPtz() {
        return isPtz;
    }

    public CameraItemBean setPtz(boolean ptz) {
        isPtz = ptz;
        return this;
    }

    public boolean isStore() {
        return isStore;
    }

    public CameraItemBean setStore(boolean store) {
        isStore = store;
        return this;
    }

    public String getDeVer() {
        return deVer;
    }

    public CameraItemBean setDeVer(String deVer) {
        this.deVer = deVer;
        return this;
    }

    public String getModel() {
        return model;
    }

    public CameraItemBean setModel(String model) {
        this.model = model;
        return this;
    }

    public int getIndensity() {
        return indensity;
    }

    public CameraItemBean setIndensity(int indensity) {
        this.indensity = indensity;
        return this;
    }

}
