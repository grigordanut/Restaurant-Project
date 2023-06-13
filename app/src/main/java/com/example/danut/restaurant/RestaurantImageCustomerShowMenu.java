package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


public class RestaurantImageCustomerShowMenu extends AppCompatActivity implements RestaurantAdapterCustomer.OnItemClickListener {

    private DatabaseReference databaseRefRest;
    private ValueEventListener eventListenerRest;

    private TextView tVRestCustomShowMenu;

    private RecyclerView customerRecyclerView;
    private RestaurantAdapterCustomer restaurantAdapterCustomer;

    private List<Restaurants> customRestList;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_image_customer_show_menu);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Customer restaurants show menu");

        //initialize variables
        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        //Retrieve data from Restaurants database
        databaseRefRest = FirebaseDatabase.getInstance().getReference("Restaurants");

        customRestList = new ArrayList<>();

        tVRestCustomShowMenu = findViewById(R.id.tvRestCustomShowMenu);

        customerRecyclerView = findViewById(R.id.customRecyclerView);
        customerRecyclerView.setHasFixedSize(true);
        customerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        restaurantAdapterCustomer = new RestaurantAdapterCustomer(RestaurantImageCustomerShowMenu.this, customRestList);
        customerRecyclerView.setAdapter(restaurantAdapterCustomer);
        restaurantAdapterCustomer.setOnItmClickListener(RestaurantImageCustomerShowMenu.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadRestaurantsList();
    }

    public void loadRestaurantsList() {

        eventListenerRest = databaseRefRest.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    customRestList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Restaurants rest_Data = postSnapshot.getValue(Restaurants.class);
                        assert rest_Data != null;
                        rest_Data.setRest_Key(postSnapshot.getKey());
                        customRestList.add(rest_Data);
                        tVRestCustomShowMenu.setText("Select the Restaurant");
                    }

                    restaurantAdapterCustomer.notifyDataSetChanged();
                } else {
                    tVRestCustomShowMenu.setText("No registered Restaurants");
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RestaurantImageCustomerShowMenu.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on Restaurant onClick
    @Override
    public void onItemClick(int position) {

        Restaurants selected_Rest = customRestList.get(position);
        Intent intent_Update = new Intent(RestaurantImageCustomerShowMenu.this, MenuImageCustomer.class);
        intent_Update.putExtra("RName", selected_Rest.getRest_Name());
        intent_Update.putExtra("RKey", selected_Rest.getRest_Key());
        startActivity(intent_Update);
    }
}
