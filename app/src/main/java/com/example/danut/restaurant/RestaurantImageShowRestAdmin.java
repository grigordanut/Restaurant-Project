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

public class RestaurantImageShowRestAdmin extends AppCompatActivity implements RestaurantAdapterShowRestAdmin.OnItemClickListener{

    TextView tVRestImageShowRestAdmin;

    private DatabaseReference databaseReference;
    private ValueEventListener restaurantEventListener;

    private RecyclerView restaurantRecyclerView;
    private RestaurantAdapterShowRestAdmin restaurantAdapterShowRestAdmin;

    public List<Restaurants> restaurantList;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_image_show_rest_admin);

        tVRestImageShowRestAdmin = findViewById(R.id.tvRestListAdmin);
        tVRestImageShowRestAdmin.setText("No Restaurants available");

        restaurantRecyclerView = findViewById(R.id.restRecyclerView);
        restaurantRecyclerView.setHasFixedSize(true);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        restaurantList = new ArrayList<>();

        progressDialog.show();

        Button buttonAddMoreRest = findViewById(R.id.btnAddMoreRest);
        buttonAddMoreRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantImageShowRestAdmin.this, AddNewRestaurant.class));
            }
        });

        Button buttonBackAdminPageRest = findViewById(R.id.btnBackAdminPageRest);
        buttonBackAdminPageRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantImageShowRestAdmin.this, AdminPage.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadRestListAdmin();
    }

    private void loadRestListAdmin() {
        //initialize the restaurant database
        databaseReference = FirebaseDatabase.getInstance().getReference("Restaurants");

        restaurantEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                restaurantList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Restaurants rest = postSnapshot.getValue(Restaurants.class);
                    assert rest != null;
                    rest.setRest_Key(postSnapshot.getKey());
                    restaurantList.add(rest);
                    tVRestImageShowRestAdmin.setText(restaurantList.size()+" Restaurants available");
                }
                restaurantAdapterShowRestAdmin = new RestaurantAdapterShowRestAdmin(RestaurantImageShowRestAdmin.this, restaurantList);
                restaurantRecyclerView.setAdapter(restaurantAdapterShowRestAdmin);
                restaurantAdapterShowRestAdmin.setOnItmClickListener(RestaurantImageShowRestAdmin.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RestaurantImageShowRestAdmin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Press long click to show more actions: ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddMenuClick(int position) {
        Intent intent = new Intent(RestaurantImageShowRestAdmin.this, AddNewMenu.class);
        Restaurants selected_Rest = restaurantList.get(position);
        intent.putExtra("RName", selected_Rest.getRest_Name());
        intent.putExtra("RKey", selected_Rest.getRest_Key());
        startActivity(intent);
    }

    @Override
    public void onUpdateRestClick(int position) {

        Intent intent = new Intent(RestaurantImageShowRestAdmin.this, UpdateRestaurant.class);
        Restaurants selected_Rest = restaurantList.get(position);
        intent.putExtra("RName", selected_Rest.getRest_Name());
        intent.putExtra("RAddress", selected_Rest.getRest_Address());
        intent.putExtra("RKey", selected_Rest.getRest_Key());
        startActivity(intent);
    }

    //Action of the menu Delete and alert dialog
    @Override
    public void onDeleteRestClick(final int position) {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(RestaurantImageShowRestAdmin.this);
        Restaurants selectedRest = restaurantList.get(position);
        builderAlert.setMessage("Are sure to delete " + selectedRest.getRest_Name() + " Restaurant?");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    Restaurants selectedRest1 = restaurantList.get(position);
                    String selectedKeyRest = selectedRest1.getRest_Key();
                    databaseReference.child(selectedKeyRest).removeValue();
                    Toast.makeText(RestaurantImageShowRestAdmin.this, "The Restaurant " + selectedRest1.getRest_Name() + " has been deleted successfully", Toast.LENGTH_SHORT).show();

                });

        builderAlert.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert1 = builderAlert.create();
        alert1.show();
    }

    @Override
    public void alertDialogRestaurantNotEmpty(final int position) {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(RestaurantImageShowRestAdmin.this);
        Restaurants selected_rest = restaurantList.get(position);
        builderAlert.setMessage("The " +selected_rest.getRest_Name()+ " Restaurant still has Menus and cannot be deleted \nDelete the Menus first and after delete the Restaurant");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Ok",
                (arg0, arg1) -> {
                });

        AlertDialog alert1 = builderAlert.create();
        alert1.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(restaurantEventListener);
        tVRestImageShowRestAdmin.setText(restaurantList.size()+" Restaurants available");
    }
}