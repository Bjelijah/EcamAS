package com.howell.bean;

import java.util.List;

/**
 * Created by howell on 2016/11/28.
 */

public class NoticeItemBean {
    private String title;
    private String description;
    private String time;
    private List<String> picID;
    public String getDescription() {
        return description;
    }

    public List<String> getPicID() {
        return picID;
    }

    public NoticeItemBean setPicID(List<String> picID) {
        this.picID = picID;
        return this;
    }

    public NoticeItemBean setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getTime() {
        return time;
    }

    public NoticeItemBean setTime(String time) {
        this.time = time;
        return this;
    }

    public NoticeItemBean(String notice) {
        this.title = notice;
    }
    public NoticeItemBean(){};

    public String getTitle() {
        return title;
    }

    public NoticeItemBean setTitle(String notice) {
        this.title = notice;
        return this;
    }

    public NoticeItemBean(String notice, String description, String time) {
        this.title = notice;
        this.description = description;
        this.time = time;
    }

    @Override
    public String toString() {
        return "NoticeItemBean{" +
                "notice='" + title + '\'' +
                '}';
    }
}
