package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UpdateRestaurant extends AppCompatActivity {

    //Check Restaurant name into Restaurant database
    private DatabaseReference databaseRefRestNameCheck;

    //Save updated Restaurant data to database
    private DatabaseReference databaseRefRestUpdate;

    //Check Restaurant name into Menu database
    private DatabaseReference databaseRefMenuCheck;

    //Save updated Restaurant name to Menu database
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

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN: Update Restaurant");

        progressDialog = new ProgressDialog(this);

        tVRestUpdate = findViewById(R.id.tvRestUpdate);
        restNameUp = findViewById(R.id.etRestNameUp);
        restAddressUp = findViewById(R.id.etRestAddressUp);

        Bundle bundleRest = getIntent().getExtras();
        if (bundleRest != null) {
            restNameUpdate = bundleRest.getString("RName");
            restAddressUpdate = bundleRest.getString("RAddress");
            restKeyUpdate =  bundleRest.getString("RKey");
        }

        tVRestUpdate.setText("Update: " + restNameUpdate + " Restaurant");

        restNameUp.setText(restNameUpdate);
        restAddressUp.setText(restAddressUpdate);

        Button btnSaveRestUpdates = findViewById(R.id.btnSaveRestUpdate);
        btnSaveRestUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRestaurantName();
            }
        });
    }

    private void checkRestaurantName() {

        final String rest_nameCheck = restNameUp.getText().toString().trim();

        databaseRefRestNameCheck = FirebaseDatabase.getInstance().getReference("Restaurants");

        databaseRefRestNameCheck.orderByChild("rest_Name").equalTo(rest_nameCheck)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            alertDialogRestExist();
                        } else {
                            uploadRestDetailsUpdate();
                            uploadRestNameMenuUp();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(UpdateRestaurant.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadRestDetailsUpdate() {

        if (validateRestDetailsUpdate()) {

            rest_NameUp = restNameUp.getText().toString().trim();
            rest_AddressUp = restAddressUp.getText().toString().trim();

            progressDialog.setMessage("The " + restNameUpdate + " restaurant is updating!!");
            progressDialog.show();

            databaseRefRestUpdate = FirebaseDatabase.getInstance().getReference("Restaurants");

            databaseRefRestUpdate.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String rest_Key = postSnapshot.getKey();
                        assert rest_Key != null;

                        if (rest_Key.equals(restKeyUpdate)){
                            postSnapshot.getRef().child("rest_Name").setValue(rest_NameUp);
                            postSnapshot.getRef().child("rest_Address").setValue(rest_AddressUp);
                        }
                    }

                    //progressDialog.dismiss();
                    //Toast.makeText(UpdateRestaurant.this, "The Restaurant has been updated", Toast.LENGTH_SHORT).show();
                    //finish();
                    //startActivity(new Intent(UpdateRestaurant.this, AdminPage.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UpdateRestaurant.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Boolean validateRestDetailsUpdate() {

        boolean result = false;

        rest_NameUp = restNameUp.getText().toString().trim();
        rest_AddressUp = restAddressUp.getText().toString().trim();

        if (TextUtils.isEmpty(rest_NameUp)) {
            restNameUp.setError("Enter Restaurant name");
            restNameUp.requestFocus();
        } else if (TextUtils.isEmpty(rest_AddressUp)) {
            restAddressUp.setError("Enter Restaurant Address");
            restAddressUp.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    private void uploadRestNameMenuUp() {

        final String menuRest_NameUp = restNameUp.getText().toString().trim();

        //progressDialog.show();

        databaseRefMenuCheck = FirebaseDatabase.getInstance().getReference("Menus");

        databaseRefMenuCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    Menus menus_restUpdate = postSnapshot.getValue(Menus.class);

                    if (menus_restUpdate != null){
                        String menuRest_Key = menus_restUpdate.getRestaurant_Key();

                        if (menuRest_Key.equals(restKeyUpdate)){
                            postSnapshot.getRef().child("restaurant_Name").setValue(menuRest_NameUp);
                        }
                    }
                }

                progressDialog.dismiss();
                startActivity(new Intent(UpdateRestaurant.this, AdminPage.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateRestaurant.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void alertDialogRestExist(){
        final String rest_alertNameCheck = restNameUp.getText().toString().trim();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("The Restaurant " + rest_alertNameCheck + " already exists!")
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}