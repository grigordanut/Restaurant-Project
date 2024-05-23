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
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantImageAdminShowMenu extends AppCompatActivity implements RestaurantAdapterAdmin.OnItemClickListener {

    private DatabaseReference databaseRefRest;

    private DatabaseReference databaseRefAddMenu;
    private ValueEventListener evListenerRest;

    private FirebaseStorage firebaseStMenu;
    private DatabaseReference databaseRefMenu;

    private Restaurants rest;

    private TextView tVRestImageShowMenusAdmin;

    private RecyclerView restaurantRecyclerView;
    private RestaurantAdapterAdmin restaurantAdapterAdmin;

    private List<Restaurants> restaurantsList;

    private ProgressDialog progressDialog;

    //private RestaurantAdapterAdminAddMenu restaurantAdapterAdminAddMenu;

    private ArrayAdapter<String> arrayAdapter;

    private List<String> restaurantsListAddMenu;

    private String restAddMenu_Key = "";
    private String restAddMenu_Name = "";

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

        databaseRefAddMenu = FirebaseDatabase.getInstance().getReference("Restaurants");

        firebaseStMenu = FirebaseStorage.getInstance();
        databaseRefMenu = FirebaseDatabase.getInstance().getReference("Menus");

        restaurantsList = new ArrayList<>();
        restaurantsListAddMenu = new ArrayList<>();

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RestaurantImageAdminShowMenu.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        progressDialog.dismiss();
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

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_restaurant_image_admin_show_menu, menu);
        return true;
    }

    public void menuRestImgAdminShowMenuGoBack() {
        startActivity(new Intent(RestaurantImageAdminShowMenu.this, AdminPage.class));
        finish();
    }

    public void menuRestImgAdminShowMenuAddMenu() {
        getRestNameAddMenu();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menuRestImgAdminShowMenu_goBack) {
            menuRestImgAdminShowMenuGoBack();
        }

        if (item.getItemId() == R.id.menuRestImgAdminShowMenu_addMenu) {
            menuRestImgAdminShowMenuAddMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    public void getRestNameAddMenu() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RestaurantImageAdminShowMenu.this);

        arrayAdapter = new ArrayAdapter<>(RestaurantImageAdminShowMenu.this, android.R.layout.simple_list_item_single_choice, restaurantsListAddMenu);
        databaseRefAddMenu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                restaurantsListAddMenu.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    rest = postSnapshot.getValue(Restaurants.class);

                    assert rest != null;

                    rest.setRest_Key(postSnapshot.getKey());
                    restaurantsListAddMenu.add(rest.getRest_Name());

                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RestaurantImageAdminShowMenu.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilder
                .setTitle("Select the Restaurant!!")
                .setCancelable(false)
                .setSingleChoiceItems(arrayAdapter, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restAddMenu_Name = restaurantsListAddMenu.get(i);

                        //get Restaurant Key
                        Query query = databaseRefAddMenu.orderByChild("rest_Name").equalTo(restAddMenu_Name);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    postSnapshot.getKey();
                                    restAddMenu_Key = postSnapshot.getKey();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(RestaurantImageAdminShowMenu.this, error.getCode(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                })
                .setPositiveButton("NEXT", (dialogInterface, i) -> {
                    Intent intent_addMenu = new Intent(RestaurantImageAdminShowMenu.this, AddNewMenu.class);
                    intent_addMenu.putExtra("RName", restAddMenu_Name);
                    intent_addMenu.putExtra("RKey", restAddMenu_Key);
                    startActivity(intent_addMenu);
                })
                .setNegativeButton("CLOSE", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

//    public void confirmSelection(final int position) {
//
//        rest = restaurantsListAddMenu.get(position);
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RestaurantImageAdminShowMenu.this);
//        alertDialogBuilder
//                .setTitle("You selected the: " + rest.getRest_Name() + " restaurant.")
//                .setMessage("Are sure to add menu to the " + rest.getRest_Name() + " restaurant?")
//                .setCancelable(false)
//                .setPositiveButton("YES", (dialog, which) -> {
//
//                    Intent intent_addMenu = new Intent(RestaurantImageAdminShowMenu.this, AddNewMenu.class);
//                    intent_addMenu.putExtra("RName", restAddMenu_Name);
//                    intent_addMenu.putExtra("RKey", restAddMenu_Key);
//                    startActivity(intent_addMenu);
//                })
//
//                .setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }
}