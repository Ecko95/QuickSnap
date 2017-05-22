package com.joshuaduffill.quicksnap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mukesh.image_processing.ImageProcessor;

import java.io.File;

public class FullScreenImageActivity extends AppCompatActivity {

    private ImageView fullScreenImageView;
    private ProgressBar mProgressBar;
    private Bitmap mPhotoBitmap;
    private Uri fileUri;

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
            fileUri = imageUri;
            if(imageUri != null && fullScreenImageView != null){
                Glide.with(this)
                        .load(imageUri)
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
                Toast.makeText(this, imageUri.getPath(), Toast.LENGTH_SHORT).show();
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

    Thread clearCache = new Thread(){
        @Override
        public void run() {
            try{
                Glide.get(getApplicationContext()).clearDiskCache();

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    };

    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
        switch (item.getItemId()){
            case R.id.option_delete:
                try{
                    Toast.makeText(this,"deleted", Toast.LENGTH_SHORT).show();

                    //not working
//                    new File (fileUri.getPath()).getAbsoluteFile().delete();
//                    File file = new File(fileUri.getPath());
//                    file.delete();
//                    if(file.exists()){
//                        file.getCanonicalFile().delete();
//                        if(file.exists()){
//                            getApplicationContext().deleteFile(file.getName());
//                        }
//                    }
                    clearCache.start();

                }catch(Exception ex){

                }
                return true;
            case R.id.option_edit:
                try{
                    Toast.makeText(this,"open editor", Toast.LENGTH_SHORT).show();

                    Intent editImageIntent = new Intent(this, EditImageActivity.class);
                    editImageIntent.setData(fileUri);
                    startActivity(editImageIntent);

                }catch(Exception ex){

                }
                //add more here
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.context, menu);
        return true;
    }




}

