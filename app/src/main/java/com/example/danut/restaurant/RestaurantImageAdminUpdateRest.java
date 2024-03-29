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
import java.util.Objects;

public class RestaurantImageAdminUpdateRest extends AppCompatActivity implements RestaurantAdapterAdmin.OnItemClickListener {

    private DatabaseReference databaseReference;
    private ValueEventListener restaurantsEventListener;

    private TextView tVRestImageUpdateRest;

    private RecyclerView restaurantsRecyclerView;
    private RestaurantAdapterAdmin restaurantAdapterAdmin;

    private List<Restaurants> restaurantsList;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_image_admin_update_rest);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN: Restaurants available");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        //Retrieve data from Restaurants database
        databaseReference = FirebaseDatabase.getInstance().getReference("Restaurants");

        restaurantsList = new ArrayList<>();

        tVRestImageUpdateRest = findViewById(R.id.tvRestImageUpdateRest);

        restaurantsRecyclerView = findViewById(R.id.evRecyclerView);
        restaurantsRecyclerView.setHasFixedSize(true);
        restaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        restaurantAdapterAdmin = new RestaurantAdapterAdmin(RestaurantImageAdminUpdateRest.this, restaurantsList);
        restaurantsRecyclerView.setAdapter(restaurantAdapterAdmin);
        restaurantAdapterAdmin.setOnItmClickListener(RestaurantImageAdminUpdateRest.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadRestListUpdateRest();
    }

    private void loadRestListUpdateRest() {

        restaurantsEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                restaurantsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Restaurants restaurants = postSnapshot.getValue(Restaurants.class);
                    assert restaurants != null;
                    restaurants.setRest_Key(postSnapshot.getKey());
                    restaurantsList.add(restaurants);
                    tVRestImageUpdateRest.setText("Select the Restaurant");
                }

                restaurantAdapterAdmin.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RestaurantImageAdminUpdateRest.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on Restaurant onClick
    @Override
    public void onItemClick(int position) {
        Restaurants selected_Rest = restaurantsList.get(position);
        Intent intent_Update = new Intent(RestaurantImageAdminUpdateRest.this, UpdateRestaurant.class);
        intent_Update.putExtra("RName", selected_Rest.getRest_Name());
        intent_Update.putExtra("RAddress", selected_Rest.getRest_Address());
        intent_Update.putExtra("RKey", selected_Rest.getRest_Key());
        startActivity(intent_Update);
    }
}