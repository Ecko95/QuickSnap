package com.joshuaduffill.quicksnap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBarImage);
        mPhotoCaptuedImageView = (ImageView) findViewById(R.id.editImageView);
        Intent intent = getIntent();

        if (getIntent().getExtras() != null) {
            Glide.with(this)
                    .load(intent.getParcelableExtra("URL"))
                    .asBitmap()
                    .override(600,600)
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
            Toast.makeText(this, "Image is null", Toast.LENGTH_SHORT).show();
        }



    }


    public void editPicture(View view){

//        mPhotoBitmap= ((GlideBitmapDrawable)mPhotoCaptuedImageView.getDrawable().getCurrent()).getBitmap();

//        ImageProcessor imageProcessor = new ImageProcessor();
//        imageProcessor.doGreyScale(mPhotoBitmap);

//        mPhotoBitmap = ((BitmapDrawable)mPhotoCaptuedImageView.getDrawable()).getBitmap();

//        mEditOperation = Bitmap.createBitmap(mPhotoBitmap.getWidth(),mPhotoBitmap.getHeight(), mPhotoBitmap.getConfig());
//        double red = 0.33;
//        double green = 0.59;
//        double blue = 0.11;
//
//        for (int i = 0; i < mPhotoBitmap.getWidth(); i++) {
//            for (int j = 0; j < mPhotoBitmap.getHeight(); j++) {
//                int p = mPhotoBitmap.getPixel(i, j);
//                int r = Color.red(p);
//                int g = Color.green(p);
//                int b = Color.blue(p);
//
//                r = (int) red * r;
//                g = (int) green * g;
//                b = (int) blue * b;
//                mEditOperation.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b));
//            }
//        }

//        mPhotoCaptuedImageView.setImageBitmap(imageProcessor.doGreyScale(mPhotoBitmap));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
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
                    ImageProcessor imageProcessor = new ImageProcessor();
                    mPhotoCaptuedImageView.setImageBitmap(imageProcessor.doGreyScale(mPhotoBitmap));
                }catch(android.content.ActivityNotFoundException anfe){

                }
                return true;
            case R.id.filter_invert:
                try{
                    mPhotoBitmap = ((BitmapDrawable)mPhotoCaptuedImageView.getDrawable()).getBitmap();
//                    mPhotoBitmap= ((GlideBitmapDrawable)mPhotoCaptuedImageView.getDrawable().getCurrent()).getBitmap();
                    ImageProcessor imageProcessor = new ImageProcessor();
                    mPhotoCaptuedImageView.setImageBitmap(imageProcessor.doInvert(mPhotoBitmap));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filters, menu);
        return true;
    }
}
