package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
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

    private TextView tVRestImageAddMenusAdmin;

    private RecyclerView restaurantsRecyclerView;
    private RestaurantAdapterMenus restaurantAdapterMenus;

    private List<Restaurants> restaurantsList;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_image_add_menus);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        //initialize the Restaurants database
        databaseReference = FirebaseDatabase.getInstance().getReference("Restaurants");

        restaurantsList = new ArrayList<>();

        tVRestImageAddMenusAdmin = findViewById(R.id.tvRestImageAddMenusAdmin);
        tVRestImageAddMenusAdmin.setText("No Restaurants available");

        restaurantsRecyclerView = findViewById(R.id.evRecyclerView);
        restaurantsRecyclerView.setHasFixedSize(true);
        restaurantsRecyclerView .setLayoutManager(new LinearLayoutManager(this));

        restaurantAdapterMenus = new RestaurantAdapterMenus(RestaurantImageAddMenus.this, restaurantsList);
        restaurantsRecyclerView.setAdapter(restaurantAdapterMenus);
        restaurantAdapterMenus.setOnItmClickListener(RestaurantImageAddMenus.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadRestaurantsListAdmin();
    }

    private void loadRestaurantsListAdmin(){


        restaurantsEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Restaurants restaurants = postSnapshot.getValue(Restaurants.class);
                    assert restaurants != null;
                    restaurants.setRest_Key(postSnapshot.getKey());
                    restaurantsList.add(restaurants);
                    tVRestImageAddMenusAdmin.setText("Select a restaurant");
                }

                restaurantAdapterMenus.notifyDataSetChanged();
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
        restaurantsList.clear();
    }
}