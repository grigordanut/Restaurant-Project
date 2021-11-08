package com.example.danut.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

public class AdminPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        Button buttonAddRest = findViewById(R.id.btnAddRestaurant);
        buttonAddRest.setOnClickListener(v -> startActivity(new Intent(AdminPage.this, AddNewRestaurant.class)));

        Button buttonShowRest = findViewById(R.id.btnShowRestaurant);
        buttonShowRest.setOnClickListener(v -> startActivity(new Intent(AdminPage.this, RestaurantImageShowRestAdmin.class)));

        Button buttonAddMenu = findViewById(R.id.btnAddMenu);
        buttonAddMenu.setOnClickListener(v -> startActivity(new Intent(AdminPage.this, RestaurantImageAddMenus.class)));

        Button buttonShowMenu = findViewById(R.id.btnShowMenu);
        buttonShowMenu.setOnClickListener(v -> startActivity(new Intent(AdminPage.this, RestaurantImageShowMenuAdmin.class)));
    }

    //log out Admin user
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    private void LogOut(){
        finish();
        startActivity(new Intent(AdminPage.this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logOutUser) {
            LogOut();
        }

        return super.onOptionsItemSelected(item);
    }
}