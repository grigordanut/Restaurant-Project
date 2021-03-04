package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddRestaurant extends AppCompatActivity {

    //declare variables
    DatabaseReference databaseRefRest;
    ValueEventListener eventListener;
    List<Restaurants> restaurants;
    List<String> restNames;

    ListView listView;
    ArrayAdapter<String> adapter;

    Button buttonNewRestaurant, buttonShowMap;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        //initialize variables
        progressDialog = new ProgressDialog(this);

        restNames = new ArrayList<>();
        restNames.add("No Restaurant found");

        listView = findViewById(R.id.listViewRest);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, restNames);
        listView.setAdapter(adapter);

        //action of the clicked listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (restaurants.size() > 0) {
                    String restaurantName = restNames.get(position);
                    Intent intent = new Intent(AddRestaurant.this, ImageActivityAdmin.class);
                    intent.putExtra("RESID", restaurantName);
                    startActivity(intent);
                }
            }
        });

        //action of the button show map
        buttonShowMap = (Button) findViewById(R.id.btnShowMap);
        buttonShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddRestaurant.this, MapsActivity.class));
            }
        });

        buttonNewRestaurant = findViewById(R.id.btnNewRestaurant);
        buttonNewRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get prompts.xml view
                Context context = AddRestaurant.this;
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.add_restaurant, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to alert dialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText rest_name = (EditText) promptsView
                        .findViewById(R.id.etRestName);

                final EditText rest_address = (EditText) promptsView
                        .findViewById(R.id.etRestAddress);

                final String rest_Name = rest_name.getText().toString().trim();

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (TextUtils.isEmpty(rest_Name)) {
                                            rest_name.setError("Enter Restaurant name");
                                        } else if (TextUtils.isEmpty(rest_address.getText().toString())) {
                                            rest_address.setError("Enter Restaurant address");
                                        } else {
                                            createRestaurant(rest_name.getText().toString(), rest_address.getText().toString());
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
    }

    // Create a new restaurant item in the database if it not already added
    public void createRestaurant(final String restName, String restAddress) {
        boolean restaurantExist = false;
        progressDialog.show();
        if (databaseRefRest == null) {
            databaseRefRest = FirebaseDatabase.getInstance().getReference().child("Restaurants");
        }
        loadRestaurantsList();
        for (Restaurants r : restaurants) {
            if (r != null) {
                if (r.getRest_Name().equals(restName)) {
                    restaurantExist = true;
                    break;
                }
            }
        }
        if (!restaurantExist) {
            String rest_ID = databaseRefRest.push().getKey();
            Restaurants re = new Restaurants(restName, restAddress, rest_ID);
            databaseRefRest.push().setValue(re).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        loadRestaurantsList();
                    } else {
                        Toast.makeText(AddRestaurant.this
                                , "Could not add new restaurant", Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(AddRestaurant.this, "Restaurant exist!", Toast.LENGTH_SHORT).show();
        }
    }

    //Load all of the available restaurant from the database and add them to an arraylist
    private void loadRestaurantsList() {
        restNames = new ArrayList<>();
        restaurants = new ArrayList<>();
        progressDialog.show();
        if (databaseRefRest == null) {
            databaseRefRest = FirebaseDatabase.getInstance().getReference().child("Restaurants");
        }
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Restaurants rest = child.getValue(Restaurants.class);
                    if (rest != null) {
                        restaurants.add(rest);
                        restNames.add(rest.getRest_Name());
                    }
                }
                if (restNames.size() > 0) {
                    adapter.clear();
                    adapter.addAll(restNames);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddRestaurant.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseRefRest.addValueEventListener(listener);
        eventListener = listener;

        progressDialog.dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (restNames.isEmpty()) {
            restNames.add("No Restaurant found");
        } else {
            loadRestaurantsList();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (eventListener != null) {
            databaseRefRest.removeEventListener(eventListener);
        }
    }

    private void LogOutRestaurant() {
        finish();
        startActivity(new Intent(AddRestaurant.this, MainActivity.class));
    }

    private void RestaurantDetails() {
        finish();
        startActivity(new Intent(AddRestaurant.this, RestaurantDetails.class));
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_restaurant, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logOutRestaurant) {
            LogOutRestaurant();
        }

        if (item.getItemId() == R.id.restDetails) {
            RestaurantDetails();
        }
        return super.onOptionsItemSelected(item);
    }
}
