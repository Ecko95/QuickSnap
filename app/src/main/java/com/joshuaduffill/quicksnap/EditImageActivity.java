package com.joshuaduffill.quicksnap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mukesh.image_processing.ImageProcessor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditImageActivity extends AppCompatActivity {

    private ImageView mPhotoCaptuedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);


        mPhotoCaptuedImageView = (ImageView) findViewById(R.id.editImageView);
        Intent intent = getIntent();

        if (getIntent().getExtras() != null) {
            Glide.with(this)
                    .load(intent.getParcelableExtra("URL"))
                    .into(mPhotoCaptuedImageView);

        } else {
            Toast.makeText(this, "Image is null", Toast.LENGTH_SHORT).show();
        }
    }


    public void editPicture(View view){

        //open menu with edit filters
        ImageProcessor imageProcessor = new ImageProcessor();
//        imageProcessor.doInvert(editBitmap);
        //add greyscale etc
        //continue here

    }


}
