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

public class registeruser extends AppCompatActivity {


    Button btn_register2;
    FirebaseAuth fAuth;
    EditText tv_email_reg,tv_password_reg;
    ProgressBar progressBar2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeruser);

        fAuth = FirebaseAuth.getInstance();
        btn_register2= findViewById(R.id.btn_reg2);
        tv_email_reg=findViewById(R.id.tv_email_reg);
        tv_password_reg=findViewById(R.id.tv_password_reg);
        progressBar2=findViewById(R.id.progressBar2);
//
//        if(fAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(),loginUser.class));
//            finish();
//        }

        btn_register2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = tv_email_reg.getText().toString().trim();
                String password = tv_password_reg.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    tv_email_reg.setError("Email can't be empty");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    tv_password_reg.setError("Password can't be empty");
                    return;
                }
                if (password.length() <= 6) {
                    tv_password_reg.setError("Password is short");
                    return;
                }
                // progressBar2.setVisibility(View.VISIBLE);
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(registeruser.this, "User created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), loginUser.class));
                        } else {
                            Toast.makeText(registeruser.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        });

    }
}