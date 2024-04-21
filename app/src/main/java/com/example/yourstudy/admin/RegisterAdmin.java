package com.example.yourstudy.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourstudy.R;
import com.example.yourstudy.user.Register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterAdmin extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    ImageButton buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView LogNowAdmin;
    TextView UserPageReg;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivityAdmin.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_admin);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.reg_btn);
        progressBar = findViewById(R.id.progress_bar);
        LogNowAdmin = findViewById(R.id.loginNowAdmin);
        UserPageReg = findViewById(R.id.user_page_reg);
        LogNowAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginAdmin.class);
                startActivity(intent);
                finish();
            }
        });
        UserPageReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterAdmin.this, "Уведіть пошту", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterAdmin.this, "Уведіть пароль", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!email.toLowerCase().contains("uzhnu.edu.ua") || email.toLowerCase().contains("student")) {
                    Toast.makeText(RegisterAdmin.this, "Тільки викладачі можуть тут зареєструватись.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterAdmin.this, "Акаунт створено!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivityAdmin.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(RegisterAdmin.this, "Не вдалось зареєструватись.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}