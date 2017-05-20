package com.joshuaduffill.quicksnap;

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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.bumptech.glide.request.target.Target;
import com.mukesh.image_processing.ImageProcessor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditImageActivity extends AppCompatActivity {

    private ImageView mPhotoCaptuedImageView;
    private Bitmap mPhotoBitmap;
    private Bitmap mEditOperation;
    private ProgressBar mProgressBar;
    private HorizontalScrollView mfilterView;
    private ImageButton mButtonFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        mButtonFilter = (ImageButton) findViewById(R.id.btn_filters);
        mfilterView = (HorizontalScrollView) findViewById(R.id.filterScrollView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarImage);
        mPhotoCaptuedImageView = (ImageView) findViewById(R.id.editImageView);
        Intent intent = getIntent();

        if (getIntent().getExtras() != null) {
            Glide.with(this)
                    .load(intent.getParcelableExtra("URL"))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(500,500)
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
                            return false;
                        }
                    })
                    .into(mPhotoCaptuedImageView);
//

        } else {
            Intent callActivityIntent = getIntent();
            Uri imageUri = callActivityIntent.getData();
            if(imageUri != null && mPhotoCaptuedImageView != null) {
                Glide.with(this)
                        .load(imageUri)
                        .asBitmap()
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
                                return false;
                            }
                        })
                        .into(mPhotoCaptuedImageView);
            }
        }

//        Intent callActivityIntent = getIntent();
//        if(callActivityIntent != null){
//            Uri imageUri = callActivityIntent.getData();
//            if(imageUri != null && mPhotoCaptuedImageView != null){
//                Glide.with(this)
//                        .load(imageUri)
//                        .listener(new RequestListener<Uri, GlideDrawable>() {
//                            @Override
//                            public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                mProgressBar.setVisibility(View.GONE);
//                                return false;
//                            }
//                            @Override
//                            public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                mProgressBar.setVisibility(View.GONE);
//                                return false;
//                            }
//                        })
//                        .into(mPhotoCaptuedImageView);
//            }
//        }



    }

    public void openFilters(View view){
        mfilterView.setVisibility(View.VISIBLE);
        mButtonFilter.setVisibility(View.GONE);
    }

    public void cancelProcess(View view){
        mfilterView.setVisibility(View.GONE);
        mButtonFilter.setVisibility(View.VISIBLE);
        finish();

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

    Thread greyScaleRendering = new Thread(){
        @Override
        public void run() {
            try{
                //renders grayscale image
                ImageProcessor imageProcessor = new ImageProcessor();
                mPhotoCaptuedImageView.setImageBitmap(imageProcessor.doGreyScale(mPhotoBitmap));

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

    };
    Thread invertRendering = new Thread(){
        @Override
        public void run() {
            try{
                //renders invert image
                ImageProcessor imageProcessor = new ImageProcessor();
                mPhotoCaptuedImageView.setImageBitmap(imageProcessor.doInvert(mPhotoBitmap));
            }catch (Exception ex){
                ex.printStackTrace();
            }
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
        try {
            mPhotoBitmap = ((BitmapDrawable)mPhotoCaptuedImageView.getDrawable()).getBitmap();
            greyScaleRendering.start();
            //join method waits for the thread to finish, then refresh ********Adds lag********* needs loader
            greyScaleRendering.join();
            Toast.makeText(this,"this is greyscale", Toast.LENGTH_SHORT).show();

        }catch (InterruptedException e){

        }
    }

    public void invertFilter(View view){
        try {
            mPhotoBitmap = ((BitmapDrawable)mPhotoCaptuedImageView.getDrawable()).getBitmap();
            invertRendering.start();
            invertRendering.join();
            Toast.makeText(this,"this is invert", Toast.LENGTH_SHORT).show();
        }catch (InterruptedException e){

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filters, menu);
        return true;
    }
}
