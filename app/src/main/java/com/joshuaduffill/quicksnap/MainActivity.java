package com.joshuaduffill.quicksnap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //initialize our variables

    private Button btnRegister;
    private EditText etEmail;
    private EditText etPassword;
    private TextView txtSignIn;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing FireBase Auth object
        firebaseAuth = FirebaseAuth.getInstance();


        //initialising Views
        progressDialog = new ProgressDialog(this);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        txtSignIn = (TextView) findViewById(R.id.txtSignIn);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();

            }
        });
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));

            }
        });

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
}
