package com.howell.bean;

/**
 * Created by Administrator on 2017/1/11.
 */

public class ApTimeBean {
    short year;
    short month;
    short dayOfWeek;
    short day;
    short hour;
    short minute;
    short second;
    short msecond;

    public short getYear() {
        return year;
    }

    public void setYear(short year) {
        this.year = year;
    }

    public short getMonth() {
        return month;
    }

    public void setMonth(short month) {
        this.month = month;
    }

    public short getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(short dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public short getDay() {
        return day;
    }

    public void setDay(short day) {
        this.day = day;
    }

    public short getHour() {
        return hour;
    }

    public void setHour(short hour) {
        this.hour = hour;
    }

    public short getMinute() {
        return minute;
    }

    public void setMinute(short minute) {
        this.minute = minute;
    }

    public short getSecond() {
        return second;
    }

    public void setSecond(short second) {
        this.second = second;
    }

    public short getMsecond() {
        return msecond;
    }

    public void setMsecond(short msecond) {
        this.msecond = msecond;
    }

    public ApTimeBean(short year, short month, short dayOfWeek, short day, short hour, short minute, short second, short msecond) {
        this.year = year;
        this.month = month;
        this.dayOfWeek = dayOfWeek;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.msecond = msecond;
    }

    public ApTimeBean() {
    }

    @Override
    public String toString() {
        return "ApTimeBean{" +
                "year=" + year +
                ", month=" + month +
                ", dayOfWeek=" + dayOfWeek +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                ", msecond=" + msecond +
                '}';
    }
}
