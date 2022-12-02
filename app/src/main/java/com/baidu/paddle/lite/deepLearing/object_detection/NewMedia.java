package com.baidu.paddle.lite.deepLearing.object_detection;

import static com.baidu.paddle.lite.deepLearing.object_detection.MainActivity.isPlayingMusic;

import android.media.MediaPlayer;
import android.media.VolumeShaper;
import android.support.annotation.NonNull;

/**
 * @author uruom
 * @version 1.0
 * @ClassName NewMedia
 * @date 2022/11/9 13:07
 */
public class NewMedia extends MediaPlayer {
    public void start(){
        isPlayingMusic = true;
        super.start();
        while(isPlaying()){

        }
        isPlayingMusic = false;
    }

    @NonNull
    @Override
    public VolumeShaper createVolumeShaper(@NonNull VolumeShaper.Configuration configuration) {
        return super.createVolumeShaper(configuration);
    }

}
