package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserEditProfile extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    private DatabaseReference databaseReferenceUp;

    //declare variables
    private EditText firstNameUserUp, lastNameUserUp, phoneUserUp;
    private TextView tVUserNameUp, emailUserUp;

    private String firstName_UserUp, lastName_UserUp, phone_UserUp, email_UserUp;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_profile);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Update User profile");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        tVUserNameUp = findViewById(R.id.tvUserNameUp);

        //initialise the variables
        firstNameUserUp = findViewById(R.id.etFirstNameUp);
        lastNameUserUp = findViewById(R.id.etLastNameUp);
        phoneUserUp = findViewById(R.id.etPhoneUp);
        emailUserUp = findViewById(R.id.tvEmailUp);

        emailUserUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertEmailChangePlace();
            }
        });

        //save the user details in the database
        Button buttonSaveUp = (Button) findViewById(R.id.btnSaveUp);
        buttonSaveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDetails();
            }
        });
    }

    private void updateUserDetails() {

        if (validateUserUpdateData()) {

            databaseReferenceUp = FirebaseDatabase.getInstance().getReference("Users");

            firstName_UserUp = firstNameUserUp.getText().toString().trim();
            lastName_UserUp = lastNameUserUp.getText().toString().trim();
            phone_UserUp = phoneUserUp.getText().toString().trim();
            email_UserUp = emailUserUp.getText().toString().trim();

            progressDialog.setMessage("The User details are updating!!");
            progressDialog.show();

            databaseReferenceUp.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        final FirebaseUser user_Key = firebaseAuth.getCurrentUser();
                        if (user_Key != null) {
                            if (user_Key.getUid().equals(postSnapshot.getKey())) {
                                postSnapshot.getRef().child("user_firstName").setValue(firstName_UserUp);
                                postSnapshot.getRef().child("user_lastName").setValue(lastName_UserUp);
                                postSnapshot.getRef().child("user_phone").setValue(phone_UserUp);
                                postSnapshot.getRef().child("user_email").setValue(email_UserUp);
                            }
                        }
                    }

                    progressDialog.dismiss();
                    Toast.makeText(UserEditProfile.this, "Your details has been changed successfully", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();

                    startActivity(new Intent(UserEditProfile.this, LoginUser.class));
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserEditProfile.this, error.getCode(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Boolean validateUserUpdateData() {

        boolean result = false;

        firstName_UserUp = firstNameUserUp.getText().toString().trim();
        lastName_UserUp = lastNameUserUp.getText().toString().trim();
        phone_UserUp = phoneUserUp.getText().toString().trim();

        if (TextUtils.isEmpty(firstName_UserUp)) {
            firstNameUserUp.setError("Enter First Name");
            firstNameUserUp.requestFocus();
        } else if (TextUtils.isEmpty(lastName_UserUp)) {
            lastNameUserUp.setError("Enter Last Name");
            lastNameUserUp.requestFocus();
        } else if (TextUtils.isEmpty(phone_UserUp)) {
            phoneUserUp.setError("Enter Phone number");
            firstNameUserUp.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadUserData();
    }

    private void loadUserData() {

        progressDialog.setMessage("User details are displaying!!");
        progressDialog.show();

        //Retrieve and load user details into the edit texts
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Users user_Data = postSnapshot.getValue(Users.class);

                    if (user_Data != null) {
                        if (firebaseUser.getUid().equals(postSnapshot.getKey())) {
                            firstNameUserUp.setText(user_Data.getUser_firstName());
                            lastNameUserUp.setText(user_Data.getUser_lastName());
                            phoneUserUp.setText(user_Data.getUser_phone());
                            emailUserUp.setText(user_Data.getUser_email());
                            tVUserNameUp.setText("Edit profile of: " + user_Data.getUser_firstName() + " " + user_Data.getUser_lastName());
                        }
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserEditProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void alertEmailChangePlace() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("The Email Address cannot be change here.\nPlease use Change Email option!")
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_edit_profile, menu);
        return true;
    }

    private void userEditProfileGoBack(){
        startActivity(new Intent(UserEditProfile.this, UserPage.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.userEditProfileGoBack) {
            userEditProfileGoBack();
        }

        return super.onOptionsItemSelected(item);
    }
}
