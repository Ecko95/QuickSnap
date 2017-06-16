package com.joshuaduffill.quicksnap;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.*;
import android.support.design.BuildConfig;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v4.provider.DocumentFile;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kobakei.ratethisapp.RateThisApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.id;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.joshuaduffill.quicksnap.R.id.WebView;
import static com.joshuaduffill.quicksnap.R.id.cameraFab;
import static com.joshuaduffill.quicksnap.R.id.drawer_layout;
import static com.joshuaduffill.quicksnap.R.id.fullScreenImageView;

public class UserProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>,
        MediaStoreAdapter.OnClickThumbnailListener{

    private final static int MEDIASTORE_LOADER_ID = 0;
    private final static int ACTIVITY_START_CAMERA_APP = 0;
    static final int REQUEST_CODE = 1;

    private RecyclerView mThumbailRecyclerView;
    private MediaStoreAdapter mMediaStoreAdapter;

    private FirebaseAuth firebaseAuth;
    private TextView txtUserEmail;
    private NavigationView v;
    private View mHeaderView;
    private TextView mDrawerHeaderTitle;

    private FloatingActionButton mCameraFab;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    private String mImageFileLocation = "";
    private String GALLERY_DIRECTORY_NAME = "QuickSnap";
    private File mGalleryFolder;
    private Uri fileUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RateThisApp.Config config = new RateThisApp.Config(3, 5);
        RateThisApp.init(config);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            //close current activity
            finish();
            //start user profile activity
            startActivity(new Intent(this, MainActivity.class));

        }

        //creates IMAGE GALLERY FOLDER
//        createImageGallery();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        final FloatingActionButton mCameraFab = (FloatingActionButton) findViewById(R.id.cameraFab);

        //displays NAV header subTitle to logged in user's email
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View mHeaderView = navigationView.getHeaderView(0);
        TextView companyNameTxt = (TextView) mHeaderView.findViewById(R.id.txtProfileEmail);

        companyNameTxt.setText(user.getEmail());

        //sets number og columns
        mThumbailRecyclerView = (RecyclerView) findViewById(R.id.thumbnailRecyclerView);


        //sets the number of columns on gird layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        mThumbailRecyclerView.setLayoutManager(gridLayoutManager);
        mMediaStoreAdapter = new MediaStoreAdapter(this);
        mThumbailRecyclerView.setAdapter(mMediaStoreAdapter);
        mThumbailRecyclerView.setHasFixedSize(true);
        mThumbailRecyclerView.setItemViewCacheSize(20);
        mThumbailRecyclerView.setDrawingCacheEnabled(true);
        mThumbailRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        //initialise Load of RECYCLER VIEW
        getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);

        mThumbailRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    mCameraFab.show();
                }

                if(newState == RecyclerView.SCROLL_STATE_IDLE && mCameraFab.isShown()){

                    mCameraFab.hide();
                }


                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int width, int height) {

                if(height == 0){

                    mCameraFab.hide();
                }

                if (height > 0 || height < 0 && mCameraFab.isShown()){

                    mCameraFab.hide();
                }


            }
        });


        mCameraFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            mThumbailRecyclerView.getLayoutManager().smoothScrollToPosition(mThumbailRecyclerView,null,0);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView mNavigation = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    //continue here

    protected void onActivityResult (int requestCode, int resultCode, Intent resultData){

        if (resultCode == RESULT_OK){
            //scans for new or deleted images when it returns from childs.
            getSupportLoaderManager().restartLoader(MEDIASTORE_LOADER_ID,null,this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.get(getApplicationContext()).clearDiskCache();
                }
            }).start();

            MediaScannerConnection.scanFile(
                    getApplicationContext(),
                    new String[]{mImageFileLocation},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.v("Joshua",
                                    "file" + path + "was scanned successfully: " + uri);
                        }
                    }
            );
        }

        //check if activity camera has started
        if(requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK){



            Intent editScreenIntent = new Intent(this, EditImageActivity.class);
            editScreenIntent.setData(fileUri);
            startActivity(editScreenIntent);

            //assign photo to image view on edit image activity
//            Intent editPhotoIntent = new Intent(this, EditImageActivity.class);
//            editPhotoIntent.putExtra("URL", fileUri);


//            startActivity(editPhotoIntent);
            Toast.makeText(this, "photo taken and saved", Toast.LENGTH_SHORT).show();

            //refreshes the recycler view (list of images)
            getSupportLoaderManager().restartLoader(MEDIASTORE_LOADER_ID,null,this);

//            Intent mediaScanIntent = new Intent (Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            sendBroadcast(mediaScanIntent);


            MediaScannerConnection.scanFile(
                    getApplicationContext(),
                    new String[]{mImageFileLocation},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.v("Joshua",
                                    "file" + path + "was scanned successfully: " + uri);
                        }
                    }

            );


        }
    }

    //refreshes loader after picture is taken
    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(MEDIASTORE_LOADER_ID,null,this);
    }

    private void createImageGallery(){
//        File mGalleryFolder = new File(Environment.getExternalStorageDirectory(), GALLERY_DIRECTORY_NAME);

        //reference to the device storage
//        mGalleryFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), GALLERY_DIRECTORY_NAME);

        Toast.makeText(this, mGalleryFolder.toString(), Toast.LENGTH_LONG).show();
        //does PICTURES directory exist?
        if(!mGalleryFolder.exists()){
            mGalleryFolder.mkdirs();
        }
    }

    private File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp;

//        //Stores a original copy of the picture on PICTURES FOLDER
//        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(imageFileName,".jpg",storageDirectory());

        //stores picture in extra output directory
//        File tempDir= Environment.getExternalStorageDirectory();
//        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");

        File image = File.createTempFile(imageFileName,".jpg",getExternalCacheDir());
        image.deleteOnExit();

//        //Saves original copy on custom folder "QuickSnap"
//        File image = new File(mGalleryFolder + File.separator + imageFileName + ".jpg");

        //creates image and stores in external chache directory
//        File image = File.createTempFile(imageFileName,".jpg",getExternalCacheDir());
        mImageFileLocation = image.getAbsolutePath();
        mMediaStoreAdapter.notifyDataSetChanged();
        return image;
    }

    public void openCamera(){

                Intent callCameraAppIntent = new Intent();
                callCameraAppIntent.setAction(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                File photoFile = null;
                try{
                    photoFile = createImageFile();

                }catch(IOException e){
                    e.printStackTrace();
                }

                String authorities = getApplicationContext().getPackageName() + ".fileprovider";
                fileUri = FileProvider.getUriForFile(getApplicationContext(), authorities, photoFile);

                callCameraAppIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                startActivityForResult(callCameraAppIntent, ACTIVITY_START_CAMERA_APP);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
        switch (item.getItemId()){
            case R.id.action_resfresh:
                try{
                    getSupportLoaderManager().restartLoader(MEDIASTORE_LOADER_ID,null,this);
                    Toast.makeText(this,"refresh", Toast.LENGTH_SHORT).show();
                }catch(android.content.ActivityNotFoundException anfe){
                    anfe.printStackTrace();
                }
                return true;
            case R.id.action_camera:
                try{
                    openCamera();
                }catch(android.content.ActivityNotFoundException anfe){
                    anfe.printStackTrace();
                }
            //add more here
            default:
                return super.onOptionsItemSelected(item);
        }

////        noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favourites) {
            Toast.makeText(this, "This feature is yet to be implemented", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_manage) {
            Toast.makeText(this, "Settings menu is not available", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_rate_us) {

            // Monitor launch times and interval from installation
            RateThisApp.onCreate(this);
            // If the condition is satisfied, "Rate this app" dialog will be shown
            RateThisApp.showRateDialog(this);

        } else if (id == R.id.nav_about) {
            Intent webIntent = new Intent(this, WebActivity.class);
            startActivity(webIntent);

        } else if (id == R.id.nav_log_out){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //loads content from device memory using CursorLoader on a background asyncTask
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        };
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        return new CursorLoader(
                this,
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMediaStoreAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMediaStoreAdapter.changeCursor(null);
    }

    @Override
    public void OnClickImage(Uri imageUri) {
        //Toast.makeText(UserProfileActivity.this,"Image uri = " + imageUri.toString(), Toast.LENGTH_SHORT).show();

        //provides the location of calling activity & data of location of selected image

        //opens image in full activity
//        Intent fullScreenIntent = new Intent(this, FullScreenImageActivity.class);
//        fullScreenIntent.setData(imageUri);
//        startActivity(fullScreenIntent);

        //opens image in editimage activity
        Intent fullScreenIntent = new Intent(this, FullScreenImageActivity.class);
        fullScreenIntent.setData(imageUri);
        startActivityForResult(fullScreenIntent, REQUEST_CODE);


    }


    @Override
    public void OnClickVideo(Uri videoUri) {
        Intent videoPlayIntent = new Intent(this, VideoPlayActivity.class);
        videoPlayIntent.setData(videoUri);
        startActivity(videoPlayIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        fileUri = savedInstanceState.getParcelable("file_uri");
    }
}
