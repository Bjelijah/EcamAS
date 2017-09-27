package com.howell.bean;

import java.io.Serializable;

/**
 * Created by howell on 2016/11/18.
 */

public class CameraItemBean implements Serializable {
    private PlayType type;
    private String cameraName;
    private String cameraDescription;
    private String deviceId;
    private int channelNo;
    private boolean isOnline;
    private boolean isPtz;
    private boolean isStore;
    private String deVer;
    private String model;
    private int indensity;
    private String picturePath;
    private String upnpIP; // for ap
    private int upnpPort;
    private int methodType;//ecam turn =0; ap 0 h264,  1 h264Crypto, 2 h265,  3 h265crypto
    private String devKey;//just ecam add need;

    public String getDevKey() {
        return devKey;
    }

    public CameraItemBean setDevKey(String devKey) {
        this.devKey = devKey;
        return this;
    }

    public int getMethodType() {
        return methodType;
    }

    public CameraItemBean setMethodType(int methodType) {
        this.methodType = methodType;
        return this;
    }

    public String getCameraDescription() {
        return cameraDescription;
    }

    public CameraItemBean setCameraDescription(String cameraDescription) {
        this.cameraDescription = cameraDescription;
        return this;
    }

    public PlayType getType() {
        return type;
    }

    public String getUpnpIP() {
        return upnpIP;
    }

    public CameraItemBean setUpnpIP(String upnpIP) {
        this.upnpIP = upnpIP;
        return this;
    }

    public int getUpnpPort() {
        return upnpPort;
    }

    public CameraItemBean setUpnpPort(int upnpPort) {
        this.upnpPort = upnpPort;
        return this;
    }

    public CameraItemBean setType(PlayType type) {
        this.type = type;
        return this;
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

    @Override
    public String toString() {
        return "CameraItemBean{" +
                "type=" + type +
                ", cameraName='" + cameraName + '\'' +
                ", cameraDescription='" + cameraDescription + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", channelNo=" + channelNo +
                ", isOnline=" + isOnline +
                ", isPtz=" + isPtz +
                ", isStore=" + isStore +
                ", deVer='" + deVer + '\'' +
                ", model='" + model + '\'' +
                ", indensity=" + indensity +
                ", picturePath='" + picturePath + '\'' +
                ", upnpIP='" + upnpIP + '\'' +
                ", upnpPort=" + upnpPort +
                ", methodType=" + methodType +
                ", devKey='" + devKey + '\'' +
                '}';
    }
}
