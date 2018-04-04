package com.howell.di.ui.activity;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.howell.activity.Activities;
import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/2/13.
 */
@Module
public class SendWifiModule {
    @Provides
    Activities provideActivities(){
        return Activities.getInstance();
    }

    @Provides
    HomeKeyEventBroadCastReceiver provideReceive(){
       return new HomeKeyEventBroadCastReceiver();
    }

    @Provides
    AudioTrack provideAudioTrack(){
        int buffer_size = AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        return  new AudioTrack(AudioManager.STREAM_MUSIC,16000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,buffer_size,AudioTrack.MODE_STREAM);
    }
}
