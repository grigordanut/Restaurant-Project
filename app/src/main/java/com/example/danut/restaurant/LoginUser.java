package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginUser extends AppCompatActivity {

    //declare variables
    private FirebaseAuth firebaseAuth;

    private EditText emailLogUser, passwordLogUser;
    private TextView tVForgotPassUser;
    private String emailLog_User, passLog_User;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        Objects.requireNonNull(getSupportActionBar()).setTitle("User login");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        //initialize variables
        emailLogUser = findViewById(R.id.etEmailLogUser);
        passwordLogUser = findViewById(R.id.etPassLogUser);

        //Action TextView Forgotten Password
        tVForgotPassUser = findViewById(R.id.tvForgotPassUser);
        tVForgotPassUser.setOnClickListener(view -> startActivity(new Intent(LoginUser.this, ResetPassword.class)));

        //Action button Cancel Log in user
        Button btn_CancelLogUser = findViewById(R.id.btnCancelLogUser);
        btn_CancelLogUser.setOnClickListener(v -> {
            emailLogUser.setText("");
            passwordLogUser.setText("");
        });

        //Action button Register User
        Button btn_RegLogUser = findViewById(R.id.btnRegLogUser);
        btn_RegLogUser.setOnClickListener(view -> startActivity(new Intent(LoginUser.this, RegisterUser.class)));

        //Action button LogIn User
        Button btn_LogInUser = findViewById(R.id.btnLogInUser);
        btn_LogInUser.setOnClickListener(view -> logInUser());
    }

    public void logInUser() {

        if (validateUserData()) {

            progressDialog.setTitle("User login!!");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(emailLog_User, passLog_User).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    checkEmailVerification();

                } else {

                    progressDialog.dismiss();

                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthInvalidUserException e) {
                        emailLogUser.setError("This email is not registered.");
                        emailLogUser.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        passwordLogUser.setError("Invalid password");
                        passwordLogUser.requestFocus();
                    } catch (Exception e) {
                        Toast.makeText(LoginUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //check if the email has been verified
    @SuppressLint("SetTextI18n")
    public void checkEmailVerification() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        assert firebaseUser != null;
        if (firebaseUser.isEmailVerified()) {

            //progressDialog.dismiss();

            LayoutInflater inflater = getLayoutInflater();
            @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
            TextView text = layout.findViewById(R.id.tvToast);
            ImageView imageView = layout.findViewById(R.id.imgToast);
            text.setText("Login successful!!");
            imageView.setImageResource(R.drawable.ic_baseline_login_24);
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();

            Intent intent = new Intent(LoginUser.this, UserPage.class);
            startActivity(intent);
            finish();

        } else {

            progressDialog.dismiss();

            LayoutInflater inflater = getLayoutInflater();
            @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
            TextView text = layout.findViewById(R.id.tvToast);
            ImageView imageView = layout.findViewById(R.id.imgToast);
            text.setText("Please verify your email first!!");
            imageView.setImageResource(R.drawable.ic_baseline_mark_email_unread_24);
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
    }

    //validate input data into the editText
    public Boolean validateUserData() {

        boolean result = false;

        emailLog_User = emailLogUser.getText().toString();
        passLog_User = passwordLogUser.getText().toString();

        if (emailLog_User.isEmpty()) {
            emailLogUser.setError("Enter your email address");
            emailLogUser.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailLog_User).matches()) {
            emailLogUser.setError("Enter a valid email address");
        } else if (passLog_User.isEmpty()) {
            passwordLogUser.setError("Enter your password");
            passwordLogUser.requestFocus();
        } else if (emailLog_User.equals("admin@gmail.com") && (passLog_User.equals("admin"))) {
            progressDialog.setMessage("Login Admin");
            progressDialog.show();
            startActivity(new Intent(LoginUser.this, AdminPage.class));
            progressDialog.dismiss();
        } else {
            result = true;
        }
        return result;
    }
}
