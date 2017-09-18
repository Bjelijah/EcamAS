package com.howell.modules.login.bean;

/**
 * Created by Administrator on 2017/8/15.
 */

public enum Type {
    OK,
    ERROR,
    SESSION_EXPIRED,
    ACCOUNT_NOT_EXIST,
    ACCOUNT_EXIST,
    EMAIL_EXIST,
    AUTHENCATION,//验证失败
    FIRST_LOGIN,
    ERROR_LINK,
}
