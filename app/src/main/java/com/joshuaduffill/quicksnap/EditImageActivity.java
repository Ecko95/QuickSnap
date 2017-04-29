package com.joshuaduffill.quicksnap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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

}
