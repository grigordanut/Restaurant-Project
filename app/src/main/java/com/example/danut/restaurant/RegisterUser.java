package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterUser extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference databaseReference;

    private EditText firstNameRegUser, lastNameRegUser, phoneNrRegUser, emailRegUser, passRegUser, confPassRegUser;
    private String firstName_regUser, lastName_regUser, phoneNr_regUser, email_regUser, pass_regUser, confPass_regUser;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Register User");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Create table Users into database
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        firstNameRegUser = findViewById(R.id.etFirstNameRegUser);
        lastNameRegUser = findViewById(R.id.etLastNameRegUser);
        phoneNrRegUser = findViewById(R.id.etPhoneNrRegUser);
        emailRegUser = findViewById(R.id.etEmailRegUser);
        passRegUser = findViewById(R.id.etPassRegUser);
        confPassRegUser = findViewById(R.id.etConfPassRegUser);

        Button buttonRegUser = findViewById(R.id.btnRegUser);
        buttonRegUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        //action button cancel
        Button buttonCancelRegUser = (Button) findViewById(R.id.btnCancelRegUser);
        buttonCancelRegUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstNameRegUser.setText("");
                lastNameRegUser.setText("");
                phoneNrRegUser.setText("");
                emailRegUser.setText("");
                passRegUser.setText("");
                confPassRegUser.setText("");
            }
        });

        //Action TextView Log In
        Button buttonRegLogUser = findViewById(R.id.btnRegLogUser);
        buttonRegLogUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent textLog = new Intent(RegisterUser.this, LoginUser.class);
                startActivity(textLog);
            }
        });
    }

    private void registerUser() {

        if (validateRegUserData()) {

            progressDialog.setMessage("Registering User details!");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email_regUser, pass_regUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        uploadUserData();

                    } else {
                        alertDialogEmailUsed();
                    }

                    progressDialog.dismiss();
                }
            });
        }
    }

    private Boolean validateRegUserData() {

        boolean result = false;

        firstName_regUser = firstNameRegUser.getText().toString().trim();
        lastName_regUser = lastNameRegUser.getText().toString().trim();
        phoneNr_regUser = phoneNrRegUser.getText().toString().trim();
        email_regUser = emailRegUser.getText().toString().trim();
        pass_regUser = passRegUser.getText().toString().trim();
        confPass_regUser = confPassRegUser.getText().toString().trim();

        if (TextUtils.isEmpty(firstName_regUser)) {
            firstNameRegUser.setError("Enter your First Name");
            firstNameRegUser.requestFocus();
        } else if (TextUtils.isEmpty(lastName_regUser)) {
            lastNameRegUser.setError("Enter your Last Name");
            lastNameRegUser.requestFocus();
        } else if (phoneNr_regUser.isEmpty()) {
            phoneNrRegUser.setError("Enter your Phone Number");
            phoneNrRegUser.requestFocus();
        } else if (email_regUser.isEmpty()) {
            emailRegUser.setError("Enter your Email Address");
            emailRegUser.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email_regUser).matches()) {
            emailRegUser.setError("Enter a valid Email Address");
            emailRegUser.requestFocus();
        } else if (pass_regUser.isEmpty()) {
            passRegUser.setError("Enter your Password");
            passRegUser.requestFocus();
        } else if (pass_regUser.length() < 6) {
            passRegUser.setError("Password too short, enter minimum 6 character long");
            passRegUser.requestFocus();
        } else if (confPass_regUser.isEmpty()) {
            confPassRegUser.setError("Enter your Confirm Password");
            confPassRegUser.requestFocus();
        } else if (!confPass_regUser.equals(pass_regUser)) {
            confPassRegUser.setError("The Confirm Password does not match Password");
            confPassRegUser.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    private void uploadUserData() {

        String user_Id = firebaseUser.getUid();
        Users user_data = new Users(firstName_regUser, lastName_regUser, phoneNr_regUser, email_regUser);

        databaseReference.child(user_Id).setValue(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    sendEmailVerification();

                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {
                        Toast.makeText(RegisterUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                progressDialog.dismiss();
            }
        });
    }

    private void sendEmailVerification() {

        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    alertDialogUserRegistered();

                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {
                        Toast.makeText(RegisterUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                progressDialog.dismiss();
            }
        });
    }

    private void alertDialogUserRegistered() {

        AlertDialog.Builder builderAlert = new AlertDialog.Builder(RegisterUser.this);
        builderAlert
                .setTitle("Register User.")
                .setMessage("Hi " + firstName_regUser + " " + lastName_regUser + " you are successfully registered.\nVerification email sent. Please verify your email before Log in!")
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> {

                    startActivity(new Intent(RegisterUser.this, LoginUser.class));
                    finish();

                });

        AlertDialog alertDialog = builderAlert.create();
        alertDialog.show();
    }

    private void alertDialogEmailUsed() {

        AlertDialog.Builder builderAlert = new AlertDialog.Builder(RegisterUser.this);
        builderAlert
                .setTitle("Register User.")
                .setMessage("Registration failed, the email: \n" + email_regUser + " was already used to open an account on this app!")
                .setCancelable(true)
                .setPositiveButton("Ok", (dialog, which) -> {
                    emailRegUser.requestFocus();
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builderAlert.create();
        alertDialog.show();
    }
}