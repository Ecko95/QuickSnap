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
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnSignIn;
    private EditText etEmail;
    private EditText etPassword;
    private TextView txtSignUp;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        btnSignIn.setOnClickListener(this);
        txtSignUp.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
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
                            Toast.makeText(LoginActivity.this, "Failed to log in", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if(view == btnSignIn){
            userLogin();
        }
        if(view == txtSignUp){
            startActivity(new Intent(this,MainActivity.class));
        }
    }
}
