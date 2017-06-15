package com.joshuaduffill.quicksnap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

public class FullScreenImageActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnTouchListener{

    private ImageView fullScreenImageView;
    private ProgressBar mProgressBar;
    private Bitmap mPhotoBitmap;
    private Uri fileUri;

    // An object that manages Messages in a Thread
    private Handler mHandler;
    private File deleteFile;
    private String deletedFileLocation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title and add back button
        try{
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }catch (NullPointerException ex){
            //error
        }


        setContentView(R.layout.activity_full_screen_image);



        mProgressBar = (ProgressBar) findViewById(R.id.progressBarImage);
        fullScreenImageView = (ImageView) findViewById(R.id.fullScreenImageView);

        //set listener method to image view
        fullScreenImageView.setOnLongClickListener(this);
        fullScreenImageView.setOnTouchListener(this);

        Intent callActivityIntent = getIntent();
        if(callActivityIntent != null){
            Uri imageUri = callActivityIntent.getData();
            fileUri = imageUri;
            if(imageUri != null && fullScreenImageView != null){
                Glide.with(this)
                        .load(imageUri)
                        .skipMemoryCache(true)
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
        if(hasFocus) {
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//            | View.SYSTEM_UI_FLAG_FULLSCREEN
//            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//            | View.SYSTEM_UI_LAYOUT_FLAGS
//            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            | View.SYSTEM_UI_FLAG_FULLSCREEN);
//        }
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_LAYOUT_FLAGS
            );
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
//        int id = item.getItemId();

        switch (item.getItemId()){
            case R.id.home:
                finish();
                return true;
            case R.id.option_delete:
                AlertDialog diaBox = DeleteConfirmation();
                diaBox.show();
                return true;
            case R.id.option_edit:

                Intent editImageIntent = new Intent(this, EditImageActivity.class);
                editImageIntent.setData(fileUri);
                startActivity(editImageIntent);
                return true;
            case R.id.option_share:
                Intent shareIntent = createShareIntent();
                startActivity(Intent.createChooser(shareIntent, "send to"));
                return true;
            case R.id.option_favourite:
                File photoFile = null;
                photoFile = getFilesDir();

                String authorities = getApplicationContext().getPackageName() + ".fileprovider";
                fileUri = FileProvider.getUriForFile(getApplicationContext(), authorities, new File(fileUri.getPath()));

                Intent favIntent = new Intent(this, FavouritesActivity.class);
                favIntent.setData(fileUri);
                startActivity(favIntent);

                return true;
                //add more here
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Intent createShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        return shareIntent;
    }

    private AlertDialog DeleteConfirmation()
    {

        AlertDialog mDeleteConfirmDialog =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this image?")
                .setIcon(R.mipmap.ic_delete_forever)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (fileUri != null) {

                            //gets fileUri path and deletes the file
                            deleteFile = new File(fileUri.getPath());
                            deletedFileLocation = deleteFile.getAbsolutePath();

                            boolean deleted = deleteFile.delete();

                            //scans for new, edited or deleted files on device
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(deleteFile)));

                            //        informs parent activity that the child acitivity been opened. Then we can refresh parent activity
                            setResult(RESULT_OK,null);

                        }

//                        File scanDeleteFile = new File (deleteFile.getPath());
//                        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                        scanIntent.setData(Uri.fromFile(scanDeleteFile));
//                        sendBroadcast(scanIntent);

//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Glide.get(getApplicationContext()).clearDiskCache();
//                            }
//                        }).start();

                        //updates Media Store, so no cache image is being generated by GLIDE.
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(deleteFile)));

                        dialog.dismiss();
                        finish();

                    }


                })



                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return mDeleteConfirmDialog;

    }

    @Override
    protected void onDestroy() {
        setResult(RESULT_OK,null);
        finish();

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.full_image_menu, menu);
        return true;
    }


    @Override
    public boolean onLongClick(View v) {
        AlertDialog diaBox = DeleteConfirmation();
        diaBox.show();
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        View decorView = getWindow().getDecorView();
        switch (event.getAction()){

//            case MotionEvent.ACTION_CANCEL:
//                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//                );
//            break;
//            case MotionEvent.ACTION_DOWN:
//                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//                );
//            break;
//            case MotionEvent.ACTION_UP:
//                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        |View.SYSTEM_UI_LAYOUT_FLAGS
//                );
//            break;
            case MotionEvent.ACTION_MOVE:

                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                );

                break;
        }
        //runs loop once pressed
        return false;
    }
}

