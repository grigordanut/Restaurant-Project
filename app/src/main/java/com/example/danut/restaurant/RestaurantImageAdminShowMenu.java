package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantImageAdminShowMenu extends AppCompatActivity implements RestaurantAdapterAdmin.OnItemClickListener {

    private DatabaseReference databaseRefRest;
    private ValueEventListener evListenerRest;

    private FirebaseStorage firebaseStMenu;
    private DatabaseReference databaseRefMenu;

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
        databaseRefRest = FirebaseDatabase.getInstance().getReference("Restaurants");

        firebaseStMenu = FirebaseStorage.getInstance();
        databaseRefMenu = FirebaseDatabase.getInstance().getReference("Menus");

        restaurantsList = new ArrayList<>();

        tVRestImageShowMenusAdmin = findViewById(R.id.tvRestImageShowMenusAdmin);

        restaurantRecyclerView = findViewById(R.id.restRecyclerView);
        restaurantRecyclerView.setHasFixedSize(true);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        restaurantAdapterAdmin = new RestaurantAdapterAdmin(RestaurantImageAdminShowMenu.this, restaurantsList);
        restaurantRecyclerView.setAdapter(restaurantAdapterAdmin);
        restaurantAdapterAdmin.setOnItmClickListener(RestaurantImageAdminShowMenu.this);
    }

    //Action on Restaurants onClick
    @Override
    public void onItemClick(int position) {

        Restaurants selected_Rest = restaurantsList.get(position);

        databaseRefMenu.orderByChild("restaurant_Key").equalTo(selected_Rest.getRest_Key())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Intent intentAdd = new Intent(RestaurantImageAdminShowMenu.this, MenuImageAdmin.class);
                            intentAdd.putExtra("RName", selected_Rest.getRest_Name());
                            intentAdd.putExtra("RKey", selected_Rest.getRest_Key());
                            startActivity(intentAdd);
                        } else {
                            alertNoMenusAvailableShow(position);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RestaurantImageAdminShowMenu.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        loadRestaurantsListAdmin();
    }

    private void loadRestaurantsListAdmin() {

        evListenerRest = databaseRefRest.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                restaurantsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Restaurants restaurants = postSnapshot.getValue(Restaurants.class);
                    assert restaurants != null;
                    restaurants.setRest_Key(postSnapshot.getKey());
                    restaurantsList.add(restaurants);
                    tVRestImageShowMenusAdmin.setText("Select the Restaurant");
                }

                restaurantAdapterAdmin.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RestaurantImageAdminShowMenu.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void alertNoMenusAvailableShow(int position) {

        Restaurants selected_Rest = restaurantsList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RestaurantImageAdminShowMenu.this);
        alertDialogBuilder
                .setTitle("No Menus available!!")
                .setMessage("There are no menus available at the restaurant: " + selected_Rest.getRest_Name()
                        + ".\nAdd new menus using the Add Menus menu and then you can use this service.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}