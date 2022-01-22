package com.example.danut.restaurant;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class ResetPassword extends AppCompatActivity {

    //declare variables
    private FirebaseAuth firebaseAuth;
    private EditText emailResetPass;
    private String emailReset_Pass;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Objects.requireNonNull(getSupportActionBar()).setTitle("CUSTOMER: Reset Password");

        progressDialog = new ProgressDialog(this);

        //initialize variables
        emailResetPass = findViewById(R.id.etEmailResetPass);

        firebaseAuth = FirebaseAuth.getInstance();

        //Action of the button Reset password
        Button buttonResetPass = findViewById(R.id.btnResetPassword);
        buttonResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //change the old password to a new password
                resetPassword();
            }
        });
    }

    private void resetPassword() {

        if (validateResetPassData()) {

            progressDialog.setMessage("The password is reset!!");
            progressDialog.show();

            firebaseAuth.sendPasswordResetEmail(emailReset_Pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        progressDialog.dismiss();
                        Toast.makeText(ResetPassword.this, "The password reset email was sent", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ResetPassword.this, LoginUser.class));
                        finish();
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthInvalidUserException e) {
                            emailResetPass.setError("This email is not registered.");
                            emailResetPass.requestFocus();
                        } catch (Exception e) {
                            Toast.makeText(ResetPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressDialog.dismiss();
                }
            });
        }
    }

    private Boolean validateResetPassData() {

        boolean result = false;

        emailReset_Pass = emailResetPass.getText().toString().trim();

        //check the input fields
        if (TextUtils.isEmpty(emailReset_Pass)) {
            emailResetPass.setError("Please enter your registered email");
            emailResetPass.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailReset_Pass).matches()) {
            Toast.makeText(ResetPassword.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            emailResetPass.setError("Enter a valid Email Address");
            emailResetPass.requestFocus();
        } else {
            result = true;
        }
        return result;
    }
}
