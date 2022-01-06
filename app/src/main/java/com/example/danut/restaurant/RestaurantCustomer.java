package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RestaurantCustomer extends AppCompatActivity {

    //declare variables
    FirebaseAuth firebaseAuth;

    private TextView tVShowRestCustom;

    private DatabaseReference databaseRefRest;
    private ValueEventListener eventListenerRest;
    private List<Restaurants> restList;
    private List<String> restNames;

    private ListView lVRestCustomer;
    private ArrayAdapter<String> adapter;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_customer);

        //initialize variables
        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        firebaseAuth = FirebaseAuth.getInstance();

        restList = new ArrayList<>();
        restNames = new ArrayList<>();

        tVShowRestCustom = findViewById(R.id.tvShowRestCustom);
        tVShowRestCustom.setText("No Restaurant found");

        lVRestCustomer = findViewById(R.id.lvRestCustomer);

        adapter = new ArrayAdapter<String>(this, R.layout.image_restaurant_customer, R.id.tvRestNameCustom, restNames);
        lVRestCustomer.setAdapter(adapter);

        //show the menu of restaurant when the list view is clicked
        lVRestCustomer.setOnItemClickListener((parent, view, position, id) -> {

            Restaurants rest_Data = restList.get(position);

            Intent i = new Intent(RestaurantCustomer.this, MenuImageCustomer.class);
            i.putExtra("RKey", rest_Data.getRest_Key());
            i.putExtra("RName", rest_Data.getRest_Name());
            startActivity(i);

            restNames.clear();
            restList.clear();
        });

        //action of the button show map
        Button buttonShowMap = findViewById(R.id.btnShowMap);
        buttonShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantCustomer.this, MapsActivity.class));
            }
        });
    }

    public void loadRestaurantsList() {

        //Retrieve data from Restaurants database
        databaseRefRest = FirebaseDatabase.getInstance().getReference("Restaurants");

        eventListenerRest = databaseRefRest.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                restList.clear();
                restNames.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Restaurants rest = postSnapshot.getValue(Restaurants.class);
                    if (rest != null) {
                        restList.add(rest);
                        restNames.add(rest.getRest_Name());
                        tVShowRestCustom.setText("Select a restaurant");
                    }
                }
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RestaurantCustomer.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadRestaurantsList();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (eventListenerRest != null) {
            databaseRefRest.removeEventListener(eventListenerRest);
        }
    }

    //log aut restaurant user
    private void LogOut() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(RestaurantCustomer.this, MainActivity.class));
    }

    private void UserDetails() {
        finish();
        startActivity(new Intent(RestaurantCustomer.this, UpdateUser.class));
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logOutUser) {
            LogOut();
        }

        if (item.getItemId() == R.id.userDetails) {
            UserDetails();
        }
        return super.onOptionsItemSelected(item);
    }
}
