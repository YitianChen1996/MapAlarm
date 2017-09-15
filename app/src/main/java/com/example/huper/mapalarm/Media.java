package com.example.huper.mapalarm;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by chl on 2017/4/16.
 */

public class Media{
    Vibrator vibrator;
    AudioManager am;
    MediaPlayer mediaPlayer;
    Context context;
    public Media(Context context)
    {
        this.context = context;
    }
    public void play(){
        //vibrator
        vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

        //meida
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            mediaPlayer = new MediaPlayer();
            if(vibrator != null)
            {
                long[] pattern = {1000, 1000}; // OFF/ON/OFF/ON...
                vibrator.vibrate(pattern, 0);
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setLooping(true);
            AssetFileDescriptor file = context.getResources().openRawResourceFd(R.raw.chlsq);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(0.8f, 0.8f);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException ex) {
                mediaPlayer = null;
            }
        }
        else if(am.getRingerMode() == AudioManager.VIBRATE_SETTING_ON)
        {
            if(vibrator != null)
            {
                long[] pattern = {1000, 1000}; // OFF/ON/OFF/ON...
                vibrator.vibrate(pattern, 0);
            }
        }
    }
    public  void pause()
    {
        if (vibrator != null) {
            vibrator.cancel();
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}

