package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class UserChangePassword extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private TextView tVUserAuthChangePass;

    private EditText userEmail, userOldPassword, userNewPassword;

    private String user_Email, userOld_Password, userNew_Password;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_change_password);

        Objects.requireNonNull(getSupportActionBar()).setTitle("USER: change Password");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userEmail = findViewById(R.id.etUserEmailPass);
        userEmail.setEnabled(false);
        userOldPassword = findViewById(R.id.etUserOldPasswordPass);
        userNewPassword = findViewById(R.id.etUserNewPassword);

        tVUserAuthChangePass = findViewById(R.id.tvUserAuthChangePass);
        tVUserAuthChangePass.setText("Your profile is not authenticated yet. Please authenticate your profile first and then change the Password!!");
        tVUserAuthChangePass.setTextColor(Color.RED);

        user_Email = firebaseUser.getEmail();
        userEmail.setText(user_Email);

        Button btn_ChangePassword = findViewById(R.id.btnUserChangePass);
        btn_ChangePassword.setOnClickListener(view -> alertUserNotAuthPassword());

        Button btn_AuthUserPass = findViewById(R.id.btnAuthUserPass);
        btn_AuthUserPass.setOnClickListener(view -> {

            user_Email = userEmail.getText().toString().trim();

            userOld_Password = userOldPassword.getText().toString().trim();

            if (TextUtils.isEmpty(userOld_Password)) {
                userOldPassword.setError("Enter your password");
                userOldPassword.requestFocus();
            } else {

                progressDialog.setMessage("The user is authenticating!!");
                progressDialog.show();

                AuthCredential credential = EmailAuthProvider.getCredential(user_Email, userOld_Password);

                firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            tVUserAuthChangePass.setText("Your profile is authenticated.\nYou can change the Password now!!");
                            tVUserAuthChangePass.setTextColor(Color.BLACK);

                            userOldPassword.setEnabled(false);
                            btn_AuthUserPass.setEnabled(false);
                            btn_AuthUserPass.setText("Disabled");
                            userNewPassword.requestFocus();

                            btn_ChangePassword.setOnClickListener(view1 -> {

                                userNew_Password = userNewPassword.getText().toString().trim();

                                if (TextUtils.isEmpty(userNew_Password)) {
                                    userNewPassword.setError("Enter your new Password");
                                    userNewPassword.requestFocus();
                                } else if (userNew_Password.length() < 6) {
                                    userNewPassword.setError("The password is too short, enter minimum 6 character long");
                                } else {

                                    progressDialog.setTitle("The user Password is changing!!");
                                    progressDialog.show();

                                    firebaseUser.updatePassword(userNew_Password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task1) {
                                            if (task1.isSuccessful()) {

                                                Toast.makeText(UserChangePassword.this, "The password will be changed.", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(UserChangePassword.this, LoginUser.class));
                                                finish();
                                            }

                                            else {
                                                try {
                                                    throw Objects.requireNonNull(task1.getException());
                                                } catch (Exception e) {
                                                    Toast.makeText(UserChangePassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                            });

                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                userOldPassword.setError("Invalid Password");
                                userOldPassword.requestFocus();
                                tVUserAuthChangePass.setText("Your profile is not authenticated yet. Please authenticate your profile first and then change the Password!!");
                                tVUserAuthChangePass.setTextColor(Color.RED);
                            } catch (Exception e) {
                                Toast.makeText(UserChangePassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void alertUserNotAuthPassword() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("User Unauthenticated!!")
                .setMessage("Your profile is not authenticated yet.\nPlease authenticate your profile first and then change the Password!!")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_change_password, menu);
        return true;
    }

    private void userChangePassGoBack() {
        startActivity(new Intent(UserChangePassword.this, UserPage.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.userChangePass_goBack) {
            userChangePassGoBack();
        }

        return super.onOptionsItemSelected(item);
    }
}