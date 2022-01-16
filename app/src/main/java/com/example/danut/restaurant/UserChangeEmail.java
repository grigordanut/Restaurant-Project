package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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

        Objects.requireNonNull(getSupportActionBar()).setTitle("Change User Email");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userOdlEmail = findViewById(R.id.etUserOldEmail);
        userOdlEmail.setEnabled(false);
        userPassword = findViewById(R.id.etUserPassword);
        userNewEmail = findViewById(R.id.etUserNewEmail);

        tVUserAuthChangeEmail = findViewById(R.id.tvUserAuthChangeEmail);
        tVUserAuthChangeEmail.setText("Your profile is not authenticated yet. Please authenticate your profile first and then change the Email!!");
        tVUserAuthChangeEmail.setTextColor(Color.RED);

        userOdl_Email = firebaseUser.getEmail();
        userOdlEmail.setText(userOdl_Email);

        Button buttonChangeEmail = findViewById(R.id.btnUserChangeEmail);
        buttonChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertUserNotAuthEmail();
            }
        });

        Button buttonAuthUser = findViewById(R.id.btnAuthUser);
        buttonAuthUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userOdl_Email =  userOdlEmail.getText().toString().trim();
                user_Password = userPassword.getText().toString().trim();

                if (TextUtils.isEmpty(user_Password)) {
                    userPassword.setError("Enter your password");
                    userPassword.requestFocus();
                }
                else{

                    progressDialog.setMessage("The user is authenticating!");
                    progressDialog.show();

                    AuthCredential credential = EmailAuthProvider.getCredential(userOdl_Email, user_Password);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                progressDialog.dismiss();

                                tVUserAuthChangeEmail.setText("Your profile is authenticated.\nYou can change the Email now!!");
                                tVUserAuthChangeEmail.setTextColor(Color.BLACK);
                                userPassword.setEnabled(false);
                                buttonAuthUser.setEnabled(false);
                                buttonAuthUser.setText("Disabled");
                                userNewEmail.requestFocus();

                                buttonChangeEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        userNew_Email = userNewEmail.getText().toString().trim();

                                        if (TextUtils.isEmpty(userNew_Email)){
                                            userNewEmail.setError("Enter your new Email Address");
                                            userNewEmail.requestFocus();
                                        }
                                        else if (!Patterns.EMAIL_ADDRESS.matcher(userNew_Email).matches()) {
                                            Toast.makeText(UserChangeEmail.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                                            userNewEmail.setError("Enter a valid Email Address");
                                            userNewEmail.requestFocus();
                                        }
                                        else{

                                            progressDialog.setMessage("The user Email is changing!");
                                            progressDialog.show();

                                            firebaseUser.updateEmail(userNew_Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        sendEmailVerification();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                            else{
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthInvalidCredentialsException e){
                                    userPassword.setError("Invalid Password");
                                    userPassword.requestFocus();
                                    tVUserAuthChangeEmail.setText("Your profile is not authenticated yet. Please authenticate your profile first and then change the email!!");
                                    tVUserAuthChangeEmail.setTextColor(Color.RED);
                                } catch (Exception e) {
                                    Toast.makeText(UserChangeEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void alertUserNotAuthEmail(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Your profile is not authenticated yet.\nPlease authenticate your profile first and then change the Email!!")
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserChangeEmailData();
                    } else {
                        Toast.makeText(UserChangeEmail.this, "Email verification  has not been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserChangeEmailData() {

        userNew_Email = userNewEmail.getText().toString().trim();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    final FirebaseUser user_Key = firebaseAuth.getCurrentUser();

                    if (user_Key != null) {
                        if (user_Key.getUid().equals(postSnapshot.getKey())){
                            postSnapshot.getRef().child("user_email").setValue(userNew_Email);
                        }
                    }
                }

                progressDialog.dismiss();
                Toast.makeText(UserChangeEmail.this, "Email will be changed. Email verification has been sent", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                startActivity(new Intent(UserChangeEmail.this, LoginUser.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserChangeEmail.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_change_email, menu);
        return true;
    }

    private void userChangeEmailGoBack(){
        finish();
        startActivity(new Intent(UserChangeEmail.this, UserPage.class));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.userChangeEmailGoBack) {
            userChangeEmailGoBack();
        }

        return super.onOptionsItemSelected(item);
    }
}