package com.example.danut.restaurant;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

        Objects.requireNonNull(getSupportActionBar()).setTitle("CUSTOMER: reset Password");

        progressDialog = new ProgressDialog(this);

        //initialize variables
        emailResetPass = findViewById(R.id.etEmailResetPass);

        firebaseAuth = FirebaseAuth.getInstance();

        //Action of the button Reset password
        Button btn_ResetPass = findViewById(R.id.btnResetPassword);
        btn_ResetPass.setOnClickListener(view -> resetPassword());
    }

    private void resetPassword() {

        if (validateResetPassData()) {

            progressDialog.setTitle("Reset user Password!!");
            progressDialog.show();

            firebaseAuth.sendPasswordResetEmail(emailReset_Pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(ResetPassword.this, "Password reset email has been sent", Toast.LENGTH_SHORT).show();
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

        if (emailReset_Pass.isEmpty()) {
            emailResetPass.setError("Enter your Email Address");
            emailResetPass.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailReset_Pass).matches()) {
            emailResetPass.setError("Enter a valid Email Address");
        } else {
            result = true;
        }
        return result;
    }
}
