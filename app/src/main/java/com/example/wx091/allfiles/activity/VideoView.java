package com.example.wx091.allfiles.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

import com.example.wx091.allfiles.R;
import com.example.wx091.allfiles.Utils.FileUtil;
import com.example.wx091.allfiles.Utils.FullScreenVideoView;
import com.example.wx091.allfiles.beans.IConstant;

import java.io.File;

public class VideoView extends AppCompatActivity {
    int position,currentPosition=0;
    File[] files;
    String TAG="Video";
    private FullScreenVideoView vv_video;
    private MediaController mController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_video_view);
        initview();
    }

    private void initview() {
        Intent intent = getIntent();
        position = intent.getIntExtra("childPosition",-1);
        File file = FileUtil.makeDir(IConstant.FoldPath[0]);
        files = file.listFiles();

        vv_video=(FullScreenVideoView) findViewById(R.id.video_view);
        mController=new MediaController(this);
        vv_video.setMediaController(mController);
        mController.setMediaPlayer(vv_video);
        mController.setPrevNextListeners(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (position < files.length - 1) {
                    position+=1;
                    vv_video.setVideoPath(files[position].getAbsolutePath());
                    vv_video.start();
                } else {
                    Toast.makeText(VideoView.this, "已经是最后一个视频了", Toast.LENGTH_SHORT).show();
                }
            }
        }, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (position >0) {
                    position-=1;
                    vv_video.setVideoPath(files[position].getAbsolutePath());
                    vv_video.start();
                } else {
                    Toast.makeText(VideoView.this, "已经是最后一个视频了", Toast.LENGTH_SHORT).show();
                }
            }
        });
        vv_video.setVideoPath(files[position].getAbsolutePath());
        vv_video.start();

    }
}
