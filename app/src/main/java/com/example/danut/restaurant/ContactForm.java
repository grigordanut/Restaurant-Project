package com.example.danut.restaurant;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

public class ContactForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_form);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Contact Us form");

        //Declare variable
        final EditText myName = findViewById(R.id.myName);
        final EditText myEmail = findViewById(R.id.myEmail);
        final EditText mySubject = findViewById(R.id.mySubject);
        final EditText myMessage = findViewById(R.id.myMessage);

        Button btn_SendMessage = (Button) findViewById(R.id.btnSendMessage);
        btn_SendMessage.setOnClickListener(v -> {

            String name = myName.getText().toString();
            String email = myEmail.getText().toString();
            String subject = mySubject.getText().toString();
            String message = myMessage.getText().toString();

            if (TextUtils.isEmpty(name)) {
                myName.setError("Enter Your Name");
                myName.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                myEmail.setError("Enter a valid Email Address");
                myEmail.requestFocus();
            } else if (TextUtils.isEmpty(subject)) {
                mySubject.setError("Enter Your Subject");
                mySubject.requestFocus();
            } else if (TextUtils.isEmpty(message)) {
                myMessage.setError("Enter Your Message");
                myMessage.requestFocus();
            } else {
                Intent sendEmail = new Intent(Intent.ACTION_SEND);

                /* insert email Data */
                sendEmail.setType("plain/text");
                sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"restaurant.deals70@gmail.com"});
                sendEmail.putExtra(Intent.EXTRA_SUBJECT, subject);
                sendEmail.putExtra(Intent.EXTRA_TEXT,
                        "name:" + name + '\n' + "Email ID:" + email + '\n' + "Message:" + '\n' + message);

                // Send message to the Activity
                startActivity(Intent.createChooser(sendEmail, "Send mail..."));
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
