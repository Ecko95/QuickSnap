package com.joshuaduffill.quicksnap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Tasks;
import com.mukesh.image_processing.ImageProcessor;

import java.util.concurrent.ThreadPoolExecutor;

public class FullScreenImageActivity extends AppCompatActivity {

    private ImageView fullScreenImageView;
    private ProgressBar mProgressBar;
    private Bitmap mPhotoBitmap;

    // An object that manages Messages in a Thread
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        setContentView(R.layout.activity_full_screen_image);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBarImage);
        fullScreenImageView = (ImageView) findViewById(R.id.fullScreenImageView);

        Intent callActivityIntent = getIntent();
        if(callActivityIntent != null){
            Uri imageUri = callActivityIntent.getData();
            if(imageUri != null && fullScreenImageView != null){
                Glide.with(this)
                        .load(imageUri)
                        .skipMemoryCache( true )
                        .listener(new RequestListener<Uri, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                                mProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                mProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(fullScreenImageView);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if(hasFocus){
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//            | View.SYSTEM_UI_FLAG_FULLSCREEN
//            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

    }



    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
        switch (item.getItemId()){
            case R.id.filter_greyscale:
                try{
//                    mPhotoBitmap = ((BitmapDrawable)fullScreenImageView.getDrawable()).getBitmap();
                    mPhotoBitmap= ((GlideBitmapDrawable)fullScreenImageView.getDrawable().getCurrent()).getBitmap();
//                    mPhotoBitmap= ((GlideBitmapDrawable)fullScreenImageView.getDrawable().getCurrent()).getBitmap();
                    Toast.makeText(this,"rendering greyscale", Toast.LENGTH_SHORT).show();

                    ImageProcessor imageProcessor = new ImageProcessor();
                    fullScreenImageView.setImageBitmap(imageProcessor.doGreyScale(mPhotoBitmap));
                }catch(android.content.ActivityNotFoundException anfe){

                }
                return true;
            case R.id.filter_invert:
                try{
//                    mPhotoBitmap = ((BitmapDrawable)fullScreenImageView.getDrawable()).getBitmap();
                    mPhotoBitmap= ((GlideBitmapDrawable)fullScreenImageView.getDrawable().getCurrent()).getBitmap();
                    Toast.makeText(this,"rendering invert", Toast.LENGTH_SHORT).show();

                    ImageProcessor imageProcessor = new ImageProcessor();
                    fullScreenImageView.setImageBitmap(imageProcessor.doInvert(mPhotoBitmap));
                }catch(android.content.ActivityNotFoundException anfe){

                }
                //add more here
            default:
                return super.onOptionsItemSelected(item);
        }

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
    }

    // https://developer.android.com/training/multiple-threads/define-runnable.html

    //https://stackoverflow.com/questions/1921514/how-to-run-a-runnable-thread-in-android

//    Thread renderingThread = new Thread(){
//        @Override
//        public void run() {
//            try{
//                //renders grayscale image
//                ImageProcessor imageProcessor = new ImageProcessor();
//                fullScreenImageView.setImageBitmap(imageProcessor.doGreyScale(mPhotoBitmap));
//
//            }catch (Exception ex){
//                ex.printStackTrace();
//            }
//        }
//    };


    public class Rendering implements Runnable{
        @Override
        public void run() {
            //moves the current Thread into the background
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

//            mHandler = new Handler(Looper.getMainLooper()) {
//                @Override
//                public void handleMessage(Message msg) {
//
//                    PhotoTask photoTask = (PhotoTask) inputMessage.obj;
//                    super.handleMessage(msg);
//                }
//            };

            //renders grayscale image
            ImageProcessor imageProcessor = new ImageProcessor();
            fullScreenImageView.setImageBitmap(imageProcessor.doGreyScale(mPhotoBitmap));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filters, menu);
        return true;
    }




}

