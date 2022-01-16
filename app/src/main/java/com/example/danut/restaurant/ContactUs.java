package com.example.danut.restaurant;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.Objects;

public class ContactUs extends AppCompatActivity {

    private Button buttonInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Contact Us page");

        buttonInsert = (Button)findViewById(R.id.btnInsert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent textLog =new Intent(ContactUs.this,ContactForm.class);
                startActivity(textLog);
            }
        });
    }
}
