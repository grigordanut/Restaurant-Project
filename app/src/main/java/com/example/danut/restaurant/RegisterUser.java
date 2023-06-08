package com.example.danut.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterUser extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

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

        //Create table Users into database
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        firstNameRegUser = findViewById(R.id.etFirstNameRegUser);
        lastNameRegUser = findViewById(R.id.etLastNameRegUser);
        phoneNrRegUser = findViewById(R.id.etPhoneNrRegUser);
        emailRegUser = findViewById(R.id.etEmailRegUser);
        passRegUser = findViewById(R.id.etPassRegUser);
        confPassRegUser = findViewById(R.id.etConfPassRegUser);

        Button btn_RegUser = findViewById(R.id.btnRegUser);
        btn_RegUser.setOnClickListener(view -> registerUser());

        //action button cancel
        Button btn_CancelRegUser = findViewById(R.id.btnCancelRegUser);
        btn_CancelRegUser.setOnClickListener(v -> {
            firstNameRegUser.setText("");
            lastNameRegUser.setText("");
            phoneNrRegUser.setText("");
            emailRegUser.setText("");
            passRegUser.setText("");
            confPassRegUser.setText("");
        });

        //Action TextView Log In
        Button btn_RegLogUser = findViewById(R.id.btnRegLogUser);
        btn_RegLogUser.setOnClickListener(view -> startActivity(new Intent(RegisterUser.this, LoginUser.class)));
    }

    @SuppressLint("SetTextI18n")
    public void registerUser() {

        if (validateRegUserData()) {

            progressDialog.setTitle("Register User details!!");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email_regUser, pass_regUser).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    uploadUserData();

                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {

                        LayoutInflater inflater = getLayoutInflater();
                        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                        TextView text = layout.findViewById(R.id.tvToast);
                        ImageView imageView = layout.findViewById(R.id.imgToast);
                        text.setText(e.getMessage());
                        imageView.setImageResource(R.drawable.ic_baseline_shopping_cart_24);
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                        //Toast.makeText(RegisterUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                progressDialog.dismiss();
            });
        }
    }

    @SuppressLint("SetTextI18n")
    public void uploadUserData() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        assert firebaseUser != null;
        String user_Id = firebaseUser.getUid();
        Users user_data = new Users(firstName_regUser, lastName_regUser, phoneNr_regUser, email_regUser);

        databaseReference.child(user_Id).setValue(user_data).addOnCompleteListener(RegisterUser.this, task -> {

            if (task.isSuccessful()) {

                firebaseUser.sendEmailVerification();

                LayoutInflater inflater = getLayoutInflater();
                @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                TextView text = layout.findViewById(R.id.tvToast);
                ImageView imageView = layout.findViewById(R.id.imgToast);
                text.setText("Registered Successful. Verification Email has been sent!!");
                imageView.setImageResource(R.drawable.ic_baseline_email_24);
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();

                Intent intent = new Intent(RegisterUser.this, LoginUser.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (Exception e) {
                    Toast.makeText(RegisterUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            progressDialog.dismiss();
        });
    }

    public Boolean validateRegUserData() {

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
        } else if (pass_regUser.isEmpty()) {
            passRegUser.setError("Enter your Password");
            passRegUser.requestFocus();
        } else if (pass_regUser.length() < 6) {
            passRegUser.setError("Password too short, enter minimum 6 character long");
        } else if (confPass_regUser.isEmpty()) {
            confPassRegUser.setError("Enter your Confirm Password");
            confPassRegUser.requestFocus();
        } else if (!confPass_regUser.equals(pass_regUser)) {
            confPassRegUser.setError("The Confirm Password does not match Password");
        } else {
            result = true;
        }
        return result;
    }
}