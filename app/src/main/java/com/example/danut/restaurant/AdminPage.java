package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminPage extends AppCompatActivity {

    //Retrieve data from Restaurant database
    private DatabaseReference databaseRefRestAv;
    private ValueEventListener eventListenerRestAv;

    //Retrieve data from Menus database
    private DatabaseReference databaseRefMenuAv;
    private ValueEventListener eventListenerMenuAv;

    private List<Restaurants> restListAv;
    private List<Menus> menuListAv;

    private int numberRestAv;
    private int numberMenuAv;

    private TextView tVAdminRestAv, tVAdminMenusAv;

    //Declaring some objects
    private DrawerLayout drawerLayoutAdmin;
    private ActionBarDrawerToggle drawerToggleAdmin;
    private NavigationView navigationViewAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Admin Page");

        restListAv = new ArrayList<>();
        menuListAv = new ArrayList<>();

        tVAdminRestAv = findViewById(R.id.tvAdminRestAv);
        tVAdminMenusAv = findViewById(R.id.tvAdminMenusAv);

        drawerLayoutAdmin = findViewById(R.id.activity_admin_page);
        navigationViewAdmin = findViewById(R.id.navViewAdmin);

        drawerToggleAdmin = new ActionBarDrawerToggle(this, drawerLayoutAdmin, R.string.open_adminPage, R.string.close_adminPage);

        drawerLayoutAdmin.addDrawerListener(drawerToggleAdmin);
        drawerToggleAdmin.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Adding Click Events to navigation drawer item
        navigationViewAdmin.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {

                    //Add new Restaurant
                    case R.id.adminAdd_restaurant:
                        Toast.makeText(AdminPage.this, "Add New Restaurant", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, AddNewRestaurant.class));
                        break;

                    //Show the list of Restaurants available
                    case R.id.adminShow_restaurant:
                        Toast.makeText(AdminPage.this, "Show Restaurants", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, RestaurantImageAdminShowRest.class));
                        break;

                    //Edit the Restaurants available
                    case R.id.adminUpdate_restaurant:
                        Toast.makeText(AdminPage.this, "Update Restaurant", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, RestaurantImageAdminUpdateRest.class));
                        break;

                    //Add Menus to the Restaurants available
                    case R.id.adminAdd_menu:
                        Toast.makeText(AdminPage.this, "Add Menu to Restaurant", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, RestaurantImageAdminAddMenu.class));
                        break;

                    //Show the list of Menus available ordered by Restaurants
                    case R.id.adminShow_menus:
                        Toast.makeText(AdminPage.this, "Show list of Menus", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, RestaurantImageAdminShowMenu.class));
                        break;

                    //Show the full list of Menus available
                    case R.id.adminShow_menusFullList:
                        Toast.makeText(AdminPage.this, "Show full list of Menus", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, MenuImageAdminFullList.class));
                        break;

                    //Edit the Menus available ordered by Restaurants
                    case R.id.adminUpdate_menu:
                        Toast.makeText(AdminPage.this, "Edit Menus", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, RestaurantImageAdminUpdateMenu.class));
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
        getMenuInflater().inflate(R.menu.menu_admin_page, menu);
        return true;
    }

    private void adminLogOut() {
        startActivity(new Intent(AdminPage.this, LoginUser.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (drawerToggleAdmin.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.admin_logOut) {
            alertDialogAdminLogout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void alertDialogAdminLogout() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdminPage.this);
        alertDialogBuilder
                .setTitle("Log out Admin.")
                .setMessage("Are sure to Log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> adminLogOut())

                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadRestaurantsAv();
        loadMenusAv();
    }

    public void loadRestaurantsAv() {

        databaseRefRestAv = FirebaseDatabase.getInstance().getReference("Restaurants");

        eventListenerRestAv = databaseRefRestAv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Restaurants rest_Data = postSnapshot.getValue(Restaurants.class);

                    restListAv.add(rest_Data);
                    numberRestAv = restListAv.size();
                    tVAdminRestAv.setText(String.valueOf(numberRestAv));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadMenusAv() {

        databaseRefMenuAv = FirebaseDatabase.getInstance().getReference("Menus");

        eventListenerMenuAv = databaseRefMenuAv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Menus menu_data = postSnapshot.getValue(Menus.class);
                    menuListAv.add(menu_data);
                    numberMenuAv = menuListAv.size();
                    tVAdminMenusAv.setText(String.valueOf(numberMenuAv));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}