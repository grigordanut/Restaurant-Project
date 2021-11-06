package com.example.danut.restaurant;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Action button Restaurant
        Button buttonMainLogIn = findViewById(R.id.btnMainLogIn);
        buttonMainLogIn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginUser.class);
            startActivity(intent);
        });

        //Action button Deals
//        Button buttonMainDeals = (Button)findViewById(R.id.btnMainDeals);
//        buttonMainDeals.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent deals = new Intent(MainActivity.this,AddRestNew.class);
//                startActivity(deals);
//            }
//        });

        //Action button LogIn/RegisterUser
        Button buttonMainReg = findViewById(R.id.btnMainReg);
        buttonMainReg.setOnClickListener(view -> {
            Intent logReg =new Intent(MainActivity.this, RegisterUser.class);
            startActivity(logReg);
        });

        //Action button Contact Us
        Button buttonContUs = findViewById(R.id.btnContUs);
        buttonContUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactUs =new Intent(MainActivity.this, ContactUs.class);
                startActivity(contactUs);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_restaurant; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
