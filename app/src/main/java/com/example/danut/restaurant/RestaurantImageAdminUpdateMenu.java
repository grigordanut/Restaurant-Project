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

public class RestaurantImageAdminUpdateMenu extends AppCompatActivity implements RestaurantAdapterAdmin.OnItemClickListener {

    private DatabaseReference databaseRefRest;
    private ValueEventListener evListenerRest;

    private FirebaseStorage firebaseStMenu;
    private DatabaseReference databaseRefMenu;

    private TextView tVRestImageUpdateMenusAdmin;

    private RecyclerView restaurantsRecyclerView;
    private RestaurantAdapterAdmin restaurantAdapterAdmin;

    private List<Restaurants> restaurantsList;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_image_admin_update_menu);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN: Restaurants update Menu");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        //Retrieve data from Restaurants database
        databaseRefRest = FirebaseDatabase.getInstance().getReference("Restaurants");

        firebaseStMenu = FirebaseStorage.getInstance();
        databaseRefMenu = FirebaseDatabase.getInstance().getReference("Menus");

        restaurantsList = new ArrayList<>();

        tVRestImageUpdateMenusAdmin = findViewById(R.id.tvRestImageUpdateMenusAdmin);

        restaurantsRecyclerView = findViewById(R.id.evRecyclerView);
        restaurantsRecyclerView.setHasFixedSize(true);
        restaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        restaurantAdapterAdmin = new RestaurantAdapterAdmin(RestaurantImageAdminUpdateMenu.this, restaurantsList);
        restaurantsRecyclerView.setAdapter(restaurantAdapterAdmin);
        restaurantAdapterAdmin.setOnItmClickListener(RestaurantImageAdminUpdateMenu.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadRestaurantsListAdmin();
    }

    private void loadRestaurantsListAdmin() {

        evListenerRest = databaseRefRest.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                restaurantsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Restaurants restaurants = postSnapshot.getValue(Restaurants.class);
                    assert restaurants != null;
                    restaurants.setRest_Key(postSnapshot.getKey());
                    restaurantsList.add(restaurants);
                    tVRestImageUpdateMenusAdmin.setText("Select the Restaurant");
                }

                restaurantAdapterAdmin.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RestaurantImageAdminUpdateMenu.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on Restaurant onClick
    @Override
    public void onItemClick(int position) {

        Restaurants selected_Rest = restaurantsList.get(position);

        databaseRefMenu.orderByChild("restaurant_Key").equalTo(selected_Rest.getRest_Key())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Intent intent_Update = new Intent(RestaurantImageAdminUpdateMenu.this, MenuImageAdminUpdateMenu.class);
                            intent_Update.putExtra("RName", selected_Rest.getRest_Name());
                            intent_Update.putExtra("RKey", selected_Rest.getRest_Key());
                            startActivity(intent_Update);
                        } else {
                            alertNoMenusAvailableUp(position);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RestaurantImageAdminUpdateMenu.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public void alertNoMenusAvailableUp(int position) {

        Restaurants selected_Rest = restaurantsList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RestaurantImageAdminUpdateMenu.this);
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