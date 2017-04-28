package com.joshuaduffill.quicksnap;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

public class VideoPlayActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener{

    private MediaPlayer mMediaPlayer;
    private Uri mVideoUri;
    private ImageButton btnPlayPause;
    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        //initialise views
        btnPlayPause = (ImageButton) findViewById(R.id.btnVideoPause);
        mSurfaceView = (SurfaceView) findViewById(R.id.videoSurfaceView);

        Intent callIntent = this.getIntent();
        if (callIntent != null) {
            mVideoUri = callIntent.getData();
        }
    }

    public void playPauseClick(View view) {
        if(mMediaPlayer.isPlaying()){
            mediaPause();
        }else{
            mediaPlay();
        }
    }

    @Override
    protected void onStop() {

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (mMediaPlayer != null) {
            mediaPlay();
        } else {
            SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
            surfaceHolder.addCallback(this);
        }
    }

    private void mediaPlay() {
        mMediaPlayer.start();
        btnPlayPause.setImageResource(R.drawable.ic_menu_gallery);
    }

    private void mediaPause() {
        mMediaPlayer.pause();
        btnPlayPause.setImageResource(R.drawable.ic_menu_send);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mMediaPlayer = MediaPlayer.create(this, mVideoUri, surfaceHolder);
        mMediaPlayer.setOnCompletionListener(this);
        //plays video right away
        mediaPlay();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        btnPlayPause.setImageResource(R.drawable.ic_menu_send);
    }
}
