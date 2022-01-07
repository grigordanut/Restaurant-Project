package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminPage extends AppCompatActivity {

    //Declare Bike Store database variables (Retrieve data)
    private DatabaseReference dbRefBikeStoresAv;
    private ValueEventListener evListenerBikeStoreAv;

    //Declare Bike database variables (Retrieve data)
    private DatabaseReference dbRefBikesRentAv;
    private ValueEventListener evListenerBikesRentAv;

    private List<Restaurants> bikeStoresList;
    private List<Menus> bikesRentListAvRent;

    private int numberStoresAvailable;
    private int numberBikesAvRent;

    private TextView tVAdminRestAv, tVAdminMenusAv;

    //Declaring some objects
    private DrawerLayout drawerLayoutAdmin;
    private ActionBarDrawerToggle drawerToggleAdmin;
    private NavigationView navigationViewAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        //Retrieve data from Bike Store table
        dbRefBikeStoresAv = FirebaseDatabase.getInstance().getReference("Bike Stores");

        //Retrieve data from BikesRent table
        dbRefBikesRentAv = FirebaseDatabase.getInstance().getReference("Bikes");


        bikeStoresList = new ArrayList<>();
        bikesRentListAvRent = new ArrayList<>();

        tVAdminRestAv = findViewById(R.id.tvAdminRestAv);
        tVAdminMenusAv = findViewById(R.id.tvAdminMenusAv);

        drawerLayoutAdmin = findViewById(R.id.activity_admin_page);
        navigationViewAdmin = findViewById(R.id.navViewAdmin);


        drawerToggleAdmin = new ActionBarDrawerToggle(this,drawerLayoutAdmin, R.string.open_adminPage, R.string.close_adminPage);

        drawerLayoutAdmin.addDrawerListener(drawerToggleAdmin);
        drawerToggleAdmin.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Adding Click Events to navigation drawer item
        navigationViewAdmin.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){

                    //Add new Restaurant
                    case R.id.adminAdd_restaurant:
                        Toast.makeText(AdminPage.this, "Add New Restaurant",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, AddNewRestaurant.class));
                        break;

                    //Show the list of Restaurants available
                    case R.id.adminShow_restaurant:
                        Toast.makeText(AdminPage.this, "Show Restaurants",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, RestaurantImageShowRestAdmin.class));
                        break;

                    //Edit the Restaurants available
                    case R.id.adminUpdate_restaurant:
                        Toast.makeText(AdminPage.this, "Update Restaurant",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, UpdateRestaurant.class));
                        break;

                    //Add Menus to the Restaurants available
                    case R.id.adminAdd_menu:
                        Toast.makeText(AdminPage.this, "Add Menu to Restaurant",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, RestaurantImageAddMenus.class));
                        break;

                    //Show the list of Menus available ordered by Restaurants
                    case R.id.adminShow_menus:
                        Toast.makeText(AdminPage.this, "Show list of Menus",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, RestaurantImageShowMenuAdmin.class));
                        break;

                    //Show the full list of Menus available
                    case R.id.adminShow_menusFullList:
                        Toast.makeText(AdminPage.this, "Show full list of Menus",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, MenuImageFullListAdmin.class));
                        break;

                    //Edit the Menus available ordered by Restaurants
                    case R.id.adminUpdate_menu:
                        Toast.makeText(AdminPage.this, "Edit Menus",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, RestaurantImageUpdateMenus.class));
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
    }

    //log out Admin user
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    private void LogOut(){
        finish();
        startActivity(new Intent(AdminPage.this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if(drawerToggleAdmin.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.logOutAdmin) {
            LogOut();
        }

        return super.onOptionsItemSelected(item);
    }
}