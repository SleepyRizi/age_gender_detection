package com.example.gender_age_detection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginUser extends AppCompatActivity {
    Button btn_login,btn_signup;
    EditText tv_email,tv_password;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
        btn_signup=findViewById(R.id.btn_signup);
        btn_login=findViewById(R.id.btn_login);
        tv_email=findViewById(R.id.tv_email);
        tv_password=findViewById(R.id.tv_password);
        //progressBar=findViewById(R.id.progressBar);
        fAuth=FirebaseAuth.getInstance();

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),registeruser.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = tv_email.getText().toString().trim();
                String password = tv_password.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    tv_email.setError("Email can't be empty");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    tv_password.setError("Password can't be empty");
                    return;
                }
                if (password.length() <= 6) {
                    tv_password.setError("Password is short");
                    return;
                }
//                progressBar.setVisibility(View.VISIBLE);
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(loginUser.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(loginUser.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}