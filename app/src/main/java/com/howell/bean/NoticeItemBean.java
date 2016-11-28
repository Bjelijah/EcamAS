package com.howell.bean;

/**
 * Created by howell on 2016/11/28.
 */

public class NoticeItemBean {
    private String notice;
    private String description;
    private String time;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public NoticeItemBean(String notice) {
        this.notice = notice;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public NoticeItemBean(String notice, String description, String time) {
        this.notice = notice;
        this.description = description;
        this.time = time;
    }

    @Override
    public String toString() {
        return "NoticeItemBean{" +
                "notice='" + notice + '\'' +
                '}';
    }
}
