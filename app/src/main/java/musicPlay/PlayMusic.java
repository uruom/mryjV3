package musicPlay;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;


import com.baidu.paddle.lite.demo.object_detection.R;

import Veriable.TransmissionVeriable;

/**
 * @author uruom
 * @version 1.0
 * @ClassName PlayMusic
 * @date 2022/9/11 14:52
 */
public class PlayMusic extends AppCompatActivity {
    private MediaPlayer mediaPlayerbus_double;
    private MediaPlayer mediaPlayerbus_left;
    private MediaPlayer mediaPlayerbus_right;
    private MediaPlayer mediaPlayercar_double;
    private MediaPlayer mediaPlayercar_left;
    private MediaPlayer mediaPlayercar_right;
    private MediaPlayer mediaPlayerfront_double;
    private MediaPlayer mediaPlayerfront_right;
    private MediaPlayer mediaPlayerfront_left;
    private MediaPlayer mediaPlayerlight_double;
    private MediaPlayer mediaPlayerlight_right;
    private MediaPlayer mediaPlayerlight_left;
    private MediaPlayer mediaPlayermotor_double;
    private MediaPlayer mediaPlayermotor_left;
    private MediaPlayer mediaPlayermotor_right;
    private MediaPlayer mediaPlayerperson_double;
    private MediaPlayer mediaPlayerperson_left;
    private MediaPlayer mediaPlayerperson_right;
    private MediaPlayer mediaPlayerside_double;
    private MediaPlayer mediaPlayerside_left;
    private MediaPlayer mediaPlayerside_right;
    private MediaPlayer mediaPlayersignage_double;
    private MediaPlayer mediaPlayersignage_left;
    private MediaPlayer mediaPlayersignage_right;

    public PlayMusic(){
        mediaPlayerbus_double = MediaPlayer.create(this,R.raw.bus_double);
        mediaPlayerbus_left = MediaPlayer.create(this,R.raw.bus_left);
        mediaPlayerbus_right = MediaPlayer.create(this,R.raw.bus_right);
        mediaPlayercar_double = MediaPlayer.create(this,R.raw.car_double);
        mediaPlayercar_left = MediaPlayer.create(this,R.raw.car_left);
        mediaPlayercar_right = MediaPlayer.create(this,R.raw.car_right);
        mediaPlayerfront_double = MediaPlayer.create(this,R.raw.front_double);
        mediaPlayerfront_left = MediaPlayer.create(this,R.raw.front_left);
        mediaPlayerfront_right = MediaPlayer.create(this,R.raw.front_right);
        mediaPlayerlight_double = MediaPlayer.create(this,R.raw.light_double);
        mediaPlayerlight_right = MediaPlayer.create(this,R.raw.light_right);
        mediaPlayerlight_left = MediaPlayer.create(this,R.raw.light_left);
        mediaPlayermotor_double = MediaPlayer.create(this,R.raw.motor_double);
        mediaPlayermotor_left = MediaPlayer.create(this,R.raw.motor_left);
        mediaPlayermotor_right = MediaPlayer.create(this,R.raw.motor_right);
        mediaPlayerperson_double = MediaPlayer.create(this,R.raw.person_double);
        mediaPlayerperson_right = MediaPlayer.create(this,R.raw.person_right);
        mediaPlayerperson_left = MediaPlayer.create(this,R.raw.person_left);
        mediaPlayerside_double = MediaPlayer.create(this,R.raw.side_double);
        mediaPlayerside_right = MediaPlayer.create(this,R.raw.side_right);
        mediaPlayerside_left = MediaPlayer.create(this,R.raw.side_left);
        mediaPlayersignage_double =MediaPlayer.create(this,R.raw.signage_double);
        mediaPlayersignage_left = MediaPlayer.create(this,R.raw.signage_left);
        mediaPlayersignage_right = MediaPlayer.create(this,R.raw.signage_right);
    }



    public void startPlay(TransmissionVeriable transmissionVeriable){
        switch (transmissionVeriable.getDirection()){
            case LEFT: mediaPlayerside_left.start();
            case LEFT_FONT: mediaPlayerfront_left.start();
            case FONT: mediaPlayerfront_double.start();
            case RIGHT: mediaPlayerside_right.start();
            case RIGHT_FONT: mediaPlayerfront_right.start();
        }
//        switch (transmissionVeriable.)
    }

}
