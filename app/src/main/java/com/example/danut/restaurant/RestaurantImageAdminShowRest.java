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
import android.view.View;
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

public class RestaurantImageAdminShowRest extends AppCompatActivity implements RestaurantAdapterAdminShowRest.OnItemClickListener {

    private DatabaseReference databaseReference;
    private ValueEventListener restaurantEventListener;

    private TextView tVRestImageShowRestAdmin;

    private RecyclerView restaurantRecyclerView;
    private RestaurantAdapterAdminShowRest restaurantAdapterAdminShowRest;

    public List<Restaurants> restaurantList;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_image_admin_show_rest);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN: Restaurants available");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        //Retrieve data from Restaurants database
        databaseReference = FirebaseDatabase.getInstance().getReference("Restaurants");

        restaurantList = new ArrayList<>();

        tVRestImageShowRestAdmin = findViewById(R.id.tvRestListAdmin);

        restaurantRecyclerView = findViewById(R.id.restRecyclerView);
        restaurantRecyclerView.setHasFixedSize(true);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        restaurantAdapterAdminShowRest = new RestaurantAdapterAdminShowRest(RestaurantImageAdminShowRest.this, restaurantList);
        restaurantRecyclerView.setAdapter(restaurantAdapterAdminShowRest);
        restaurantAdapterAdminShowRest.setOnItmClickListener(RestaurantImageAdminShowRest.this);

        Button btn_AddMoreRest = findViewById(R.id.btnAddMoreRest);
        btn_AddMoreRest.setOnClickListener(v -> startActivity(new Intent(RestaurantImageAdminShowRest.this, AddNewRestaurant.class)));

        Button btn_BackAdminPageRest = findViewById(R.id.btnBackAdminPageRest);
        btn_BackAdminPageRest.setOnClickListener(v -> startActivity(new Intent(RestaurantImageAdminShowRest.this, AdminPage.class)));
    }

    @Override
    public void onStart() {
        super.onStart();
        loadRestListAdmin();
    }

    private void loadRestListAdmin() {

        restaurantEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    restaurantList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Restaurants rest = postSnapshot.getValue(Restaurants.class);
                        assert rest != null;
                        rest.setRest_Key(postSnapshot.getKey());
                        restaurantList.add(rest);
                        tVRestImageShowRestAdmin.setText(restaurantList.size() + " Restaurants available");
                    }

                    restaurantAdapterAdminShowRest.notifyDataSetChanged();
                }
                else {
                    tVRestImageShowRestAdmin.setText("No registered Restaurants.");
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RestaurantImageAdminShowRest.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action of the Menu onClick
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Press long click to show more actions: ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddMenuClick(int position) {
        Intent intent = new Intent(RestaurantImageAdminShowRest.this, AddNewMenu.class);
        Restaurants selected_Rest = restaurantList.get(position);
        intent.putExtra("RName", selected_Rest.getRest_Name());
        intent.putExtra("RKey", selected_Rest.getRest_Key());
        startActivity(intent);
    }

    @Override
    public void onUpdateRestClick(int position) {
        Intent intent = new Intent(RestaurantImageAdminShowRest.this, UpdateRestaurant.class);
        Restaurants selected_Rest = restaurantList.get(position);
        intent.putExtra("RName", selected_Rest.getRest_Name());
        intent.putExtra("RAddress", selected_Rest.getRest_Address());
        intent.putExtra("RKey", selected_Rest.getRest_Key());
        startActivity(intent);
    }

    //Action of the menu Delete and alert dialog
    @Override
    public void onDeleteRestClick(final int position) {

        Restaurants selectedRest = restaurantList.get(position);

        AlertDialog.Builder builderAlert = new AlertDialog.Builder(RestaurantImageAdminShowRest.this);
        builderAlert
                .setTitle("Delete Restaurant!!")
                .setMessage("Are sure to delete the restaurant:\n" + selectedRest.getRest_Name() + "?")
                .setCancelable(true)
                .setPositiveButton("YES", (dialog, id) -> {
                    String selectedRestKey = selectedRest.getRest_Key();
                    databaseReference.child(selectedRestKey).removeValue();
                    Toast.makeText(RestaurantImageAdminShowRest.this, "The Restaurant " + selectedRest.getRest_Name() + " has been deleted successfully", Toast.LENGTH_SHORT).show();
                })

                .setNegativeButton("NO", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = builderAlert.create();
        alertDialog.show();
    }

    @Override
    public void alertDialogRestaurantNotEmpty(final int position) {

        Restaurants selected_rest = restaurantList.get(position);

        AlertDialog.Builder builderAlert = new AlertDialog.Builder(RestaurantImageAdminShowRest.this);
        builderAlert
                .setMessage("The " + selected_rest.getRest_Name() + " Restaurant still has Menus and cannot be deleted \nDelete the Menus first and after delete the Restaurant.")
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = builderAlert.create();
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(restaurantEventListener);
    }
}