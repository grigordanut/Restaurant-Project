package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UpdateRestaurant extends AppCompatActivity {

    //Check Restaurant name into Restaurant database
    private DatabaseReference databaseRefRestNameCheck;

    //Save updated Restaurant data to database
    private DatabaseReference databaseRefRestUpdate;

    //Check Restaurant name into Menu database
    private DatabaseReference databaseRefMenuUpdate;

    private TextView tVRestUpdate;

    private EditText restNameUp, restAddressUp;

    private String rest_NameUp, rest_AddressUp;

    private String restNameUpdate = "";
    private String restAddressUpdate = "";
    private String restKeyUpdate = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_restaurant);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN: update Restaurant");

        progressDialog = new ProgressDialog(this);

        databaseRefRestNameCheck = FirebaseDatabase.getInstance().getReference("Restaurants");
        databaseRefRestUpdate = FirebaseDatabase.getInstance().getReference("Restaurants");
        databaseRefMenuUpdate = FirebaseDatabase.getInstance().getReference("Menus");

        tVRestUpdate = findViewById(R.id.tvRestUpdate);
        restNameUp = findViewById(R.id.etRestNameUp);
        restAddressUp = findViewById(R.id.etRestAddressUp);

        Bundle bundleRest = getIntent().getExtras();
        if (bundleRest != null) {
            restNameUpdate = bundleRest.getString("RName");
            restAddressUpdate = bundleRest.getString("RAddress");
            restKeyUpdate = bundleRest.getString("RKey");
        }

        tVRestUpdate.setText("Update Restaurant: " + restNameUpdate);

        restNameUp.setText(restNameUpdate);
        restAddressUp.setText(restAddressUpdate);

        Button btn_SaveRestUpdates = findViewById(R.id.btnSaveRestUpdate);
        btn_SaveRestUpdates.setOnClickListener(v -> checkRestaurantName());
    }

    private void checkRestaurantName() {

        progressDialog.setTitle("Update the Restaurant: " + restNameUpdate + "!!");
        progressDialog.show();

        final String rest_nameCheck = restNameUp.getText().toString().trim();

        Query query = databaseRefRestNameCheck.orderByChild("rest_Name").equalTo(rest_nameCheck);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    alertDialogRestExist();
                } else {
                    updateRestDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateRestaurant.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRestDetails() {

        if (validateRestDetailsUpdate()) {

            Restaurants rest_Data = new Restaurants(rest_NameUp, rest_AddressUp);

            databaseRefRestUpdate.child(restKeyUpdate).setValue(rest_Data).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    uploadRestNameMenuUp();
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {
                        Toast.makeText(UpdateRestaurant.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void uploadRestNameMenuUp() {

        final String menuRest_NameUp = restNameUp.getText().toString().trim();

        databaseRefMenuUpdate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    Menus menus_restUpdate = postSnapshot.getValue(Menus.class);

                    assert menus_restUpdate != null;
                    String menuRest_Key = menus_restUpdate.getRestaurant_Key();

                    if (menuRest_Key.equals(restKeyUpdate)) {
                        postSnapshot.getRef().child("restaurant_Name").setValue(menuRest_NameUp);
                    }
                }

                progressDialog.dismiss();
                Toast.makeText(UpdateRestaurant.this, "Restaurant Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateRestaurant.this, AdminPage.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateRestaurant.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Boolean validateRestDetailsUpdate() {

        boolean result = false;

        rest_NameUp = restNameUp.getText().toString().trim();
        rest_AddressUp = restAddressUp.getText().toString().trim();

        if (TextUtils.isEmpty(rest_NameUp)) {
            restNameUp.setError("Enter Restaurant name");
            restNameUp.requestFocus();
        } else if (TextUtils.isEmpty(rest_AddressUp)) {
            restAddressUp.setError("Enter Restaurant address");
            restAddressUp.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    private void alertDialogRestExist() {

        final String rest_alertNameCheck = restNameUp.getText().toString().trim();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("The Restaurant: " + rest_alertNameCheck + " already exists!")
                .setMessage("Save the Restaurant with same name?")
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, id) -> updateRestDetails())

                .setNegativeButton("NO", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}