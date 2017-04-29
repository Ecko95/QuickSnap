package com.joshuaduffill.quicksnap;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class UserProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>,
        MediaStoreAdapter.OnClickThumbnailListener{

    private final static int MEDIASTORE_LOADER_ID = 0;
    private static final int ACTIVITY_START_CAMERA_APP = 0;

    private RecyclerView mThumbailRecyclerView;
    private MediaStoreAdapter mMediaStoreAdapter;

    private FirebaseAuth firebaseAuth;
    private TextView txtUserEmail;
    private NavigationView v;
    private View mHeaderView;
    private TextView mDrawerHeaderTitle;

    private Button btnLogOut;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            //close current activity
            finish();
            //start user profile activity
            startActivity(new Intent(this, LoginActivity.class));

        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        //displays NAV header subTitle to logged in user's email
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View mHeaderView = navigationView.getHeaderView(0);
        TextView companyNameTxt = (TextView) mHeaderView.findViewById(R.id.txtProfileEmail);
        companyNameTxt.setText(user.getEmail());

//        checkReadExternalStoragePermission();

        //sets number og columns
        mThumbailRecyclerView = (RecyclerView) findViewById(R.id.thumbnailRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        mThumbailRecyclerView.setLayoutManager(gridLayoutManager);

        mMediaStoreAdapter = new MediaStoreAdapter(this);
        mThumbailRecyclerView.setAdapter(mMediaStoreAdapter);

        //initialise Load of RECYCLER VIEW
        getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);

        FloatingActionButton cameraFab = (FloatingActionButton) findViewById(R.id.cameraFab);
        cameraFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //opens camera app
                Intent callCameraApp = new Intent();
                callCameraApp.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(callCameraApp, ACTIVITY_START_CAMERA_APP);


//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
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
        //check if activity camera has started
        if(requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK){
//            Toast.makeText(this, "Picture taken successfully", Toast.LENGTH_SHORT).show();
//            Bundle extras = resultData.getExtras();
//            Bitmap photoCapturedBitmap = (Bitmap) extras.get("resultData");

            Uri uri = resultData.getData();
            Intent editPhotoIntent = new Intent(this, EditImageActivity.class);
            editPhotoIntent.putExtra("URL", uri);
            startActivity(editPhotoIntent);


// Restore state


        }
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
            case R.id.action_settings:
                try{

                    Toast.makeText(this,"this is a settings menu", Toast.LENGTH_SHORT).show();
                }catch(android.content.ActivityNotFoundException anfe){
                }
                return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logOut){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //continue here
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
        Intent fullScreenIntent = new Intent(this, FullScreenImageActivity.class);
        fullScreenIntent.setData(imageUri);
        startActivity(fullScreenIntent);
    }

    @Override
    public void OnClickVideo(Uri videoUri) {
        Intent videoPlayIntent = new Intent(this, VideoPlayActivity.class);
        videoPlayIntent.setData(videoUri);
        startActivity(videoPlayIntent);
    }
}
