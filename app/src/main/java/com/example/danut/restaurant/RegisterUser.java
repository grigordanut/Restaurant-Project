package com.example.danut.restaurant;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity {

    //Declaree variables
    private EditText firstNameReg, lastNameReg, phoneReg, emailReg, passwordReg, confPassReg;
    private Button buttonRegUser, buttonCancelRegUser;
    private TextView textViewLogIn;
    private FirebaseAuth firebaseAuth;
    String firstName_Reg, lastName_Reg, phone_Reg, email_Reg, password_Reg, confPass_Reg;

    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        //initialize variables
        firstNameReg=(EditText)findViewById(R.id.etFirstNameReg);
        lastNameReg=(EditText)findViewById(R.id.etLastNameReg);
        phoneReg=(EditText)findViewById(R.id.etPhoneNumberReg);
        emailReg=(EditText)findViewById(R.id.etEmailReg);
        passwordReg=(EditText)findViewById(R.id.etPasswordReg);
        confPassReg=(EditText)findViewById(R.id.etConfPassReg);

        progressDialog = new ProgressDialog(this);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        //action button cancel
        buttonCancelRegUser = (Button)findViewById(R.id.btnCancelRegUser);
        buttonCancelRegUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstNameReg.setText("");
                lastNameReg.setText("");
                phoneReg.setText("");
                emailReg.setText("");
                passwordReg.setText("");
                confPassReg.setText("");
            }
        });

        //action button register user
        buttonRegUser = (Button) findViewById(R.id.btnRegUser);
        buttonRegUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validate()){
                    //upload data to the database
                    //String first_Name=firstNameReg.getText().toString().trim();
                    String email_Reg=emailReg.getText().toString().trim();
                    String pass_Reg=passwordReg.getText().toString().trim();

                    progressDialog.setMessage("RegisterUser user details");
                    progressDialog.show();
                    //create new user into FirebaseDatabase
                    firebaseAuth.createUserWithEmailAndPassword(email_Reg, pass_Reg).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                sendEmailVerification();

                                //clear input fields
                                firstNameReg.setText("");
                                lastNameReg.setText("");
                                phoneReg.setText("");
                                emailReg.setText("");
                                passwordReg.setText("");
                                confPassReg.setText("");
                            }

                            else{
                                progressDialog.dismiss();
                                Toast.makeText(RegisterUser.this, "Registration Failed, this email address was alredy used to other account",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        //Action TextView Log In
        textViewLogIn=(TextView)findViewById(R.id.tvLogInUser);
        textViewLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent textLog =new Intent(RegisterUser.this,LoginUser.class);
                startActivity(textLog);
            }
        });
    }

    //validate data in the input fields
    private Boolean validate() {
        Boolean result = false;

        firstName_Reg = firstNameReg.getText().toString();
        lastName_Reg = lastNameReg.getText().toString();
        phone_Reg = phoneReg.getText().toString();
        email_Reg = emailReg.getText().toString();
        password_Reg = passwordReg.getText().toString();
        confPass_Reg = confPassReg.getText().toString();

        if (firstName_Reg.isEmpty()) {
            firstNameReg.setError("Enter your First Name");
            firstNameReg.requestFocus();
        }
        else if (lastName_Reg.isEmpty()) {
            lastNameReg.setError("Enter your Last Name");
            lastNameReg.requestFocus();
        }
        else if (phone_Reg.isEmpty()) {
            phoneReg.setError("Enter your Phone Number");
            passwordReg.requestFocus();
        }
        else if (email_Reg.isEmpty()) {
            emailReg.setError("Enter your Email Address");
            emailReg.requestFocus();
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email_Reg).matches()){
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            emailReg.setError("Enter a valid Email Address");
            emailReg.requestFocus();
        }

        else if (password_Reg.isEmpty()) {
            passwordReg.setError("Enter your Password");
            passwordReg.requestFocus();
        }
        else if (password_Reg.length()>0 && password_Reg.length()<6) {
            passwordReg.setError("The password is too short, enter mimimum 6 character long");
            Toast.makeText(this, "The password is too short, enter mimimum 6 character long", Toast.LENGTH_SHORT).show();
        }

        else if (!password_Reg.equals(confPass_Reg)) {
            Toast.makeText(this, "Confirm Password does not match Password", Toast.LENGTH_SHORT).show();
            confPassReg.setError("Enter same Password");
        }

        else {
            result = true;
        }
        return result;
    }

    //send email to user to verify if the email is real
    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        sendUserData();
                        progressDialog.dismiss();
                        Toast.makeText(RegisterUser.this, "Succesful Registered, Email verification was sent", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegisterUser.this, LoginUser.class));
                    }

                    else{
                        progressDialog.dismiss();
                        Toast.makeText(RegisterUser.this, "Verification email has not been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //send user date to the FirebaseDatabase
    private void sendUserData(){
        String user_id = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference currentUser = databaseReference.child(user_id);
        UserProfile userProf = new UserProfile(firstName_Reg, lastName_Reg, phone_Reg, email_Reg);
        currentUser.setValue(userProf);
    }
}
