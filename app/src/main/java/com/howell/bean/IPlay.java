package com.howell.bean;

/**
 * Created by howell on 2016/11/29.
 */

public interface IPlay {
    final static int MSG_CONNECT_ERROR = 0x00a0;
    final static int MSG_CONNECT_OK    = 0x00a1;

    void init();
    void deInit();
    void attch(IPlayCallbackObserver ob);
    void detach(IPlayCallbackObserver ob);
    void connect();
    void disconnect();
    void play();
    void stop();



    interface IPlayCallbackObserver{

    }

}
