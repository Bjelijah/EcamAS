package com.howell.bean;

/**
 * Created by Administrator on 2017/7/19.
 */

public class ShareItem2Bean {
    String shareDevID;
    String shareDevName;
    String shareName;
    String shareTime;

    @Override
    public String toString() {
        return "ShareItem2Bean{" +
                "shareDevID='" + shareDevID + '\'' +
                ", shareDevName='" + shareDevName + '\'' +
                ", shareName='" + shareName + '\'' +
                ", shareTime='" + shareTime + '\'' +
                '}';
    }

    public String getShareDevID() {
        return shareDevID;
    }

    public void setShareDevID(String shareDevID) {
        this.shareDevID = shareDevID;
    }

    public String getShareDevName() {
        return shareDevName;
    }

    public void setShareDevName(String shareDevName) {
        this.shareDevName = shareDevName;
    }

    public ShareItem2Bean(String shareDevID, String shareDevName, String shareName, String shareTime) {

        this.shareDevID = shareDevID;
        this.shareDevName = shareDevName;
        this.shareName = shareName;
        this.shareTime = shareTime;
    }

    public ShareItem2Bean(String shareName, String shareTime) {

        this.shareName = shareName;
        this.shareTime = shareTime;
    }

    public String getShareName() {

        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    public String getShareTime() {
        return shareTime;
    }

    public void setShareTime(String shareTime) {
        this.shareTime = shareTime;
    }
}
