package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginUser extends AppCompatActivity {

    //declare variables
    private FirebaseAuth firebaseAuth;

    private EditText emailLogUser, passwordLogUser;
    private TextView tVForgotPassUser;
    private String emailLog_User, passLog_User;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Log in User");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        //initialize variables
        emailLogUser = findViewById(R.id.etEmailLogUser);
        passwordLogUser = findViewById(R.id.etPassLogUser);

        //Action TextView Forgotten Password
        tVForgotPassUser = findViewById(R.id.tvForgotPassUser);
        tVForgotPassUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fPassword = new Intent(LoginUser.this, ResetPassword.class);
                startActivity(fPassword);
            }
        });

        //Action button Cancel Log in user
        Button buttonCancelLogUser = findViewById(R.id.btnCancelLogUser);
        buttonCancelLogUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLogUser.setText("");
                passwordLogUser.setText("");
            }
        });

        //Action button Register User
        Button buttonRegLogUser = findViewById(R.id.btnRegLogUser);
        buttonRegLogUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign = new Intent(LoginUser.this, RegisterUser.class);
                startActivity(sign);
            }
        });

        //Action button LogIn User
        Button buttonLogInUser = findViewById(R.id.btnLogInUser);
        buttonLogInUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInUser();
            }
        });
    }

    private void logInUser() {

        //Log in a new user
        if (validateUserData()) {

            progressDialog.setMessage("Log in User!");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(emailLog_User, passLog_User).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        checkEmailVerification();

                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthInvalidUserException e) {
                            emailLogUser.setError("This email is not registered.");
                            emailLogUser.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            passwordLogUser.setError("Invalid Password");
                            passwordLogUser.requestFocus();
                        } catch (Exception e) {
                            Toast.makeText(LoginUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    progressDialog.dismiss();
                }
            });
        }
    }

    //validate input data into the editText
    private Boolean validateUserData() {

        boolean result = false;

        emailLog_User = emailLogUser.getText().toString();
        passLog_User = passwordLogUser.getText().toString();

        if (emailLog_User.isEmpty()) {
            emailLogUser.setError("Enter your Email Address");
            emailLogUser.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailLog_User).matches()) {
            emailLogUser.setError("Enter a valid Email Address");
            emailLogUser.requestFocus();
        } else if (passLog_User.isEmpty()) {
            passwordLogUser.setError("Enter your Password");
            passwordLogUser.requestFocus();
        } else if (emailLog_User.equals("admin@gmail.com") && (passLog_User.equals("admin"))) {
            progressDialog.setMessage("Login Admin");
            progressDialog.show();
            startActivity(new Intent(LoginUser.this, AdminPage.class));
            progressDialog.dismiss();
        } else {
            result = true;
        }
        return result;
    }

    //check if the email has been verified
    private void checkEmailVerification() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            if (firebaseUser.isEmailVerified()) {

                Toast.makeText(LoginUser.this, "User successfully Log in!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginUser.this, UserPage.class));
                finish();

            } else {
                Toast.makeText(this, "Please verify your Email first", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }
}
