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

public class RestaurantImageShowMenuAdmin extends AppCompatActivity implements RestaurantAdapterMenus.OnItemClickListener {

    private DatabaseReference databaseReference;
    private ValueEventListener restaurantEventListener;

    private RecyclerView restaurantRecyclerView;
    private RestaurantAdapterMenus restaurantAdapterMenus;

    private List<Restaurants> restaurantsList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_image_show_menu_admin);

        restaurantRecyclerView = (RecyclerView) findViewById(R.id.restRecyclerView);
        restaurantRecyclerView.setHasFixedSize(true);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        restaurantsList = new ArrayList<>();

        progressDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListAdmin();
    }

    private void loadBikeStoresListAdmin(){
        //initialize the bike store database
        databaseReference = FirebaseDatabase.getInstance().getReference("Restaurants");

        restaurantEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Restaurants restaurants = postSnapshot.getValue(Restaurants.class);
                    assert restaurants != null;
                    restaurants.setRest_Key(postSnapshot.getKey());
                    restaurantsList.add(restaurants);
                }
                restaurantAdapterMenus = new RestaurantAdapterMenus(RestaurantImageShowMenuAdmin.this, restaurantsList);
                restaurantRecyclerView.setAdapter(restaurantAdapterMenus);
                restaurantAdapterMenus.setOnItmClickListener(RestaurantImageShowMenuAdmin.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RestaurantImageShowMenuAdmin.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on Restaurants onClick
    @Override
    public void onItemClick(int position) {
        Restaurants selected_Rest = restaurantsList.get(position);

        Intent intentAdd = new Intent(RestaurantImageShowMenuAdmin.this,MenuImageAdmin.class);
        intentAdd.putExtra("RName",selected_Rest.getRest_Name());
        intentAdd.putExtra("RKey",selected_Rest.getRest_Key());
        startActivity(intentAdd);
        restaurantsList.clear();
    }
}