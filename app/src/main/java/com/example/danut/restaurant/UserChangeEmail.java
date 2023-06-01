package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserChangeEmail extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private TextView tVUserAuthChangeEmail;

    private EditText userOdlEmail, userPassword, userNewEmail;

    private String userOdl_Email, user_Password, userNew_Email;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_change_email);

        Objects.requireNonNull(getSupportActionBar()).setTitle("USER: change Email");

        progressDialog = new ProgressDialog(UserChangeEmail.this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        userOdlEmail = findViewById(R.id.etUserOldEmail);
        userOdlEmail.setEnabled(false);
        userPassword = findViewById(R.id.etUserPassEmail);
        userNewEmail = findViewById(R.id.etUserNewEmail);

        tVUserAuthChangeEmail = findViewById(R.id.tvUserAuthChangeEmail);
        tVUserAuthChangeEmail.setText("Your profile is not authenticated yet. Please authenticate your profile first and then change the Email!!");
        tVUserAuthChangeEmail.setTextColor(Color.RED);

        userOdl_Email = firebaseUser.getEmail();
        userOdlEmail.setText(userOdl_Email);

        Button btn_ChangeEmail = findViewById(R.id.btnUserChangeEmail);
        btn_ChangeEmail.setOnClickListener(view -> alertUserEmailNotAuth());

        Button btn_AuthUser = findViewById(R.id.btnAuthUser);
        btn_AuthUser.setOnClickListener(view -> {

            userOdl_Email = userOdlEmail.getText().toString().trim();
            user_Password = userPassword.getText().toString().trim();

            if (TextUtils.isEmpty(user_Password)) {
                userPassword.setError("Enter your password");
                userPassword.requestFocus();
            } else {

                progressDialog.setTitle("User authentication!!");
                progressDialog.show();

                AuthCredential credential = EmailAuthProvider.getCredential(userOdl_Email, user_Password);

                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        tVUserAuthChangeEmail.setText("Your profile is authenticated.\nYou can change the Email now!!");
                        tVUserAuthChangeEmail.setTextColor(Color.BLACK);

                        userPassword.setEnabled(false);
                        btn_AuthUser.setEnabled(false);
                        btn_AuthUser.setText("Disabled");
                        userNewEmail.requestFocus();

                        btn_ChangeEmail.setOnClickListener(view1 -> {

                            userNew_Email = userNewEmail.getText().toString().trim();

                            if (TextUtils.isEmpty(userNew_Email)) {
                                userNewEmail.setError("Enter your new Email Address");
                                userNewEmail.requestFocus();
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(userNew_Email).matches()) {
                                userNewEmail.setError("Enter a valid Email Address");
                            } else if (userOdl_Email.matches(userNew_Email)) {
                                userNewEmail.setError("Please enter a new Email\nNew Email cannot same as old");
                            } else {

                                updateUserEmail();
                            }
                        });

                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            userPassword.setError("Invalid Password");
                            userPassword.requestFocus();
                            tVUserAuthChangeEmail.setText("Your profile is not authenticated yet. Please authenticate your profile first and then change the email!!");
                            tVUserAuthChangeEmail.setTextColor(Color.RED);
                        } catch (Exception e) {
                            Toast.makeText(UserChangeEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    progressDialog.dismiss();
                });
            }
        });
    }

    public void updateUserEmail() {

        progressDialog.setTitle("Changing user Email!!");
        progressDialog.show();

        firebaseUser.updateEmail(userNew_Email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        uploadChangeUserEmailData();
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(UserChangeEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    progressDialog.dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(UserChangeEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void uploadChangeUserEmailData() {

        String user_Id = firebaseUser.getUid();

        databaseReference.child(user_Id).child("user_email").setValue(userNew_Email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        firebaseUser.sendEmailVerification();

                        Toast.makeText(UserChangeEmail.this, "Email was changed. Email verification has been sent.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UserChangeEmail.this, LoginUser.class));
                        finish();

                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(UserChangeEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    progressDialog.dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(UserChangeEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void alertUserEmailNotAuth() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserChangeEmail.this);
        alertDialogBuilder
                .setTitle("User Unauthenticated!!")
                .setMessage("Your profile is not authenticated yet.\nPlease authenticate your profile first and then change the Email!!")
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_change_email, menu);
        return true;
    }

    public void userChangeEmailGoBack() {
        startActivity(new Intent(UserChangeEmail.this, UserPage.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.userChangeEmail_goBack) {
            userChangeEmailGoBack();
        }

        return super.onOptionsItemSelected(item);
    }
}