package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

        Objects.requireNonNull(getSupportActionBar()).setTitle("Edit user profile");

        progressDialog = new ProgressDialog(UserEditProfile.this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Retrieve from Users database and load user details into the edit texts
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        //Upload updated data into Users table
        databaseReferenceUp = FirebaseDatabase.getInstance().getReference("Users");

        tVUserNameUp = findViewById(R.id.tvUserNameUp);

        //initialise the variables
        firstNameUserUp = findViewById(R.id.etFirstNameUp);
        lastNameUserUp = findViewById(R.id.etLastNameUp);
        phoneUserUp = findViewById(R.id.etPhoneUp);
        emailUserUp = findViewById(R.id.tvEmailUp);

        emailUserUp.setOnClickListener(view -> alertChangeEmailPlace());

        //save the user details in the database
        Button btn_SaveUp = findViewById(R.id.btnSaveUp);
        btn_SaveUp.setOnClickListener(v -> updateUserDetails());
    }

    public void updateUserDetails() {

        if (validateUserUpdateData()) {

            progressDialog.setTitle("Updating user details!!");
            progressDialog.show();

            firstName_UserUp = firstNameUserUp.getText().toString().trim();
            lastName_UserUp = lastNameUserUp.getText().toString().trim();
            phone_UserUp = phoneUserUp.getText().toString().trim();
            email_UserUp = emailUserUp.getText().toString().trim();

            databaseReferenceUp.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        if (firebaseUser.getUid().equals(postSnapshot.getKey())) {
                            postSnapshot.getRef().child("user_firstName").setValue(firstName_UserUp);
                            postSnapshot.getRef().child("user_lastName").setValue(lastName_UserUp);
                            postSnapshot.getRef().child("user_phone").setValue(phone_UserUp);
                            postSnapshot.getRef().child("user_email").setValue(email_UserUp);
                        }
                    }

                    progressDialog.dismiss();

                    LayoutInflater inflater = getLayoutInflater();
                    @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                    TextView text = layout.findViewById(R.id.tvToast);
                    ImageView imageView = layout.findViewById(R.id.imgToast);
                    text.setText("Your details have been successfully updated!!");
                    imageView.setImageResource(R.drawable.baseline_person_add_alt_1_24);
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();

                    startActivity(new Intent(UserEditProfile.this, UserPage.class));
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserEditProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public Boolean validateUserUpdateData() {

        boolean result = false;

        firstName_UserUp = firstNameUserUp.getText().toString().trim();
        lastName_UserUp = lastNameUserUp.getText().toString().trim();
        phone_UserUp = phoneUserUp.getText().toString().trim();

        if (TextUtils.isEmpty(firstName_UserUp)) {
            firstNameUserUp.setError("Enter your First Name");
            firstNameUserUp.requestFocus();
        } else if (TextUtils.isEmpty(lastName_UserUp)) {
            lastNameUserUp.setError("Enter your Last Name");
            lastNameUserUp.requestFocus();
        } else if (TextUtils.isEmpty(phone_UserUp)) {
            phoneUserUp.setError("Enter your Phone number");
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

    public void loadUserData() {

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Users user_Data = postSnapshot.getValue(Users.class);

                    assert user_Data != null;
                    if (firebaseUser.getUid().equals(postSnapshot.getKey())) {
                        firstNameUserUp.setText(user_Data.getUser_firstName());
                        lastNameUserUp.setText(user_Data.getUser_lastName());
                        phoneUserUp.setText(user_Data.getUser_phone());
                        emailUserUp.setText(user_Data.getUser_email());
                        tVUserNameUp.setText("Edit profile of: " + user_Data.getUser_firstName() + " " + user_Data.getUser_lastName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserEditProfile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void alertChangeEmailPlace() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserEditProfile.this);
        alertDialogBuilder
                .setTitle("Changing user email!!")
                .setMessage("The email address cannot be change here.\nPlease use Change Email option.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_edit_profile, menu);
        return true;
    }

    public void userEditProfileGoBack() {
        startActivity(new Intent(UserEditProfile.this, UserPage.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.userEditProfile_goBack) {
            userEditProfileGoBack();
        }

        return super.onOptionsItemSelected(item);
    }
}
