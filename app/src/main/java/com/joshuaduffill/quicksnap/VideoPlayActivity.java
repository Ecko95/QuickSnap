package com.joshuaduffill.quicksnap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

public class VideoPlayActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener,
    AudioManager.OnAudioFocusChangeListener{

    private MediaPlayer mMediaPlayer;
    private Uri mVideoUri;
    private ImageButton btnPlayPause;
    private SurfaceView mSurfaceView;
    private AudioManager mAudioManager;
    private NoisyAudio mNoisyAudio;
    //intent filter to specify the right action to get notify of
    private IntentFilter mNoisyIntentFilter;

    //broacast receviver for noise audio
    private class NoisyAudio extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            mediaPause();
        }
    }

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

        //casting AudioManager to system service
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mNoisyAudio = new NoisyAudio();
        mNoisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
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
        registerReceiver(mNoisyAudio,mNoisyIntentFilter);
        //request permission to use media audio focus
        int requestAudioFocusResult = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        if(requestAudioFocusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mMediaPlayer.start();
            btnPlayPause.setImageResource(R.drawable.ic_menu_gallery);
        }
    }

    private void mediaPause() {
        mMediaPlayer.pause();
        btnPlayPause.setImageResource(R.drawable.ic_menu_send);
        mAudioManager.abandonAudioFocus(this);
        unregisterReceiver(mNoisyAudio);
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if(hasFocus){
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

    }

    @Override
    public void onAudioFocusChange(int audioFocusChanged) {
        switch (audioFocusChanged){
            //stops playing video if other apps interrupt
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                mediaPause();
                break;
            //resumes playing after interruption
            case AudioManager.AUDIOFOCUS_GAIN:
                mediaPlay();

                //if another applications takes over the audio for a long period of time
            case AudioManager.AUDIOFOCUS_LOSS:
                mediaPause();
                break;

        }
    }
}
