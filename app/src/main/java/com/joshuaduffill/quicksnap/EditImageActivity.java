package com.joshuaduffill.quicksnap;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcelable;
import android.os.Process;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.mukesh.image_processing.ImageProcessor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.graphics.Bitmap.CompressFormat.PNG;
import static java.lang.System.out;

public class EditImageActivity extends AppCompatActivity {

    private ImageView mPhotoCaptuedImageView;
    private Bitmap mPhotoBitmap;
    private Bitmap mEditedBitmap;
    private Bitmap mEditOperation;
    private Bitmap mOriginalBitmap;
    private ProgressBar mProgressBar;
    private ProgressBar mRenderingBar;
    private HorizontalScrollView mfilterView;
    private ImageButton mButtonFilter;
    private Button mCancelButton;

    private int progressBarStatus = 0;
    private Handler progressBarbHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        mButtonFilter = (ImageButton) findViewById(R.id.btn_filters);
        mfilterView = (HorizontalScrollView) findViewById(R.id.filterScrollView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarImage);
        mRenderingBar = (ProgressBar) findViewById(R.id.renderingProgressBar);
        mPhotoCaptuedImageView = (ImageView) findViewById(R.id.editImageView);
        mCancelButton = (Button) findViewById(R.id.btn_cancel);

        Intent intent = getIntent();
        Uri imageUri = intent.getParcelableExtra("URL");
        if (getIntent().getExtras() != null) {
            Glide.with(this)
                    .load(imageUri)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(1920,1080)
                    .fitCenter()
                    .listener(new RequestListener<Parcelable, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, Parcelable model, Target<Bitmap> target, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Parcelable model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            mOriginalBitmap = resource;
                            return false;
                        }
                    })
                    .into(mPhotoCaptuedImageView);
                    Toast.makeText(this, "Original Image saved", Toast.LENGTH_SHORT).show();
        } else {
            Intent callActivityIntent = getIntent();
            Uri imageUri2 = callActivityIntent.getData();
            if(imageUri2 != null && mPhotoCaptuedImageView != null) {
                Glide.with(this)
                        .load(imageUri2)
                        .asBitmap()
                        .override(1920,1080)
                        .skipMemoryCache(true)
                        .listener(new RequestListener<Uri, Bitmap>() {
                            @Override
                            public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                                mProgressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                mProgressBar.setVisibility(View.GONE);
                                //stores the original image
                                mOriginalBitmap = resource;
                                return false;
                            }
                        })
                        .into(mPhotoCaptuedImageView);
                        Toast.makeText(this, "Original Image saved", Toast.LENGTH_SHORT).show();
            }
//            try{
////                mOriginalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                mOriginalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri2);
//                Toast.makeText(this, "Original Image saved", Toast.LENGTH_SHORT).show();
//
//            }catch(IOException ex){
//                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
//            }


        }
    }
    public void finishRendering(){
        Toast.makeText(EditImageActivity.this, "Done!", Toast.LENGTH_SHORT).show();
        mRenderingBar.setVisibility(View.GONE);
    }

    public void openFilters(View view){
        mfilterView.setVisibility(View.VISIBLE);
        mButtonFilter.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.VISIBLE);

        mfilterView.animate().translationY(-30);

        // Prepare the View for the animation

        mfilterView.setAlpha(0.0f);

// Start the animation
        mfilterView.animate()
                .alpha(1.0f);

    }

    public void cancelProcess(View view){
        mfilterView.setVisibility(View.GONE);
        mButtonFilter.setVisibility(View.VISIBLE);
        mCancelButton.setVisibility(View.GONE);
        mfilterView.animate().translationY(0);



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
//            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

    }





    Thread greyScaleRendering = new Thread(){

        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            progressBarbHandler.post(new Runnable() {
                @Override
                public void run() {
                    mRenderingBar.setVisibility(View.VISIBLE);
                }
            });

            try{

                ImageProcessor imageProcessor = new ImageProcessor();
                mEditedBitmap = imageProcessor.doGreyScale(mPhotoBitmap);

            }catch (Exception ex){
                ex.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mPhotoCaptuedImageView.setImageBitmap(mEditedBitmap);

                    finishRendering();
                }
            });
        }

    };
    Thread invertRendering = new Thread(){
        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            progressBarbHandler.post(new Runnable() {
                @Override
                public void run() {
                    mRenderingBar.setVisibility(View.VISIBLE);
                }
            });
            try{
                //renders invert image
                ImageProcessor imageProcessor = new ImageProcessor();
                mEditedBitmap = imageProcessor.doInvert(mPhotoBitmap);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPhotoCaptuedImageView.setImageBitmap(mEditedBitmap);
                    finishRendering();
                }
            });
        }
    };

    Thread normalRendering = new Thread(){
        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            progressBarbHandler.post(new Runnable() {
                @Override
                public void run() {
                    mRenderingBar.setVisibility(View.VISIBLE);
                }
            });
            try{
                //renders normal image

//                mOriginalBitmap.compress(PNG, 50, out);
                mEditedBitmap = mOriginalBitmap;


            }catch (Exception ex){
                ex.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPhotoCaptuedImageView.setImageBitmap(mEditedBitmap);
                    finishRendering();
                }
            });
        }
    };


    public boolean onOptionsItemSelected(MenuItem item){


        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
        switch (item.getItemId()){
            case R.id.filter_greyscale:
                try{
                    mPhotoBitmap = ((BitmapDrawable)mPhotoCaptuedImageView.getDrawable()).getBitmap();
//                    mPhotoBitmap= ((GlideBitmapDrawable)mPhotoCaptuedImageView.getDrawable().getCurrent()).getBitmap();

                    Toast.makeText(this,"this is greyscale", Toast.LENGTH_SHORT).show();

                    greyScaleRendering.start();
//                    ImageProcessor imageProcessor = new ImageProcessor();
//                    mPhotoCaptuedImageView.setImageBitmap(imageProcessor.doGreyScale(mPhotoBitmap));


                }catch(android.content.ActivityNotFoundException anfe){

                }
                return true;
            case R.id.filter_invert:
                try{
                    mPhotoBitmap = ((BitmapDrawable)mPhotoCaptuedImageView.getDrawable()).getBitmap();
//                    mPhotoBitmap= ((GlideBitmapDrawable)mPhotoCaptuedImageView.getDrawable().getCurrent()).getBitmap();
                    invertRendering.start();
//                    ImageProcessor imageProcessor = new ImageProcessor();
//                    mPhotoCaptuedImageView.setImageBitmap(imageProcessor.doInvert(mPhotoBitmap));
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

    public void greyscaleFilter(View view){


//        try {
            mPhotoBitmap = ((BitmapDrawable)mPhotoCaptuedImageView.getDrawable()).getBitmap();
            greyScaleRendering.start();
            //join method waits for the thread to finish, then refresh ********Adds lag********* needs loader
//            greyScaleRendering.join();
            Toast.makeText(this,"this is greyscale", Toast.LENGTH_SHORT).show();

//        }catch (InterruptedException e){
//
//        }
    }

    public void invertFilter(View view){
//        try {
            mPhotoBitmap = ((BitmapDrawable)mPhotoCaptuedImageView.getDrawable()).getBitmap();
            invertRendering.start();
//            invertRendering.join();
            Toast.makeText(this,"this is invert", Toast.LENGTH_SHORT).show();
//        }catch (InterruptedException e){
//
//        }

    }

    public void normalFilter (View view){
//        try {
        mPhotoBitmap = ((BitmapDrawable)mPhotoCaptuedImageView.getDrawable()).getBitmap();
        normalRendering.start();
//            invertRendering.join();
        Toast.makeText(this,"This is the Original", Toast.LENGTH_SHORT).show();
//        }catch (InterruptedException e){
//
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filters, menu);
        return true;
    }
}
