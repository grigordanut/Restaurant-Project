package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginUser extends AppCompatActivity {

    //declare variables
    private EditText emailLogUser, passwordLogUser;
    private TextView textViewInfoLog, textViewForgotPassUser;
    private String emailLog_User, passLog_User;
    private Button buttonSignUp, buttonLogIn, buttonCancelLogUser;
    private int counter = 5;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        //initialize variables
        emailLogUser = (EditText) findViewById(R.id.etEmailLogUser);
        passwordLogUser = (EditText) findViewById(R.id.etPassLogUser);
        textViewInfoLog = (TextView) findViewById(R.id.tvInfoUser);
        buttonLogIn = (Button) findViewById(R.id.btnLogInUser);

        textViewInfoLog.setText("No of attempts remaining: " + counter);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();


        if (user != null) {
            finish();
            startActivity(new Intent(LoginUser.this, RestaurantCustomer.class));
        }

        //Action TextView Forgotten Password
        textViewForgotPassUser = (TextView)findViewById(R.id.tvForgotPasswordUser);
        textViewForgotPassUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fPassword = new Intent(LoginUser.this, ResetPassword.class);
                startActivity(fPassword);
            }
        });

        //Action button log in user
        buttonCancelLogUser = (Button) findViewById(R.id.btnCancelLogUser);
        buttonCancelLogUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLogUser.setText("");
                passwordLogUser.setText("");
            }
        });

        //Action button SignUp
        buttonSignUp = (Button) findViewById(R.id.btnSignUpUser);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign = new Intent(LoginUser.this, RegisterUser.class);
                startActivity(sign);
            }
        });

        //Action button LogIn
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //log in a new user
                if (validateUserData()){

                    progressDialog.setMessage("Welcome to restaurant");
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(emailLog_User, passLog_User).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                //clear data
                                emailLogUser.setText("");
                                passwordLogUser.setText("");
                                checkEmailVerification();
                            }

                            else {
                                Toast.makeText(LoginUser.this, "Log In failed, you entered a wrong email or password", Toast.LENGTH_SHORT).show();
                                counter--;
                                progressDialog.dismiss();
                                textViewInfoLog.setText("No of attempts remaining: " + counter);

                                if (counter == 0) {
                                    textViewInfoLog.setText("No more attempts remaining, please press Forgoten Password");
                                    buttonLogIn.setEnabled(false);
                                    buttonLogIn.setBackgroundColor(Color.parseColor("#cc3333"));
                                    buttonLogIn.setText("Stop");
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    //validate input data into the editText
    public Boolean validateUserData() {

        boolean result = false;

        emailLog_User = emailLogUser.getText().toString();
        passLog_User = passwordLogUser.getText().toString();

        if (emailLog_User.isEmpty()) {
            emailLogUser.setError("Enter your Email Address");
            Toast.makeText(this, "Please enter your Email Address", Toast.LENGTH_SHORT).show();
            emailLogUser.requestFocus();
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(emailLog_User).matches()){
            emailLogUser.setError("Enter a valid Email Address");
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            emailLogUser.requestFocus();
        }

        else if (passLog_User.isEmpty()){
            passwordLogUser.setError("Enter your Password");
            Toast.makeText(this, "Please enter your Password",Toast.LENGTH_SHORT).show();
            passwordLogUser.requestFocus();
        }

        else if (emailLog_User.equals("admin@gmail.com") && (passLog_User.equals("admin"))) {
            progressDialog.setMessage("Login Admin");
            progressDialog.show();
            startActivity(new Intent(LoginUser.this, AddRestaurant.class));
            emailLogUser.setText("");
            passwordLogUser.setText("");
            progressDialog.dismiss();
        }
        else{
            result = true;
        }
        return result;
    }

    //check if the email has been verified
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        boolean emailFlag = firebaseUser.isEmailVerified();

        if(emailFlag){
            progressDialog.dismiss();
            finish();
            Toast.makeText(LoginUser.this, "Log In successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginUser.this, RestaurantCustomer.class));
        }

        else{
            progressDialog.dismiss();
            Toast.makeText(this, "Please verify your Email first", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
}
