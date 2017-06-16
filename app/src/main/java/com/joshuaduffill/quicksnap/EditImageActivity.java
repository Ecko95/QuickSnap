package com.joshuaduffill.quicksnap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mukesh.image_processing.ImageProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditImageActivity extends AppCompatActivity {

    private ImageView mPhotoCapturedImageView;
    private Bitmap mEditedBitmap;
    private Bitmap mOriginalBitmap;
    private Bitmap mGreyscaleBitmap;
    private Bitmap mInvertBitmap;
    private ProgressBar mProgressBar;
    private ProgressBar mRenderingBar;
    private HorizontalScrollView mfilterView;
    private ImageButton mButtonFilter;
    private Button mCancelButton;
    private Uri fileUri;
    private String GALLERY_DIRECTORY_NAME = "QuickSnap";
    private File mGalleryFolder;

    private Handler progressBarbHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }catch (NullPointerException ex){
            //error
        }

        setContentView(R.layout.activity_edit_image);

        mButtonFilter = (ImageButton) findViewById(R.id.btn_filters);
        mfilterView = (HorizontalScrollView) findViewById(R.id.filterScrollView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarImage);
        mRenderingBar = (ProgressBar) findViewById(R.id.renderingProgressBar);
        mPhotoCapturedImageView = (ImageView) findViewById(R.id.editImageView);
        mCancelButton = (Button) findViewById(R.id.btn_cancel);

        createImageGallery();

        Intent callActivityIntent = getIntent();
        if (callActivityIntent != null) {
           fileUri = callActivityIntent.getData();
            Glide.with(this)
                    .load(fileUri)
                    .asBitmap()
                    .override(1024,576)
                    .skipMemoryCache( true )
                    .fitCenter()
                    .listener(new RequestListener<Uri, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            mOriginalBitmap = resource;
                            return false;
                        }
                    })

                    .into(mPhotoCapturedImageView);
//                    Toast.makeText(this, "Original Image saved - from camera", Toast.LENGTH_SHORT).show();
        }

    }

    private void createImageGallery(){
        mGalleryFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), GALLERY_DIRECTORY_NAME);
//        Toast.makeText(this, mGalleryFolder.toString(), Toast.LENGTH_LONG).show();
        //does PICTURES directory exist?
        if(!mGalleryFolder.exists()){
            mGalleryFolder.mkdirs();

        }
    }

    public void saveEditedImage(View view){
        try{
            File photoFile = null;
            photoFile = saveImage();

            MediaScannerConnection.scanFile(
                    getApplicationContext(),
                    new String[]{photoFile.getAbsolutePath()},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.v("Joshua",
                                    "file" + path + "was scanned successfully: " + uri);
                        }
                    }

            );
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void backHome(View view){
        onBackPressed();
        finish();
    }



    private File saveImage() throws IOException{


        mEditedBitmap = ((BitmapDrawable)mPhotoCapturedImageView.getDrawable()).getBitmap();
        if (mEditedBitmap == null){
            Toast.makeText(this, "No picture found", Toast.LENGTH_SHORT).show();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "EDIT_IMAGE_" + timeStamp + "_";
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File editedImage = new File(mGalleryFolder + File.separator + imageFileName + ".jpg");
        FileOutputStream fos = new FileOutputStream(editedImage);

        mEditedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        fos.flush();
        fos.close();

        Toast.makeText(this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();
        return editedImage;
    }


    public void finishRendering(){
//        Toast.makeText(EditImageActivity.this, "Done!", Toast.LENGTH_SHORT).show();
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
        mfilterView.setVisibility(View.INVISIBLE);
        mButtonFilter.setVisibility(View.VISIBLE);
        mCancelButton.setVisibility(View.INVISIBLE);
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
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
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

            if(mGreyscaleBitmap != null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPhotoCapturedImageView.setImageBitmap(mGreyscaleBitmap);
                        finishRendering();
                    }
                });

            }else{
                try{

                    ImageProcessor imageProcessor = new ImageProcessor();
                    mEditedBitmap = imageProcessor.doGreyScale(mOriginalBitmap);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mPhotoCapturedImageView.setImageBitmap(mEditedBitmap);
                        mGreyscaleBitmap = mEditedBitmap;

                        finishRendering();
                    }
                });
            }

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

            if(mInvertBitmap != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPhotoCapturedImageView.setImageBitmap(mInvertBitmap);
                        finishRendering();
                    }
                });
            }
            else{
                try{
                    //renders invert image
                    ImageProcessor imageProcessor = new ImageProcessor();
                    mEditedBitmap = imageProcessor.doInvert(mOriginalBitmap);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPhotoCapturedImageView.setImageBitmap(mEditedBitmap);
                        mInvertBitmap = mEditedBitmap;

                        finishRendering();
                    }
                });
            }

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

            }catch (Exception ex){
                ex.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPhotoCapturedImageView.setImageBitmap(mOriginalBitmap);
                    finishRendering();
                }
            });
        }
    };

    Thread rotate90 = new Thread(){
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
                mEditedBitmap = imageProcessor.rotate(mEditedBitmap,90);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPhotoCapturedImageView.setImageBitmap(mEditedBitmap);
                    finishRendering();
                }
            });


        }
    };

    Thread rotate180 = new Thread(){
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
                mEditedBitmap = imageProcessor.rotate(mEditedBitmap,180);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPhotoCapturedImageView.setImageBitmap(mEditedBitmap);
                    finishRendering();
                }
            });


        }
    };

    Thread sharpen = new Thread(){
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
                mEditedBitmap = imageProcessor.sharpen(mEditedBitmap,15);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPhotoCapturedImageView.setImageBitmap(mEditedBitmap);
                    finishRendering();
                }
            });


        }
    };

    Thread hueBlue = new Thread(){
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
                int x = 0;

                if (x == 0){
                    mEditedBitmap = imageProcessor.boost(mOriginalBitmap,3,0.8);
                    Log.v("execute: ", "boost");
                    x = 1;

                }if(x == 1){
                    Log.v("execute: ", "contrast");
                    //darkroom filters
                    //mEditedBitmap = imageProcessor.doBrightness(mEditedBitmap,-20);
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPhotoCapturedImageView.setImageBitmap(mEditedBitmap);
                    finishRendering();
                }
            });
        }
    };

    public void btn_normal_filter(View view){
        normalRendering.start();
    }
    public void btn_greyscale_filter(View view){
        greyScaleRendering.start();
    }
    public void btn_invert_filter(View view){
        invertRendering.start();
    }
    public void btn_rotate_90 (View view){
        mEditedBitmap = ((BitmapDrawable)mPhotoCapturedImageView.getDrawable()).getBitmap();
        rotate90.start();
    }
    public void btn_rotate_180 (View view){
        mEditedBitmap = ((BitmapDrawable)mPhotoCapturedImageView.getDrawable()).getBitmap();
        rotate180.start();
    }
    public void btn_sharpen_filter (View view){
        mEditedBitmap = ((BitmapDrawable)mPhotoCapturedImageView.getDrawable()).getBitmap();
        sharpen.start();
    }
    public void btn_hue(View view){
        mEditedBitmap = ((BitmapDrawable)mPhotoCapturedImageView.getDrawable()).getBitmap();
        hueBlue.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.full_image_menu, menu);
        return true;
    }
}
