package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RestaurantImageAddMenus extends AppCompatActivity implements RestaurantAdapterMenus.OnItemClickListener {

    private DatabaseReference databaseReference;
    private ValueEventListener restaurantsEventListener;

    private RecyclerView restaurantsRecyclerView;
    private RestaurantAdapterMenus restaurantAdapterMenus;

    private List<Restaurants> restaurantsList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_image_add_menus);

        restaurantsRecyclerView = findViewById(R.id.evRecyclerView);
        restaurantsRecyclerView.setHasFixedSize(true);
        restaurantsRecyclerView .setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        restaurantsList = new ArrayList<>();

        progressDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadRestaurantsListAdmin();
    }

    private void loadRestaurantsListAdmin(){
        //initialize the bike store database
        databaseReference = FirebaseDatabase.getInstance().getReference("Restaurants");

        restaurantsEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Restaurants restaurants = postSnapshot.getValue(Restaurants.class);
                    assert restaurants != null;
                    restaurants.setRest_Key(postSnapshot.getKey());
                    restaurantsList.add(restaurants);
                }
                restaurantAdapterMenus = new RestaurantAdapterMenus(RestaurantImageAddMenus.this, restaurantsList);
                restaurantsRecyclerView.setAdapter(restaurantAdapterMenus);
                restaurantAdapterMenus.setOnItmClickListener(RestaurantImageAddMenus.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RestaurantImageAddMenus.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on Restaurants onClick
    @Override
    public void onItemClick(int position) {
        Restaurants selected_Rest = restaurantsList.get(position);

        Intent intentAdd = new Intent(RestaurantImageAddMenus.this,AddNewMenu.class);
        intentAdd.putExtra("RName",selected_Rest.getRest_Name());
        intentAdd.putExtra("RKey",selected_Rest.getRest_Key());
        startActivity(intentAdd);
    }
}