package com.joshuaduffill.quicksnap;


import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private final static int READ_EXTERNAL_STORAGE_PERMISSION_RESULT = 0;

    //initialize our variables

//    private Button btnRegister;
//    private EditText etEmail;
//    private EditText etPassword;
//    private TextView txtSignIn;
//
//    private ProgressDialog progressDialog;
//
//    private FirebaseAuth firebaseAuth;

    private Button btnSignIn;
    private EditText etEmail;
    private EditText etPassword;
    private TextView txtSignUp;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        //runs AppIntro
//        startActivity(new Intent(getApplicationContext(),IntroActivity.class));




        progressDialog = new ProgressDialog(this);

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();


//        //initializing FireBase Auth object
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        //initialising Views
//        progressDialog = new ProgressDialog(this);
//        btnRegister = (Button) findViewById(R.id.btnRegister);
//        etEmail = (EditText) findViewById(R.id.etEmail);
//        etPassword = (EditText) findViewById(R.id.etPassword);
//        txtSignIn = (TextView) findViewById(R.id.txtSignIn);
//
//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                registerUser();
//
//            }
//        });
//        txtSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
//
//            }
//        });


        //initialise database Auth
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //start user profile activity
            startActivity(new Intent(this, UserProfileActivity.class));
            //close current activity
            finish();
        }

        //initialise views

        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        txtSignUp = (TextView)findViewById(R.id.txtSignUp);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();

            }
        });
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));

            }
        });

//        checkReadExternalStoragePermission();

    }
    private void userLogin(){
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show();
            //stops the function from executing further
            return;
        }

        if (TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            //stops the function from executing further
            return;
        }
        progressDialog.setMessage("Sign In... Please Wait");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            //close current activity;
                            finish();
                            //start user profile activity
                            startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));

                        }
                        else {
                            Toast.makeText(MainActivity.this, "Failed to log in", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case READ_EXTERNAL_STORAGE_PERMISSION_RESULT:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //call cursor loader
                    Toast.makeText(this,"now you have access to view thumbnails", Toast.LENGTH_SHORT).show();
                }
                    break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void registerUser() {
        
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show();
            //stops the function from executing further
            return;
        }

        if (TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            //stops the function from executing further
            return;
        }

        /*
        If users enters valid email & password
        we will show a progressDialog to indicate that a new User is being created
         */

        progressDialog.setMessage("Registering User... Please Wait");
        progressDialog.show();

        //creates a new User inside FireBase Database
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //start user profile activity
                            startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                            //close current activity
                            finish();

                            /*
                            User is registered and logged in successfully
                            Start profile activity
                            For now we only are displaying a message
                             */

                            Toast.makeText(MainActivity.this, "You have Registered Successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(MainActivity.this, "Failed to register, Please try again", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();

                    }
                });
    }

    private void checkReadExternalStoragePermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED){
            }else{
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Toast.makeText(this,"App nedds to view thumbnails", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_PERMISSION_RESULT);
            }
        }else{

        }
    }

}
