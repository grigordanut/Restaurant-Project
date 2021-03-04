package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

    private FirebaseAuth firebaseAuthRest;
    private FirebaseUser currentUser;

    DatabaseReference databaseRefRest;
    ValueEventListener eventListener;
    List<Restaurants> restaurants;
    List<String> restNames;

    ListView listView;
    ArrayAdapter<String> adapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_customer);

        //initialize variables
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuthRest = FirebaseAuth.getInstance();
        currentUser = firebaseAuthRest.getCurrentUser();

        restNames = new ArrayList<>();
        restNames.add("No Restaurant found");

        listView = findViewById(R.id.listViewRestCustomer);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, restNames);
        listView.setAdapter(adapter);

        //shoew the menu of restaurant whemn the list view is clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(restaurants.size() > 0) {
                    String restaurantName = restNames.get(position);
                    Intent i = new Intent(RestaurantCustomer.this, ImageActivityCustomer.class);
                    i.putExtra("RESID", restaurantName);
                    //finish();
                    startActivity(i);
                }
            }
        });
    }

    //load the list of the restaurants
    private void loadRestaurantsList(){
        restNames = new ArrayList<>();
        restaurants = new ArrayList<>();
        progressDialog.show();
        if(databaseRefRest == null){
            databaseRefRest = FirebaseDatabase.getInstance().getReference().child("Restaurants");
        }
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Restaurants rest = child.getValue(Restaurants.class);
                    if(rest != null) {
                        restaurants.add(rest);
                        restNames.add(rest.getRest_Name());
                    }
                }
                if(restNames.size()> 0){
                    adapter.clear();
                    adapter.addAll(restNames);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RestaurantCustomer.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseRefRest.addValueEventListener(listener);
        eventListener = listener;

        progressDialog.dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (restNames.isEmpty()){
            restNames.add("No Restaurant found");
        }
        else {
            loadRestaurantsList();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(eventListener != null){
            databaseRefRest.removeEventListener(eventListener);
        }
    }

    //log aut restaurant user
    private void LogOut(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(RestaurantCustomer.this, MainActivity.class));
    }

    private void UserDetails(){
        finish();
        startActivity(new Intent(RestaurantCustomer.this, UserPage.class));
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
