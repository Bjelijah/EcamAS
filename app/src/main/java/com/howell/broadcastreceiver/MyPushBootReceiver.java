package com.howell.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.howell.service.MyPushService;

/**
 * Created by Administrator on 2016/12/23.
 */

public class MyPushBootReceiver extends BroadcastReceiver {
    private final String ACTION_BOOT1 = "android.intent.action.BOOT_COMPLETED";
    private final String ACTION_BOOT2 = "android.intent.action.QUICKBOOT_POWERON";
    private final String ACTION_BOOT3 = "com.htc.intent.action.QUICKBOOT_POWERON";
    private final String ACTION_BOOT4 = "android.intent.action.REBOOT";
    private final String ACTION_PUSH = "com.howell.service.pushService";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("123","we get boot completed");
        //TODO start my push server in background;
        if (ACTION_BOOT1.equals(intent.getAction())
                ||  ACTION_BOOT2.equals(intent.getAction())
                || ACTION_BOOT3.equals(intent.getAction())
                || ACTION_BOOT4.equals(intent.getAction())
                || ACTION_PUSH.equals(intent.getAction())){
            Intent myIntent = new Intent(context, MyPushService.class);
            context.startService(myIntent);
        }
    }
}
