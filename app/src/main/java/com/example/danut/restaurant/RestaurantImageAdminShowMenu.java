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

public class RestaurantImageAdminShowMenu extends AppCompatActivity implements RestaurantAdapterAdmin.OnItemClickListener {

    private DatabaseReference databaseReference;
    private ValueEventListener restaurantEventListener;

    private TextView tVRestImageShowMenusAdmin;

    private RecyclerView restaurantRecyclerView;
    private RestaurantAdapterAdmin restaurantAdapterAdmin;

    private List<Restaurants> restaurantsList;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_image_admin_show_menu);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Admin restaurants show menu");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        //Retrieve data from Restaurants database
        databaseReference = FirebaseDatabase.getInstance().getReference("Restaurants");

        restaurantsList = new ArrayList<>();

        tVRestImageShowMenusAdmin = findViewById(R.id.tvRestImageShowMenusAdmin);

        restaurantRecyclerView = findViewById(R.id.restRecyclerView);
        restaurantRecyclerView.setHasFixedSize(true);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        restaurantAdapterAdmin = new RestaurantAdapterAdmin(RestaurantImageAdminShowMenu.this, restaurantsList);
        restaurantRecyclerView.setAdapter(restaurantAdapterAdmin);
        restaurantAdapterAdmin.setOnItmClickListener(RestaurantImageAdminShowMenu.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListAdmin();
    }

    private void loadBikeStoresListAdmin() {

        restaurantEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    restaurantsList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Restaurants restaurants = postSnapshot.getValue(Restaurants.class);
                        assert restaurants != null;
                        restaurants.setRest_Key(postSnapshot.getKey());
                        restaurantsList.add(restaurants);
                        tVRestImageShowMenusAdmin.setText("Select the Restaurant");
                    }

                    restaurantAdapterAdmin.notifyDataSetChanged();
                } else {
                    tVRestImageShowMenusAdmin.setText("No registered Restaurants.");
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RestaurantImageAdminShowMenu.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on Restaurants onClick
    @Override
    public void onItemClick(int position) {

        Restaurants selected_Rest = restaurantsList.get(position);
        Intent intentAdd = new Intent(RestaurantImageAdminShowMenu.this, MenuImageAdmin.class);
        intentAdd.putExtra("RName", selected_Rest.getRest_Name());
        intentAdd.putExtra("RKey", selected_Rest.getRest_Key());
        startActivity(intentAdd);
    }
}