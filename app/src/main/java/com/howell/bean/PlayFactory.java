package com.howell.bean;

/**
 * Created by howell on 2016/11/29.
 */

public class PlayFactory {
    private static IPlay mp;
    public static IPlay buildPlay(PlayType type){
        switch (type){
            case ECAM:
                mp = new EcamPlay();
                break;
            case TURN:
                break;
            case HW5198:
                break;
            default:
                break;
        }
        return mp;
    }


}
