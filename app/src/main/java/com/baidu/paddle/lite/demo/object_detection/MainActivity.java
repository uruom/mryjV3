package com.baidu.paddle.lite.demo.object_detection;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.baidu.paddle.lite.demo.common.CameraSurfaceView;
import com.baidu.paddle.lite.demo.common.Utils;
import com.baidu.paddle.lite.demo.object_detection.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import Veriable.TransmissionVeriable;
import musicPlay.MusicBroadcast;

public class MainActivity extends Activity implements View.OnClickListener, CameraSurfaceView.OnTextureChangedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
//android:name="com.baidu.paddle.lite.demo.object_detection.MainActivity"
    CameraSurfaceView svPreview;
    TextView tvStatus;
    ImageButton btnSwitch;
    ImageButton btnShutter;
    ImageButton btnSettings;

    String savedImagePath = "result.jpg";
    int lastFrameIndex = 0;
    long lastFrameTime;

    Native predictor = new Native();

    public static Boolean isPlayingMusic = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("test1");
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        System.out.println("test2");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        System.out.println("test3");

        setContentView(R.layout.activity_main);

        System.out.println("test4");
        // Clear all setting items to avoid app crashing due to the incorrect settings
        initSettings();
        System.out.println("test5");

        // Init the camera preview and UI components
        initView();
        System.out.println("test6");

        // Check and request CAMERA and WRITE_EXTERNAL_STORAGE permissions
        if (!checkAllPermissions()) {
            System.out.println("test7");
            requestAllPermissions();
            System.out.println("test8");
        }
        System.out.println("test11");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_switch:
                svPreview.switchCamera();
                break;
            case R.id.btn_shutter:
                SimpleDateFormat date = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                synchronized (this) {
                    savedImagePath = Utils.getDCIMDirectory() + File.separator + date.format(new Date()).toString() + ".png";
                }
                Toast.makeText(MainActivity.this, "Save snapshot to " + savedImagePath, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }
    }
    private MediaPlayer getMediaDirection(int[] modfied){
        MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.bus_left);;
        System.out.println("=====================");
        System.out.println(modfied);

        switch (modfied[0]){
            case 0:

        }
        System.out.println("===============");
        return mediaPlayer;
    }
    private MediaPlayer getMediaObject(int[] modified){
        /*    rider
                train
        car
                bike
        person
        traffic sign
        traffic light
        truck
                bus
        motor*/
        MediaPlayer mediaPlayer  = MediaPlayer.create(this,R.raw.isstart);
//        所有没有的音频我用isend代替，这个花花说不用，我也觉得不用qwq
        switch (modified[0]){
            case 0:
//                rider
                mediaPlayer = MediaPlayer.create(this,R.raw.isend);
                break;
            case 1:
//                train
                mediaPlayer = MediaPlayer.create(this,R.raw.isend);
                break;
            case 2:
                mediaPlayer = MediaPlayer.create(this,R.raw.car_double);
                break;
            case 3:
//                bike
                mediaPlayer = MediaPlayer.create(this,R.raw.isend);
                break;
            case 4:
                mediaPlayer = MediaPlayer.create(this,R.raw.person_double);
                break;
            case 5:
                mediaPlayer = MediaPlayer.create(this,R.raw.signage_double);
                break;
            case 6:
                mediaPlayer = MediaPlayer.create(this,R.raw.light_double);
                break;
            case 7:
//                trunk
                mediaPlayer = MediaPlayer.create(this,R.raw.isend);
                break;
            case 8:
                mediaPlayer = MediaPlayer.create(this,R.raw.bus_double);
                break;
            case 9:
                mediaPlayer = MediaPlayer.create(this,R.raw.motor_double);
                break;
            default:
                mediaPlayer = MediaPlayer.create(this,R.raw.isstart);
                break;
        }
        return mediaPlayer;
    }

    @Override
    public boolean onTextureChanged(Bitmap ARGB8888ImageBitmap) {
        TransmissionVeriable transmissionVeriable = TransmissionVeriable.getInstance();
        System.out.println("T1");
        String savedImagePath = "";
        synchronized (this) {
            savedImagePath = MainActivity.this.savedImagePath;
        }
        //预测
        int[] modified = predictor.process(ARGB8888ImageBitmap, savedImagePath);
        if (!savedImagePath.isEmpty()) {
            synchronized (this) {
                MainActivity.this.savedImagePath = "result.jpg";
            }
        }
        lastFrameIndex++;
        if (lastFrameIndex >= 30) {
            final int fps = (int) (lastFrameIndex * 1e9 / (System.nanoTime() - lastFrameTime));
            runOnUiThread(new Runnable() {
                public void run() {
                    tvStatus.setText(Integer.toString(fps) + "fps");
                }
            });
            lastFrameIndex = 0;
            lastFrameTime = System.nanoTime();
        }
        System.out.println("T2");
//        id,x,y,三倍于物体大小
        transmissionVeriable.setModified(modified);
//        MusicBroadcast musicBroadcast = new MusicBroadcast();
//        MediaPlayer mediaPlayer = MediaPlayer.create(this,musicBroadcast.reTest());
//        final MediaPlayer mediaPlayer =  MediaPlayer.create(this,R.raw.bus_left);
//        final MediaPlayer mediaDirection = getMediaDirection(modified);
//        final MediaPlayer mediaObject = getMediaObject(modified);
        if(modified.length==0){
            return false;
        }
//        NewMedia newMedia = (NewMedia) NewMedia.create(this,R.raw.bus_left);
        if(!isPlayingMusic){
            final MediaPlayer mediaObject = getMediaObject(modified);
            Thread thread = new Thread(){
                @Override
                public void run() {
//                    MediaPlayer mediaPlayer =  MediaPlayer.create(this, R.raw.bus_left);
                    isPlayingMusic = true;
//                    mediaDirection.start();
//                    while(mediaDirection.isPlaying()){
//
//                    }
                    mediaObject.start();
                    while(mediaObject.isPlaying()){

                    }
                    isPlayingMusic = false;

                }
            };
            thread.start();
//            newMedia.start();
        }
        return true;

    }

    @Override
    protected void onResume() {
        System.out.println("T3");
        super.onResume();
        // Reload settings and re-initialize the predictor
        checkAndUpdateSettings();
        // Open camera until the permissions have been granted
        if (!checkAllPermissions()) {
            svPreview.disableCamera();
        }
        svPreview.onResume();
        System.out.println("T4");
    }

    @Override
    protected void onPause() {
        System.out.println("T5");
        super.onPause();
        svPreview.onPause();
        System.out.println("T6");
    }

    @Override
    protected void onDestroy() {
        System.out.println("T7");
        if (predictor != null) {
            predictor.release();
        }
        super.onDestroy();
        System.out.println("T8");
    }

    public void initView() {
        System.out.println("T9");
        svPreview = (CameraSurfaceView) findViewById(R.id.sv_preview);
        svPreview.setOnTextureChangedListener(this);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        btnSwitch = (ImageButton) findViewById(R.id.btn_switch);
        btnSwitch.setOnClickListener(this);
        btnShutter = (ImageButton) findViewById(R.id.btn_shutter);
        btnShutter.setOnClickListener(this);
        btnSettings = (ImageButton) findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(this);
        System.out.println("T10");
    }

    public void initSettings() {
        System.out.println("T11");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        SettingsActivity.resetSettings();
        System.out.println("T12");
    }

    public void checkAndUpdateSettings() {
        System.out.println("T13");
        if (SettingsActivity.checkAndUpdateSettings(this)) {
            String realModelDir = getCacheDir() + "/" + SettingsActivity.modelDir;
            Utils.copyDirectoryFromAssets(this, SettingsActivity.modelDir, realModelDir);
            String realLabelPath = getCacheDir() + "/" + SettingsActivity.labelPath;
            Utils.copyFileFromAssets(this, SettingsActivity.labelPath, realLabelPath);
            System.out.printf("中点; model dir: %s %s, label dir: %s %s\n",
                    realModelDir, SettingsActivity.modelDir, realLabelPath, SettingsActivity.labelPath);
            // 初始化
            predictor.init(
                    realModelDir,
                    realLabelPath,
                    SettingsActivity.cpuThreadNum,
                    SettingsActivity.cpuPowerMode,
                    SettingsActivity.inputWidth,
                    SettingsActivity.inputHeight,
                    SettingsActivity.inputMean,
                    SettingsActivity.inputStd,
                    SettingsActivity.scoreThreshold);
        }
        System.out.println("T14");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        System.out.println("T15");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Permission denied")
                    .setMessage("Click to force quit the app, then open Settings->Apps & notifications->Target " +
                            "App->Permissions to grant all of the permissions.")
                    .setCancelable(false)
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    }).show();
        }
        System.out.println("T6");
    }

    private void requestAllPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA}, 0);
    }

    private boolean checkAllPermissions() {
        System.out.println("test9");
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
//        System.out.println("test10");
    }
}
