package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class RestaurantImageCustomerShowRest extends AppCompatActivity implements RestaurantAdapterCustomer.OnItemClickListener{

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
        setContentView(R.layout.activity_restaurant_image_customer_show_rest);

        Objects.requireNonNull(getSupportActionBar()).setTitle("CUSTOMER: Restaurants available");

        //initialize variables
        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        customRestList = new ArrayList<>();

        tVRestCustomShowMenu = findViewById(R.id.tvRestCustomShowMenu);
        tVRestCustomShowMenu.setText("No Restaurant found!!");

        customerRecyclerView = findViewById(R.id.customRecyclerView);
        customerRecyclerView.setHasFixedSize(true);
        customerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        restaurantAdapterCustomer = new RestaurantAdapterCustomer(RestaurantImageCustomerShowRest.this,customRestList);
        customerRecyclerView.setAdapter(restaurantAdapterCustomer);
        restaurantAdapterCustomer.setOnItmClickListener(RestaurantImageCustomerShowRest.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadRestaurantsList();
    }

    public void loadRestaurantsList() {

        //Retrieve data from Restaurants database
        databaseRefRest = FirebaseDatabase.getInstance().getReference("Restaurants");

        eventListenerRest = databaseRefRest.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customRestList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    Restaurants rest_Data = postSnapshot.getValue(Restaurants.class);

                    if (rest_Data != null) {
                        rest_Data.setRest_Key(postSnapshot.getKey());
                        customRestList.add(rest_Data);
                        tVRestCustomShowMenu.setText("Select your Restaurant");
                    }
                }

                restaurantAdapterCustomer.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RestaurantImageCustomerShowRest.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on Restaurants onClick
    @Override
    public void onItemClick(final int position) {

        final String[] options = {"Show Menus", "Back User Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);
        Restaurants selected_Rest =  customRestList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("You selected "+ selected_Rest.getRest_Name()+" Restaurant"+"\nSelect an option:")
                .setCancelable(false)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            Toast.makeText(RestaurantImageCustomerShowRest.this, "Show Restaurant Menus", Toast.LENGTH_SHORT).show();
                            Intent intent_Update = new Intent(RestaurantImageCustomerShowRest.this, MenuImageCustomer.class);
                            intent_Update.putExtra("RName",selected_Rest.getRest_Name());
                            intent_Update.putExtra("RKey",selected_Rest.getRest_Key());
                            startActivity(intent_Update);
                            customRestList.clear();
                        }

                        if (which == 1) {
                            Toast.makeText(RestaurantImageCustomerShowRest.this, "Back to User page", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RestaurantImageCustomerShowRest.this, UserPage.class));
                        }
                    }
                })

                .setNegativeButton("CLOSE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}