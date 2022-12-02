package mryj;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.baidu.paddle.lite.demo.object_detection.MainActivity;
import com.baidu.paddle.lite.demo.object_detection.R;

import Veriable.TransmissionVeriable;
import musicPlay.PlayMusic;
import picturetransmission.PictureTransmission2;

public class TestMainActivity extends AppCompatActivity {

    private Button btn_start;
    private Automat automat;
    private MediaPlayer mediaPlayerEnd;
    private MediaPlayer mediaPlayerIsEnd;
    private MediaPlayer mediaPlayerIsStart;
    private MediaPlayer mediaPlayerRunning;
    private ImageView imageViewLeft;
    private ImageView imageViewRight;
    private MediaPlayer mediaPlayerLeft;
    public String text_p;
    Intent intent = null;
    TransmissionVeriable transmissionVeriable = TransmissionVeriable.getInstance();
//    PlayMusic playMusic = new PlayMusic();
    PictureTransmission2 pictureTransmission2 = new PictureTransmission2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        System.out.println("????");
        mediaPlayerEnd = MediaPlayer.create(this, R.raw.end);
        mediaPlayerIsEnd = MediaPlayer.create(this,R.raw.isend);
        mediaPlayerIsStart= MediaPlayer.create(this,R.raw.isstart);
        mediaPlayerRunning = MediaPlayer.create(this,R.raw.running);
        intent = new Intent(TestMainActivity.this,MainActivity.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testmain);
        btn_start = findViewById(R.id.btn_main_start);
        imageViewLeft = findViewById(R.id.image_left);
        imageViewRight = findViewById(R.id.image_right);
//        System.out.println("传输位置1");
//        startActivity(intent);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                System.out.println("传输位置1");
                automat = automat.getAutomat();
                automat.updateStatu(false,true,false);
                try {
                    musicPlay(automat.getAutomat().getStatu());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    protected void musicPlay(Estatus estatus) throws InterruptedException {
//        startActivity(intent);
        switch (estatus){
            case Prime:
                mediaPlayerEnd.start();
                btn_start.setText("终止状态");
                break;
            case IsStart:
                mediaPlayerIsStart.start();
                System.out.println("oijiojoio");
//                btn_start.setText("确认开始");
                running();
                break;
            case IsClose:
                mediaPlayerIsEnd.start();
                btn_start.setText("确认结束");
                break;
            case Running:
                mediaPlayerRunning.start();
                btn_start.setText("运行中");

                break;
        }

    }

    private void running() throws InterruptedException {
        System.out.println("Test");
        pictureTransmission2.startPictureTransmission();
            sleep(1000);
            if(transmissionVeriable.getRightImage()!=null){
                if (transmissionVeriable.getLeftImage()!=null) {
                    btn_start.setText("传输成功");
                    imageViewLeft.setImageBitmap(transmissionVeriable.getLeftImage());
                    imageViewRight.setImageBitmap(transmissionVeriable.getRightImage());
                }else{
                    btn_start.setText("右成左败");
                }
            }else{
                if (transmissionVeriable.getLeftImage()!=null) {
                    btn_start.setText("左成右败");
                }else{
                    btn_start.setText("传输失败");
                }

            }
//            sleep(2000);
        System.out.println("test");
        startActivity(intent);
            System.out.println("返回正常");
//        playMusic.startPlay(transmissionVeriable);
    }

}