package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterUser extends AppCompatActivity {

    private EditText firstNameRegUser, lastNameRegUser, phoneNrRegUser, emailRegUser, passRegUser, confPassRegUser;
    private String firstName_regUser, lastName_regUser, phoneNr_regUser, email_regUser, pass_regUser, confPass_regUser;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Register User");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        firstNameRegUser = findViewById(R.id.etFirstNameRegUser);
        lastNameRegUser =  findViewById(R.id.etLastNameRegUser);
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
        Button buttonCancelRegUser = (Button)findViewById(R.id.btnCancelRegUser);
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
                Intent textLog =new Intent(RegisterUser.this,LoginUser.class);
                startActivity(textLog);
            }
        });
    }

    private void registerUser(){
        if (validateRegUserData()) {
            progressDialog.setMessage("Register User Details");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email_regUser, pass_regUser)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sendEmailVerification();

                        //clear input text fields
                        firstNameRegUser.setText("");
                        lastNameRegUser.setText("");
                        phoneNrRegUser.setText("");
                        emailRegUser.setText("");
                        passRegUser.setText("");
                        confPassRegUser.setText("");

                    } else {
                        progressDialog.dismiss();
                        alertDialogEmailUsed();
                    }
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
            firstNameRegUser.setError("First Name can be empty");
            firstNameRegUser.requestFocus();
        } else if (TextUtils.isEmpty(lastName_regUser)) {
            lastNameRegUser.setError("Last Name cannot be empty");
            lastNameRegUser.requestFocus();
        } else if (phoneNr_regUser.isEmpty()) {
            phoneNrRegUser.setError("Phone Number cannot be empty");
            phoneNrRegUser.requestFocus();
        } else if (email_regUser.isEmpty()) {
            emailRegUser.setError("Email Address cannot be empty");
            emailRegUser.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email_regUser).matches()) {
            Toast.makeText(RegisterUser.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            emailRegUser.setError("Enter a valid Email Address");
            emailRegUser.requestFocus();
        } else if (pass_regUser.isEmpty()) {
            passRegUser.setError("Password cannot be empty");
            passRegUser.requestFocus();
        } else if (pass_regUser.length() < 6) {
            passRegUser.setError("The password is too short, enter minimum 6 character long");
            Toast.makeText(RegisterUser.this, "The password is too short, enter minimum 6 character long", Toast.LENGTH_SHORT).show();
        } else if (confPass_regUser.isEmpty()) {
            confPassRegUser.setError("Confirm Password cannot be empty");
            confPassRegUser.requestFocus();
        } else if (!pass_regUser.equals(confPass_regUser)) {
            Toast.makeText(RegisterUser.this, "Confirm Password does not match Password", Toast.LENGTH_SHORT).show();
            confPassRegUser.setError("The Password does not match");
            confPassRegUser.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserRegData();
                        progressDialog.dismiss();
                        alertDialogUserRegistered();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterUser.this, "Verification email has not been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserRegData() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        Users user_data = new Users(firstName_regUser, lastName_regUser, phoneNr_regUser, email_regUser);
        databaseReference.child(userID).setValue(user_data);
    }

    private void alertDialogUserRegistered(){
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(RegisterUser.this);
        builderAlert.setMessage("Hi " + firstName_regUser + " " + lastName_regUser + " you are successfully registered, Email verification was sent. Please verify your email before Log in");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegisterUser.this, LoginUser.class));
                    }
                });

        AlertDialog alertDialog = builderAlert.create();
        alertDialog.show();
    }

    private void alertDialogEmailUsed(){
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(RegisterUser.this);
        builderAlert.setMessage("Registration failed, the email: \n"+email_regUser+" was already used to open an account on this app.");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Ok",
                (arg0, arg1) -> emailRegUser.requestFocus());

        AlertDialog alertDialog = builderAlert.create();
        alertDialog.show();
    }
}