package com.example.wx091.allfiles.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wx091.allfiles.R;
import com.example.wx091.allfiles.Utils.FileUtil;
import com.example.wx091.allfiles.Utils.TimeUtil;
import com.example.wx091.allfiles.beans.IConstant;

import java.io.File;
import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class VideoActivity extends AppCompatActivity {
    TextView titleTV,onplaylengthTV,lengthTV;
    SeekBar progressSB;
    Button playBtn,  nextBtn, backBtn;
    LinearLayout autoHideLY;
    SurfaceView sv;
    MediaPlayer mediaPlayer;
    boolean isplaying;
    int position,currentPosition=0;
    File[] files;
    String TAG="Video";

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements

            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_video);

        mVisible = true;
        mControlsView = findViewById(R.id.show_video_auto_hide);
        mContentView = findViewById(R.id.show_video_sv);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        mControlsView.setOnTouchListener(mDelayHideTouchListener);
        initView();
        loadVideo(0);
    }
    @Override
    protected void onStop(){
        mediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    private void initView() {
        Intent intent = getIntent();
        position = intent.getIntExtra("childPosition",-1);
        titleTV=(TextView) findViewById(R.id.show_video_title);
        onplaylengthTV= (TextView) findViewById(R.id.show_video_onplaylength);
        lengthTV= (TextView) findViewById(R.id.show_video_length);
        progressSB= (SeekBar) findViewById(R.id.show_video_seekBar);
        playBtn= (Button) findViewById(R.id.show_video_play);
        nextBtn= (Button) findViewById(R.id.show_video_next);
        backBtn= (Button) findViewById(R.id.show_video_back);
        sv= (SurfaceView) findViewById(R.id.show_video_sv);

        progressSB.setOnSeekBarChangeListener(change);
        playBtn.setOnClickListener(click);
        nextBtn.setOnClickListener(click);
        backBtn.setOnClickListener(click);

        sv.getHolder().addCallback(callback);
        //read all files in foldpath[0], same with mainactivity
        File file = FileUtil.makeDir(IConstant.FoldPath[0]);
        files = file.listFiles();

        titleTV.setText(files[position].getName());
    }

    private SeekBar.OnSeekBarChangeListener change = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 当进度条停止修改的时候触发
            // 取得当前进度条的刻度
            int progress = seekBar.getProgress();
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                // 设置当前播放的位置
                mediaPlayer.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }
    };

    private View.OnClickListener click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.show_video_play:
                    play();
                    break;
                case R.id.show_video_next:
                    next();
                    break;
                case R.id.show_video_back:
                    back();
                    break;
            }

        }
    };
    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        // SurfaceHolder被修改的时候回调
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "SurfaceHolder 被销毁");
            // 销毁SurfaceHolder的时候记录当前的播放位置并停止播放
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                currentPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "SurfaceHolder 被创建");
            mediaPlayer.setDisplay(sv.getHolder());
            if (currentPosition > 0) {
                // 创建SurfaceHolder的时候，如果存在上次播放的位置，则按照上次播放位置进行播放
                loadVideo(currentPosition);
                currentPosition = 0;
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            Log.i(TAG, "SurfaceHolder 大小被改变");
        }
    };

    public void loadVideo(final int second){
        try{
            mediaPlayer=new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(files[position].getAbsolutePath());
            try{
                mediaPlayer.setDisplay(sv.getHolder());
            }catch (Exception e){

            }
//            mediaPlayer.prepare();
//            mediaPlayer.start();
            mediaPlayer.prepareAsync();
            Log.i(TAG, "开始装载");
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.i(TAG, "装载完成");
                    mediaPlayer.start();
                    mediaPlayer.seekTo(second);
                    progressSB.setMax(mediaPlayer.getDuration());
                    lengthTV.setText(TimeUtil.intToTime(mediaPlayer.getDuration()));
                    titleTV.setText(files[position].getName());
                    //onplaylengthTV.setText("unknow length");
                    new Thread() {
                        @Override
                        public void run() {
                            isplaying = true;
                            try {
                                while (isplaying) {
                                    int currentplay = mediaPlayer.getCurrentPosition();
                                    progressSB.setProgress(currentplay);
                                    //onplaylengthTV.setText(TimeUtil.intToTime(currentplay));
                                    sleep(500);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void play(){
        if(isplaying){
            playBtn.setBackground(getResources().getDrawable(R.drawable.icon_play));
            mediaPlayer.pause();
            isplaying=false;
        }else{
            playBtn.setBackground(getResources().getDrawable(R.drawable.icon_stop));
            mediaPlayer.start();
            isplaying=true;
        }
    }
    public void next(){
        if(position<files.length-1){
            position+=1;
            mediaPlayer.reset();

            loadVideo(0);
        }else{
            Toast.makeText(VideoActivity.this, "已经是最后一个视频了", Toast.LENGTH_SHORT).show();
        }
    }
    public void back(){
        if(position>0){
            position-=1;
            mediaPlayer.reset();
            loadVideo(0);
        }else{
            Toast.makeText(VideoActivity.this,"已经是第一个视频了",Toast.LENGTH_SHORT).show();
        }
    }
    protected void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isplaying = false;
        }
    }


}
